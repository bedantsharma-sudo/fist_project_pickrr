package com.example.demo.config;

import com.example.demo.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginRateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        if (uri.equals("/login")) {
            String key = "rate_limit:" + ip + ":" + uri;

            if (rateLimiterService.isRateLimited(key, 3, 30)) {
                response.setStatus(429);
                response.getWriter().write("Too many login attempts. Please wait and try again.");
                return false;
            }
        }

        return true;
    }
}
