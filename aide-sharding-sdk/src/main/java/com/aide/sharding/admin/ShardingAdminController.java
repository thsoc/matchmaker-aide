package com.aide.sharding.admin;

import com.aide.sharding.algorithm.BucketShardingAlgorithm;
import com.aide.sharding.common.Result;
import com.aide.sharding.migration.DataMigrationService;
import com.aide.sharding.migration.MigrationTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author mazg
 * @description 分片管理控制器
 * @date 2026/5/31
 * @date 19:54
 */
@Slf4j
@RestController
@RequestMapping("/admin/sharding")
@RequiredArgsConstructor
public class ShardingAdminController {

    private final DataMigrationService migrationService;

    /**
     * 查看当前桶映射
     */
    @GetMapping("/mapping")
    public Result getBucketMapping() {
        Map<Integer, String> mapping = BucketShardingAlgorithm.getCurrentMapping();

        // 统计每个数据源的桶数量
        Map<String, Integer> dsCount = new HashMap<>();
        for (String ds : mapping.values()) {
            dsCount.merge(ds, 1, Integer::sum);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalBuckets", mapping.size());
        result.put("distribution", dsCount);
        result.put("mapping", mapping);

        return Result.success(result);
    }

    /**
     * 动态更新桶映射（在线扩容）
     *
     * 示例：POST /admin/sharding/update-mapping?startBucket=128&endBucket=191&dataSource=ds2
     */
    @PostMapping("/update-mapping")
    public Result updateBucketMapping(@RequestParam int startBucket,
                                      @RequestParam int endBucket,
                                      @RequestParam String dataSource) {
        try {
            if (startBucket < 0 || startBucket >= 256 || endBucket < 0 || endBucket >= 256) {
                return Result.error("桶编号必须在 0-255 范围内");
            }

            if (startBucket > endBucket) {
                return Result.error("起始桶不能大于结束桶");
            }

            BucketShardingAlgorithm.updateBucketMapping(startBucket, endBucket, dataSource);
            log.info("桶映射更新成功：[{}-{}] → {}", startBucket, endBucket, dataSource);

            return Result.success("映射更新成功，新请求将路由到 " + dataSource);

        } catch (Exception e) {
            log.error("桶映射更新失败", e);
            return Result.error("映射更新失败: " + e.getMessage());
        }
    }

    /**
     * 启动数据迁移任务
     *
     * 示例：POST /admin/sharding/migrate?startBucket=128&endBucket=191&fromDs=ds1&toDs=ds2
     */
    @PostMapping("/migrate")
    public Result startMigration(@RequestParam int startBucket,
                                 @RequestParam int endBucket,
                                 @RequestParam String fromDs,
                                 @RequestParam String toDs) {
        try {
            String taskId = UUID.randomUUID().toString();

            migrationService.migrateBuckets(taskId, startBucket, endBucket, fromDs, toDs);

            log.info("迁移任务已启动：{}", taskId);

            Map<String, String> result = new HashMap<>();
            result.put("taskId", taskId);
            result.put("message", "迁移任务已启动，请使用 taskId 查询进度");

            return Result.success(result);

        } catch (Exception e) {
            log.error("启动迁移任务失败", e);
            return Result.error("启动失败: " + e.getMessage());
        }
    }

    /**
     * 查询迁移任务状态
     *
     * 示例：GET /admin/sharding/migration-status?taskId=xxx
     */
    @GetMapping("/migration-status")
    public Result getMigrationStatus(@RequestParam String taskId) {
        MigrationTask task = migrationService.getTaskStatus(taskId);

        if (task == null) {
            return Result.error("任务不存在: " + taskId);
        }

        // 计算进度
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", task.getTaskId());
        result.put("status", task.getStatus());
        result.put("progress", task.getTotalCount() > 0 ?
                String.format("%.2f%%", (double) task.getMigratedCount() / task.getTotalCount() * 100) : "0%");
        result.put("migratedCount", task.getMigratedCount());
        result.put("totalCount", task.getTotalCount());
        result.put("currentBucket", task.getCurrentBucket());
        result.put("startBucket", task.getStartBucket());
        result.put("endBucket", task.getEndBucket());
        result.put("fromDs", task.getFromDs());
        result.put("toDs", task.getToDs());
        result.put("duration", task.getEndTime() > 0 ?
                (task.getEndTime() - task.getStartTime()) / 1000.0 + "s" : "running");

        if (task.getError() != null) {
            result.put("error", task.getError());
        }

        return Result.success(result);
    }

    /**
     * 重置为默认配置（测试用）
     */
    @PostMapping("/reset")
    public Result resetMapping() {
        try {
            BucketShardingAlgorithm.resetToDefault();
            return Result.success("已重置为默认配置");
        } catch (Exception e) {
            return Result.error("重置失败: " + e.getMessage());
        }
    }
}
