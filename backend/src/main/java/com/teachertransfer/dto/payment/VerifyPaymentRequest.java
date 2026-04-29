package com.teachertransfer.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyPaymentRequest {

    @NotBlank(message = "Razorpay order ID is required")
    @Size(max = 100, message = "Order ID must not exceed 100 characters")
    private String razorpayOrderId;

    @NotBlank(message = "Razorpay payment ID is required")
    @Size(max = 100, message = "Payment ID must not exceed 100 characters")
    private String razorpayPaymentId;

    @NotBlank(message = "Razorpay signature is required")
    @Size(max = 255, message = "Signature must not exceed 255 characters")
    private String razorpaySignature;

    public VerifyPaymentRequest() {}

    public VerifyPaymentRequest(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
    }

    // Getters and Setters
    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
}