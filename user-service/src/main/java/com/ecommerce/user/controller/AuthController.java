package com.ecommerce.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
import com.ecommerce.user.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/data")
    public String getAdminData() {
        return "Only admins can see this";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/data")
    public String getUserData() {
        return "User or Admin can see this";
    }

}

