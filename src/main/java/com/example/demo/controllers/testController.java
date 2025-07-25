package com.example.demo.controllers;


import com.example.demo.helper.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@RestController
//@RequiredArgsConstructor
public class testController {
   // private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/test")
    @RateLimit(limit = 1,duration = 1)
    public String test(){
        return "Hello";
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       // String username = authentication.getName();
      //  redisTemplate.opsForHash().put("last_seen", username, Instant.now().toString());
        return ResponseEntity.ok("User: " + authentication.getName() +
                ", Roles: " + authentication.getAuthorities() +
                ", Authenticated: " + authentication.isAuthenticated());
    }
}
