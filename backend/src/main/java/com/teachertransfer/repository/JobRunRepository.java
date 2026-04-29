package com.teachertransfer.repository;

import com.teachertransfer.entity.JobRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRunRepository extends JpaRepository<JobRun, Long> {

    Optional<JobRun> findTopByJobTypeOrderByStartedAtDesc(String jobType);

    @Query("SELECT j FROM JobRun j WHERE j.jobType = :jobType AND j.status = 'RUNNING'")
    Optional<JobRun> findRunningJob(@Param("jobType") String jobType);

    @Query("SELECT j FROM JobRun j WHERE j.jobType = :jobType AND j.startedAt >= :since")
    List<JobRun> findRecentJobRuns(@Param("jobType") String jobType,
                                   @Param("since") LocalDateTime since);

    @Query("SELECT j FROM JobRun j WHERE j.startedAt < :date")
    List<JobRun> findOldJobRuns(@Param("date") LocalDateTime date);
}