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

@GetMapping("/validate")
public ResponseEntity<?> validateSession(@RequestHeader("Authorization") String authHeader,
                                         HttpServletResponse response) {
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
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

        // If all checks pass, refresh token (only for active user)
        String newToken = jwtUtil.generateToken(username);
        redisTemplate.opsForHash().put("active_user_tokens", username, newToken);
        logger.info("Active session validated — token refreshed for user: {}", username);
        logger.info("New Token: {}", newToken);

        // Set HttpOnly cookie
        Cookie cookie = new Cookie("jwt", newToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(55); // e.g.,  50 sec
        response.addCookie(cookie);

        // Return refreshed token to update localStorage
        return ResponseEntity.ok(Map.of("token", newToken));
    } catch (Exception e) {
        logger.error("Session validation error: ", e);
        return ResponseEntity.status(401).body("Error validating session");
    }
}


//@PostMapping("/refresh")
//public ResponseEntity<?> refreshSession(@RequestHeader("Authorization") String authHeader,
//                                        HttpServletResponse response) {
//    try {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
//        }
//
//        String oldToken = authHeader.substring(7);
//        String username = jwtUtil.extractUsername(oldToken);
//
//        if (!jwtUtil.validateToken(oldToken, username)) {
//            return ResponseEntity.status(401).body("Old token is invalid or expired");
//        }
//
//        // Generate new token
//        String newToken = jwtUtil.generateToken(username);
//
//        // Update Redis with new token
//        redisTemplate.opsForHash().put("active_user_tokens", username, newToken);
//        logger.info("Token refreshed for user: {}", username);
//        logger.info("New Token: {}", newToken);
//
//        // Set HttpOnly cookie
//        Cookie cookie = new Cookie("jwt", newToken);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(55); //  55 sec
//        response.addCookie(cookie);
//
//        // Return new token in JSON (for JS to update localStorage)
//        return ResponseEntity.ok(Map.of("token", newToken));
//    } catch (Exception e) {
//        logger.error("Token refresh error: ", e);
//        return ResponseEntity.status(401).body("Error refreshing token");
//    }
//}

}
