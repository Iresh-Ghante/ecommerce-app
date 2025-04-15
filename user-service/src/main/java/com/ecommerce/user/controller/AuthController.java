package com.ecommerce.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.LogoutRequest;
import com.ecommerce.user.dto.RegisterRequest;
import com.ecommerce.user.dto.TokenRefreshRequest;
import com.ecommerce.user.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Register attempt for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        log.info("User registered successfully: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        log.info("Login successful for: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid TokenRefreshRequest request) {
        log.debug("Token refresh requested");
        AuthResponse response = authService.refreshToken(request);
        log.info("Token refreshed for: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody @Valid LogoutRequest request, @RequestHeader("Authorization") String authHeader) {
        log.info("Logout request received");
        log.info(authHeader);
        authService.logout(request.getRefreshToken(), authHeader);
        log.info("User logged out and refresh token revoked");
        return ResponseEntity.ok("Logged out successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/data")
    public ResponseEntity<String> getAdminData() {
        log.info("Admin data accessed");
        return ResponseEntity.ok("Only admins can see this");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/data")
    public ResponseEntity<String> getUserData() {
        log.info("User data accessed");
        return ResponseEntity.ok("User or Admin can see this");
    }
}
