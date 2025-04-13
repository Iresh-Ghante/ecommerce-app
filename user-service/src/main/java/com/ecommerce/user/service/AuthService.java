package com.ecommerce.user.service;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.JwtUtil;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public String registerUser(String username, String password, String role) {
		if (userRepository.findByUsername(username).isPresent()) {
			throw new RuntimeException("Username already exist");
		}

		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));

		HashSet<String> roles = new HashSet<>();
		roles.add(role);
		user.setRoles(roles);

		userRepository.save(user);

		return "User Registered Sucessfully";
	}
	
	public String loginUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return jwtUtil.generateToken(username, user.get().getRoles());
        }
        throw new RuntimeException("Invalid username or password");
    }
	
}
