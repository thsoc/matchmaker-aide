package com.springcloud;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/7
 * @date 22:28
 */

import com.aide.MoneyClientApp;
import com.aide.infrastructure.persistence.entity.RechargeRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import com.aide.infrastructure.persistence.mapper.RechargeRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(classes = MoneyClientApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MinimalTransactionTest {

    private static final Logger log = LoggerFactory.getLogger(MinimalTransactionTest.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RechargeRecordMapper mapper;

    @Test
    void testMinimalTransaction() {
        log.info("=== 最小化事务测试 ===");

        // 1. 创建最简单的事务管理器
        DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);

        // 2. 生成唯一测试数据
        String testOrderNo = "MIN_TEST_" + System.currentTimeMillis();

        // 3. 清理旧数据
        mapper.delete(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));

        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        try {
            Boolean result = txTemplate.execute(status -> {
                log.info("事务开始");

                // 插入记录1
                RechargeRecord r1 = new RechargeRecord();
                r1.setId(generateId());
                r1.setOrderNo(testOrderNo);
                r1.setUserId(1000L);
                r1.setAmount(new BigDecimal("100"));
                r1.setStatus(1);
                r1.setPayType(1);
                r1.setCreateTime(LocalDateTime.now());
                r1.setUpdateTime(LocalDateTime.now());

                int insert1 = mapper.insert(r1);
                log.info("插入1结果: {}", insert1);

                // 插入记录2（相同订单号）
                RechargeRecord r2 = new RechargeRecord();
                r2.setId(generateId());
                r2.setOrderNo(testOrderNo);  // 相同订单号！
                r2.setUserId(1000L);
                r2.setAmount(new BigDecimal("200"));
                r2.setStatus(1);
                r2.setPayType(1);
                r2.setCreateTime(LocalDateTime.now());
                r2.setUpdateTime(LocalDateTime.now());

                try {
                    int insert2 = mapper.insert(r2);
                    log.error("插入2成功: {} (不应该！)", insert2);
                    return false;
                } catch (DuplicateKeyException e) {
                    log.info("捕获DuplicateKeyException");
                    // 标记回滚
                    status.setRollbackOnly();
                    throw e;
                }
            });

            log.error("事务不应该成功: {}", result);

        } catch (Exception e) {
            log.info("事务异常: {}", e.getClass().getSimpleName());
        }

        // 检查结果
        int count = mapper.selectCount(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));
        if (count == 0) {
            log.info("✓ 事务回滚成功");
        } else {
            log.error("✗ 事务回滚失败，有{}条记录", count);
        }

        // 清理
        mapper.delete(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));
    }

    private Long generateId() {
        return System.currentTimeMillis() + Thread.currentThread().getId();
    }
}