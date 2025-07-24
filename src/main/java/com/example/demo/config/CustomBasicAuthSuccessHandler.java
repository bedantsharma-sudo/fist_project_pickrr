package com.example.demo.config;

import com.example.demo.helper.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

@Component
public class CustomBasicAuthSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomBasicAuthSuccessHandler.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String name = authentication.getName();
        String token = jwtUtil.generateToken(name);

        logger.info("JWT generated for user [{}]: {}", name, token);
        //logger.info("Redirecting user [{}] to /quotes?token={}", name, token);
        redisTemplate.opsForHash().put("active_user_tokens", name, token);
        redisTemplate.expire("active_user_tokens", Duration.ofMinutes(3));

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3 * 60 + 30); // 3 minutes 30 seconds
        response.addCookie(cookie);

        response.sendRedirect("/quotes?token=" + token);
    }
}
