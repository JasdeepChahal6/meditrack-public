package com.meditrack.backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.meditrack.backend.Model.User;
import java.util.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Component // marks this class as a Spring component, making it eligible for component scanning and dependency injection
public class JwtUtil {
    
    // Injecting values from application properties
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Injecting expiration time from application properties
    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    // Method to get the signing key for JWT, converts the secret key string to a Key object
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // getBytes() converts the string to a byte array, then hmacShaKeyFor creates a Key suitable for HMAC-SHA algorithms
    }

    // Method to generate a JWT token for a given user
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // Adding user-specific claims to the token
        claims.put("userId", user.getId());

        //Use email as subject
        return createToken(claims, user.getEmail());
    }

    // Method to create a JWT token with given claims and subject
    public String createToken(Map<String, Object> claims, String subject) { 
        // current time in milliseconds
        long now = System.currentTimeMillis();
        // Building the JWT token with claims, subject, issued time, expiration time, and signing it with the secret key
        return Jwts.builder()
                .setClaims(claims) // custom claims like userId
                .setSubject(subject) // identifies the principal that is the subject of the JWT
                .setIssuedAt(new Date(now)) // token creation time
                .setExpiration(new Date(now + EXPIRATION_TIME)) // token expiration time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // signing the token with the secret key using HS256 algorithm
                .compact(); // builds the JWT and serializes it to a compact, URL-safe string
    }

    // Method to extract email (subject) from the JWT token
    public String extractEmail(String token) {
        return Jwts.parserBuilder() // creates a new JwtParserBuilder
                .setSigningKey(getSigningKey()) // sets the signing key to validate the token's signature
                .build() // builds the JwtParser
                .parseClaimsJws(token) // parses the JWT and verifies its signature
                .getBody() // retrieves the body of the JWT, which contains the claims
                .getSubject(); // extracts the subject (email) from the claims
    }

    // Method to check if the JWT token has expired
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // sets the signing key to validate the token's signature
                .build() 
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    // Method to validate the JWT token against a given user
    public boolean validateToken(String token, User user) {
        String email = extractEmail(token); // extract email from token
        return (email.equals(user.getEmail()) && !isTokenExpired(token)); // check if email matches and token is not expired
    }

    // Overloaded method to validate token without user object
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token); // if parsing is successful, token is valid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // any exception during parsing indicates the token is invalid
            return false;
        }
    }
}

