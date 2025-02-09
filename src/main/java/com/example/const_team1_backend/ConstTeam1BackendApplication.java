package com.example.const_team1_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication (exclude = SecurityAutoConfiguration.class)
public class ConstTeam1BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConstTeam1BackendApplication.class, args);
    }

}
