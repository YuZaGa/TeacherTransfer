package com.teachertransfer.repository;

import com.teachertransfer.entity.TransferInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferInterestRepository extends JpaRepository<TransferInterest, Long> {

    @Query("SELECT i FROM TransferInterest i WHERE i.fromTeacher = :fromTeacherId ORDER BY i.createdAt DESC")
    List<TransferInterest> findByFromTeacherIdOrderByCreatedAtDesc(@Param("fromTeacherId") Long fromTeacherId);

    @Query("SELECT i FROM TransferInterest i WHERE i.toTeacher = :toTeacherId ORDER BY i.createdAt DESC")
    List<TransferInterest> findByToTeacherIdOrderByCreatedAtDesc(@Param("toTeacherId") Long toTeacherId);

    @Query("SELECT i FROM TransferInterest i WHERE i.fromTeacher = :teacherId AND i.status = :status")
    List<TransferInterest> findByFromTeacherIdAndStatus(@Param("teacherId") Long teacherId,
                                                        @Param("status") Integer status);

    @Query("SELECT i FROM TransferInterest i WHERE i.toTeacher = :teacherId AND i.status = :status")
    List<TransferInterest> findByToTeacherIdAndStatus(@Param("teacherId") Long teacherId,
                                                      @Param("status") Integer status);

    @Query("SELECT i FROM TransferInterest i WHERE i.fromTeacher = :teacherId AND i.toTeacher = :otherTeacherId")
    Optional<TransferInterest> findExistingInterest(@Param("teacherId") Long teacherId,
                                                    @Param("otherTeacherId") Long otherTeacherId);

    @Query("SELECT i FROM TransferInterest i WHERE " +
           "((i.fromTeacher = :teacher1 AND i.toTeacher = :teacher2) OR " +
           "(i.fromTeacher = :teacher2 AND i.toTeacher = :teacher1)) AND " +
           "i.status = 1")
    List<TransferInterest> findMutualInterests(@Param("teacher1") Long teacher1,
                                                @Param("teacher2") Long teacher2);

    @Query("SELECT i FROM TransferInterest i WHERE i.type = :type AND i.status = 1")
    List<TransferInterest> findByTypeAndStatus(@Param("type") Integer type);

    @Query("SELECT i FROM TransferInterest i WHERE i.status = 0 AND i.createdAt < :date")
    List<TransferInterest> findExpiredPendingInterests(@Param("date") java.time.LocalDateTime date);
}