//package com.aide.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.datasource.DataSourceUtils;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.PostConstruct;
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//
///**
// * @author mazg
// * @description TODO
// * @date 2026/6/7
// * @date 20:50
// */
//@Component
//@Slf4j
//public class TransactionTest {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @PostConstruct
//    public void testAutoCommit() throws SQLException {
//        // 测试1：直接获取连接的auto-commit状态
//        try (Connection conn = dataSource.getConnection()) {
//            boolean autoCommit = conn.getAutoCommit();
//            log.info("数据库连接默认auto-commit: {}", autoCommit);
//
//            // 测试2：在事务中测试
//            conn.setAutoCommit(false);  // 手动关闭自动提交
//            log.info("手动设置auto-commit为false");
//
//            // 执行测试SQL
//            Statement stmt = conn.createStatement();
//            stmt.execute("SELECT 1");
//
//            // 查看当前事务状态
//            log.info("事务是否活跃: {}", !conn.getAutoCommit());
//
//            // 回滚
//            conn.rollback();
//        }
//    }
//
//    @Transactional
//    public void testSpringTransaction() throws SQLException {
//        // 在Spring事务中获取连接
//        Connection conn = DataSourceUtils.getConnection(dataSource);
//        try {
//            log.info("Spring事务中auto-commit: {}", conn.getAutoCommit());
//            log.info("Spring事务中事务隔离级别: {}", conn.getTransactionIsolation());
//        } finally {
//            DataSourceUtils.releaseConnection(conn, dataSource);
//        }
//    }
//}