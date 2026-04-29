package com.teachertransfer.repository;

import com.teachertransfer.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {

    Optional<District> findByCode(String code);

    List<District> findAllByOrderByName();

    @Query("SELECT d FROM District d WHERE " +
           "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) OR " +
           "(:name IS NULL OR LOWER(d.nameHindi) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<District> searchByName(@Param("name") String name);

    @Query(value = "SELECT * FROM district WHERE " +
           "ST_DWithin(ST_MakePoint(lng, lat)::geography, " +
           "ST_MakePoint(:lng, :lat)::geography, :radius * 1000)", nativeQuery = true)
    List<District> findNearby(@Param("lat") double lat, @Param("lng") double lng, @Param("radius") double radius);
}