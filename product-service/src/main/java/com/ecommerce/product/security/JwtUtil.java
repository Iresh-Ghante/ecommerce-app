package com.ecommerce.product.security;

import java.security.Key;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "bG9uZy1zZWN1cmUta2V5LWhlcmUtbG9uZy1zZWN1cmUta2V5LWhlcmU="; // At least 32 characters required
    
    // Extract Claims
    public Claims extractClaims(String token) {
    	return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaims(token).get("sub", String.class); // ✅ Correct claim extraction
    }
    
    public boolean hasRole(String token, String role) {
        Claims claims = extractClaims(token);
        return claims.get("roles", java.util.List.class).contains(role);
    }
    
    private Key getSignKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
