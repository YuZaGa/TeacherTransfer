package com.teachertransfer.entity;

import com.teachertransfer.enums.MatchType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Cached match result entity
 */
@Entity
@Table(name = "match_result", indexes = {
    @Index(name = "idx_match_teacher", columnList = "teacher_id"),
    @Index(name = "idx_match_generated", columnList = "match_generated_at"),
    @Index(name = "idx_match_unique", columnList = "teacher_id, matched_teacher_id", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "matched_teacher_id", nullable = false)
    private Long matchedTeacherId;

    @Builder.Default
    @Column(name = "match_type", nullable = false)
    private Integer matchType = MatchType.DIRECT.getCode();

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Builder.Default
    @Column(name = "hop_count")
    private Integer hopCount = 1;

    @Column(name = "hop_chain", columnDefinition = "JSONB")
    private String hopChain;

    @Column(name = "score")
    private Double score;

    @Column(name = "match_reason")
    private String matchReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @CreatedDate
    @Column(name = "match_generated_at", nullable = false, updatable = false)
    private LocalDateTime matchGeneratedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_teacher_id", insertable = false, updatable = false)
    private Teacher matchedTeacher;

    // Helper methods
    public MatchType getMatchTypeEnum() {
        return MatchType.fromCode(matchType);
    }

    public void setMatchTypeEnum(MatchType matchType) {
        this.matchType = matchType.getCode();
    }
}
