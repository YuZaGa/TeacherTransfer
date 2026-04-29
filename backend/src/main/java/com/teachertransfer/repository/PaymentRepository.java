package com.teachertransfer.repository;

import com.teachertransfer.entity.Payment;
import com.teachertransfer.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<Payment> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);

    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.completedAt IS NULL")
    List<Payment> findPendingPayments(@Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.createdAt < :date AND p.status = 0")
    List<Payment> findExpiredPendingPayments(@Param("date") LocalDateTime date);

    @Query("SELECT p FROM Payment p WHERE p.teacherId = :teacherId AND p.status = 1 " +
           "AND p.completedAt >= :startDate")
    List<Payment> findSuccessfulPaymentsSince(@Param("teacherId") Long teacherId,
                                               @Param("startDate") LocalDateTime startDate);
}