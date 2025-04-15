package com.ecommerce.user.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
import com.ecommerce.user.dto.TokenRefreshRequest;
import com.ecommerce.user.model.Role;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.RoleRepository;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.CustomUserDetails;
import com.ecommerce.user.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final JwtUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;
	private final RedisService redisService;
	private final BCryptPasswordEncoder passwordEncoder;

	public AuthResponse register(RegisterRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("Email already in use");
		}

		Role userRole = roleRepository.findByName("ROLE_USER")
				.orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.getRoles().add(userRole);

		userRepository.save(user);

		UserDetails userDetails = new CustomUserDetails(user);
		String accessToken = jwtUtil.generateToken(userDetails);
		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		redisTemplate.opsForValue().set("refresh_token:" + user.getEmail(), refreshToken, 7, TimeUnit.DAYS);

		List<String> roles = user.getRoles().stream().map(Role::getName).toList();
		log.info("User registered successfully: {}", user.getEmail());

		return new AuthResponse(accessToken, refreshToken, user.getUsername(), roles);
	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}

		UserDetails userDetails = new CustomUserDetails(user);
		String accessToken = jwtUtil.generateToken(userDetails);
		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		redisTemplate.opsForValue().set("refresh_token:" + user.getEmail(), refreshToken, 7, TimeUnit.DAYS);

		List<String> roles = user.getRoles().stream().map(Role::getName).toList();
		log.info("User logged in successfully: {}", user.getEmail());

		return new AuthResponse(accessToken, refreshToken, user.getUsername(), roles);
	}

	public AuthResponse refreshToken(TokenRefreshRequest request) {
		String refreshToken = request.getRefreshToken();
		String userEmail = jwtUtil.getUsername(refreshToken);

		if (userEmail == null) {
			throw new RuntimeException("Invalid refresh token");
		}

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new RuntimeException("User not found"));

		UserDetails userDetails = new CustomUserDetails(user);

		if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
			throw new RuntimeException("Refresh token is invalid or expired");
		}

		String storedToken = redisTemplate.opsForValue().get("refresh_token:" + userEmail);
		if (!refreshToken.equals(storedToken)) {
			throw new RuntimeException("Refresh token reuse detected or revoked");
		}

		String newAccessToken = jwtUtil.generateToken(userDetails);
		String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

		redisTemplate.opsForValue().set("refresh_token:" + userEmail, newRefreshToken, 7, TimeUnit.DAYS);

		List<String> roles = user.getRoles().stream().map(Role::getName).toList();
		log.info("Token refreshed successfully for user: {}", userEmail);

		return new AuthResponse(newAccessToken, newRefreshToken, user.getUsername(), roles);
	}

	public void logout(String accessToken, String refreshToken) {
	    if (refreshToken == null || refreshToken.isBlank()) {
	        throw new IllegalArgumentException("Refresh token must not be empty");
	    }
	    
	    accessToken = stripBearerPrefix(accessToken);
	    refreshToken = stripBearerPrefix(refreshToken);
	    
	    String userEmail = jwtUtil.getUsername(refreshToken);

	    if (userEmail != null) {
	        // Blacklist Refresh Token
	        long refreshTtl = jwtUtil.getExpirationDuration(refreshToken);
	        redisService.blacklistToken(refreshToken, refreshTtl);

	        // Blacklist Access Token
	        if (accessToken != null && !accessToken.isBlank()) {
	            long accessTtl = jwtUtil.getExpirationDuration(accessToken);
	            redisService.blacklistToken(accessToken, accessTtl);
	        }

	        // Remove stored refresh token if you're saving it in Redis or DB
	        redisTemplate.delete("refresh_token:" + userEmail);

	        log.info("User logged out. Tokens revoked for: {}", userEmail);
	    } else {
	        throw new IllegalArgumentException("Invalid refresh token");
	    }
	}
	
	private String stripBearerPrefix(String token) {
	    if (token != null && token.startsWith("Bearer ")) {
	        return token.substring(7);
	    }
	    return token;
	}

}
