package com.meditrack.backend.dto;

public class LoginRequest {
    
    private String email;
    private String password;

    // default constructor
    public LoginRequest() {
    }

    // constructor
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters 
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
