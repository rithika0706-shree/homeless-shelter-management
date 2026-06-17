package com.tn.homeless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Homeless Shelter Management System.
 *
 * CHANGE from original:
 *  - Added @EnableScheduling so that EmailScheduler's @Scheduled cron job fires.
 *    Without this annotation Spring Boot ignores all @Scheduled methods.
 */
@SpringBootApplication
@EnableScheduling
public class HomelessShelterApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomelessShelterApplication.class, args);
    }
}
