package com.example.demo.controllers;


import com.example.demo.model.Writer;
import com.example.demo.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public Writer registerUser(@RequestBody Writer writer){
        writer.setPassword(passwordEncoder.encode(writer.getPassword()));
        return writerRepository.save(writer);
    }

}
