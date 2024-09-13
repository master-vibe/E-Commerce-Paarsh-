package com.paarsh.admin_paarsh.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;
    private final JwtDecoder jwtDecoder;
    private final SecurityCustomUserDetailService securityCustomUserDetailService;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder, SecurityCustomUserDetailService userDetailsService) {
        this.jwtDecoder = jwtDecoder;
        this.securityCustomUserDetailService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Inside Internal Filter>>>>>>>>>>>>>\n" + request.getRequestURI());
        String authHeader = request.getHeader("Authorization");
        String token = null;
        boolean authorization = false;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                Jwt decodedJwt = jwtDecoder.decode(token);
                authorization=true;
            } catch (Exception e) {
                logger.error("JWT decoding failed: {}",e.getCause());
            }
        }
        filterChain.doFilter(request, response);
        response.setStatus(authorization? response.getStatus():HttpServletResponse.SC_NOT_FOUND);
    }
}
