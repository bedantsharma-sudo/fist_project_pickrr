package com.example.demo.controllers;

import com.example.demo.helper.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    /**
     * Endpoint to validate whether a token is valid and still active in Redis.
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String username = jwtUtil.extractUsername(token);

            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(401).body("Token is invalid or expired");
            }

            // Check Redis for active session
            Object storedToken = redisTemplate.opsForHash().get("active_user_tokens", username);
            if (storedToken == null || !storedToken.equals(token)) {
                logger.info("Token not found in Redis or doesn't match for user: {}", username);
                return ResponseEntity.status(401).body("Session not active");
            }

            return ResponseEntity.ok("Session is active");
        } catch (Exception e) {
            logger.error("Session validation error: ", e);
            return ResponseEntity.status(401).body("Error validating session");
        }
    }

    /**
     * Endpoint to refresh a token if it's still valid.
     * Sets new token in Redis and updates HttpOnly cookie.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshSession(@RequestHeader("Authorization") String authHeader,
                                            HttpServletResponse response) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }

            String oldToken = authHeader.substring(7);
            String username = jwtUtil.extractUsername(oldToken);

            if (!jwtUtil.validateToken(oldToken, username)) {
                return ResponseEntity.status(401).body("Old token is invalid or expired");
            }

            // Generate new token
            String newToken = jwtUtil.generateToken(username);

            // Update Redis with new token
            redisTemplate.opsForHash().put("active_user_tokens", username, newToken);
            logger.info("Token refreshed and updated in Redis for user: {}", username);

            // Set HttpOnly cookie with new token
            Cookie cookie = new Cookie("jwt", newToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(4 * 60); // 4 minutes
            response.addCookie(cookie);

            // Return token in response body as well (for JS to update localStorage)
            return ResponseEntity.ok(Map.of("token", newToken));
        } catch (Exception e) {
            logger.error("Token refresh error: ", e);
            return ResponseEntity.status(401).body("Error refreshing token");
        }
    }
}
