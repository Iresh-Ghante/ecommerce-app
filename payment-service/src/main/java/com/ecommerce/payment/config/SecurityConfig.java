package com.ecommerce.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.payment.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthFilter authFilter;

	public SecurityConfig(JwtAuthFilter authFilter) {
		this.authFilter = authFilter;
	}

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/payments/**").authenticated()  // All payment-related endpoints require authentication
                .anyRequest().permitAll()
                )
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
