package com.example.demo.controllers;

import com.example.demo.helper.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String email;

        try {
            email = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token parsing failed: " + e.getMessage());
        }

        if (!jwtUtil.validateToken(token, email)) {
            return ResponseEntity.status(401).body("Token is invalid or expired");
        }

        return ResponseEntity.ok("Token is valid");
    }
}
