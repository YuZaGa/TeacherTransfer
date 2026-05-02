package com.teachertransfer.dto.onboarding;

public class OnboardingResponse {

    private String message;
    private String redirectTo;

    public OnboardingResponse() {}

    public OnboardingResponse(String message, String redirectTo) {
        this.message = message;
        this.redirectTo = redirectTo;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRedirectTo() { return redirectTo; }
    public void setRedirectTo(String redirectTo) { this.redirectTo = redirectTo; }
}
