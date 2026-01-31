package com.teachertransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Teacher Transfer Platform
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class TeacherTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeacherTransferApplication.class, args);
    }
}
