package com.example.demo.controllers;


import com.example.demo.helper.RateLimit;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/test")
    @RateLimit(limit = 3,duration = 60)
    public String test(){
        return "test";
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("User: " + authentication.getName() +
                ", Roles: " + authentication.getAuthorities() +
                ", Authenticated: " + authentication.isAuthenticated());
    }


}
