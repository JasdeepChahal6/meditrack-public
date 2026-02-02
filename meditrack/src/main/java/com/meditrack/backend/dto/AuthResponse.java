package com.meditrack.backend.dto;

// for login request response details
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserResponse user;

    // Constructor
    public AuthResponse(String token, String refreshToken, UserResponse user) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    // Getters
    public String getToken() {
        return token;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public UserResponse getUser() {
        return user;
    }
}
