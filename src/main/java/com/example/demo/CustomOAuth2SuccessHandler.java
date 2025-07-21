package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = authToken.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");

        // ✅ Store user in session
        HttpSession session = request.getSession();
        session.setAttribute("user", attributes);

        // ✅ Generate JWT
        String jwt = jwtUtil.generateToken(email);

        // ✅ Send JWT to client (e.g., redirect or set in header)
        response.sendRedirect("/welcome?token=" + jwt);
    }
}
