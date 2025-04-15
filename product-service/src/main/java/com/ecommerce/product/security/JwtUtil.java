package com.ecommerce.product.security;

import java.security.Key;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private static final String SECRET_KEY = "bG9uZy1zZWN1cmUta2V5LWhlcmUtbG9uZy1zZWN1cmUta2V5LWhlcmU="; // Move to configuration

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", token, e);
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean hasRole(String token, String role) {
        Claims claims = extractClaims(token);
        boolean hasRole = claims.get("roles", java.util.List.class).contains(role);
        log.debug("Token has role {}: {}", role, hasRole);
        return hasRole;
    }

    private Key getSignKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
