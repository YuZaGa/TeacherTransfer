package com.teachertransfer.dto.interest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teachertransfer.enums.InterestStatus;
import com.teachertransfer.enums.InterestType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterestResponse {

    private Long id;
    private Long fromTeacherId;
    private String fromTeacherName;
    private Long toTeacherId;
    private String toTeacherName;
    private InterestType type;
    private InterestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public InterestResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromTeacherId() {
        return fromTeacherId;
    }

    public void setFromTeacherId(Long fromTeacherId) {
        this.fromTeacherId = fromTeacherId;
    }

    public String getFromTeacherName() {
        return fromTeacherName;
    }

    public void setFromTeacherName(String fromTeacherName) {
        this.fromTeacherName = fromTeacherName;
    }

    public Long getToTeacherId() {
        return toTeacherId;
    }

    public void setToTeacherId(Long toTeacherId) {
        this.toTeacherId = toTeacherId;
    }

    public String getToTeacherName() {
        return toTeacherName;
    }

    public void setToTeacherName(String toTeacherName) {
        this.toTeacherName = toTeacherName;
    }

    public InterestType getType() {
        return type;
    }

    public void setType(InterestType type) {
        this.type = type;
    }

    public InterestStatus getStatus() {
        return status;
    }

    public void setStatus(InterestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}