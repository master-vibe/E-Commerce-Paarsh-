package com.paarsh.admin_paarsh.controller;

import com.paarsh.admin_paarsh.dto.LoginDTO;
import com.paarsh.admin_paarsh.exceptions.UserNotFoundException;
import com.paarsh.admin_paarsh.model.Admin;
import com.paarsh.admin_paarsh.service.AdminService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<HttpStatus> signUp(@RequestBody Admin admin) {
        try {
            System.out.println(admin.toString());
            adminService.signUp(admin);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user-id")) {
                    long userId = Long.parseLong(cookie.getValue());
                    // You might want to return a specific response or handle this userId
                    // For now, we'll just return OK if userId is present
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        }

        long token = adminService.login(loginDTO.getEmail(), loginDTO.getPassword());
        Cookie cookie = new Cookie("user-id", "");
        cookie.setMaxAge(0); // delete the cookie initially

        if (token > 0) {
            cookie.setValue(String.valueOf(token));
            cookie.setMaxAge(86400); // expires in 1 day
            response.addCookie(cookie);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            response.addCookie(cookie); // set cookie with no value to delete
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<Admin> updateProfile(@RequestBody Admin updatedAdmin, HttpServletRequest request) {
        // Extract user ID from cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user-id")) {
                    try {
                        Long userId = Long.parseLong(cookie.getValue());
                        return ResponseEntity.ok(adminService.updateProfile(userId, updatedAdmin));
                    } catch (UserNotFoundException e) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    } catch (Exception e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<HttpStatus> forgotPassword(@RequestBody String email) throws UserNotFoundException {
        adminService.initiatePasswordReset(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<HttpStatus> resetPassword(@RequestParam String token, @RequestBody String newPassword) {
        try {
            adminService.resetPassword(token, newPassword);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
