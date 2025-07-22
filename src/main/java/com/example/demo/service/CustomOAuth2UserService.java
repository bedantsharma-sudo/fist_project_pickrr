package com.example.demo.service;

import com.example.demo.model.Writer;
import com.example.demo.repository.WriterRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final WriterRepository writerRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    public CustomOAuth2UserService(WriterRepository writerRepository, PasswordEncoder passwordEncoder) {
        this.writerRepository = writerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        if (email != null) {
            Optional<Writer> existing = writerRepository.findByUsername(email);
            if (existing.isEmpty()) {
                Writer writer = new Writer();
                writer.setUsername(email);
                writer.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                writer.setRole("USER");
                writerRepository.save(writer);

                logger.info("new user created: {}",writer.getUsername());
            }
        }
        else{
            logger.info("email of your is empty");
        }

        return oAuth2User;
    }
} 