package com.anycomp.marketplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/**").permitAll() // Allow all API endpoints
                .requestMatchers("/h2-console/**").permitAll() // Allow H2 console if needed
                .requestMatchers("/actuator/**").permitAll() // Allow actuator endpoints
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().sameOrigin()); // For H2 console

        return http.build();
    }
}