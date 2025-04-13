package com.ecommerce.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
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
        String refreshToken = jwtUtil.generateToken(userDetails);

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        return new AuthResponse(accessToken, refreshToken, user.getUsername(), roles);
    }
	
}
