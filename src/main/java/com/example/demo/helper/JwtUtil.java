package com.example.demo.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // Convert string to SecretKey
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();

            Claims claims = parser.parseClaimsJws(token).getBody();
            return true;
        } catch (Exception e) {
            System.out.println("Invalid token");
            log.error("Invalid token because: ", e);
            return false;
        }
    }
}
