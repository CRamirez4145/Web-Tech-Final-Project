package edu.tcu.cs.projectpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ProjectPulseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectPulseBackendApplication.class, args);
    }
}
