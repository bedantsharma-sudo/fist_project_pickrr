package com.example.demo.controllers;


import com.example.demo.model.Writer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @PostMapping
    public Writer registerUser(@RequestBody Writer writer){

    }

}
