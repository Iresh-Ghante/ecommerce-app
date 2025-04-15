package com.ecommerce.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.ecommerce.gateway.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
@EnableReactiveMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter authenticationFilter;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/auth/**").permitAll()
//                .anyRequest().authenticated())
//            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
//            .build();
//    }

	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
		http.csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(
				exchange -> exchange
				.pathMatchers("/auth/**").permitAll()
				.anyExchange().authenticated());
		return http.build();
	}

}
