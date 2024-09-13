package com.paarsh.admin_paarsh.controller;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.paarsh.admin_paarsh.dto.ForgotPasswordDTO;
import com.paarsh.admin_paarsh.dto.LoginDTO;
import com.paarsh.admin_paarsh.dto.newPasswordDTO;
import com.paarsh.admin_paarsh.model.Admin;
import com.paarsh.admin_paarsh.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;

@RestController
public class AdminController {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;

    private final JwtDecoder jwtDecoder;

    @Autowired
    public AdminController(AdminService adminService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtDecoder jwtDecoder) {
        this.adminService = adminService;
        this.authenticationManager = authenticationManager;
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping("/auth")
    public ResponseEntity<Object> auth(@RequestHeader("Authorization") String authHead){
        try{
            authHead = authHead.substring(7);
            Jwt jwt = jwtDecoder.decode(authHead);
            String username = jwt.getClaim("sub"); // 'sub' claim usually contains username
            String jwtToken = createJwtToken(username);
            var responseBody = new HashMap<String,Object>();
            responseBody.put("token",jwtToken);
            return new ResponseEntity<>(responseBody,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody Admin admin) {
        try {
//            System.out.println(admin.toString());
            admin = adminService.signUp(admin);
            String jwtToken = createJwtToken(admin.getUsername());
            var response = new HashMap<String,Object>();
            response.put("token",jwtToken);
            response.put("user",admin);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO) {
        try {
            System.out.println(loginDTO.getPassword());
            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),loginDTO.getPassword()));
            System.out.println(authentication.getCredentials());
            Admin admin = adminService.findByUsername(loginDTO.getEmail());
            System.out.println("Password>>>>>>>>"+(admin.getPassword()));
            String jwtToken = createJwtToken(admin.getUsername());

            var response = new HashMap<String,Object>();
            response.put("token",jwtToken);
            response.put("user",admin);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch (Exception e){
            System.out.println("Error<<<<<<<<"+e.getMessage());

            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<Object> updateProfile(@RequestBody Admin updatedAdmin, Authentication authentication) {
        var response = new HashMap<String,Object>();
        response.put("Username",authentication.getName());
        try {
            Admin admin = adminService.findByUsername(authentication.getName());
            admin = adminService.updateProfile(admin.getUserId(),updatedAdmin);
            response.put("Admin",admin);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody ForgotPasswordDTO passwordDTO){
        try {
            adminService.initiatePasswordReset(passwordDTO.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<HttpStatus> resetPassword(@RequestParam String token, @RequestBody newPasswordDTO newPassword) {
        try {
            adminService.resetPassword(token, newPassword.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    private String createJwtToken(String username){
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(1))
                .subject(username)
                .build();
        var encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(jwtSecretKey.getBytes())
        );
        var params = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return encoder.encode(params).getTokenValue();
    }
}
