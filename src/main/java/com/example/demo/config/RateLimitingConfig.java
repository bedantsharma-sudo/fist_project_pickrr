package com.example.demo.config;

import com.example.demo.helper.RateLimit;
import com.nimbusds.jose.jwk.source.RateLimitReachedException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingConfig{

    private final StringRedisTemplate redisTemplate;
    private final HttpServletRequest request;

//    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String addr = request.getRemoteAddr();
        String key = "rate_limit:" + addr + ":" + joinPoint.getSignature().toShortString();
        int limit = rateLimit.limit();
        int duration = rateLimit.duration();
        Long current = redisTemplate.opsForValue().increment(key);
        if (current == 1) {
            redisTemplate.expire(key, duration, TimeUnit.SECONDS);
        }

        System.out.println("RateLimiting triggered for: " + key + ", current count: " + current);

        if (current > limit) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many requests. Try again later.");
        }

        return joinPoint.proceed();
    }

//    @Around("@annotation(rateLimit)")
    public Object leakyBucket(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable{
        long LeakRatePerSec = 10;
        long capacity = 50;

        String addr = request.getRemoteAddr();
        String count = "leaky:count:"+addr;
        String last_ts = "leaky:last_ts:"+addr;

        long now = System.currentTimeMillis();

        String lastTs = redisTemplate.opsForValue().get(last_ts);
        String counter = redisTemplate.opsForValue().get(count);

        double currentCount = counter == null ? 0 : Double.parseDouble(counter);
        long lastLeakTime = lastTs == null ? now : Long.parseLong(lastTs);

        double timePassed = (now - lastLeakTime)/1000.0;
        double leaked = timePassed * LeakRatePerSec;

        currentCount = Math.max(0,currentCount - leaked);

        System.out.println("Leaky triggered for: " + addr + ", current count: " + currentCount);

        if(currentCount >= capacity){
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many requests. Try again later.");
        }
        currentCount +=1;
        redisTemplate.opsForValue().set(count,String.valueOf(currentCount));
        redisTemplate.opsForValue().set(last_ts,String.valueOf(now));

        return joinPoint.proceed();
    }

//    @Around("@annotation(rateLimit)")
    public Object tokenBucket(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        long refillPerSec = 5;
        long capacity = 50;

        long now = System.currentTimeMillis();
        String addr = request.getRemoteAddr();
        String count = "token:count:"+addr;
        String last_ts = "token:last_ts:"+addr;

        String counter = redisTemplate.opsForValue().get(count);
        String lastTs = redisTemplate.opsForValue().get(last_ts);

        double currentCount = counter == null ? capacity : Double.parseDouble(counter);
        long lastLeakTime = lastTs == null ? now : Long.parseLong(lastTs);

        double timePassed = (now -lastLeakTime)/1000.0;
        currentCount = Math.min((timePassed * refillPerSec) + currentCount,capacity);

        System.out.println("Token triggered for: " + addr + ", current count: " + currentCount);

        if(currentCount <= 0){
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many requests. Try again later.");
        }
        currentCount -=1;
        redisTemplate.opsForValue().set(count,String.valueOf(currentCount));
        redisTemplate.opsForValue().set(last_ts,String.valueOf(now));
        return joinPoint.proceed();
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket createLimitBucket(){
        return Bucket.builder().addLimit(limit -> limit.capacity(30).refillGreedy(30, Duration.ofMinutes(1)))
                .build();
    }
    public Bucket createThrottlingsBucket(){
        return Bucket.builder().addLimit(limit -> limit.capacity(1).refillGreedy(1, Duration.ofMillis(300)))
                .build();
    }

    @Around("@annotation(rateLimit)")
    public Object tokenThrottler(ProceedingJoinPoint joinPoint,RateLimit rateLimit) throws Throwable {
        String key = request.getRemoteAddr();
        Bucket limitBucket = buckets.computeIfAbsent(key, k -> createLimitBucket());
        Bucket throttlerBucket = buckets.computeIfAbsent(key,k-> createThrottlingsBucket());

        if(!limitBucket.tryConsume(1)){
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Too many requests. Try again later.");
        }

        throttlerBucket.asBlocking().consume(1);

        return joinPoint.proceed();
    }


    @Bean
    public HttpServletRequest request(HttpServletRequest request) {
        return request;
    }
}
