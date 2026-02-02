package com.meditrack.backend.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.meditrack.backend.Model.User;
import com.meditrack.backend.Service.UserService;
import com.meditrack.backend.Service.EmailService;
import com.meditrack.backend.dto.ChangePasswordRequest;
import com.meditrack.backend.dto.UpdateProfileRequest;
import com.meditrack.backend.dto.UserResponse;
import com.meditrack.backend.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserRepository userRepository, UserService userService, EmailService emailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    // Get current user profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getCreatedAt()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update user profile
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            log.info("Profile update attempt for user: {}", email);
            
            // Check if new email is already taken by another user
            if (!request.getEmail().equals(user.getEmail())) {
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                    return ResponseEntity.badRequest().body("Email already in use");
                }
            }
            
            // Update user details
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            User updatedUser = userRepository.save(user);
            
            log.info("Profile updated successfully for user: {}", email);
            
            UserResponse response = new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.isEmailVerified(),
                updatedUser.getCreatedAt()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Profile update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Change password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Password change attempt for user: {}", email);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            userService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());
            
            // Send confirmation email
            emailService.sendPasswordChangeConfirmation(user.getEmail(), user.getName());
            
            log.info("Password changed successfully for user: {}", email);
            return ResponseEntity.ok().body("{\"message\":\"Password changed successfully\"}");
        } catch (IllegalArgumentException e) {
            log.error("Password change failed for user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
