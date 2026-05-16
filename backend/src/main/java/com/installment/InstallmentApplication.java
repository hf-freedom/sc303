package com.installment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InstallmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(InstallmentApplication.class, args);
    }
}
