package com.teachertransfer.enums;

import lombok.Getter;

/**
 * School type enumeration for Bihar government schools
 */
@Getter
public enum SchoolType {
    PRIMARY(1, "Primary School (1-5)"),
    MIDDLE(2, "Middle School (6-8)"),
    HIGH(3, "High School (9-10)"),
    PLUS_TWO(4, "+2 School (11-12)"),
    KASTURBA(5, "Kasturba Gandhi Balika Vidyalaya"),
    MODEL(6, "Model School"),
    OTHER(99, "Other");

    private final int code;
    private final String displayName;

    SchoolType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static SchoolType fromCode(int code) {
        for (SchoolType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return OTHER;
    }
}
