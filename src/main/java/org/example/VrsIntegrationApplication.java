package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VrsIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(VrsIntegrationApplication.class, args);
    }
}