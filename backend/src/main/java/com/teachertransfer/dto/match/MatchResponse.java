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
    private Integer hopCount;
    private LocalDateTime createdAt;

    public MatchResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public TeacherInfo getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherInfo teacher) {
        this.teacher = teacher;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public Integer getHopCount() {
        return hopCount;
    }

    public void setHopCount(Integer hopCount) {
        this.hopCount = hopCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class TeacherInfo {
        private Long id;
        private String name;
        private String subject;
        private String schoolType;
        private String currentDistrict;
        private String currentBlock;
        private String preferredDistrict;
        private String preferredBlock;

        public TeacherInfo() {}

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

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getSchoolType() {
            return schoolType;
        }

        public void setSchoolType(String schoolType) {
            this.schoolType = schoolType;
        }

        public String getCurrentDistrict() {
            return currentDistrict;
        }

        public void setCurrentDistrict(String currentDistrict) {
            this.currentDistrict = currentDistrict;
        }

        public String getCurrentBlock() {
            return currentBlock;
        }

        public void setCurrentBlock(String currentBlock) {
            this.currentBlock = currentBlock;
        }

        public String getPreferredDistrict() {
            return preferredDistrict;
        }

        public void setPreferredDistrict(String preferredDistrict) {
            this.preferredDistrict = preferredDistrict;
        }

        public String getPreferredBlock() {
            return preferredBlock;
        }

        public void setPreferredBlock(String preferredBlock) {
            this.preferredBlock = preferredBlock;
        }
    }
}