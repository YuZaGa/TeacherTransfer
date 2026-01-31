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

    @Column(name = "type", nullable = false)
    private Integer type = InterestType.ONE_WAY.getCode();

    @Column(name = "status", nullable = false)
    private Integer status = InterestStatus.PENDING.getCode();

    @Column(name = "message")
    private String message;

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
}
