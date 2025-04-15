package com.ecommerce.gateway.security;

import java.security.Key;
import java.util.Base64;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {

    private static final String SECRET_KEY = "bG9uZy1zZWN1cmUta2V5LWhlcmUtbG9uZy1zZWN1cmUta2V5LWhlcmU=";

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            log.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }
}
