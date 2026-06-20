package com.aide.util;

/**
 * @author mazg
 * @description 雪花生成器
 * @date 2026/6/20
 * @date 19:34
 */
public class ImprovedSnowflakeGenerator {

    // ==================== 核心位分配配置 ====================
    private final long workerIdBits = 10L;      // 机器ID占10位，最多支持 1024 个节点
    private final long sequenceBits = 12L;      // 序列号占12位，单毫秒最多 4096 个并发
    private final long maxWorkerId = ~(-1L << workerIdBits); // 机器ID最大值 1023

    // ==================== 位移偏移量 ====================
    private final long workerIdShift = sequenceBits;           // 机器ID左移12位
    private final long timestampLeftShift = sequenceBits + workerIdBits; // 时间戳左移22位

    // ==================== 掩码与初始值 ====================
    private final long sequenceMask = ~(-1L << sequenceBits);  // 序列号掩码 (4095)
    private final long twepoch = 1609459200000L;               // 自定义起始时间戳 (2021-01-01 00:00:00)

    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 构造方法
     * @param workerId 机器ID (0 ~ 1023)
     */
    public ImprovedSnowflakeGenerator(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worker ID 必须在 0 到 " + maxWorkerId + " 之间");
        }
        this.workerId = workerId;
    }

    /**
     * 核心方法：生成带业务前缀的订单号
     * @param bizType 2位业务线标识，如 "AP"
     * @return 20位订单号，如：AP7136000000000000001
     */
    public synchronized String generateOrderNo(String bizType) {
        if (bizType == null || bizType.length() != 2) {
            throw new IllegalArgumentException("业务线标识必须为2位字符");
        }

        long snowflakeId = nextId();
        return bizType.toUpperCase() + snowflakeId;
    }

    /**
     * 获取纯数字的改进版雪花ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        // 1. 时钟回拨保护
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                // 允许5毫秒内的轻微回拨，短暂自旋等待
                try {
                    Thread.sleep(offset << 1);
                    timestamp = System.currentTimeMillis();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException("时钟回拨异常，拒绝生成ID");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("时钟回拨等待被中断", e);
                }
            } else {
                throw new RuntimeException("时钟回拨异常，拒绝生成ID。回拨时间: " + offset + "ms");
            }
        }

        // 2. 处理同一毫秒内的并发
        if (timestamp == lastTimestamp) {
            //取模运算
            sequence = (sequence + 1) & sequenceMask;
            // 当前毫秒序列号用尽，等待下一毫秒
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 新的一毫秒，序列号归零
            sequence = 0L;
        }

        // 3. 记录上次时间戳
        lastTimestamp = timestamp;

        // 4. 位运算拼接最终的 Long 型 ID
        return ((timestamp - twepoch) << timestampLeftShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /**
     * 阻塞等待直到下一毫秒
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}