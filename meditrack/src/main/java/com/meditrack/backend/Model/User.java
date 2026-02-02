package com.meditrack.backend.Model;

import jakarta.persistence.*;
import java.util.Date;

// Marks this class as a JPA entity mapped to a database table
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    //unique email
    @Column(unique=true)
    private String email;

    // may remove later becuase of use of UserResponse DTO
    private String password;

    // Email verification status
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    // Timestamp for when the user was created
    //Temporal annotation to specify date and time
    //timestamp store both date and time 
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Constructor
    public User() {
        // Initialize createdAt to current date and time
        this.createdAt = new Date();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public boolean isEmailVerified() {
        return emailVerified;
    }
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }


}
