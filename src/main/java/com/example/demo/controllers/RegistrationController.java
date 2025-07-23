package com.example.demo.controllers;


import com.example.demo.helper.RateLimit;
import com.example.demo.model.Writer;
import com.example.demo.repository.WriterRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @RateLimit(limit = 10,duration = 60)
    public String showRegister(Model model){
        model.addAttribute("writer",new Writer());
        return "register";
    }

    @PostMapping
    @RateLimit(limit = 3,duration = 60)
    public String registerUser(@Valid @ModelAttribute Writer writer, BindingResult result, Model model){
        if (writerRepository.findByUsername(writer.getUsername()).isPresent()){
            result.rejectValue("username","error.username","Username already taken");
            return "register";
        }
        writer.setPassword(passwordEncoder.encode(writer.getPassword()));
        writer.setRole("USER");
        writerRepository.save(writer);
        return "redirect:/Log";
    }

}
