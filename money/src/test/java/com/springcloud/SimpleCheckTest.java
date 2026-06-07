package com.springcloud;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/7
 * @date 22:27
 */

import com.aide.MoneyClientApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

@SpringBootTest(classes = MoneyClientApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleCheckTest {

    private static final Logger log = LoggerFactory.getLogger(SimpleCheckTest.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Test
    void checkDataSourceAndTransaction() throws Exception {
        log.info("=== 简单检查 ===");

        // 1. 检查DataSource数量
        Map<String, DataSource> dataSources = applicationContext.getBeansOfType(DataSource.class);
        log.info("1. DataSource数量: {}", dataSources.size());
        dataSources.forEach((name, ds) -> {
            log.info("   {} -> {}", name, ds.getClass().getName());
        });

        // 2. 检查事务管理器
        Map<String, PlatformTransactionManager> txManagers =
                applicationContext.getBeansOfType(PlatformTransactionManager.class);
        log.info("2. 事务管理器数量: {}", txManagers.size());
        txManagers.forEach((name, manager) -> {
            log.info("   {} -> {}", name, manager.getClass().getName());
        });

        // 3. 简单测试连接
        log.info("3. 连接测试:");
        try (Connection conn = dataSource.getConnection()) {
            log.info("   连接类: {}", conn.getClass().getName());
            log.info("   autoCommit: {}", conn.getAutoCommit());

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                if (rs.next()) {
                    log.info("   连接测试成功");
                }
            }
        }
    }
}
