package com.teachertransfer.entity;

import com.teachertransfer.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Payment transaction entity
 */
@Entity
@Table(name = "payment", indexes = {
    @Index(name = "idx_payment_teacher", columnList = "teacher_id"),
    @Index(name = "idx_payment_order_id", columnList = "razorpay_order_id", unique = true),
    @Index(name = "idx_payment_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "razorpay_order_id", length = 100, unique = true)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", length = 255)
    private String razorpaySignature;

    @Column(name = "amount_paise", nullable = false)
    private Integer amountPaise;

    @Builder.Default
    @Column(name = "currency", length = 3)
    private String currency = "INR";

    @Column(name = "plan", nullable = false, length = 20)
    private String plan;

    @Builder.Default
    @Column(name = "status", nullable = false)
    private Integer status = PaymentStatus.PENDING.getCode();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    // Helper methods
    public PaymentStatus getStatusEnum() {
        return PaymentStatus.fromCode(status);
    }

    public void setStatusEnum(PaymentStatus status) {
        this.status = status.getCode();
    }
}
