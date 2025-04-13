package com.ecommerce.user.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

    private final static String PREFIX = "blacklisted_token:";

    public void blacklistToken(String token, long durationInSeconds) {
        redisTemplate.opsForValue().set(PREFIX + token, "revoked", durationInSeconds, TimeUnit.SECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }
}
