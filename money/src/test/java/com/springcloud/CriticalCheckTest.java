package com.springcloud;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/7
 * @date 22:19
 */

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;

@SpringBootTest
public class CriticalCheckTest {

    private static final Logger log = LoggerFactory.getLogger(CriticalCheckTest.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void checkCriticalPoints() {
        log.info("=== 关键点检查 ===");

        // 1. 检查是否有多个DataSource
        Map<String, DataSource> dataSources = applicationContext.getBeansOfType(DataSource.class);
        log.info("1. 找到 {} 个DataSource:", dataSources.size());
        dataSources.forEach((name, ds) -> {
            log.info("   {}: {}", name, ds.getClass().getName());
        });

        // 2. 检查事务管理器
        Map<String, PlatformTransactionManager> txManagers =
                applicationContext.getBeansOfType(PlatformTransactionManager.class);
        log.info("2. 找到 {} 个事务管理器:", txManagers.size());
        txManagers.forEach((name, manager) -> {
            log.info("   {}: {}", name, manager.getClass().getName());
        });

        // 3. 检查ShardingSphere版本
        checkShardingSphereVersion();
    }

    private void checkShardingSphereVersion() {
        try {
            Class<?> shardingClass = Class.forName("org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource");
            Package pkg = shardingClass.getPackage();
            log.info("3. ShardingSphere版本: {}", pkg.getImplementationVersion());
        } catch (ClassNotFoundException e) {
            log.info("3. 无法获取ShardingSphere版本");
        }
    }
}