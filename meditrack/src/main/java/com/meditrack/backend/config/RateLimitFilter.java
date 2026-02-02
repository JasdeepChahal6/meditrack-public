package com.meditrack.backend.config;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Store rate limiters per IP address
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    // 10 requests per minute = 10/60 = 0.1667 permits per second
    private static final double PERMITS_PER_SECOND = 10.0 / 60.0;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Apply rate limiting only to auth endpoints
        if (path.startsWith("/auth/")) {
            String clientIp = getClientIp(request);
            RateLimiter limiter = limiters.computeIfAbsent(clientIp, 
                k -> RateLimiter.create(PERMITS_PER_SECOND));
            
            if (!limiter.tryAcquire()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Too many requests. Please try again later.\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        } else {
            // X-Forwarded-For can contain multiple IPs, take the first one
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
