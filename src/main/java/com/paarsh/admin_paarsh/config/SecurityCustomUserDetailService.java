package com.paarsh.admin_paarsh.config;

import com.paarsh.admin_paarsh.exceptions.UserNotFoundException;
import com.paarsh.admin_paarsh.model.Admin;
import com.paarsh.admin_paarsh.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityCustomUserDetailService implements UserDetailsService {

    private final AdminRepository adminRepository;
    @Autowired
    public SecurityCustomUserDetailService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("Username Not found with this Email ID"+username));
        System.out.println(admin.getPassword());
        return User.withUsername(admin.getUsername())
                .password(admin.getPassword())
                .build();
    }
}
