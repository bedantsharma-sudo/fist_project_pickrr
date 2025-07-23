package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;


@Component
@RequiredArgsConstructor
public class GlobalRateLimiting extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long leakPerSec =100;
        long capacity =300;
        String globalCount = "rate_limit:global_key:count";
        String globalTs = "rate_limit:global_key:ts";

        long now =  System.currentTimeMillis();

        String count = redisTemplate.opsForValue().get(globalCount);
        String lastTs = redisTemplate.opsForValue().get(globalTs);

        double counter = count == null ? 0 : Double.parseDouble(count);
        long lastLeakTime = lastTs == null ? now : Long.parseLong(lastTs);

        double timePassed = (now - lastLeakTime)/1000.0;
        double tokenLeaked = timePassed * leakPerSec;

        counter = Math.max(0,counter - tokenLeaked);
        if(counter >= capacity){
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many requests. Try again later.");
        }
        counter +=1;
        redisTemplate.opsForValue().set(globalCount,String.valueOf(counter));
        redisTemplate.opsForValue().set(globalTs,String.valueOf(now));

        filterChain.doFilter(request,response);
    }
}
