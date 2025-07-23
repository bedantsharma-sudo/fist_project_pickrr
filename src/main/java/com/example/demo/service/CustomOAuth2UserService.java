package com.example.demo.service;

import com.example.demo.model.Writer;
import com.example.demo.repository.WriterRepository;
import com.example.demo.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final WriterRepository writerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ReactiveRedisOperations<Object, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    public CustomOAuth2UserService(
            WriterRepository writerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ReactiveRedisOperations<Object, Object> redisTemplate) {
        this.writerRepository = writerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        if (email != null) {
            Optional<Writer> existing = writerRepository.findByUsername(email);
            Writer writer;

            if (existing.isEmpty()) {
                writer = new Writer();
                writer.setUsername(email);
                writer.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                writer.setRole("USER");
                writerRepository.save(writer);
                logger.info("New user created: {}", writer.getUsername());
            } else {
                writer = existing.get();
                logger.info("User already exists: {}", writer.getUsername());
            }

            // ✅ Generate token and store in Redis
            String token = jwtService.generateToken(email); // use email as subject
            redisTemplate.opsForHash().put("active_user_tokens", email, token).subscribe();
            redisTemplate.opsForValue().increment("current_logged_in_users").subscribe();

        } else {
            logger.warn("Email attribute is missing in OAuth2 response");
        }

        return oAuth2User;
    }
}
