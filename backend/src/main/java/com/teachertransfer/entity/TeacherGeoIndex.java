package com.teachertransfer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_geo_index", indexes = {
    @Index(name = "idx_geo6_match", columnList = "subject, school_type, geohash6"),
    @Index(name = "idx_current_geo6_match", columnList = "subject, school_type, current_geohash6"),
    @Index(name = "idx_geo5_match", columnList = "subject, school_type, geohash5"),
    @Index(name = "idx_current_geo5_match", columnList = "subject, school_type, current_geohash5"),
    @Index(name = "idx_geo_teacher", columnList = "teacher_id", unique = true),
    @Index(name = "idx_geo_premium", columnList = "is_premium, geohash6")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherGeoIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false, unique = true)
    private Long teacherId;

    @Column(name = "geohash6", nullable = false, length = 12)
    private String geohash6;

    @Column(name = "current_geohash6", length = 12)
    private String currentGeohash6;

    @Column(name = "geohash5", length = 10)
    private String geohash5;

    @Column(name = "current_geohash5", length = 10)
    private String currentGeohash5;

    @Column(name = "subject", nullable = false)
    private Integer subject;

    @Column(name = "school_type", nullable = false)
    private Integer schoolType;

    @Builder.Default
    @Column(name = "is_premium")
    private Boolean isPremium = false;

    @Column(name = "current_lat", nullable = false)
    private Double currentLat;

    @Column(name = "current_lng", nullable = false)
    private Double currentLng;

    @Column(name = "preferred_lat", nullable = false)
    private Double preferredLat;

    @Column(name = "preferred_lng", nullable = false)
    private Double preferredLng;

    @Column(name = "radius_km")
    private Integer radiusKm;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;
}
