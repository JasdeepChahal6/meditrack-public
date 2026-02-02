package com.meditrack.backend.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import com.meditrack.backend.Model.User;
import com.meditrack.backend.Model.RefreshToken;
import com.meditrack.backend.Model.EmailVerificationToken;
import com.meditrack.backend.Model.PasswordResetToken;
import com.meditrack.backend.Service.UserService;
import com.meditrack.backend.Service.RefreshTokenService;
import com.meditrack.backend.Service.EmailVerificationService;
import com.meditrack.backend.Service.PasswordResetService;
import com.meditrack.backend.Service.EmailService;
import com.meditrack.backend.dto.AuthResponse;
import com.meditrack.backend.dto.LoginRequest;
import com.meditrack.backend.dto.RegisterRequest;
import com.meditrack.backend.dto.RefreshTokenRequest;
import com.meditrack.backend.dto.UserResponse;
import com.meditrack.backend.dto.MessageResponse;
import com.meditrack.backend.util.JwtUtil;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    // Dependency Injection of UserService, which handles business logic
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    // Constructor
    public AuthController(UserService userService, RefreshTokenService refreshTokenService, 
                         EmailVerificationService emailVerificationService, 
                         PasswordResetService passwordResetService,
                         EmailService emailService,
                         JwtUtil jwtUtil) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    // Endpoint for user registration
    @PostMapping("/register") 
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) { // ? indicates any type of response / accepting register request dto
        try {
            log.info("Registration attempt for email: {}", request.getEmail());
            // Call the UserService to register the user
            User registeredUser = userService.registerUser(request);
            // map User to UserResponse DTO
            UserResponse response = new UserResponse( // converts entity to dto for response without password
                registeredUser.getId(),
                registeredUser.getName(),
                registeredUser.getEmail(),
                registeredUser.isEmailVerified(),
                registeredUser.getCreatedAt()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Registration failed for email: {} - {}", request.getEmail(), e.getMessage());
            // catch any validation errors and return bad request
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) { // ? indicates any type of response / accepting login request dto
        try {
            log.info("Login attempt for email: {}", request.getEmail());
            // Call the UserService to authenticate and generate token
            AuthResponse authResponse = userService.loginAndGenerateToken(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException e) {
            log.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            // catch authentication errors and return unauthorized
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // Endpoint to refresh access token using refresh token
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            log.info("Token refresh attempt");
            // Verify refresh token
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
            // Generate new access token
            String newAccessToken = jwtUtil.generateToken(refreshToken.getUser());
            
            return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                refreshToken.getToken(),
                new UserResponse(
                    refreshToken.getUser().getId(),
                    refreshToken.getUser().getName(),
                    refreshToken.getUser().getEmail(),
                    refreshToken.getUser().isEmailVerified(),
                    refreshToken.getUser().getCreatedAt()
                )
            ));
        } catch (IllegalArgumentException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // Endpoint to logout (revoke refresh token)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        try {
            log.info("Logout attempt");
            refreshTokenService.deleteByToken(request.getRefreshToken());
            return ResponseEntity.ok().body("{\"message\":\"Logged out successfully\"}");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to verify email with token
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            EmailVerificationToken verificationToken = emailVerificationService.validateToken(token);
            User user = verificationToken.getUser();
            
            // Mark email as verified
            user.setEmailVerified(true);
            userService.updateUser(user);
            
            // Delete the used token
            emailVerificationService.deleteToken(verificationToken);
            
            return ResponseEntity.ok(new MessageResponse("Email verified successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to resend verification email
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        try {
            log.info("Resend verification attempt for email: {}", email);
            User user = userService.findByEmail(email);
            
            if (user.isEmailVerified()) {
                return ResponseEntity.badRequest().body("Email is already verified");
            }
            
            emailVerificationService.createAndSendVerificationToken(user);
            return ResponseEntity.ok().body("{\"message\":\"Verification email sent\"}");
        } catch (IllegalArgumentException e) {
            log.error("Resend verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to request password reset
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            log.info("Password reset request for email: {}", email);
            User user = userService.findByEmail(email);
            passwordResetService.createAndSendResetToken(user);
            return ResponseEntity.ok().body("{\"message\":\"Password reset email sent\"}");
        } catch (IllegalArgumentException e) {
            log.error("Password reset request failed: {}", e.getMessage());
            // Return success even if user not found (security best practice)
            return ResponseEntity.ok().body("{\"message\":\"If that email exists, a reset link has been sent\"}");
        }
    }

    // Endpoint to reset password with token
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            log.info("Password reset attempt");
            PasswordResetToken resetToken = passwordResetService.validateToken(token);
            User user = resetToken.getUser();
            
            // Update password
            userService.updatePassword(user, newPassword);
            
            // Mark token as used
            passwordResetService.markTokenAsUsed(resetToken);
            
            // Send confirmation email
            emailService.sendPasswordChangeConfirmation(user.getEmail(), user.getName());
            
            return ResponseEntity.ok().body("{\"message\":\"Password reset successfully\"}");
        } catch (IllegalArgumentException e) {
            log.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}