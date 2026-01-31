package com.teachertransfer.entity;

import com.teachertransfer.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Teacher entity representing a registered teacher on the platform
 */
@Entity
@Table(name = "teacher", indexes = {
    @Index(name = "idx_teacher_phone", columnList = "phone"),
    @Index(name = "idx_teacher_status", columnList = "status"),
    @Index(name = "idx_teacher_subscription", columnList = "subscription_status"),
    @Index(name = "idx_teacher_last_interaction", columnList = "last_interaction_at"),
    @Index(name = "idx_teacher_subject_school", columnList = "subject, school_type"),
    @Index(name = "idx_teacher_referral_code", columnList = "referral_code")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Personal Info
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false, unique = true, length = 15)
    private String phone;

    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "gender")
    private Integer gender;

    // Professional Info
    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @Column(name = "udise_code", length = 20)
    private String udiseCode;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "subject", nullable = false)
    private Integer subject;

    @Column(name = "school_type", nullable = false)
    private Integer schoolType;

    // Current Posting Location
    @Column(name = "current_district_id", nullable = false)
    private Integer currentDistrictId;

    @Column(name = "current_block_id", nullable = false)
    private Integer currentBlockId;

    @Column(name = "current_lat", nullable = false)
    private Double currentLat;

    @Column(name = "current_lng", nullable = false)
    private Double currentLng;

    // Preferred Transfer Location
    @Column(name = "preferred_district_id")
    private Integer preferredDistrictId;

    @Column(name = "preferred_block_id")
    private Integer preferredBlockId;

    @Column(name = "preferred_lat", nullable = false)
    private Double preferredLat;

    @Column(name = "preferred_lng", nullable = false)
    private Double preferredLng;

    @Column(name = "preferred_school_ids")
    private Long[] preferredSchoolIds;

    @Column(name = "radius_km")
    private Integer radiusKm = 30;

    // Status & Activity
    @Column(name = "status")
    private Integer status = TeacherStatus.ACTIVE.getCode();

    @Column(name = "last_interaction_at")
    private LocalDateTime lastInteractionAt = LocalDateTime.now();

    @Column(name = "profile_updated_at")
    private LocalDateTime profileUpdatedAt = LocalDateTime.now();

    // Subscription
    @Column(name = "subscription_status")
    private Integer subscriptionStatus = SubscriptionStatus.FREE.getCode();

    @Column(name = "subscription_plan", length = 20)
    private String subscriptionPlan;

    @Column(name = "subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    // Referral System
    @Column(name = "referral_code", length = 10, unique = true)
    private String referralCode;

    @Column(name = "referred_by")
    private Long referredBy;

    @Column(name = "referral_count")
    private Integer referralCount = 0;

    // Timestamps
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_by", insertable = false, updatable = false)
    private Teacher referrer;

    // Helper methods
    public boolean isActive() {
        return TeacherStatus.fromCode(status) == TeacherStatus.ACTIVE;
    }

    public boolean isPaidActive() {
        return SubscriptionStatus.fromCode(subscriptionStatus).isPaidActive();
    }

    public boolean isPremium() {
        return isPaidActive() && "PREMIUM".equals(subscriptionPlan);
    }

    public Subject getSubjectEnum() {
        return Subject.fromCode(subject);
    }

    public SchoolType getSchoolTypeEnum() {
        return SchoolType.fromCode(schoolType);
    }

    public TeacherStatus getStatusEnum() {
        return TeacherStatus.fromCode(status);
    }

    public Gender getGenderEnum() {
        return Gender.fromCode(gender);
    }

    public void setSubjectEnum(Subject subject) {
        this.subject = subject.getCode();
    }

    public void setSchoolTypeEnum(SchoolType schoolType) {
        this.schoolType = schoolType.getCode();
    }

    public void setStatusEnum(TeacherStatus status) {
        this.status = status.getCode();
    }

    public void setGenderEnum(Gender gender) {
        this.gender = gender.getCode();
    }
}
