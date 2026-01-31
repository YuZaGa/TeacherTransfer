package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Payment status enumeration
 */
@Getter
public enum PaymentStatus {
    PENDING(0, "Pending"),
    SUCCESS(1, "Success"),
    FAILED(2, "Failed"),
    REFUNDED(3, "Refunded");

    private final int code;
    private final String displayName;

    PaymentStatus(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }
}
