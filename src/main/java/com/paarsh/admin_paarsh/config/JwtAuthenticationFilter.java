package com.paarsh.admin_paarsh.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.paarsh.admin_paarsh.service.AdminService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

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

        System.out.println("Inside Internal Filter>>>>>>>>>>>>>"+request.getRequestURI() );
        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            // Extract username from token, you can use JwtDecoder here
            try {
                Jwt decodedJwt = jwtDecoder.decode(token);

                Instant expiresAt = decodedJwt.getExpiresAt();

                logger.error(expiresAt);


            } catch (Exception e) {
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                logger.error("JWT decoding failed: {}");
            }
        }
        filterChain.doFilter(request, response);
    }
}
