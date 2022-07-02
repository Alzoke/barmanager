package com.example.barmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BarmanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarmanagerApplication.class, args);
    }

}
