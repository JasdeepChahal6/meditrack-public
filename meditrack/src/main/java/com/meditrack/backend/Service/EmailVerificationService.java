package com.meditrack.backend.Service;

import com.meditrack.backend.Model.EmailVerificationToken;
import com.meditrack.backend.Model.User;
import com.meditrack.backend.repository.EmailVerificationTokenRepository;
import com.meditrack.backend.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);
    
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url:https://example.com}")
    private String frontendUrl;

    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository, EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void createAndSendVerificationToken(User user) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Generate new token
        String token = TokenUtil.generateToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // 24 hours expiry

        // Save token
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);
        tokenRepository.save(verificationToken);

        // Send email (or log for development)
        try {
            emailService.sendVerificationEmail(user.getEmail(), token);
        } catch (Exception e) {
            log.warn("Email sending failed (expected in dev): {}", e.getMessage());
            log.info("====================================");
            log.info("VERIFICATION LINK FOR DEVELOPMENT:");
            log.info("{}/verify-email?token={}", frontendUrl, token);
            log.info("====================================");
        }
    }

    public EmailVerificationToken validateToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        return verificationToken;
    }

    @Transactional
    public void deleteToken(EmailVerificationToken token) {
        tokenRepository.delete(token);
    }
}
