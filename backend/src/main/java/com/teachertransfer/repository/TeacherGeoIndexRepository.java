package com.teachertransfer.repository;

import com.teachertransfer.entity.TeacherGeoIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TeacherGeoIndexRepository extends JpaRepository<TeacherGeoIndex, Long> {

    List<TeacherGeoIndex> findByTeacherId(Long teacherId);

    @Query("SELECT DISTINCT t FROM TeacherGeoIndex t WHERE " +
           "t.geohash6 IN :geohashes AND t.subject = :subject " +
           "AND t.schoolType = :schoolType AND t.teacherId != :excludeTeacherId")
    List<TeacherGeoIndex> findByGeohash6(@Param("geohashes") Set<String> geohashes,
                                          @Param("subject") Integer subject,
                                          @Param("schoolType") Integer schoolType,
                                          @Param("excludeTeacherId") Long excludeTeacherId);

    @Query("SELECT DISTINCT t FROM TeacherGeoIndex t WHERE " +
           "t.currentGeohash6 IN :geohashes AND t.subject = :subject " +
           "AND t.schoolType = :schoolType AND t.teacherId != :excludeTeacherId")
    List<TeacherGeoIndex> findByCurrentGeohash6(@Param("geohashes") Set<String> geohashes,
                                                 @Param("subject") Integer subject,
                                                 @Param("schoolType") Integer schoolType,
                                                 @Param("excludeTeacherId") Long excludeTeacherId);

    @Query("SELECT DISTINCT t FROM TeacherGeoIndex t WHERE " +
           "t.geohash5 IN :geohashes AND t.subject = :subject " +
           "AND t.schoolType = :schoolType AND t.teacherId != :excludeTeacherId")
    List<TeacherGeoIndex> findByGeohash5(@Param("geohashes") Set<String> geohashes,
                                          @Param("subject") Integer subject,
                                          @Param("schoolType") Integer schoolType,
                                          @Param("excludeTeacherId") Long excludeTeacherId);

    @Query("SELECT DISTINCT t FROM TeacherGeoIndex t WHERE " +
           "t.currentGeohash5 IN :geohashes AND t.subject = :subject " +
           "AND t.schoolType = :schoolType AND t.teacherId != :excludeTeacherId")
    List<TeacherGeoIndex> findByCurrentGeohash5(@Param("geohashes") Set<String> geohashes,
                                                 @Param("subject") Integer subject,
                                                 @Param("schoolType") Integer schoolType,
                                                 @Param("excludeTeacherId") Long excludeTeacherId);

    void deleteByTeacherId(Long teacherId);
}
