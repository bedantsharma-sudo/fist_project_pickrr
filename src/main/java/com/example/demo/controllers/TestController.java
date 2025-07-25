package com.example.demo.controllers;


import com.example.demo.helper.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/test")
    @RateLimit(limit = 1,duration = 1)
    public String test(){
        return "Hello";
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("User: " + authentication.getName() +
                ", Roles: " + authentication.getAuthorities() +
                ", Authenticated: " + authentication.isAuthenticated());
    }


}
