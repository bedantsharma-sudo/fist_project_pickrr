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

@Controller
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

  /*  @PostMapping
    @RateLimit(limit = 3,duration = 60)
    public String registerUser(@Valid @ModelAttribute Writer writer, BindingResult result, Model model){
        if (writerRepository.findByUsername(writer.getUsername()).isPresent()){
            result.rejectValue("username","error.username","Username already taken");
            return "register";
        }
        writer.setPassword(passwordEncoder.encode(writer.getPassword()));
        writer.setRole("ROLE_USER");
        writerRepository.save(writer);
        return "redirect:/Log";
    }
  */
  @PostMapping
  @RateLimit(limit = 3, duration = 60)
  public String registerUser(@ModelAttribute Writer writer, BindingResult result, Model model) {

      // Manual validation for username length - valid not working idk why
      String username = writer.getUsername();
      if (username == null || username.length() < 3 || username.length() > 20) {
          result.rejectValue("username", "error.username", "Username must be between 3 and 20 characters");
      }

      // Manual validation for password length
      String password = writer.getPassword();
      if (password == null || password.length() < 3) {
          result.rejectValue("password", "error.password", "Password must be at least 3 characters long");
      }

      // Check if username is already taken
      if (writerRepository.findByUsername(username).isPresent()) {
          result.rejectValue("username", "error.username", "Username already taken");
      }

      // If any errors found, return to registration page
      if (result.hasErrors()) {
          return "register";
      }

      // Save new user - all valid
      writer.setPassword(passwordEncoder.encode(password));
      writer.setRole("ROLE_USER");
      writerRepository.save(writer);

      return "redirect:/Log";
  }
}
