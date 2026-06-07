//package com.aide.config;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
///**
// * @author mazg
// * @description TODO
// * @date 2026/6/7
// * @date 21:30
// */
//@Component
//@Slf4j
//public class SafeConfigDebugger implements ApplicationRunner {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        log.info("=== 安全配置诊断（不使用反射）===");
//
//        // 1. 打印数据源类型
//        log.info("1. 数据源类型: {}", dataSource.getClass().getName());
//
//        // 2. 简单测试连接
//        testConnectionSimply();
//
//        // 3. 检查Hikari配置
//        checkHikariConfig();
//
//        // 4. 最重要的：测试实际的事务行为
//        testRealTransactionBehavior();
//    }
//
//    private void testConnectionSimply() throws SQLException {
//        log.info("2. 简单连接测试:");
//
//        for (int i = 0; i < 2; i++) {
//            try (Connection conn = dataSource.getConnection()) {
//                log.info("   连接{}: class={}, autoCommit={}",
//                        i, conn.getClass().getSimpleName(), conn.getAutoCommit());
//
//                // 检查MySQL服务器设置
//                try (Statement stmt = conn.createStatement();
//                     ResultSet rs = stmt.executeQuery("SELECT @@autocommit")) {
//                    if (rs.next()) {
//                        int mysqlAutoCommit = rs.getInt(1);
//                        log.info("   MySQL @@autocommit: {}", mysqlAutoCommit);
//                    }
//                }
//            }
//        }
//    }
//
//    private void checkHikariConfig() {
//        log.info("3. 检查Hikari配置（不依赖反射）:");
//
//        // 尝试获取实际的Hikari数据源
//        DataSource realDataSource = getRealDataSource(dataSource);
//
//        if (realDataSource instanceof HikariDataSource) {
//            HikariDataSource hikari = (HikariDataSource) realDataSource;
//            log.info("   找到Hikari数据源: {}", hikari.getPoolName());
//            log.info("   Hikari.autoCommit: {}", hikari.isAutoCommit());
//            log.info("   Hikari.connectionInitSql: {}", hikari.getConnectionInitSql());
//        } else {
//            log.info("   底层数据源不是HikariDataSource: {}",
//                    realDataSource.getClass().getName());
//        }
//    }
//
//    private DataSource getRealDataSource(DataSource dataSource) {
//        // 尝试解包装
//        try {
//            if (dataSource.isWrapperFor(DataSource.class)) {
//                DataSource unwrapped = dataSource.unwrap(DataSource.class);
//                if (unwrapped != dataSource) {
//                    return unwrapped;
//                }
//            }
//        } catch (SQLException e) {
//            log.warn("解包装失败: {}", e.getMessage());
//        }
//        return dataSource;
//    }
//
//    private void testRealTransactionBehavior() {
//        log.info("4. 测试实际的事务行为:");
//
//        // 创建独立的数据源进行测试
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl("jdbc:mysql://192.168.2.20:3306/matchmaker?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai");
//        config.setUsername("root");
//        config.setPassword("123456");
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//
//        // 测试两种配置
//        testWithAutoCommit(config, true, "autoCommit=true");
//        testWithAutoCommit(config, false, "autoCommit=false");
//    }
//
//    private void testWithAutoCommit(HikariConfig config, boolean autoCommit, String testName) {
//        log.info("   测试 {}:", testName);
//
//        config.setAutoCommit(autoCommit);
//        if (!autoCommit) {
//            config.setConnectionInitSql("SET autocommit=0");
//        }
//
//        try (HikariDataSource testDs = new HikariDataSource(config);
//             Connection conn = testDs.getConnection()) {
//
//            log.info("     配置autoCommit: {}", testDs.isAutoCommit());
//            log.info("     实际连接autoCommit: {}", conn.getAutoCommit());
//
//            // 测试事务
//            conn.setAutoCommit(false);
//
//            try (Statement stmt = conn.createStatement()) {
//                // 创建一个测试表
//                String tableName = "test_transaction_" + System.currentTimeMillis();
//
//                stmt.execute("CREATE TEMPORARY TABLE " + tableName + " (id INT)");
//                stmt.execute("INSERT INTO " + tableName + " VALUES (1)");
//
//                // 回滚
//                conn.rollback();
//
//                // 查询是否回滚成功
//                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
//                if (rs.next()) {
//                    int count = rs.getInt(1);
//                    log.info("     回滚后记录数: {} (应该为0)", count);
//                }
//
//                stmt.execute("DROP TEMPORARY TABLE " + tableName);
//            }
//
//        } catch (SQLException e) {
//            log.error("   测试失败: {}", e.getMessage());
//        }
//    }
//}