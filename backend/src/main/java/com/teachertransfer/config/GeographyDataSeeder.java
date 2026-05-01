package com.teachertransfer.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class GeographyDataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public GeographyDataSeeder(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM district", Integer.class);
        if (count != null && count > 0) return;

        ClassPathResource districtsResource = new ClassPathResource("geography/districts.json");
        List<Map<String, Object>> districts = objectMapper.readValue(
                districtsResource.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        for (Map<String, Object> d : districts) {
            jdbcTemplate.update(
                "INSERT INTO district (id, name, code, lat, lng) VALUES (?, ?, ?, ?, ?)",
                d.get("id"), d.get("name"), d.get("code"), d.get("lat"), d.get("lng")
            );
        }

        ClassPathResource blocksResource = new ClassPathResource("geography/blocks.json");
        List<Map<String, Object>> blocks = objectMapper.readValue(
                blocksResource.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        for (Map<String, Object> b : blocks) {
            jdbcTemplate.update(
                "INSERT INTO block (id, district_id, name, code, lat, lng) VALUES (?, ?, ?, ?, ?, ?)",
                b.get("id"), b.get("districtId"), b.get("name"), b.get("code"), b.get("lat"), b.get("lng")
            );
        }

        jdbcTemplate.execute("SELECT setval('district_id_seq', (SELECT MAX(id) FROM district))");
        jdbcTemplate.execute("SELECT setval('block_id_seq', (SELECT MAX(id) FROM block))");

        System.out.println("Seeded " + districts.size() + " districts and " + blocks.size() + " blocks.");
    }
}
