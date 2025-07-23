package com.example.demo.config;

import com.example.demo.helper.JwtUtil;
import com.example.demo.model.Writer;
import com.example.demo.repository.WriterRepository;
import com.example.demo.service.WriterService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final WriterRepository writerRepository;
    private final WriterService writerService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        // 1. Create writer if not exists
        Writer writer = writerRepository.findByUsername(email).orElse(null);
        if (writer == null) {
            Writer newWriter = new Writer();
            newWriter.setUsername(email);
            newWriter.setPassword(passwordEncoder.encode("OAuth")); // dummy password
            newWriter.setRole("ROLE_USER");
            writerRepository.save(newWriter);
        }

        // 2. Authenticate using UserDetails
        UserDetails userDetails = writerService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

        // 3. Generate JWT and store in Redis
        String jwt = jwtUtil.generateToken(email);
        redisTemplate.opsForValue().set(email, jwt, Duration.ofMinutes(30));
        redisTemplate.opsForValue().increment("current_logged_in_users");

        // 4. Set JWT as HttpOnly cookie
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60); // 30 minutes
        response.addCookie(cookie);

        // 5. Redirect
        response.sendRedirect("/quotes");
    }
}
