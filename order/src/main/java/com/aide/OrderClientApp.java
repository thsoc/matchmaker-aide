package com.aide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author mazg
 * @description 订单服务
 * @date 2026/5/28  15:53
 */
@SpringBootApplication
@MapperScan("com.aide.infrastructure.persistence.mapper")
@EnableAsync
public class OrderClientApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderClientApp.class, args);
    }
}
