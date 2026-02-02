package com.meditrack.backend.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    @Value("${app.frontend.url:https://example.com}")
    private String frontendUrl;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        
        String htmlContent = "<html><body>" +
                "<h2>Welcome to MediTrack!</h2>" +
                "<p>Please verify your email address by clicking the button below:</p>" +
                "<a href=\"" + verificationUrl + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #4F46E5; color: white; text-decoration: none; border-radius: 5px;\">Verify Email</a>" +
                "<p>Or copy and paste this link into your browser:</p>" +
                "<p>" + verificationUrl + "</p>" +
                "<p>This link will expire in 24 hours.</p>" +
                "<p>If you didn't create an account with MediTrack, please ignore this email.</p>" +
                "<p>Best regards,<br>MediTrack Team</p>" +
                "</body></html>";

        sendEmail(toEmail, "MediTrack - Verify Your Email Address", htmlContent);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        
        String htmlContent = "<html><body>" +
                "<h2>Password Reset Request</h2>" +
                "<p>We received a request to reset your password. Click the button below to create a new password:</p>" +
                "<a href=\"" + resetUrl + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #4F46E5; color: white; text-decoration: none; border-radius: 5px;\">Reset Password</a>" +
                "<p>Or copy and paste this link into your browser:</p>" +
                "<p>" + resetUrl + "</p>" +
                "<p>This link will expire in 1 hour.</p>" +
                "<p>If you didn't request a password reset, please ignore this email and your password will remain unchanged.</p>" +
                "<p>Best regards,<br>MediTrack Team</p>" +
                "</body></html>";

        sendEmail(toEmail, "MediTrack - Reset Your Password", htmlContent);
    }

    public void sendPasswordChangeConfirmation(String toEmail, String userName) {
        String htmlContent = "<html><body>" +
                "<h2>Password Changed Successfully</h2>" +
                "<p>Hello " + userName + ",</p>" +
                "<p>Your password was successfully changed.</p>" +
                "<p>If you didn't make this change, please contact our support team immediately.</p>" +
                "<p>Best regards,<br>MediTrack Team</p>" +
                "</body></html>";

        sendEmail(toEmail, "MediTrack - Password Changed Successfully", htmlContent);
    }

    public void sendPasswordChangeNotification(String toEmail) {
        String htmlContent = "<html><body>" +
                "<h2>Password Changed</h2>" +
                "<p>Hello,</p>" +
                "<p>Your MediTrack password was recently changed.</p>" +
                "<p>If you didn't make this change, please reset your password immediately or contact support.</p>" +
                "<p>Best regards,<br>MediTrack Team</p>" +
                "</body></html>";

        sendEmail(toEmail, "MediTrack - Password Changed", htmlContent);
    }

    public void sendEmailChangeVerification(String newEmail, String token) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        
        String htmlContent = "<html><body>" +
                "<h2>Verify Your New Email Address</h2>" +
                "<p>Please verify your new email address by clicking the button below:</p>" +
                "<a href=\"" + verificationUrl + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #4F46E5; color: white; text-decoration: none; border-radius: 5px;\">Verify Email</a>" +
                "<p>Or copy and paste this link into your browser:</p>" +
                "<p>" + verificationUrl + "</p>" +
                "<p>This link will expire in 24 hours.</p>" +
                "<p>If you didn't request this email change, please contact our support team.</p>" +
                "<p>Best regards,<br>MediTrack Team</p>" +
                "</body></html>";

        sendEmail(newEmail, "MediTrack - Verify Your New Email Address", htmlContent);
    }

    private void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(toEmail)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            log.info("Email sent successfully to {} with ID: {}", toEmail, data.getId());
        } catch (ResendException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
