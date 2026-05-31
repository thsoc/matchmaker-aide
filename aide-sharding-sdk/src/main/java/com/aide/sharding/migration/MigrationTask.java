package com.aide.sharding.migration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mazg
 * @description 数据迁移任务信息
 * @date 2026/5/31
 * @date 21:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MigrationTask {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 起始桶编号
     */
    private int startBucket;

    /**
     * 结束桶编号
     */
    private int endBucket;

    /**
     * 源数据源
     */
    private String fromDs;

    /**
     * 目标数据源
     */
    private String toDs;

    /**
     * 任务状态
     * PENDING - 等待中
     * RUNNING - 运行中
     * COMPLETED - 已完成
     * VERIFIED - 已验证
     * FAILED - 失败
     * FAILED_VERIFICATION - 验证失败
     */
    private String status;

    /**
     * 总记录数
     */
    private long totalCount;

    /**
     * 已迁移记录数
     */
    private long migratedCount;

    /**
     * 当前处理的桶编号
     */
    private int currentBucket;

    /**
     * 开始时间（毫秒时间戳）
     */
    private long startTime;

    /**
     * 结束时间（毫秒时间戳）
     */
    private long endTime;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 构造函数（初始化默认状态）
     */
    public MigrationTask(String taskId, int startBucket, int endBucket, String fromDs, String toDs) {
        this.taskId = taskId;
        this.startBucket = startBucket;
        this.endBucket = endBucket;
        this.fromDs = fromDs;
        this.toDs = toDs;
        this.status = "PENDING";
        this.totalCount = 0;
        this.migratedCount = 0;
        this.currentBucket = startBucket;
        this.startTime = 0;
        this.endTime = 0;
        this.error = null;
    }

    /**
     * 获取进度百分比
     */
    public double getProgress() {
        if (totalCount == 0) {
            return 0.0;
        }
        return (double) migratedCount / totalCount * 100;
    }

    /**
     * 获取耗时（秒）
     */
    public double getDurationSeconds() {
        if (startTime == 0) {
            return 0;
        }
        long endTime = this.endTime > 0 ? this.endTime : System.currentTimeMillis();
        return (endTime - startTime) / 1000.0;
    }
}
