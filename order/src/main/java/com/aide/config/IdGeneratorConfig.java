package com.aide.config;

import com.aide.util.ImprovedSnowflakeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mazg
 * @description 注入雪花ID生成器
 * @date 2026/6/20
 * @date 19:37
 */
@Configuration
public class IdGeneratorConfig {

    @Value("${app.worker-id:1}") // 从配置文件读取机器ID，防止多节点冲突
    private long workerId;

    @Bean
    public ImprovedSnowflakeGenerator improvedSnowflakeGenerator() {
        return new ImprovedSnowflakeGenerator(workerId);
    }
}