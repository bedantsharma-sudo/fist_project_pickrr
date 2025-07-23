package com.example.demo.helper;

import com.example.demo.service.WriterService;
import com.example.demo.helper.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WriterService writerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName(); // typically the user's email
        String token = jwtUtil.generateToken(email);

        log.info("Login successful for user: {}", email);

        // Track logged-in users (optional)
        redisTemplate.opsForValue().increment("current_logged_in_users");

        // Store token in Redis with expiry (optional)
        redisTemplate.opsForValue().set(email, token, Duration.ofMinutes(30));

        // Set token as HttpOnly cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(10 * 60); // 30 minutes
        response.addCookie(cookie);

        response.sendRedirect("/quotes?token=" + token);
    }
}
