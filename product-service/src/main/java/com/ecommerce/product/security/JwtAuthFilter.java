package com.ecommerce.product.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            log.warn("Authorization header missing or malformed");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);
        try {
            jwtUtil.extractUsername(token); // Validate token
            request.setAttribute("token", token); // Pass token downstream
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
