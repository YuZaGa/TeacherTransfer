package com.teachertransfer.repository;

import com.teachertransfer.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    List<MatchResult> findByTeacherIdOrderByScoreDesc(Long teacherId);

    List<MatchResult> findByTeacherId(Long teacherId);

    @Query("SELECT m FROM MatchResult m WHERE m.teacherId = :teacherId AND m.matchType = :type ORDER BY m.score DESC")
    List<MatchResult> findByTeacherIdAndMatchType(@Param("teacherId") Long teacherId,
                                                    @Param("type") Integer type);

    @Query("SELECT m FROM MatchResult m WHERE m.teacherId = :teacherId AND m.matchedTeacherId = :matchedTeacherId")
    Optional<MatchResult> findMatchBetween(@Param("teacherId") Long teacherId,
                                           @Param("matchedTeacherId") Long matchedTeacherId);

    @Query("SELECT m FROM MatchResult m WHERE m.teacherId = :teacherId AND m.matchType = :type " +
           "AND m.createdAt >= :since ORDER BY m.score DESC")
    List<MatchResult> findRecentMatches(@Param("teacherId") Long teacherId,
                                        @Param("type") Integer type,
                                        @Param("since") LocalDateTime since);

    @Query("SELECT m FROM MatchResult m WHERE m.createdAt < :date")
    List<MatchResult> findOldMatches(@Param("date") LocalDateTime date);
}