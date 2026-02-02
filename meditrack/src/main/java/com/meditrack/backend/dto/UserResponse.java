package com.meditrack.backend.dto;

import java.util.Date;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private boolean emailVerified;
    private Date createdAt;

    // Constructor
    public UserResponse(Long id, String name, String email, boolean emailVerified, Date createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public boolean isEmailVerified() {
        return emailVerified;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

}
