package com.ecommerce.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.order.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/orders/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/orders/**").authenticated()
                .anyRequest().permitAll()
                )
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        String secretKey = "bG9uZy1zZWN1cmUta2V5LWhlcmUtbG9uZy1zZWN1cmUta2V5LWhlcmU="; // use proper key management in production
//        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256")).build();
//    }
}
