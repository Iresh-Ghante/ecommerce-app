package com.ecommerce.user.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "blacklisted_token:";

    public void blacklistToken(String token, long durationInSeconds) {
        String hashedToken = hashToken(token);
        redisTemplate.opsForValue().set(PREFIX + hashedToken, "revoked", durationInSeconds, TimeUnit.SECONDS);
        log.info("Blacklisted token (hashed): {}", hashedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        String hashedToken = hashToken(token);
        boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + hashedToken));
        log.debug("Token blacklisted status (hashed): {} = {}", hashedToken, isBlacklisted);
        return isBlacklisted;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encoded) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
