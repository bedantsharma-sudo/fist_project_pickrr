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

    /**
     * Initializes the secretKey from the secret string after the bean is constructed.
     * This key is used to sign and verify JWT tokens.
     */
    @PostConstruct
    public void init() {
        // Convert string to SecretKey
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for the given email (subject).
     * The token is valid for 15 minutes from the time of creation.
     *
     * @param email the subject (typically user's email)
     * @return a signed JWT token string
     */
    public String generateToken(String email) {
        Instant now = Instant.now();
        Instant expiry = now.plus(15, ChronoUnit.MINUTES);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validates the given JWT token using the secret key.
     * Checks the signature and structure of the token.
     *
     * @param token the JWT token string
     * @return true if the token is valid, false otherwise
     */
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

    /**
     * Extracts all claims (payload data) from the given JWT token.
     *
     * @param token the JWT token string
     * @return Claims object containing all token claims
     */
    public Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
        return parser.parseClaimsJws(token).getBody();
    }

    /**
     * Checks if the given JWT token is expired based on its 'exp' claim.
     *
     * @param token the JWT token string
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration", e);
            return true;
        }
    }

    /**
     * Refreshes the given JWT token by generating a new one with the same subject (email).
     * The new token will have a fresh 15-minute expiration window.
     *
     * @param token the old JWT token string
     * @return a new JWT token string
     */
    public String refreshToken(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.getSubject();
        return generateToken(email);
    }
}
