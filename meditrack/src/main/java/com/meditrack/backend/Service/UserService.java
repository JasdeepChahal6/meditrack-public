package com.meditrack.backend.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.meditrack.backend.Model.User;
import com.meditrack.backend.Model.RefreshToken;
import com.meditrack.backend.dto.AuthResponse;
import com.meditrack.backend.dto.RegisterRequest;
import com.meditrack.backend.dto.UserResponse;
import com.meditrack.backend.repository.UserRepository;
import com.meditrack.backend.util.JwtUtil;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    // Dependencies, repo for accessing user data in db 
    private final UserRepository userRepository;
    // Password encoder for hashing passwords
    private final PasswordEncoder passwordEncoder;
    // JWT utility for token generation and validation
    private final JwtUtil jwtUtil;
    // Refresh token service
    private final RefreshTokenService refreshTokenService;
    // Email verification service
    private final EmailVerificationService emailVerificationService;
    // Email service
    private final EmailService emailService;

    // Constructor
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtUtil jwtUtil, RefreshTokenService refreshTokenService,
                      EmailVerificationService emailVerificationService,
                      EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
    }

    // Register a new user
    public User registerUser(RegisterRequest request) { //register request dto from controller
        // Basic validation (to be improved with real email validation)
         if(!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // check if email already exists in the database
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create new user and hash the password, dto to entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false); // Email not verified yet

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        
        // Send verification email
        emailVerificationService.createAndSendVerificationToken(savedUser);
        
        return savedUser;
    }

    // Authenticate user during login
    public User authenticateUser(String email, String password) { // email and password from login request dto, passed from controller
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Failed login attempt for email: {}", email);
                    return new IllegalArgumentException("Invalid email or password");
                });
        
        // Check if password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Failed login attempt - incorrect password for email: {}", email);
            throw new IllegalArgumentException("Invalid email or password");
        }
        log.info("User authenticated successfully: {}", email);
        return user;
    }

    // Login user and generate JWT token
    public AuthResponse loginAndGenerateToken(String email, String password) {
        User user = authenticateUser(email, password);
        
        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("Please verify your email before logging in");
        }
        
        // Generate access token
        String token = jwtUtil.generateToken(user);
        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.isEmailVerified(), user.getCreatedAt());
        return new AuthResponse(token, refreshToken.getToken(), userResponse);
    }

    // Change user password
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Update to new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Send password change notification email
        try {
            emailService.sendPasswordChangeNotification(user.getEmail());
        } catch (Exception e) {
            log.warn("Failed to send password change notification email: {}", e.getMessage());
        }
        
        log.info("Password changed for user: {}", email);
    }

    // Find user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Update user entity
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // Update password (for password reset)
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated for user: {}", user.getEmail());
    }
}
