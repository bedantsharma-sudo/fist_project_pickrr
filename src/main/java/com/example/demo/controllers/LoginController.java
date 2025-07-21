package com.example.demo.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {


    @GetMapping("/Log")
    public String showLoginPage() {
        return "login";
    }


    @GetMapping("/loginWithGoogle")
    public String redirectToGoogle(){
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/welcome")
    @ResponseBody
    public String welcomePage(@RequestParam("token") String token, Model model) {
        model.addAttribute("jwtToken", token);
        return "welcome! logged in successfully your token is : \n"+token;  // This should map to `welcome.html` in templates
    }


}
