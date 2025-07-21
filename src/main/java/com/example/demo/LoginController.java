package com.example.demo;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/loginWithGoogle")
    public String redirectToGoogle(){
        return "redirect:/oauth2/authorization/google";
    }
}
