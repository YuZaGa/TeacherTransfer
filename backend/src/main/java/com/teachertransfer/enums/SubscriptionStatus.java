package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Subscription status enumeration
 */
@Getter
public enum SubscriptionStatus {
    FREE(0, "Free"),
    PAID_ACTIVE(1, "Paid Active"),
    PAID_EXPIRED(2, "Paid Expired");

    private final int code;
    private final String displayName;

    SubscriptionStatus(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static SubscriptionStatus fromCode(int code) {
        for (SubscriptionStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return FREE;
    }

    public boolean isPaidActive() {
        return this == PAID_ACTIVE;
    }
}
