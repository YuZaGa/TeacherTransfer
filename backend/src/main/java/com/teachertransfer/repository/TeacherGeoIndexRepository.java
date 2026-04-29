package com.teachertransfer.repository;

import com.teachertransfer.entity.TeacherGeoIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherGeoIndexRepository extends JpaRepository<TeacherGeoIndex, Long> {

    List<TeacherGeoIndex> findByTeacherId(Long teacherId);

    @Query("SELECT t FROM TeacherGeoIndex t WHERE t.geohash LIKE :prefix")
    List<TeacherGeoIndex> findByGeohashPrefix(@Param("prefix") String prefix);

    @Query("SELECT t FROM TeacherGeoIndex t WHERE t.geohash LIKE :prefix AND t.teacherId != :excludeTeacherId")
    List<TeacherGeoIndex> findByGeohashPrefixExcluding(@Param("prefix") String prefix,
                                                         @Param("excludeTeacherId") Long excludeTeacherId);

    @Query("SELECT t FROM TeacherGeoIndex t WHERE t.geohash LIKE :prefix AND t.locationType = :locationType")
    List<TeacherGeoIndex> findByGeohashPrefixAndLocationType(@Param("prefix") String prefix,
                                                               @Param("locationType") String locationType);

    @Query("SELECT t FROM TeacherGeoIndex t WHERE t.geohash LIKE :prefix AND t.locationType = :locationType " +
           "AND t.teacherId != :excludeTeacherId")
    List<TeacherGeoIndex> findByGeohashPrefixAndLocationTypeExcluding(@Param("prefix") String prefix,
                                                                       @Param("locationType") String locationType,
                                                                       @Param("excludeTeacherId") Long excludeTeacherId);

    void deleteByTeacherId(Long teacherId);
}