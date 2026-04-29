package com.teachertransfer.repository;

import com.teachertransfer.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Integer> {

    List<Block> findByDistrictId(Integer districtId);

    List<Block> findByDistrictIdOrderByNameAsc(Integer districtId);

    Optional<Block> findByDistrictIdAndCode(Integer districtId, String code);

    @Query("SELECT b FROM Block b WHERE " +
           "(:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) OR " +
           "(:name IS NULL OR LOWER(b.nameHindi) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Block> searchByName(@Param("name") String name);

    @Query("SELECT b FROM Block b WHERE b.districtId = :districtId AND " +
           "(:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Block> searchByDistrictAndName(@Param("districtId") Integer districtId, @Param("name") String name);
}