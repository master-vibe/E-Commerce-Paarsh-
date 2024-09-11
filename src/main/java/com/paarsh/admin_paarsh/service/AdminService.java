package com.paarsh.admin_paarsh.service;

import com.paarsh.admin_paarsh.config.EmailService;
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
    private final EmailService emailService;
    @Autowired
    public AdminService(AdminRepository adminRepository, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService) {
        this.adminRepository = adminRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public void signUp(Admin admin) {
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new UserAlreadyExitsException("User already exists with username "+admin.getEmail());
        }
        admin.setPassword(hashPassword(admin.getPassword()));

        adminRepository.save(admin);
    }

    public Admin updateProfile(Long userId, Admin updatedUser) throws UserNotFoundException {
        Admin existingUser = adminRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("admin does not exist!"));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setMobile(updatedUser.getMobile());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(hashPassword(updatedUser.getPassword()));
        }

        return adminRepository.save(existingUser);
    }

    public long login(String email, String password) {

        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

        Admin admin = optionalAdmin.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!checkPassword(password, admin.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return (admin.getUserId());
    }
    public void initiatePasswordReset(String email) throws UserNotFoundException {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user found with email " + email));

        // Generate a unique token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, admin);
        passwordResetTokenRepository.save(resetToken);

        // Send an email with the reset link
        String resetLink = "http://yourdomain.com/reset-password?token=" + token;
        emailService.sendSimpleMessage(admin.getEmail(), "Password Reset Request", "To reset your password, click the following link: " + resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        Admin admin = resetToken.getAdmin();
        admin.setPassword(hashPassword(newPassword));
        adminRepository.save(admin);

        // Remove the used token
        passwordResetTokenRepository.delete(resetToken);
    }



    private String hashPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return  passwordEncoder.encode(password);
    }


    private boolean checkPassword(String rawPassword, String hashedPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

}
