package com.meditrack.backend.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.meditrack.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Filter to validate JWT tokens in incoming requests
@Component
public class JwtFilter extends OncePerRequestFilter { // Extends OncePerRequestFilter to ensure a single execution per request

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    
    private final JwtUtil jwtUtil;

    // Constructor injection of JwtUtil
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    // Override the doFilterInternal method to implement JWT validation logic
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException { 
        // Skip JWT validation for public endpoints
        String path = request.getRequestURI();
        
        if (path.startsWith("/auth/") || path.startsWith("/api/auth/") || 
            path.startsWith("/api/medications") || path.startsWith("/api/drugs") ||
            path.startsWith("/h2-console")) {
            // Set anonymous authentication with ROLE_ANONYMOUS authority to prevent 403
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymous", null, 
                    List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")))
            );
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Check if the Authorization header is present and starts with "Bearer "
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                // Extract email from the JWT token
                email = jwtUtil.extractEmail(jwt);
            } catch (Exception e) {
                log.debug("Invalid JWT token: {}", e.getMessage());
                // Invalid token
            }
        }

        // If email is extracted and no authentication is set in the context, validate the token
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) { 
            // Validate the token
            if(jwtUtil.isTokenValid(jwt)) {
                log.debug("Valid JWT token for user: {}", email);
                // Set authentication in the security context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                // Set details and update the security context
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // finally set the authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
    
}
