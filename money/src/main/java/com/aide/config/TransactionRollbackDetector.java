//package com.aide.config;
//
//import com.aide.infrastructure.persistence.entity.RechargeRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.TransactionDefinition;
//import org.springframework.transaction.TransactionStatus;
//import org.springframework.transaction.support.TransactionCallback;
//import org.springframework.transaction.support.TransactionTemplate;
//import com.aide.infrastructure.persistence.mapper.RechargeRecordMapper;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.env.Environment;
//import org.springframework.dao.DuplicateKeyException;
//
//import javax.sql.DataSource;
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//public class TransactionRollbackDetector implements ApplicationRunner {
//
//    private static final Logger log = LoggerFactory.getLogger(TransactionRollbackDetector.class);
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private RechargeRecordMapper mapper;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        log.info("=== 事务回滚探测器 ===");
//
//        // 1. 首先，直接测试MySQL连接
//        testMySQLDirectly();
//
//        // 2. 然后，测试Spring事务
//        testSpringTransactionDirectly();
//
//        // 3. 最后，追踪问题的根源
//        traceProblemRoot();
//    }
//
//    private void testMySQLDirectly() throws SQLException {
//        log.info("1. 直接MySQL连接测试:");
//
//        String testTable = "direct_test_" + System.currentTimeMillis();
//
//        try (Connection conn = dataSource.getConnection()) {
//            log.info("   连接类: {}", conn.getClass().getName());
//            log.info("   原始autoCommit: {}", conn.getAutoCommit());
//
//            // 保存原始状态
//            boolean originalAutoCommit = conn.getAutoCommit();
//
//            // 强制设置auto-commit=false
//            conn.setAutoCommit(false);
//            log.info("   设置后autoCommit: {}", conn.getAutoCommit());
//
//            try (Statement stmt = conn.createStatement()) {
//                // 创建临时表
//                stmt.execute("CREATE TEMPORARY TABLE " + testTable + " (id INT PRIMARY KEY, val VARCHAR(50))");
//                log.info("   创建临时表: {}", testTable);
//
//                // 第一次插入
//                stmt.execute("INSERT INTO " + testTable + " VALUES (1, 'test1')");
//                log.info("   第一次插入完成");
//
//                // 第二次插入（相同主键）
//                try {
//                    stmt.execute("INSERT INTO " + testTable + " VALUES (1, 'test2')"); // 相同主键
//                    log.error("   第二次插入不应该成功！");
//                } catch (SQLException e) {
//                    if (e.getMessage().contains("Duplicate entry") || e.getErrorCode() == 1062) {
//                        log.info("   捕获主键冲突异常: {}", e.getMessage());
//                    } else {
//                        throw e;
//                    }
//                }
//
//                // 回滚
//                conn.rollback();
//                log.info("   手动回滚完成");
//
//                // 验证回滚
//                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + testTable);
//                if (rs.next()) {
//                    int count = rs.getInt(1);
//                    if (count == 0) {
//                        log.info("   ✓ 直接MySQL回滚成功");
//                    } else {
//                        log.error("   ✗ 直接MySQL回滚失败，有{}条记录", count);
//
//                        // 查看具体数据
//                        ResultSet dataRs = stmt.executeQuery("SELECT * FROM " + testTable);
//                        while (dataRs.next()) {
//                            log.error("      残留数据: id={}, val={}",
//                                    dataRs.getInt(1), dataRs.getString(2));
//                        }
//                    }
//                }
//
//                // 清理
//                stmt.execute("DROP TEMPORARY TABLE " + testTable);
//
//            } finally {
//                // 恢复auto-commit
//                conn.setAutoCommit(originalAutoCommit);
//            }
//        }
//    }
//
//    private void testSpringTransactionDirectly() {
//        log.info("\n2. Spring事务直接测试:");
//
//        // 使用编程式事务，避免任何注解问题
//        PlatformTransactionManager txManager = new DataSourceTransactionManager(dataSource);
//
//        String testOrderNo = "ROLLBACK_TEST_" + System.currentTimeMillis();
//
//        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
//        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//
//        try {
//            Object result = txTemplate.execute(new TransactionCallback<Object>() {
//                @Override
//                public Object doInTransaction(TransactionStatus status) {
//                    log.info("   事务内部 - 开始");
//                    log.info("   事务状态: new={}, rollbackOnly={}",
//                            status.isNewTransaction(), status.isRollbackOnly());
//
//                    // 记录事务开始时间
//                    long startTime = System.currentTimeMillis();
//
//                    // 清理旧数据
//                    mapper.delete(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));
//
//                    // 第一次插入
//                    RechargeRecord r1 = createRecord(testOrderNo, 1);
//                    int result1 = mapper.insert(r1);
//                    log.info("   第一次插入结果: {}, 耗时: {}ms",
//                            result1, System.currentTimeMillis() - startTime);
//
//                    // 强制刷新
//                    flushMyBatis();
//
//                    // 第二次插入（相同订单号）
//                    RechargeRecord r2 = createRecord(testOrderNo, 2);
//
//                    try {
//                        int result2 = mapper.insert(r2);
//                        log.error("   第二次插入成功: {} (不应该！)", result2);
//                        return false;
//                    } catch (DuplicateKeyException e) {
//                        log.info("   捕获DuplicateKeyException: {}", e.getMessage());
//                        // 标记回滚
//                        status.setRollbackOnly();
//                        throw e;
//                    }
//                }
//            });
//
//            log.error("   事务不应该成功返回: {}", result);
//
//        } catch (Exception e) {
//            log.info("   事务异常: {}", e.getClass().getSimpleName());
//
//            // 检查是否真的回滚了
//            checkIfRollbackHappened(testOrderNo);
//        }
//    }
//
//    private void flushMyBatis() {
//        try {
//            SqlSessionFactory sqlSessionFactory = SpringContextUtil.getBean(SqlSessionFactory.class);
//            try (SqlSession session = sqlSessionFactory.openSession()) {
//                session.flushStatements();
//                log.info("   强制刷新MyBatis语句");
//            }
//        } catch (Exception e) {
//            log.warn("   刷新MyBatis失败: {}", e.getMessage());
//        }
//    }
//
//    private void checkIfRollbackHappened(String testOrderNo) {
//        log.info("\n3. 检查回滚结果:");
//
//        // 等待一下
//        try { Thread.sleep(300); } catch (InterruptedException e) {}
//
//        int count = mapper.selectCount(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));
//
//        if (count == 0) {
//            log.info("   ✓ Spring事务回滚成功");
//        } else {
//            log.error("   ✗ Spring事务回滚失败，有{}条记录", count);
//
//            // 查看具体是哪些记录
//            List<RechargeRecord> records = mapper.selectList(
//                    new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));
//
//            for (RechargeRecord r : records) {
//                log.error("      残留记录: id={}, 创建时间={}", r.getId(), r.getCreateTime());
//            }
//        }
//
//        // 清理
//        if (count > 0) {
//            int deleted = mapper.delete(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));
//            log.info("   清理{}条残留记录", deleted);
//        }
//    }
//
//    private void traceProblemRoot() {
//        log.info("\n4. 追踪问题根源:");
//
//        // 检查是否启用了ShardingSphere的XA事务
//        try {
//            Environment env = SpringContextUtil.getEnvironment();
//            String transactionType = env.getProperty("spring.shardingsphere.props.default-transaction-type", "LOCAL");
//            log.info("   ShardingSphere事务模式: {}", transactionType);
//
//            if ("XA".equalsIgnoreCase(transactionType)) {
//                log.error("   ⚠️ 你使用了XA事务！这可能导致事务行为异常");
//                log.error("   XA事务需要特殊的事务管理器，可能不与Spring事务兼容");
//            }
//
//        } catch (Exception e) {
//            log.warn("   无法获取事务配置: {}", e.getMessage());
//        }
//    }
//
//    private RechargeRecord createRecord(String orderNo, int seq) {
//        RechargeRecord record = new RechargeRecord();
//        record.setId(generateId());
//        record.setOrderNo(orderNo);
//        record.setUserId(1000L);
//        record.setAmount(new BigDecimal(seq * 100));
//        record.setStatus(1);
//        record.setPayType(1);
//        record.setCreateTime(LocalDateTime.now());
//        record.setUpdateTime(LocalDateTime.now());
//        return record;
//    }
//
//    private Long generateId() {
//        // 使用系统时间生成一个简单的ID
//        return System.currentTimeMillis() + Thread.currentThread().getId();
//    }
//}