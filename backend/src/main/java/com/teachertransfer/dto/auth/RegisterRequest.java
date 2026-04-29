package com.teachertransfer.dto.auth;

import com.teachertransfer.enums.Gender;
import com.teachertransfer.enums.SchoolType;
import com.teachertransfer.enums.Subject;
import jakarta.validation.constraints.*;

public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter a valid 10-digit Indian mobile number")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Size(max = 50, message = "Employee ID must not exceed 50 characters")
    private String employeeId;

    @Size(max = 20, message = "UDISE code must not exceed 20 characters")
    private String udiseCode;

    @NotBlank(message = "School name is required")
    @Size(max = 200, message = "School name must not exceed 200 characters")
    private String schoolName;

    @NotNull(message = "Subject is required")
    private Subject subject;

    @NotNull(message = "School type is required")
    private SchoolType schoolType;

    @NotNull(message = "Current district ID is required")
    private Integer currentDistrictId;

    @NotNull(message = "Current block ID is required")
    private Integer currentBlockId;

    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private Double currentLat;

    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private Double currentLng;

    @NotNull(message = "Preferred district ID is required")
    private Integer preferredDistrictId;

    private Integer preferredBlockId;

    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private Double preferredLat;

    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private Double preferredLng;

    @Min(value = 5, message = "Radius must be at least 5 km")
    @Max(value = 200, message = "Radius must not exceed 200 km")
    private Integer radiusKm = 30;

    private String referralCode;

    // Google auth fields (passed through from Google sign-in)
    private String googleId;
    private String profilePictureUrl;

    public RegisterRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getUdiseCode() { return udiseCode; }
    public void setUdiseCode(String udiseCode) { this.udiseCode = udiseCode; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public SchoolType getSchoolType() { return schoolType; }
    public void setSchoolType(SchoolType schoolType) { this.schoolType = schoolType; }

    public Integer getCurrentDistrictId() { return currentDistrictId; }
    public void setCurrentDistrictId(Integer currentDistrictId) { this.currentDistrictId = currentDistrictId; }

    public Integer getCurrentBlockId() { return currentBlockId; }
    public void setCurrentBlockId(Integer currentBlockId) { this.currentBlockId = currentBlockId; }

    public Double getCurrentLat() { return currentLat; }
    public void setCurrentLat(Double currentLat) { this.currentLat = currentLat; }

    public Double getCurrentLng() { return currentLng; }
    public void setCurrentLng(Double currentLng) { this.currentLng = currentLng; }

    public Integer getPreferredDistrictId() { return preferredDistrictId; }
    public void setPreferredDistrictId(Integer preferredDistrictId) { this.preferredDistrictId = preferredDistrictId; }

    public Integer getPreferredBlockId() { return preferredBlockId; }
    public void setPreferredBlockId(Integer preferredBlockId) { this.preferredBlockId = preferredBlockId; }

    public Double getPreferredLat() { return preferredLat; }
    public void setPreferredLat(Double preferredLat) { this.preferredLat = preferredLat; }

    public Double getPreferredLng() { return preferredLng; }
    public void setPreferredLng(Double preferredLng) { this.preferredLng = preferredLng; }

    public Integer getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Integer radiusKm) { this.radiusKm = radiusKm; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}