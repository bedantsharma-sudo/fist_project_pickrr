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
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;


@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalRateLimiting extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long leakPerSec =1;
        long capacity =1;
        String globalCount = "rate_limit:global_key:count";
        String globalTs = "rate_limit:global_key:ts";

        long now =  System.currentTimeMillis();

        String count = redisTemplate.opsForValue().get(globalCount);
        String lastTs = redisTemplate.opsForValue().get(globalTs);

        double counter = count == null ? 0 : Double.parseDouble(count);
        long lastLeakTime = lastTs == null ? now : Long.parseLong(lastTs);

        double timePassed = (now - lastLeakTime)/1000.0;
        double tokenLeaked = timePassed * leakPerSec;

        counter +=1;
        counter = Math.max(0,counter - tokenLeaked);

       // log.info("Rate Limiter - Current Count: {}, Last Timestamp: {}", counter, lastLeakTime);
        ZonedDateTime formattedTs = Instant.ofEpochMilli(lastLeakTime)
                .atZone(ZoneId.of("Asia/Kolkata"));  // or ZoneId.systemDefault()

        String formattedTime = formattedTs.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        log.info("Rate Limiter - Current Count/Load is : {}, On the Server out of capacity of : {}, Last Timestamp: {}", counter,capacity, formattedTime);

        long activeUserCount = redisTemplate.opsForHash().size("active_user_tokens");
        log.info("Active users: {}", activeUserCount);


        int retryAttempts = 5; // max retries
        int waitMillis = 500;  // wait 0.5s per retry i gave this time to user for enough space created in bucket
        // Even after {retryAttempts} attempt no space available in bucket it will be redirected to /logout
        //
        int attempt = 0;
        while (counter > capacity && retryAttempts-- > 0  ) {
            int exwaitMillis = (int) (waitMillis * Math.pow(2, attempt)); // exponential backoff: 0.5s, 1s, 2s, 4s, 8s
            attempt++;
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Interrupted during wait");
            }

            now = System.currentTimeMillis();
            timePassed = (now - lastLeakTime) / 1000.0;
            tokenLeaked = timePassed * leakPerSec;
            counter = Math.max(0, counter - tokenLeaked);
            counter += 1;
        }

        if (counter > capacity) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please wait and try again shortly.");
            return;
        }


        redisTemplate.opsForValue().set(globalCount,String.valueOf(counter));
        redisTemplate.opsForValue().set(globalTs,String.valueOf(now));

        filterChain.doFilter(request,response);
    }
}
