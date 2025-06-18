package com.example.presidential_elections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PresidentialElectionsApplication {


    public static void main(String[] args) {
        SpringApplication.run(PresidentialElectionsApplication.class, args);
    }

}


