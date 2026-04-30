package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Match type enumeration
 */
@Getter
public enum MatchType {
    POTENTIAL(1, "Potential"),
    INTEREST_SENT(2, "Interest Sent"),
    MUTUAL(3, "Mutual");

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
        return POTENTIAL;
    }
}
