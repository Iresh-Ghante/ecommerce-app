package com.ecommerce.user.controller;

import com.ecommerce.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("role") String role) {
		System.out.println("ihhhihiu");
		return ResponseEntity.ok(authService.registerUser(username, password, role));
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam("username") String username,
			@RequestParam("password") String password) {
		return ResponseEntity.ok(authService.loginUser(username, password));
	}
}
