package com.teachertransfer.dto.onboarding;

import com.teachertransfer.enums.Gender;
import com.teachertransfer.enums.SchoolType;
import com.teachertransfer.enums.Subject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OnboardingRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter a valid 10-digit Indian mobile number")
    private String phone;

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

    private Double currentLat;
    private Double currentLng;

    @NotNull(message = "Preferred district ID is required")
    private Integer preferredDistrictId;

    private Integer preferredBlockId;

    private Double preferredLat;
    private Double preferredLng;

    @jakarta.validation.constraints.Min(value = 5, message = "Radius must be at least 5 km")
    @jakarta.validation.constraints.Max(value = 200, message = "Radius must not exceed 200 km")
    private Integer radiusKm = 30;

    public OnboardingRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

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
}
