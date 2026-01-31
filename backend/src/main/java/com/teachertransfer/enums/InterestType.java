package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Interest type enumeration
 */
@Getter
public enum InterestType {
    ONE_WAY(1, "One-Way"),
    MUTUAL(2, "Mutual"),
    MULTI_HOP(3, "Multi-Hop");

    private final int code;
    private final String displayName;

    InterestType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static InterestType fromCode(int code) {
        for (InterestType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return ONE_WAY;
    }
}
