package com.teachertransfer.dto.notification;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String type;
    private Boolean read;
    private Long relatedTeacherId;
    private String relatedTeacherName;
    private Long relatedInterestId;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Long getRelatedTeacherId() {
        return relatedTeacherId;
    }

    public void setRelatedTeacherId(Long relatedTeacherId) {
        this.relatedTeacherId = relatedTeacherId;
    }

    public String getRelatedTeacherName() {
        return relatedTeacherName;
    }

    public void setRelatedTeacherName(String relatedTeacherName) {
        this.relatedTeacherName = relatedTeacherName;
    }

    public Long getRelatedInterestId() {
        return relatedInterestId;
    }

    public void setRelatedInterestId(Long relatedInterestId) {
        this.relatedInterestId = relatedInterestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}