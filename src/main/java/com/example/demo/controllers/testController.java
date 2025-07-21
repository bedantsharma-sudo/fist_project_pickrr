package com.example.demo.controllers;


import com.example.demo.helper.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/test")
    @RateLimit(limit = 3,duration = 60)
    public String test(){
        return "test";
    }
}
