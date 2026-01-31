package com.teachertransfer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Block entity for Bihar geography
 */
@Entity
@Table(name = "block", indexes = {
    @Index(name = "idx_block_district", columnList = "district_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "district_id", nullable = false)
    private Integer districtId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "name_hindi", length = 100)
    private String nameHindi;

    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", insertable = false, updatable = false)
    private District district;
}
