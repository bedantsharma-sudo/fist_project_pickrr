package com.example.demo.helper;

import com.example.demo.model.Writer;
import com.example.demo.repository.WriterRepository;
import com.example.demo.service.WriterService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final WriterRepository writerRepository;
    @Autowired
    private WriterService writerService;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CustomOAuth2SuccessHandler(WriterRepository writerRepository,
                                      JwtUtil jwtUtil) {
        this.writerRepository = writerRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
        SecurityContextHolder.getContext().setAuthentication(authToken);
        String email = (String) attributes.get("email");

        Writer writer = writerRepository.findByUsername(email).orElse(null);

        if(writer == null){
            Writer newWriter = new Writer();
            newWriter.setUsername(email);
            newWriter.setPassword(passwordEncoder.encode("OAuth"));
            newWriter.setRole("ROLE_USER");
            writerRepository.save(newWriter);
        }

        UserDetails userDetails = writerService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(token);
        request.getSession().setAttribute("username", email);

        response.sendRedirect("/quotes");
    }
}
