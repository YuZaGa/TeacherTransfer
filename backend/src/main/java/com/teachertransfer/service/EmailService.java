package com.teachertransfer.service;

import com.teachertransfer.email.EmailMessage;
import com.teachertransfer.email.EmailSender;
import com.teachertransfer.email.EmailType;
import com.teachertransfer.entity.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final EmailSender emailSender;

    public EmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendWelcomeEmail(Teacher teacher) {
        String subject = "Welcome to TeacherTransfer, " + teacher.getName() + "!";
        String htmlBody = buildWelcomeHtml(teacher);

        EmailMessage message = new EmailMessage(
            teacher.getEmail(),
            subject,
            htmlBody,
            EmailType.WELCOME
        );

        try {
            emailSender.send(message);
            log.info("Welcome email sent to {}", teacher.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", teacher.getEmail(), e.getMessage());
        }
    }

    public void sendOtpEmail(String email, String otp, EmailType type) {
        String subject = switch (type) {
            case EMAIL_VERIFICATION_OTP -> "Verify your email for TeacherTransfer";
            case PASSWORD_RESET_OTP -> "Reset your TeacherTransfer password";
            default -> "Your TeacherTransfer OTP";
        };

        String htmlBody = buildOtpHtml(otp, type);

        EmailMessage message = new EmailMessage(email, subject, htmlBody, type);

        try {
            emailSender.send(message);
            log.info("OTP email sent to {} type={}", email, type);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {} type={}: {}", email, type, e.getMessage());
            throw e;
        }
    }

    private String buildWelcomeHtml(Teacher teacher) {
        return "<!DOCTYPE html>"
            + "<html><head><meta charset='utf-8'></head>"
            + "<body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>"
            + "<div style='background: linear-gradient(135deg, #2563eb, #4f46e5); padding: 30px; border-radius: 12px; text-align: center;'>"
            + "<h1 style='color: white; margin: 0;'>Welcome to TeacherTransfer!</h1>"
            + "</div>"
            + "<div style='padding: 30px 20px;'>"
            + "<p style='font-size: 18px; color: #1f2937;'>Hi <strong>" + teacher.getName() + "</strong>,</p>"
            + "<p style='font-size: 16px; color: #4b5563; line-height: 1.6;'>Your account has been created successfully. "
            + "You can now start discovering mutual transfer opportunities with other Bihar government school teachers.</p>"
            + "<div style='background: #f3f4f6; border-radius: 8px; padding: 20px; margin: 20px 0;'>"
            + "<h3 style='color: #1f2937; margin-top: 0;'>What's Next?</h3>"
            + "<ul style='color: #4b5563; line-height: 1.8;'>"
            + "<li>Browse your mutual matches on the Dashboard</li>"
            + "<li>Send transfer interests to teachers you'd like to swap with</li>"
            + "<li>Respond to interests received from others</li>"
            + "</ul></div>"
            + "<p style='font-size: 14px; color: #9ca3af;'>If you have any questions, feel free to reach out to our support team.</p>"
            + "<p style='font-size: 14px; color: #9ca3af;'>The TeacherTransfer Team</p>"
            + "</div></body></html>";
    }

    private String buildOtpHtml(String otp, EmailType type) {
        String heading = switch (type) {
            case EMAIL_VERIFICATION_OTP -> "Verify Your Email";
            case PASSWORD_RESET_OTP -> "Reset Your Password";
            default -> "Your OTP";
        };

        String message = switch (type) {
            case EMAIL_VERIFICATION_OTP -> "Thanks for signing up! Use the OTP below to verify your email address.";
            case PASSWORD_RESET_OTP -> "We received a request to reset your password. Use the OTP below to proceed.";
            default -> "Use the OTP below to complete your request.";
        };

        return "<!DOCTYPE html>"
            + "<html><head><meta charset='utf-8'></head>"
            + "<body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>"
            + "<div style='background: linear-gradient(135deg, #2563eb, #4f46e5); padding: 30px; border-radius: 12px; text-align: center;'>"
            + "<h1 style='color: white; margin: 0;'>" + heading + "</h1>"
            + "</div>"
            + "<div style='padding: 30px 20px; text-align: center;'>"
            + "<p style='font-size: 16px; color: #4b5563; line-height: 1.6;'>" + message + "</p>"
            + "<div style='background: #f3f4f6; border-radius: 8px; padding: 20px; margin: 20px 0; display: inline-block;'>"
            + "<span style='font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #2563eb;'>" + otp + "</span>"
            + "</div>"
            + "<p style='font-size: 14px; color: #9ca3af;'>This OTP expires in 5 minutes. If you did not request this, please ignore this email.</p>"
            + "<p style='font-size: 14px; color: #9ca3af;'>The TeacherTransfer Team</p>"
            + "</div></body></html>";
    }
}
