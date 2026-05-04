package com.teachertransfer.service;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.auth.EmailOtpResponse;
import com.teachertransfer.email.EmailType;
import com.teachertransfer.entity.OtpVerification;
import com.teachertransfer.enums.OtpPurpose;
import com.teachertransfer.repository.OtpVerificationRepository;
import com.teachertransfer.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_SECONDS = 300;
    private static final int MAX_ATTEMPTS = 3;
    private static final long RESEND_COOLDOWN_SECONDS = 60;

    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Value("${app.otp.max-attempts:3}")
    private int maxAttempts;

    public EmailVerificationService(OtpVerificationRepository otpRepository,
                                    EmailService emailService,
                                    JwtUtil jwtUtil) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public ApiResponse<EmailOtpResponse> sendOtp(String email) {
        if (!isValidEmail(email)) {
            return ApiResponse.error("Invalid email address");
        }

        OtpVerification recent = otpRepository
            .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, OtpPurpose.EMAIL_VERIFICATION.getCode()).orElse(null);

        if (recent != null) {
            long secondsSinceLastOtp = java.time.Duration.between(recent.getCreatedAt(), LocalDateTime.now()).getSeconds();
            if (secondsSinceLastOtp < RESEND_COOLDOWN_SECONDS) {
                long waitSeconds = RESEND_COOLDOWN_SECONDS - secondsSinceLastOtp;
                return ApiResponse.error("Please wait " + waitSeconds + " seconds before requesting a new OTP");
            }
        }

        String otp = generateOtp();
        String otpHash = hashOtp(otp);

        OtpVerification entity = OtpVerification.builder()
            .email(email)
            .otpHash(otpHash)
            .purpose(OtpPurpose.EMAIL_VERIFICATION.getCode())
            .attempts(0)
            .verified(false)
            .expiresAt(LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS))
            .build();

        otpRepository.save(entity);

        try {
            emailService.sendOtpEmail(email, otp, EmailType.EMAIL_VERIFICATION_OTP);
            log.info("Email OTP sent to {}", email);
            return ApiResponse.success("OTP sent to " + email, new EmailOtpResponse("OTP sent successfully"));
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", email, e.getMessage());
            return ApiResponse.error("Failed to send OTP. Please try again.");
        }
    }

    @Transactional
    public ApiResponse<EmailOtpResponse> verifyOtp(String email, String otp) {
        if (!isValidEmail(email)) {
            return ApiResponse.error("Invalid email address");
        }

        List<OtpVerification> validOtps = otpRepository.findValidEmailOtps(
            email,
            OtpPurpose.EMAIL_VERIFICATION.getCode(),
            LocalDateTime.now(),
            maxAttempts
        );

        if (validOtps.isEmpty()) {
            OtpVerification latest = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, OtpPurpose.EMAIL_VERIFICATION.getCode()).orElse(null);
            if (latest != null && latest.getAttempts() >= maxAttempts) {
                return ApiResponse.error("Maximum attempts exceeded. Please request a new OTP.");
            }
            return ApiResponse.error("OTP has expired. Please request a new one.");
        }

        OtpVerification record = validOtps.get(0);

        if (!hashOtp(otp).equals(record.getOtpHash())) {
            record.setAttempts(record.getAttempts() + 1);
            otpRepository.save(record);

            int remainingAttempts = maxAttempts - record.getAttempts();
            if (remainingAttempts <= 0) {
                return ApiResponse.error("Maximum attempts exceeded. Please request a new OTP.");
            }
            return ApiResponse.error("Invalid OTP. " + remainingAttempts + " attempts remaining.");
        }

        record.setVerified(true);
        otpRepository.save(record);

        String tempToken = jwtUtil.generateRegistrationToken(email);

        log.info("Email verified successfully for {}", email);
        return ApiResponse.success("Email verified successfully",
            new EmailOtpResponse("Email verified successfully", tempToken));
    }

    public boolean validateOtp(String email, String otp) {
        if (!isValidEmail(email)) {
            return false;
        }

        List<OtpVerification> validOtps = otpRepository.findValidEmailOtps(
            email,
            OtpPurpose.EMAIL_VERIFICATION.getCode(),
            LocalDateTime.now(),
            maxAttempts
        );

        if (validOtps.isEmpty()) {
            return false;
        }

        OtpVerification record = validOtps.get(0);

        if (!hashOtp(otp).equals(record.getOtpHash())) {
            record.setAttempts(record.getAttempts() + 1);
            otpRepository.save(record);
            return false;
        }

        record.setVerified(true);
        otpRepository.save(record);
        log.info("OTP validated successfully for {}", email);
        return true;
    }

    @Transactional
    public ApiResponse<EmailOtpResponse> sendPasswordResetOtp(String email) {
        if (!isValidEmail(email)) {
            return ApiResponse.error("Invalid email address");
        }

        OtpVerification recent = otpRepository
            .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, OtpPurpose.PASSWORD_RESET.getCode()).orElse(null);

        if (recent != null) {
            long secondsSinceLastOtp = java.time.Duration.between(recent.getCreatedAt(), LocalDateTime.now()).getSeconds();
            if (secondsSinceLastOtp < RESEND_COOLDOWN_SECONDS) {
                long waitSeconds = RESEND_COOLDOWN_SECONDS - secondsSinceLastOtp;
                return ApiResponse.error("Please wait " + waitSeconds + " seconds before requesting a new OTP");
            }
        }

        String otp = generateOtp();
        String otpHash = hashOtp(otp);

        OtpVerification entity = OtpVerification.builder()
            .email(email)
            .otpHash(otpHash)
            .purpose(OtpPurpose.PASSWORD_RESET.getCode())
            .attempts(0)
            .verified(false)
            .expiresAt(LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS))
            .build();

        otpRepository.save(entity);

        try {
            emailService.sendOtpEmail(email, otp, EmailType.PASSWORD_RESET_OTP);
            log.info("Password reset OTP sent to {}", email);
            return ApiResponse.success("OTP sent to " + email, new EmailOtpResponse("OTP sent successfully"));
        } catch (Exception e) {
            log.error("Failed to send password reset OTP email to {}: {}", email, e.getMessage());
            return ApiResponse.error("Failed to send OTP. Please try again.");
        }
    }

    @Transactional
    public ApiResponse<EmailOtpResponse> verifyPasswordResetOtp(String email, String otp) {
        if (!isValidEmail(email)) {
            return ApiResponse.error("Invalid email address");
        }

        List<OtpVerification> validOtps = otpRepository.findValidEmailOtps(
            email,
            OtpPurpose.PASSWORD_RESET.getCode(),
            LocalDateTime.now(),
            maxAttempts
        );

        if (validOtps.isEmpty()) {
            OtpVerification latest = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, OtpPurpose.PASSWORD_RESET.getCode()).orElse(null);
            if (latest != null && latest.getAttempts() >= maxAttempts) {
                return ApiResponse.error("Maximum attempts exceeded. Please request a new OTP.");
            }
            return ApiResponse.error("OTP has expired. Please request a new one.");
        }

        OtpVerification record = validOtps.get(0);

        if (!hashOtp(otp).equals(record.getOtpHash())) {
            record.setAttempts(record.getAttempts() + 1);
            otpRepository.save(record);

            int remainingAttempts = maxAttempts - record.getAttempts();
            if (remainingAttempts <= 0) {
                return ApiResponse.error("Maximum attempts exceeded. Please request a new OTP.");
            }
            return ApiResponse.error("Invalid OTP. " + remainingAttempts + " attempts remaining.");
        }

        record.setVerified(true);
        otpRepository.save(record);

        log.info("Password reset OTP verified successfully for {}", email);
        return ApiResponse.success("OTP verified successfully",
            new EmailOtpResponse("OTP verified successfully"));
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.isBlank() && email.contains("@");
    }
}
