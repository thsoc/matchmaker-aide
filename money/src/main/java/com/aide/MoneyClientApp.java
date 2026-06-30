package com.aide;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MoneyClientApp {
    public static void main(String[] args) {
        SpringApplication.run(MoneyClientApp.class, args);
    }
}
