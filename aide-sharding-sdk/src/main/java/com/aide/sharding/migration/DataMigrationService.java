package com.aide.sharding.migration;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据迁移服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataMigrationService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 迁移任务状态
     */
    private static final Map<String, MigrationTask> TASK_STATUS = new ConcurrentHashMap<>();

    /**
     * 每批次迁移的记录数
     */
    private static final int BATCH_SIZE = 1000;

    /**
     * 异步执行数据迁移
     *
     * @param taskId      任务ID
     * @param startBucket 起始桶编号
     * @param endBucket   结束桶编号
     * @param fromDs      源数据源
     * @param toDs        目标数据源
     */
    @Async("migrationExecutor")
    public void migrateBuckets(String taskId, int startBucket, int endBucket,
                               String fromDs, String toDs) {

        MigrationTask task = new MigrationTask(taskId, startBucket, endBucket, fromDs, toDs);
        TASK_STATUS.put(taskId, task);

        log.info("开始迁移任务 {}，桶范围 [{}-{}]，从 {} 到 {}",
                taskId, startBucket, endBucket, fromDs, toDs);

        try {
            task.setStatus("RUNNING");
            task.setStartTime(System.currentTimeMillis());

            // 步骤1：统计需要迁移的数据量
            long totalCount = countMigratableRecords(startBucket, endBucket, fromDs);
            task.setTotalCount(totalCount);

            log.info("需要迁移的记录总数：{}", totalCount);

            if (totalCount == 0) {
                task.setStatus("COMPLETED");
                task.setEndTime(System.currentTimeMillis());
                return;
            }

            // 步骤2：分批迁移数据
            AtomicLong migratedCount = new AtomicLong(0);

            for (int bucketId = startBucket; bucketId <= endBucket; bucketId++) {
                int bucketMigrated = migrateBucketData(bucketId, fromDs, toDs);
                migratedCount.addAndGet(bucketMigrated);

                task.setMigratedCount(migratedCount.get());
                task.setCurrentBucket(bucketId);

                log.info("桶 {} 迁移完成，已迁移 {}/{}",
                        bucketId, migratedCount.get(), totalCount);
            }

            // 步骤3：验证数据一致性
            boolean verified = verifyMigration(startBucket, endBucket, fromDs, toDs);

            if (verified) {
                task.setStatus("VERIFIED");
                log.info("迁移任务 {} 完成并验证通过", taskId);
            } else {
                task.setStatus("FAILED_VERIFICATION");
                log.error("迁移任务 {} 验证失败", taskId);
            }

            task.setEndTime(System.currentTimeMillis());

        } catch (Exception e) {
            log.error("迁移任务 {} 失败", taskId, e);
            task.setStatus("FAILED");
            task.setError(e.getMessage());
            task.setEndTime(System.currentTimeMillis());
        }
    }

    /**
     * 迁移单个桶的数据
     */
    @Transactional
    public int migrateBucketData(int bucketId, String fromDs, String toDs) {
        String tableName = "aide_money";

        try {
            // 1. 查询源数据
            List<Map<String, Object>> records = jdbcTemplate.queryForList(
                    "SELECT * FROM " + fromDs + "." + tableName + " WHERE MOD(account, 256) = ?",
                    bucketId
            );

            if (records.isEmpty()) {
                log.debug("桶 {} 无数据需要迁移", bucketId);
                return 0;
            }

            // 2. 批量插入目标数据库
            for (Map<String, Object> record : records) {
                jdbcTemplate.update(
                        "INSERT INTO " + toDs + "." + tableName + " (id, account, money, create_time, update_time) " +
                                "VALUES (?, ?, ?, ?, ?)",
                        record.get("id"),
                        record.get("account"),
                        record.get("money"),
                        record.get("create_time"),
                        record.get("update_time")
                );
            }

            // 3. 删除源数据
            jdbcTemplate.update(
                    "DELETE FROM " + fromDs + "." + tableName + " WHERE MOD(account, 256) = ?",
                    bucketId
            );

            log.info("桶 {} 迁移完成，共 {} 条记录", bucketId, records.size());

            return records.size();

        } catch (Exception e) {
            log.error("桶 {} 迁移失败", bucketId, e);
            throw new RuntimeException("桶 " + bucketId + " 迁移失败", e);
        }
    }

    /**
     * 统计需要迁移的数据量
     */
    public long countMigratableRecords(int startBucket, int endBucket, String fromDs) {
        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(*) FROM " + fromDs + ".aide_money WHERE MOD(account, 256) IN ("
            );

            List<Object> params = new ArrayList<>();
            for (int i = startBucket; i <= endBucket; i++) {
                if (i > startBucket) {
                    sql.append(",");
                }
                sql.append("?");
                params.add(i);
            }
            sql.append(")");

            return jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        } catch (Exception e) {
            log.error("统计数据量失败", e);
            return 0;
        }
    }

    /**
     * 验证迁移数据一致性
     */
    public boolean verifyMigration(int startBucket, int endBucket, String fromDs, String toDs) {
        try {
            // 检查源数据库是否还有残留数据
            long remainingCount = countMigratableRecords(startBucket, endBucket, fromDs);
            if (remainingCount > 0) {
                log.error("源数据库仍有 {} 条记录未迁移", remainingCount);
                return false;
            }

            // 检查目标数据库是否有数据
            long targetCount = countMigratableRecords(startBucket, endBucket, toDs);
            log.info("目标数据库已有 {} 条记录", targetCount);

            return true;

        } catch (Exception e) {
            log.error("验证迁移失败", e);
            return false;
        }
    }

    /**
     * 获取迁移任务状态
     */
    public MigrationTask getTaskStatus(String taskId) {
        return TASK_STATUS.get(taskId);
    }
}
