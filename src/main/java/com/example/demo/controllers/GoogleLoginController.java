package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller()
public class GoogleLoginController {
    @GetMapping("/Log")
    public String loginPage(){
        return "login";
    }
    @GetMapping("/")
    @ResponseBody
    public String homepage(){
        return "you are on the wrong page try /Log endpoint";
    }
}
