package com.teachertransfer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Job run tracking entity for batch processing
 */
@Entity
@Table(name = "job_run", indexes = {
    @Index(name = "idx_job_run_type_status", columnList = "job_type, status"),
    @Index(name = "idx_job_run_started", columnList = "started_at")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_type", nullable = false, length = 50)
    private String jobType;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private String status = "RUNNING";

    @Column(name = "last_processed_teacher_id")
    private Long lastProcessedTeacherId;

    @Builder.Default
    @Column(name = "teachers_processed")
    private Integer teachersProcessed = 0;

    @Builder.Default
    @Column(name = "teachers_failed")
    private Integer teachersFailed = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public boolean isRunning() {
        return "RUNNING".equals(status);
    }

    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }

    public boolean isFailed() {
        return "FAILED".equals(status) || "TIMEOUT".equals(status);
    }

    public void markAsSuccess() {
        this.status = "SUCCESS";
        this.completedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public void markAsTimeout() {
        this.status = "TIMEOUT";
        this.errorMessage = "Job timed out";
        this.completedAt = LocalDateTime.now();
    }
}
