package com.example.demo.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {


    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @GetMapping("/loginWithGoogle")
    public String redirectToGoogle(){
        return "redirect:/oauth2/authorization/google";
    }
}
