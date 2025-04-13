package com.ecommerce.user.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // Create user and assign default ROLE_USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(userRole);

        userRepository.save(user);

        // Generate tokens
        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateToken(userDetails); // refresh logic added later

        
        
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

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
        
        
     // Store refresh token in Redis
        redisTemplate.opsForValue().set("refresh_token:" + userDetails.getUsername(), refreshToken, 7, TimeUnit.DAYS);


        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        return new AuthResponse(accessToken, refreshToken, user.getUsername(), roles);
    }
    
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String userEmail = jwtUtil.getUsername(refreshToken);

        if (userEmail == null) throw new RuntimeException("Invalid refresh token");

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = new CustomUserDetails(user);
        if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        // Optional: check if token matches the one stored in DB
        String storedToken = redisTemplate.opsForValue().get("refresh_token:" + userEmail);
        if (!refreshToken.equals(storedToken)) {
            throw new RuntimeException("Token has been revoked or reused");
        }

        String newAccessToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails); // Optionally generate a new one

        // Store refresh token in Redis
        redisTemplate.opsForValue().set("refresh_token:" + userDetails.getUsername(), refreshToken, 7, TimeUnit.DAYS);

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        return new AuthResponse(newAccessToken, newRefreshToken, user.getUsername(), roles);
    }

    public void logout(String refreshToken) {
        String userEmail = jwtUtil.getUsername(refreshToken);
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            redisTemplate.delete("refresh_token:" + userEmail); // Invalidate stored refresh token
            userRepository.save(user);
        }
    }

	
}
