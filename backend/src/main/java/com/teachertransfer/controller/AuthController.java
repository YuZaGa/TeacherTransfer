package com.teachertransfer.controller;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.auth.*;
import com.teachertransfer.dto.onboarding.OnboardingRequest;
import com.teachertransfer.dto.onboarding.OnboardingResponse;
import com.teachertransfer.security.JwtUtil;
import com.teachertransfer.service.AuthService;
import com.teachertransfer.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        ApiResponse<AuthResponse> response = authService.googleAuth(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/complete")
    public ResponseEntity<ApiResponse<AuthResponse>> signupComplete(
            @Valid @RequestBody SignupCompleteRequest request) {
        ApiResponse<AuthResponse> response = authService.signupComplete(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        ApiResponse<AuthResponse> response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email-otp/send")
    public ResponseEntity<ApiResponse<EmailOtpResponse>> sendEmailOtp(
            @Valid @RequestBody SendEmailOtpRequest request) {
        ApiResponse<EmailOtpResponse> response = emailVerificationService.sendOtp(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email-otp/verify")
    public ResponseEntity<ApiResponse<EmailOtpResponse>> verifyEmailOtp(
            @Valid @RequestBody VerifyEmailOtpRequest request) {
        ApiResponse<EmailOtpResponse> response = emailVerificationService.verifyOtp(
                request.getEmail(), request.getOtp());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/onboarding/complete")
    public ResponseEntity<ApiResponse<OnboardingResponse>> completeOnboarding(
            @Valid @RequestBody OnboardingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        ApiResponse<OnboardingResponse> response = authService.completeOnboarding(teacherId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long teacherId = jwtUtil.extractTeacherId(
                authentication.getCredentials().toString()
        );

        AuthResponse response = new AuthResponse();
        response.setEmail(email);
        response.setTeacherId(teacherId);

        return ResponseEntity.ok(ApiResponse.success("User authenticated", response));
    }
}