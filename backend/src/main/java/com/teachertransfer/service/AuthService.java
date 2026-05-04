package com.teachertransfer.service;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.auth.*;
import com.teachertransfer.dto.onboarding.OnboardingRequest;
import com.teachertransfer.dto.onboarding.OnboardingResponse;
import com.teachertransfer.entity.Block;
import com.teachertransfer.entity.Teacher;
import com.teachertransfer.enums.SubscriptionStatus;
import com.teachertransfer.enums.TeacherStatus;
import com.teachertransfer.repository.BlockRepository;
import com.teachertransfer.repository.TeacherRepository;
import com.teachertransfer.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private TeacherService teacherService;

    // ==================== Google Auth ====================

    @SuppressWarnings("unchecked")
    public ApiResponse<AuthResponse> googleAuth(GoogleAuthRequest request) {
        Map<String, Object> googleProfile;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + request.getIdToken();
            googleProfile = restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            return ApiResponse.error("Invalid Google token");
        }

        if (googleProfile == null || googleProfile.get("email") == null) {
            return ApiResponse.error("Could not verify Google account");
        }

        String googleId = (String) googleProfile.get("sub");
        String email = (String) googleProfile.get("email");
        String name = (String) googleProfile.get("name");
        String picture = (String) googleProfile.get("picture");

        // Check if user already exists
        Optional<Teacher> existingByGoogleId = teacherRepository.findByGoogleId(googleId);
        if (existingByGoogleId.isPresent()) {
            Teacher teacher = existingByGoogleId.get();
            teacher.setLastLoginAt(LocalDateTime.now());
            teacherRepository.save(teacher);
            return buildLoginResponse(teacher);
        }

        Optional<Teacher> existingByEmail = teacherRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {
            Teacher teacher = existingByEmail.get();
            teacher.setGoogleId(googleId);
            teacher.setProfilePictureUrl(picture);
            teacher.setEmailVerified(true);
            teacher.setLastLoginAt(LocalDateTime.now());
            teacherRepository.save(teacher);
            return buildLoginResponse(teacher);
        }

        // New user - return temp token for registration
        String tempToken = jwtUtil.generateRegistrationToken(email);

        AuthResponse response = new AuthResponse();
        response.setToken(tempToken);
        response.setEmail(email);
        response.setName(name);
        response.setRedirectTo("/register");
        response.setGoogleId(googleId);
        response.setProfilePictureUrl(picture);

        return ApiResponse.success("New user. Please complete registration.", response);
    }

    // ==================== Register ====================

    public ApiResponse<AuthResponse> register(RegisterRequest request, String email) {
        if (teacherRepository.existsByEmail(email)) {
            return ApiResponse.error("Email already registered");
        }

        if (request.getPhone() != null && teacherRepository.existsByPhone(request.getPhone())) {
            return ApiResponse.error("Phone number already registered");
        }

        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setEmail(email);
        teacher.setPhone(request.getPhone());
        teacher.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        teacher.setGender(request.getGender() != null ? request.getGender().getCode() : null);
        teacher.setEmployeeId(request.getEmployeeId());
        teacher.setUdiseCode(request.getUdiseCode());
        teacher.setSchoolName(request.getSchoolName());
        teacher.setSubject(request.getSubject().getCode());
        teacher.setSchoolType(request.getSchoolType().getCode());
        teacher.setCurrentDistrictId(request.getCurrentDistrictId());
        teacher.setCurrentBlockId(request.getCurrentBlockId());

        Double currentLat = request.getCurrentLat();
        Double currentLng = request.getCurrentLng();
        if (currentLat == null || currentLng == null) {
            com.teachertransfer.entity.Block currentBlock = blockRepository.findById(request.getCurrentBlockId()).orElse(null);
            if (currentBlock != null) {
                currentLat = currentBlock.getLat();
                currentLng = currentBlock.getLng();
            }
        }
        teacher.setCurrentLat(currentLat);
        teacher.setCurrentLng(currentLng);

        teacher.setPreferredDistrictId(request.getPreferredDistrictId());
        teacher.setPreferredBlockId(request.getPreferredBlockId());

        Double preferredLat = request.getPreferredLat();
        Double preferredLng = request.getPreferredLng();
        if (preferredLat == null || preferredLng == null) {
            com.teachertransfer.entity.Block preferredBlock = blockRepository.findById(request.getPreferredBlockId()).orElse(null);
            if (preferredBlock != null) {
                preferredLat = preferredBlock.getLat();
                preferredLng = preferredBlock.getLng();
            }
        }
        teacher.setPreferredLat(preferredLat);
        teacher.setPreferredLng(preferredLng);

        teacher.setRadiusKm(request.getRadiusKm());
        teacher.setStatus(TeacherStatus.ACTIVE.getCode());
        teacher.setSubscriptionStatus(SubscriptionStatus.FREE.getCode());
        teacher.setPhoneVerified(false);
        teacher.setEmailVerified(true);
        teacher.setCreatedAt(LocalDateTime.now());

        if (request.getGoogleId() != null) {
            teacher.setGoogleId(request.getGoogleId());
        }
        if (request.getProfilePictureUrl() != null) {
            teacher.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            Teacher referrer = teacherRepository.findByReferralCode(request.getReferralCode()).orElse(null);
            if (referrer != null) {
                teacher.setReferredBy(referrer.getId());
                referrer.setReferralCount(referrer.getReferralCount() + 1);
                teacherRepository.save(referrer);
            }
        }

        teacher.setReferralCode(generateReferralCode());
        teacher = teacherRepository.save(teacher);

        try {
            emailService.sendWelcomeEmail(teacher);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        String token = jwtUtil.generateToken(email, teacher.getId());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTeacherId(teacher.getId());
        response.setEmail(teacher.getEmail());
        response.setPhone(teacher.getPhone());
        response.setName(teacher.getName());
        response.setPhoneVerified(teacher.getPhoneVerified());
        response.setEmailVerified(teacher.getEmailVerified());
        response.setSubscriptionStatus(teacher.getSubscriptionStatus());
        response.setRedirectTo("/dashboard");

        return ApiResponse.success("Registration successful", response);
    }

    // ==================== Email + Password Signup ====================

    @Transactional
    public ApiResponse<AuthResponse> signupComplete(SignupCompleteRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (teacherRepository.existsByEmail(email)) {
            return ApiResponse.error("Email already registered");
        }

        boolean otpValid = emailVerificationService.validateOtp(email, request.getOtp());
        if (!otpValid) {
            return ApiResponse.error("Invalid or expired OTP. Please request a new one.");
        }

        Teacher teacher = Teacher.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .emailVerified(true)
            .status(TeacherStatus.ACTIVE.getCode())
            .subscriptionStatus(SubscriptionStatus.FREE.getCode())
            .referralCode(generateReferralCode())
            .onboardingCompleted(false)
            .build();

        teacher = teacherRepository.save(teacher);

        log.info("New user registered with email: {}", email);

        AuthResponse response = new AuthResponse();
        response.setEmail(teacher.getEmail());
        response.setEmailVerified(true);
        response.setSubscriptionStatus(teacher.getSubscriptionStatus());
        response.setRedirectTo("/login");

        return ApiResponse.success("Registration successful. Please log in.", response);
    }

    // ==================== First-Time Onboarding ====================

    public ApiResponse<OnboardingResponse> completeOnboarding(Long teacherId, OnboardingRequest request) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (request.getPhone() != null && !request.getPhone().equals(teacher.getPhone())) {
            if (teacherRepository.existsByPhone(request.getPhone())) {
                return ApiResponse.error("Phone number already in use");
            }
        }

        teacher.setName(request.getName());
        teacher.setPhone(request.getPhone());
        teacher.setGender(request.getGender() != null ? request.getGender().getCode() : null);
        teacher.setEmployeeId(request.getEmployeeId());
        teacher.setUdiseCode(request.getUdiseCode());
        teacher.setSchoolName(request.getSchoolName());
        teacher.setSubject(request.getSubject().getCode());
        teacher.setSchoolType(request.getSchoolType().getCode());
        teacher.setCurrentDistrictId(request.getCurrentDistrictId());
        teacher.setCurrentBlockId(request.getCurrentBlockId());

        Double currentLat = request.getCurrentLat();
        Double currentLng = request.getCurrentLng();
        if (currentLat == null || currentLng == null) {
            Block currentBlock = blockRepository.findById(request.getCurrentBlockId()).orElse(null);
            if (currentBlock != null) {
                currentLat = currentBlock.getLat();
                currentLng = currentBlock.getLng();
            }
        }
        teacher.setCurrentLat(currentLat);
        teacher.setCurrentLng(currentLng);

        teacher.setPreferredDistrictId(request.getPreferredDistrictId());
        teacher.setPreferredBlockId(request.getPreferredBlockId());

        Double preferredLat = request.getPreferredLat();
        Double preferredLng = request.getPreferredLng();
        if (preferredLat == null || preferredLng == null) {
            Block preferredBlock = blockRepository.findById(request.getPreferredBlockId()).orElse(null);
            if (preferredBlock != null) {
                preferredLat = preferredBlock.getLat();
                preferredLng = preferredBlock.getLng();
            }
        }
        teacher.setPreferredLat(preferredLat);
        teacher.setPreferredLng(preferredLng);

        teacher.setRadiusKm(request.getRadiusKm());
        teacher.setOnboardingCompleted(true);
        teacher.setProfileUpdatedAt(LocalDateTime.now());

        teacherRepository.save(teacher);

        teacherService.updateGeoIndex(teacher);

        log.info("Onboarding completed for teacher id={} email={}", teacher.getId(), teacher.getEmail());

        OnboardingResponse onboardingResponse = new OnboardingResponse(
            "Onboarding completed successfully", "/dashboard");

        return ApiResponse.success("Profile setup complete", onboardingResponse);
    }

    // ==================== Password Reset ====================

    @Transactional
    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Teacher teacher = teacherRepository.findByEmail(email).orElse(null);
        if (teacher == null) {
            return ApiResponse.error("No account found with this email address");
        }

        ApiResponse<EmailOtpResponse> otpResponse = emailVerificationService.verifyPasswordResetOtp(email, request.getOtp());
        if (!otpResponse.isSuccess()) {
            return ApiResponse.error(otpResponse.getMessage());
        }

        teacher.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        teacherRepository.save(teacher);

        log.info("Password reset successfully for {}", email);
        return ApiResponse.success("Password reset successfully");
    }

    // ==================== Email + Password Login ====================

    public ApiResponse<AuthResponse> login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        teacher.setLastLoginAt(LocalDateTime.now());
        teacherRepository.save(teacher);

        return buildLoginResponse(teacher);
    }

    // ==================== Helpers ====================

    private ApiResponse<AuthResponse> buildLoginResponse(Teacher teacher) {
        String token = jwtUtil.generateToken(teacher.getEmail(), teacher.getId());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTeacherId(teacher.getId());
        response.setEmail(teacher.getEmail());
        response.setPhone(teacher.getPhone());
        response.setName(teacher.getName());
        response.setPhoneVerified(teacher.getPhoneVerified());
        response.setEmailVerified(teacher.getEmailVerified());
        response.setSubscriptionStatus(teacher.getSubscriptionStatus());
        response.setOnboardingRequired(
            teacher.getOnboardingCompleted() == null || !teacher.getOnboardingCompleted());

        String redirectTo = (teacher.getOnboardingCompleted() == null || !teacher.getOnboardingCompleted())
            ? "/onboarding" : "/dashboard";
        response.setRedirectTo(redirectTo);

        return ApiResponse.success("Login successful", response);
    }

    private String generateReferralCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}