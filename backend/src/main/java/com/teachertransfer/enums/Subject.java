package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Subject enumeration for Bihar government schools
 */
@Getter
public enum Subject {
    HINDI(1, "Hindi"),
    ENGLISH(2, "English"),
    MATHEMATICS(3, "Mathematics"),
    SCIENCE(4, "Science"),
    SOCIAL_SCIENCE(5, "Social Science"),
    SANSKRIT(6, "Sanskrit"),
    URDU(7, "Urdu"),
    PHYSICAL_EDUCATION(8, "Physical Education"),
    ART_CRAFT(9, "Art & Craft"),
    MUSIC(10, "Music"),
    COMPUTER(11, "Computer"),
    PRIMARY_TEACHER(12, "Primary Teacher"),
    OTHER(99, "Other");

    private final int code;
    private final String displayName;

    Subject(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static Subject fromCode(int code) {
        for (Subject subject : values()) {
            if (subject.code == code) {
                return subject;
            }
        }
        return OTHER;
    }
}
