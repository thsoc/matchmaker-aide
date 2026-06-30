package com.aide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author mazg
 * @description 用户服务启动类
 * @date 17:41 2026/5/14
 * @return
 **/
@SpringBootApplication
@MapperScan("com.aide.infrastructure.persistence.mapper")
@EnableAsync
public class UserClientApp {
    public static void main(String[] args) {
        SpringApplication.run(UserClientApp.class, args);
    }
}
