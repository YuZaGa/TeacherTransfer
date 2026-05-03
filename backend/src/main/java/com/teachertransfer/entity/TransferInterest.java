package com.teachertransfer.entity;

import com.teachertransfer.enums.InterestStatus;
import com.teachertransfer.enums.InterestType;
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
 * Transfer interest/ping entity
 */
@Entity
@Table(name = "transfer_interest", indexes = {
    @Index(name = "idx_interest_from", columnList = "from_teacher"),
    @Index(name = "idx_interest_to", columnList = "to_teacher"),
    @Index(name = "idx_interest_status", columnList = "status"),
    @Index(name = "idx_interest_unique", columnList = "from_teacher, to_teacher", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_teacher", nullable = false)
    private Long fromTeacher;

    @Column(name = "to_teacher", nullable = false)
    private Long toTeacher;

    @Builder.Default
    @Column(name = "type", nullable = false)
    private Integer type = InterestType.ONE_WAY.getCode();

    @Builder.Default
    @Column(name = "status", nullable = false)
    private Integer status = InterestStatus.PENDING.getCode();

    @Column(name = "message")
    private String message;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Builder.Default
    @Column(name = "is_outdated")
    private Boolean isOutdated = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_teacher", insertable = false, updatable = false)
    private Teacher fromTeacherEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_teacher", insertable = false, updatable = false)
    private Teacher toTeacherEntity;

    // Helper methods
    public InterestType getTypeEnum() {
        return InterestType.fromCode(type);
    }

    public InterestStatus getStatusEnum() {
        return InterestStatus.fromCode(status);
    }

    public void setTypeEnum(InterestType type) {
        this.type = type.getCode();
    }

    public void setStatusEnum(InterestStatus status) {
        this.status = status.getCode();
    }

    // Alias getters/setters for service compatibility
    public Long getFromTeacherId() {
        return fromTeacher;
    }

    public void setFromTeacherId(Long fromTeacherId) {
        this.fromTeacher = fromTeacherId;
    }

    public Long getToTeacherId() {
        return toTeacher;
    }

    public void setToTeacherId(Long toTeacherId) {
        this.toTeacher = toTeacherId;
    }
}
