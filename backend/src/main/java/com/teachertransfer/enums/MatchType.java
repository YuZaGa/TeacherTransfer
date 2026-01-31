package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Match type enumeration
 */
@Getter
public enum MatchType {
    DIRECT(1, "Direct"),
    ONE_WAY(2, "One-Way Interest"),
    MUTUAL(3, "Mutual"),
    MULTI_HOP(4, "Multi-Hop");

    private final int code;
    private final String displayName;

    MatchType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static MatchType fromCode(int code) {
        for (MatchType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return DIRECT;
    }
}
