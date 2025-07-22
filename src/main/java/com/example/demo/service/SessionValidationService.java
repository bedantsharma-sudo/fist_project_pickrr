package com.example.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SessionValidationService {

    private static final Logger logger = LoggerFactory.getLogger(SessionValidationService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private final String SECRET_KEY = "jR4zK2qP8xW7nM9vL3hT6cD1bY5gF0sE";
    public SessionValidationService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            if (claims.getExpiration().before(new java.util.Date())) {
                logger.warn("Token for user '{}' is expired.", claims.getSubject());
                return false;
            }

            logger.info("Token for user '{}' is valid.", claims.getSubject());
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }


    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void validateActiveSessions() {
        logger.info("Running scheduled session validation check...");

        // This part needs to be adapted based on your actual Redis key strategy.
        // If you're using Spring's @Cacheable, the keys might be "cacheName::methodParam"
        // and values are the cached objects.
        // If you're storing tokens directly, you need a way to find those keys.

        // Example: Assuming active user tokens are stored in a Redis HASH named "active_user_tokens"
        // where the field is the userId and the value is the JWT string.
        // This is a common pattern for managing active sessions.
        Map<Object, Object> activeSessionsMap = redisTemplate.opsForHash().entries("active_user_tokens");

        if (activeSessionsMap.isEmpty()) {
            logger.info("No active sessions found to validate.");
            return;
        }

        activeSessionsMap.forEach((userId, tokenObject) -> {
            String token = null;
            if (tokenObject instanceof String) {
                // If the token is directly a String (most common for JWTs)
                token = (String) tokenObject;
            } else if (tokenObject instanceof LinkedHashMap) {
                // If GenericJackson2JsonRedisSerializer wrapped the String in a LinkedHashMap
                // (e.g., if it was serialized as part of a larger object, or if it's a simple string
                // but Jackson decided to represent it this way during deserialization of a complex structure)
                // This scenario is less likely if the token is the direct value of a hash field.
                // If your token is inside a custom DTO, you'd deserialize that DTO here.
                try {
                    // Re-serialize to JSON string and then parse if needed, or directly extract if known structure
                    // For a simple string, this might be overly complex.
                    // If the original value was just a String, it should deserialize to a String.
                    // This block is more for if 'tokenObject' is a complex object containing the token.
                    token = objectMapper.writeValueAsString(tokenObject); // Convert object back to JSON string
                    // If the token is nested, you'd need to parse the JSON and extract the token field.
                    // Example: MyTokenDTO dto = objectMapper.readValue(token, MyTokenDTO.class); token = dto.getJwt();
                } catch (JsonProcessingException e) {
                    logger.error("Error processing cached token object for user {}: {}", userId, e.getMessage());
                    return; // Skip this token
                }
            } else if (tokenObject != null) {
                logger.warn("Unexpected token object type for user {}: {}", userId, tokenObject.getClass().getName());
            }

            if (token != null && !token.isEmpty()) {
                logger.debug("Validating session for user: {}", userId);
                boolean isValid = validateToken(token);
                if (!isValid) {
                    logger.warn("Session for user {} is invalid or expired. Invalidating session in cache.", userId);
                    // Remove the invalid token from cache
                    redisTemplate.opsForHash().delete("active_user_tokens", userId);
                }
            } else {
                logger.warn("Empty or null token found for user {}. Skipping validation.", userId);
            }
        });
        logger.info("Finished scheduled session validation check.");
    }
}
