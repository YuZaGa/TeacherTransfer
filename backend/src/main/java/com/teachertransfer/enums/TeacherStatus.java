package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Teacher status enumeration
 */
@Getter
public enum TeacherStatus {
    ACTIVE(1, "Active"),
    HOLD(2, "On Hold"),
    TRANSFERRED(3, "Transferred"),
    INACTIVE(4, "Inactive");

    private final int code;
    private final String displayName;

    TeacherStatus(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static TeacherStatus fromCode(int code) {
        for (TeacherStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return INACTIVE;
    }
}
