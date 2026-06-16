package com.aide.sharding.config;


import com.aide.sharding.algorithm.BucketShardingAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * 分片 SDK 自动配置
 *
 * 使用方式：
 * 1. 在 application.yml 中配置 sharding.bucket.enabled=true
 * 2. 配置 spring.shardingsphere.datasource.*
 * 3. SDK 自动初始化桶映射（无需手动包扫描）
 * @author mazg
 * @date 2026/5/31
 * @date 21:20
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
@ConditionalOnProperty(name = "sharding.bucket.enabled", havingValue = "true")
@ComponentScan(basePackages = "com.aide.sharding")
public class ShardingAutoConfiguration {

    @Autowired(required = false)
    private BucketMappingLoader customLoader;

    /**
     * 注册分片算法 Bean
     */
    @Bean
    public BucketShardingAlgorithm bucketShardingAlgorithm() {
        return new BucketShardingAlgorithm();
    }

    /**
     * 初始化桶映射（支持多种加载方式）
     */
    @PostConstruct
    public void initBucketMapping() {
        Map<Integer, String> bucketMap;

        // 优先级1：使用自定义加载器
        if (customLoader != null) {
            log.info("使用自定义加载器初始化桶映射");
            bucketMap = customLoader.loadMappings();
        }
        // 优先级2：从默认配置加载
        else {
            log.info("使用默认配置初始化桶映射");
            bucketMap = loadFromDefaultConfig();
        }

        BucketShardingAlgorithm.initMapping(bucketMap);
        log.info("桶映射初始化成功，共 {} 个桶", bucketMap.size());
    }

    /**
     * 从默认配置加载（简化版：硬编码或从环境变量读取）
     */
    private Map<Integer, String> loadFromDefaultConfig() {
        Map<Integer, String> mapping = new HashMap<>();

        // 默认配置：256个桶，均匀分布到 ds0 和 ds1
        for (int i = 0; i < 256; i++) {
//            if (i < 128) {
//                mapping.put(i, "ds0");
//            } else {
//                mapping.put(i, "ds1");
//            }
            //方便测试
            mapping.put(i, "ds0");

        }

        return mapping;
    }

    /**
     * 桶映射加载器接口（可选实现）
     * 1.查询自己业务模块桶映射数据库，前提是不要走sharding数据源配置
     * 2.放在内存中
     */
    public interface BucketMappingLoader {
        Map<Integer, String> loadMappings();
    }
}
