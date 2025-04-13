package com.ecommerce.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.CustomUserDetails;
import com.ecommerce.user.security.JwtAuthenticationFilter;
import com.ecommerce.user.util.ApplicationContextProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter authenticationFilter;
	private final UserRepository userRepository;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
						auth -> auth
						.requestMatchers("/auth/**").permitAll()
						.anyRequest().authenticated())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(daoAuthenticationProvider())
				.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(email -> {
			User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));
			return new CustomUserDetails(user);
		});
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

//	@Bean
//	public UserRepository userRepository() {
//		return ApplicationContextProvider.getBean(UserRepository.class);
//	}

}
