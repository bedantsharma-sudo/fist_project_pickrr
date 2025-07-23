package com.example.demo.helper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomBasicAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String name = authentication.getName();
        String token = jwtUtil.generateToken(name);

////        Cookie cookie = new Cookie("jwt", token);
////        cookie.setHttpOnly(true);
////        cookie.setSecure(true);
////        cookie.setPath("/");
////        cookie.setMaxAge(10 * 60);
////        response.addCookie(cookie);
//        Cookie cookie = new Cookie("jwt", token);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(10 * 60); // 30 minutes
//        response.addCookie(cookie);
//
//
//      //  System.out.println(token);
//        response.sendRedirect("/quotes");
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(10 * 60); // 10 minutes
        response.addCookie(cookie);

        response.sendRedirect("/quotes?token=" + token);
    }
}
