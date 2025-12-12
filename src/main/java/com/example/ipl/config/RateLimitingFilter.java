package com.example.ipl.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final ConcurrentHashMap<String, AttemptInfo> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final int TIME_WINDOW_MINUTES = 15;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        if ("/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
            String clientIP = getClientIP(request);
            
            if (isRateLimited(clientIP)) {
                response.setStatus(429); // Too Many Requests
                response.getWriter().write("{\"error\":\"Too many login attempts. Please try again later.\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private boolean isRateLimited(String clientIP) {
        AttemptInfo attemptInfo = loginAttempts.get(clientIP);
        LocalDateTime now = LocalDateTime.now();
        
        if (attemptInfo == null) {
            loginAttempts.put(clientIP, new AttemptInfo(now, new AtomicInteger(1)));
            return false;
        }
        
        if (now.minusMinutes(TIME_WINDOW_MINUTES).isAfter(attemptInfo.firstAttempt)) {
            // Reset counter if time window has passed
            loginAttempts.put(clientIP, new AttemptInfo(now, new AtomicInteger(1)));
            return false;
        }
        
        int attempts = attemptInfo.count.incrementAndGet();
        return attempts > MAX_ATTEMPTS;
    }
    
    private static class AttemptInfo {
        final LocalDateTime firstAttempt;
        final AtomicInteger count;
        
        AttemptInfo(LocalDateTime firstAttempt, AtomicInteger count) {
            this.firstAttempt = firstAttempt;
            this.count = count;
        }
    }
}