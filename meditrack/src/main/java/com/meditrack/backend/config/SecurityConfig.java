package com.meditrack.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

// Configuration class for setting up security parameters
@Configuration
public class SecurityConfig {
    
    private final JwtFilter jwtFilter;
    private final RateLimitFilter rateLimitFilter;

    // Constructor
    public SecurityConfig(JwtFilter jwtFilter, RateLimitFilter rateLimitFilter) {
        this.jwtFilter = jwtFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll() // Allow unauthenticated access to auth endpoints
                    .requestMatchers("/h2-console/**").permitAll() // Allow access to H2 console
                    .requestMatchers("/api/medications/**", "/api/medications").permitAll() // Allow access to medications endpoints
                    .requestMatchers("/api/drugs/**").permitAll() // Allow access to drug search endpoints
                    .requestMatchers("/api/auth/**").permitAll() // Allow access to auth endpoints
                    .requestMatchers("/actuator/**").permitAll() // Allow access to actuator endpoints (health checks)
                    .anyRequest().authenticated() // All other requests require authentication
            )
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // Disable frame options for H2 console
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class) // Add rate limit filter first
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before username/password filter

        return http.build();
    }

    // Password encoder bean for hashing passwords, used in UserService and AuthController
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Read allowed origins from environment variable, fallback to placeholder for public repo
        String corsOrigins = System.getenv("CORS_ORIGINS");
        if (corsOrigins == null || corsOrigins.isEmpty()) {
            corsOrigins = "https://example.com"; // Placeholder default
        }
        // Support multiple origins separated by comma
        configuration.setAllowedOrigins(Arrays.asList(corsOrigins.split(",")));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
