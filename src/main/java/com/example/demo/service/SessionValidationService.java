package com.example.demo.service;

import com.example.demo.helper.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionValidationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void validateActiveSessions() {
        log.info("Running scheduled session validation check...");

        Map<Object, Object> activeSessions = redisTemplate.opsForHash().entries("active_user_tokens");

        if (activeSessions.isEmpty()) {
            log.info("No active sessions found to validate.");
            return;
        }

        for (Map.Entry<Object, Object> entry : activeSessions.entrySet()) {
            String userId = (String) entry.getKey();
            String token = (String) entry.getValue();

            if (!jwtUtil.validateToken(token)) {
                log.warn("Token expired or invalid for user '{}', removing from Redis.", userId);
                redisTemplate.opsForHash().delete("active_user_tokens", userId);
            } else {
                //log.debug("Token valid for user '{}'", userId);
                log.info("User '{}' has been validated.", userId);
            }
        }

        log.info("Session validation complete.");
    }
}
