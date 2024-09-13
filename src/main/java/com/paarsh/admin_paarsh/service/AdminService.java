package com.paarsh.admin_paarsh.service;

import com.paarsh.admin_paarsh.config.EmailService;
import com.paarsh.admin_paarsh.exceptions.MailIDDoesNotExistsExecption;
import com.paarsh.admin_paarsh.exceptions.UserAlreadyExitsException;
import com.paarsh.admin_paarsh.exceptions.UserNotFoundException;
import com.paarsh.admin_paarsh.model.Admin;
import com.paarsh.admin_paarsh.model.PasswordResetToken;
import com.paarsh.admin_paarsh.repository.AdminRepository;
import com.paarsh.admin_paarsh.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private  final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    @Autowired
    public AdminService(AdminRepository adminRepository, PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.adminRepository = adminRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Admin signUp(Admin admin) {
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new UserAlreadyExitsException("User already exists with username "+admin.getEmail());
        }
        admin.setUserId(UUID.randomUUID().toString());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRoles(admin.getRole());

        adminRepository.save(admin);
        return admin;
    }

    public Admin updateProfile(String userId, Admin updatedUser) throws UserNotFoundException {
        Admin existingUser = adminRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("admin does not exist!"));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setMobile(updatedUser.getMobile());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return adminRepository.save(existingUser);
    }

    public Admin findByUsername(String email) throws UserNotFoundException {

        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

        return optionalAdmin.orElseThrow(() -> new UserNotFoundException("admin does not exist!"));
    }
    public void initiatePasswordReset(String email) throws UserNotFoundException {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user found with email " + email));
        System.out.println("______________Forgot this______________");
        System.out.println("Email>>>>>>>>>>>>>>>>>>>>"+admin.getEmail());
        System.out.println("Password>>>>>>>>>>>>>>>>>"+admin.getPassword());
        // Generate a unique token
        try {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, admin);
            passwordResetTokenRepository.save(resetToken);

            // Send an email with the reset link
            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            emailService.sendSimpleMessage(admin.getEmail(), "Password Reset Request", "To reset your password, click the following link: " + resetLink);
        } catch (MailIDDoesNotExistsExecption e) {
            throw new MailIDDoesNotExistsExecption(e.getMessage());
        }
    }

    public void resetPassword(String token, String newPassword) {
        System.out.println("Password Entered>>>>>>>>>>>>>>>"+newPassword);
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        Admin admin = resetToken.getAdmin();
        System.out.println("__________________Saving this__________");
        System.out.println("Email>>>>>>>>>>>>>>>>>>>>"+admin.getEmail());
        System.out.println("Password Before>>>>>>>>>>>>>>>>>"+admin.getPassword());
        admin.setPassword(passwordEncoder.encode(newPassword));
        System.out.println("Password After Encoding>>>>>>>>>>>>>>>>>"+admin.getPassword());

        adminRepository.save(admin);

        passwordResetTokenRepository.delete(resetToken);
    }


}
