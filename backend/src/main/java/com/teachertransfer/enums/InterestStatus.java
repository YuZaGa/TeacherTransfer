package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Interest status enumeration
 */
@Getter
public enum InterestStatus {
    PENDING(1, "Pending"),
    ACCEPTED(2, "Accepted"),
    REJECTED(3, "Rejected"),
    EXPIRED(4, "Expired"),
    WITHDRAWN(5, "Withdrawn");

    private final int code;
    private final String displayName;

    InterestStatus(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static InterestStatus fromCode(int code) {
        for (InterestStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }
}
