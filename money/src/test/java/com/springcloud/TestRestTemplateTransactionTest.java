package com.springcloud;

import com.aide.MoneyClientApp;
import com.aide.infrastructure.persistence.entity.RechargeRecord;
import com.aide.infrastructure.persistence.mapper.RechargeRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.transaction.TransactionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/7
 * @date 22:51
 */
@Slf4j
@SpringBootTest(classes = MoneyClientApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestRestTemplateTransactionTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RechargeRecordMapper mapper;

    @Test
    void diagnoseTestRestTemplateIssue() {
        log.info("=== TestRestTemplate事务诊断 ===");

        String testOrderNo = "REST_TEMPLATE_" + System.currentTimeMillis();

        // 1. 清理
        mapper.delete(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));

        // 2. 创建测试请求
        Map<String, Object> request = new HashMap<>();
//        request.put("orderNo", testOrderNo);
        request.put("userId", 1000L);
        request.put("amount", 100.00);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // 3. 调用接口
        try {
            // 调用你的Controller接口
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/money/recharge",  // 请提供你的实际接口路径
                    entity,
                    String.class
            );

            log.info("HTTP响应状态: {}", response.getStatusCode());
            log.info("HTTP响应体: {}", response.getBody());

        } catch (Exception e) {
            log.info("HTTP调用异常: {}", e.getClass().getSimpleName());
            log.info("异常信息: {}", e.getMessage());

            // 检查是否是事务异常
            if (e.getCause() instanceof TransactionException) {
                log.error("⚠️ 发现事务异常！");
            }
        }

        // 4. 等待事务完成
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 5. 检查数据库
        int count = mapper.selectCount(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));

        if (count == 0) {
            log.info("✓ TestRestTemplate调用：事务回滚成功");
        } else {
            log.error("✗ TestRestTemplate调用：事务回滚失败，有{}条记录", count);

            // 查看具体是哪些记录
            List<RechargeRecord> records = mapper.selectList(
                    new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));

            records.forEach(r -> {
                log.error("   残留记录: id={}, 创建时间={}", r.getId(), r.getCreateTime());
            });
        }

        // 6. 清理
        mapper.delete(new QueryWrapper<RechargeRecord>().eq("order_no", testOrderNo));
    }
}
