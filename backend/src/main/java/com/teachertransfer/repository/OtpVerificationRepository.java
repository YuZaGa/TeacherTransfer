package com.teachertransfer.repository;

import com.teachertransfer.entity.OtpVerification;
import com.teachertransfer.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByPhoneAndPurposeOrderByCreatedAtDesc(String phone, Integer purpose);

    @Query("SELECT o FROM OtpVerification o WHERE o.phone = :phone AND o.purpose = :purpose " +
           "AND o.expiresAt > :now AND o.attempts < 3 AND o.verified = false " +
           "ORDER BY o.createdAt DESC")
    List<OtpVerification> findValidOtp(@Param("phone") String phone,
                                       @Param("purpose") Integer purpose,
                                       @Param("now") LocalDateTime now);

    @Query("SELECT o FROM OtpVerification o WHERE o.expiresAt < :now")
    List<OtpVerification> findExpiredOtps(@Param("now") LocalDateTime now);

    @Query("SELECT o FROM OtpVerification o WHERE o.createdAt < :date")
    List<OtpVerification> findOldOtps(@Param("date") LocalDateTime date);
}