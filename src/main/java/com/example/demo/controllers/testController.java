package com.example.demo.controllers;

import com.example.demo.helper.RateLimit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/swagger-ui")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui.html"; // Spring will now perform redirect
    }

    @GetMapping("/test")
    @ResponseBody // This ensures "test" is returned as plain text
    @RateLimit(limit = 3, duration = 60)
    public String test() {
        return "test";
    }
}
