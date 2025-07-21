package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataController {

    // This GET endpoint will be accessible without a JWT due to SecurityConfig.
    // If a valid JWT is present, the user will be authenticated.
    @GetMapping("/some-resource")
    public ResponseEntity<String> getSomeData() {
        return ResponseEntity.ok("This data is always accessible via GET.");
    }

    // This POST endpoint will require a valid JWT due to SecurityConfig.
    // If no JWT or invalid JWT, it will be blocked.
    @PostMapping("/some-resource")
    public ResponseEntity<String> postSomeData(@RequestBody String data) {
        return ResponseEntity.ok("You successfully posted: '" + data + "' to a secure resource.");
    }
}