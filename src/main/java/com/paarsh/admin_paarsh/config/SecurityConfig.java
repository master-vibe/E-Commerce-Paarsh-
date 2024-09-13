package com.paarsh.admin_paarsh.config;

import com.paarsh.admin_paarsh.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Collections;
import java.util.Date;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Autowired
    private SecurityCustomUserDetailService securityCustomUserDetailService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception{
        System.out.println("Inside FilterChain>>>>>>>>>>>");

        return http

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/auth").permitAll()
                        .requestMatchers("/reports/").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/products").permitAll()
                        .requestMatchers("/signUp").permitAll()
                        .requestMatchers("/forgot-password").permitAll()
                        .requestMatchers("/orders/").permitAll()
                        .requestMatchers("/updateProfile").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2->oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session->session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .addFilterBefore(new JwtAuthenticationFilter(jwtDecoder, securityCustomUserDetailService),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList("*")); // Allow all origins
        corsConfig.setAllowedMethods(Collections.singletonList("*")); // Allow all methods
        corsConfig.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers
        corsConfig.setAllowCredentials(false); // Disable credentials
        return corsConfig;
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration()); // Apply CORS settings to all paths
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        var secretKey = new SecretKeySpec(jwtSecretKey.getBytes(),"HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256).build();
    }
    @Bean
    public AuthenticationManager authenticationManager(SecurityCustomUserDetailService securityCustomUserDetailService){
        System.out.println("Inside AuthenticationManager>>>>>>>>>");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(securityCustomUserDetailService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());

        return new ProviderManager(provider);
    }
}
