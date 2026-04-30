package com.teachertransfer.dto.match;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teachertransfer.enums.MatchType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchResponse {

    private Long id;
    private Long teacherId;
    private TeacherInfo teacher;
    private MatchType matchType;
    private Double score;
    private Double distanceKm;
    private String matchReason;
    private Boolean isMutual;
    private LocalDateTime createdAt;

    public MatchResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public TeacherInfo getTeacher() { return teacher; }
    public void setTeacher(TeacherInfo teacher) { this.teacher = teacher; }

    public MatchType getMatchType() { return matchType; }
    public void setMatchType(MatchType matchType) { this.matchType = matchType; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public String getMatchReason() { return matchReason; }
    public void setMatchReason(String matchReason) { this.matchReason = matchReason; }

    public Boolean getIsMutual() { return isMutual; }
    public void setIsMutual(Boolean isMutual) { this.isMutual = isMutual; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class TeacherInfo {
        private Long id;
        private String name;
        private String subject;
        private String schoolType;
        private String approxArea;
        private Double distanceKm;
        private String schoolName;
        private String phone;
        private Boolean identityRevealed;

        public TeacherInfo() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getSchoolType() { return schoolType; }
        public void setSchoolType(String schoolType) { this.schoolType = schoolType; }

        public String getApproxArea() { return approxArea; }
        public void setApproxArea(String approxArea) { this.approxArea = approxArea; }

        public Double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

        public String getSchoolName() { return schoolName; }
        public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public Boolean getIdentityRevealed() { return identityRevealed; }
        public void setIdentityRevealed(Boolean identityRevealed) { this.identityRevealed = identityRevealed; }
    }
}
