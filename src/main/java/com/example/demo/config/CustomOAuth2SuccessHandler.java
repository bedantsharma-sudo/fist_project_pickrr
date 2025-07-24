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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final RedisTemplate<String, Object> redisTemplate;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        Writer writer = writerRepository.findByUsername(email).orElse(null);
        if (writer == null) {
            Writer newWriter = new Writer();
            newWriter.setUsername(email);
            newWriter.setPassword(passwordEncoder().encode("OAuth"));
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

        String jwt = jwtUtil.generateToken(email);
        redisTemplate.opsForHash().put("active_user_tokens", email, jwt);
        log.info("JWT generated for user [{}]: {}", email, jwt);

        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(10 * 60); // 10 minutes
        response.addCookie(cookie);

        response.sendRedirect("/quotes?token=" + jwt); // To support JS token sync
    }
}
