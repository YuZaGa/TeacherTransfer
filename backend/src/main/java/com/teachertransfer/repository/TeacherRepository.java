package com.teachertransfer.repository;

import com.teachertransfer.entity.Teacher;
import com.teachertransfer.enums.TeacherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByPhone(String phone);

    Optional<Teacher> findByEmail(String email);

    Optional<Teacher> findByGoogleId(String googleId);

    Optional<Teacher> findByReferralCode(String referralCode);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByReferralCode(String referralCode);

    @Query("SELECT t FROM Teacher t WHERE t.status = :status")
    List<Teacher> findByStatus(@Param("status") Integer status);

    @Query("SELECT t FROM Teacher t WHERE t.status = 1")
    List<Teacher> findActiveSubscribers();

    @Query("SELECT t FROM Teacher t WHERE t.subscriptionExpiresAt < CURRENT_TIMESTAMP AND t.subscriptionStatus = 1")
    List<Teacher> findExpiredSubscriptions();

    @Query("SELECT t FROM Teacher t WHERE t.lastLoginAt < :date")
    List<Teacher> findInactiveTeachers(@Param("date") java.time.LocalDateTime date);
}