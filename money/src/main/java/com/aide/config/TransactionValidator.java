package com.aide.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/8
 * @date 18:10
 */
@Component
@Slf4j
public class TransactionValidator {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void validateTransactionSetup() {
        log.info("=== 验证事务配置 ===");

        // 1. 检查事务管理器
        log.info("1. 事务管理器: {}", transactionManager.getClass().getName());

        // 2. 检查数据源
        try {
            Connection conn = dataSource.getConnection();
            log.info("2. 数据源: {}", conn.getClass().getName());
            log.info("   AutoCommit: {}", conn.getAutoCommit());
            conn.close();
        } catch (SQLException e) {
            log.error("获取连接失败", e);
        }

        // 3. 检查事务是否活跃
        boolean active = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("3. 当前是否有活动事务: {}", active);

        // 4. 测试事务
        testTransaction();
    }

    private void testTransaction() {
        log.info("4. 测试事务功能");

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        try {
            String result = txTemplate.execute(status -> {
                log.info("事务内部 - 是否新事务: {}", status.isNewTransaction());
                throw new RuntimeException("测试回滚");
            });
        } catch (Exception e) {
            log.info("事务测试完成: {}", e.getMessage());
        }
    }
}