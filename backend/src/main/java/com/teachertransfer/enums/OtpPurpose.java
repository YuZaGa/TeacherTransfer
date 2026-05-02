package com.teachertransfer.enums;

import lombok.Getter;

/**
 * OTP purpose enumeration
 */
@Getter
public enum OtpPurpose {
    REGISTRATION(1, "Registration"),
    LOGIN(2, "Login"),
    PASSWORD_RESET(3, "Password Reset"),
    EMAIL_VERIFICATION(4, "Email Verification");

    private final int code;
    private final String displayName;

    OtpPurpose(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static OtpPurpose fromCode(int code) {
        for (OtpPurpose purpose : values()) {
            if (purpose.code == code) {
                return purpose;
            }
        }
        return REGISTRATION;
    }
}
