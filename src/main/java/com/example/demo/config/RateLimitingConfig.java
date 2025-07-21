package com.example.demo.config;

import com.example.demo.helper.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingConfig{

    private final StringRedisTemplate redisTemplate;
    private final HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String username = request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName()
                : request.getRemoteAddr();

        String key = "rate_limit:" + username + ":" + joinPoint.getSignature().toShortString();
        int limit = rateLimit.limit();
        int duration = rateLimit.duration();

        Long current = redisTemplate.opsForValue().increment(key);
        if (current == 1) {
            redisTemplate.expire(key, duration, TimeUnit.SECONDS);
        }

        System.out.println("RateLimiting triggered for: " + key + ", current count: " + current);

        if (current != null && current > limit) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many requests. Try again later.");
        }

        return joinPoint.proceed();
    }


    @Bean
    public HttpServletRequest request(HttpServletRequest request) {
        return request;
    }
}
