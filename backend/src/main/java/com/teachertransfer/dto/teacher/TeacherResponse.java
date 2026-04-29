package com.teachertransfer.dto.teacher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teachertransfer.enums.Gender;
import com.teachertransfer.enums.SchoolType;
import com.teachertransfer.enums.Subject;
import com.teachertransfer.enums.SubscriptionStatus;
import com.teachertransfer.enums.TeacherStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private Boolean phoneVerified;
    private Boolean emailVerified;
    private Gender gender;
    private String employeeId;
    private String udiseCode;
    private String schoolName;
    private Subject subject;
    private SchoolType schoolType;
    private LocationInfo currentLocation;
    private LocationInfo preferredLocation;
    private Integer radiusKm;
    private TeacherStatus status;
    private SubscriptionStatus subscriptionStatus;
    private String subscriptionPlan;
    private LocalDateTime subscriptionExpiresAt;
    private String referralCode;
    private Integer referralCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public TeacherResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUdiseCode() {
        return udiseCode;
    }

    public void setUdiseCode(String udiseCode) {
        this.udiseCode = udiseCode;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public SchoolType getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(SchoolType schoolType) {
        this.schoolType = schoolType;
    }

    public LocationInfo getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationInfo currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LocationInfo getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(LocationInfo preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public Integer getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(Integer radiusKm) {
        this.radiusKm = radiusKm;
    }

    public TeacherStatus getStatus() {
        return status;
    }

    public void setStatus(TeacherStatus status) {
        this.status = status;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public LocalDateTime getSubscriptionExpiresAt() {
        return subscriptionExpiresAt;
    }

    public void setSubscriptionExpiresAt(LocalDateTime subscriptionExpiresAt) {
        this.subscriptionExpiresAt = subscriptionExpiresAt;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Integer getReferralCount() {
        return referralCount;
    }

    public void setReferralCount(Integer referralCount) {
        this.referralCount = referralCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public static class LocationInfo {
        private Integer districtId;
        private String districtName;
        private Integer blockId;
        private String blockName;
        private Double lat;
        private Double lng;

        public LocationInfo() {}

        public LocationInfo(Integer districtId, String districtName, Integer blockId, String blockName, Double lat, Double lng) {
            this.districtId = districtId;
            this.districtName = districtName;
            this.blockId = blockId;
            this.blockName = blockName;
            this.lat = lat;
            this.lng = lng;
        }

        // Getters and Setters
        public Integer getDistrictId() {
            return districtId;
        }

        public void setDistrictId(Integer districtId) {
            this.districtId = districtId;
        }

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public Integer getBlockId() {
            return blockId;
        }

        public void setBlockId(Integer blockId) {
            this.blockId = blockId;
        }

        public String getBlockName() {
            return blockName;
        }

        public void setBlockName(String blockName) {
            this.blockName = blockName;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }
}