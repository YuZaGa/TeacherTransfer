package com.teachertransfer.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateOrderRequest {

    @NotBlank(message = "Plan code is required")
    @Size(max = 20, message = "Plan code must not exceed 20 characters")
    private String planCode;

    public CreateOrderRequest() {}

    public CreateOrderRequest(String planCode) {
        this.planCode = planCode;
    }

    // Getters and Setters
    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }
}