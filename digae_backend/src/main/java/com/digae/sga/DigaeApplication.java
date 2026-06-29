package com.digae.sga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DigaeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigaeApplication.class, args);
    }
}