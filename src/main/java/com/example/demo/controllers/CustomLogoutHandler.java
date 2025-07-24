package com.example.demo.controllers;

import com.example.demo.helper.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutHandler.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Get JWT from cookie
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            String email = jwtUtil.extractUsername(token);  // Or extract email
            // Remove the token from the active_user_tokens hash in Redis
            redisTemplate.opsForHash().delete("active_user_tokens", email);
            logger.info("JWT removed from active_user_tokens in Redis for user: {}", email);
        }
    }
}
