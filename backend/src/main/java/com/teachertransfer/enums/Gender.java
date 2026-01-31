package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Gender enumeration
 */
@Getter
public enum Gender {
    MALE(1, "Male"),
    FEMALE(2, "Female"),
    OTHER(3, "Other");

    private final int code;
    private final String displayName;

    Gender(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static Gender fromCode(int code) {
        for (Gender gender : values()) {
            if (gender.code == code) {
                return gender;
            }
        }
        return OTHER;
    }
}
