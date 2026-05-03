package com.teachertransfer.repository;

import com.teachertransfer.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);

    @Query("SELECT n FROM Notification n WHERE n.teacherId = :teacherId AND n.read = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.teacherId = :teacherId AND n.read = false")
    long countUnreadByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT n FROM Notification n WHERE n.createdAt < :date")
    List<Notification> findOldNotifications(@Param("date") LocalDateTime date);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.teacherId = :teacherId")
    void deleteByTeacherId(@Param("teacherId") Long teacherId);
}