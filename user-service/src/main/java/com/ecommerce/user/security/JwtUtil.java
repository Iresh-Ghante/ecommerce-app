package com.ecommerce.user.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    // Base64-encoded secret key (at least 256-bit for HS256)
    private static final String SECRET_KEY = "bG9uZy1zZWN1cmUta2V5LWhlcmUtbG9uZy1zZWN1cmUta2V5LWhlcmU=";
    
    private static final long ACCESS_TOKEN_EXPIRATION_MS = Duration.ofMinutes(15).toMillis(); // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_MS = Duration.ofDays(7).toMillis();    // 7 days

    /**
     * Generates a JWT access token with user authorities.
     */
    public String generateToken(UserDetails userDetails) {
        log.debug("Generating access token for user: {}", userDetails.getUsername());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT refresh token.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        log.debug("Generating refresh token for user: {}", userDetails.getUsername());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the token against the username and expiration.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = getUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isExpired(token);
            log.debug("Token validity for user {}: {}", username, isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts username (email) from token.
     */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Returns true if the token is expired.
     */
    private boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extracts claims from token.
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the HMAC signing key from the encoded string.
     */
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(SECRET_KEY));
    }
    
    public long getExpirationDuration(String token) {
        Date expiration = getClaims(token).getExpiration();
        long now = System.currentTimeMillis();
        return (expiration.getTime() - now) / 1000; // seconds
    }

}
