package com.springcloud.aide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author mazg
 * @description 积分服务
 * @date 2026/5/28  15:53
 */
@SpringBootApplication
@MapperScan("com.aide.infrastructure.persistence.mapper")
public class PointsClientApp {
    public static void main(String[] args) {
        SpringApplication.run(PointsClientApp.class, args);
    }
}
