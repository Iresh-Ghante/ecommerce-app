package com.ecommerce.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.product.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/products/admin/**").hasRole("ADMIN")  // Admin endpoints require ADMIN role
                .requestMatchers("/api/products/**").authenticated()  // All product-related endpoints require authentication
                .anyRequest().permitAll()  // All other requests are permitted without authentication
            )
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);  // Apply JWT filter before other filters
        
        return http.build();
    }
}
