package com.teachertransfer.entity;

import com.teachertransfer.enums.OtpPurpose;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * OTP verification entity
 */
@Entity
@Table(name = "otp_verification", indexes = {
    @Index(name = "idx_otp_phone", columnList = "phone, purpose"),
    @Index(name = "idx_otp_email", columnList = "email, purpose")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "otp_hash", nullable = false)
    private String otpHash;

    @Builder.Default
    @Column(name = "purpose", nullable = false)
    private Integer purpose = OtpPurpose.REGISTRATION.getCode();

    @Builder.Default
    @Column(name = "attempts")
    private Integer attempts = 0;

    @Builder.Default
    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public OtpPurpose getPurposeEnum() {
        return OtpPurpose.fromCode(purpose);
    }

    public void setPurposeEnum(OtpPurpose purpose) {
        this.purpose = purpose.getCode();
    }
}
