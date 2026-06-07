package com.aide.sharding.algorithm;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;


/**
 * @author mazg
 * 基于哈希桶的分片算法
 *
 * <p>核心思想：</p>
 * <pre>
 * 1. user_id % 256 = 桶编号（固定256个桶）
 * 2. 桶编号 → 物理数据库（可动态调整映射关系）
 * </pre>
 *
 * <p>优势：</p>
 * <ul>
 *   <li>扩容时只迁移部分桶的数据（非全部）</li>
 *   <li>支持在线热更新映射关系，无需重启</li>
 *   <li>可以逐个桶灰度迁移，风险可控</li>
 * </ul>
 * @date 2026/5/31
 * @date 20:39
 */
@Slf4j
public class BucketShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    /**
     * 固定桶数量（2的幂次方，便于取模运算优化）
     */
    private static final int BUCKET_COUNT = 256;

    /**
     * 桶与数据源的映射关系缓存
     * Key: 桶编号 (0-255)
     * Value: 数据源名称 (ds0, ds1, ds2...)
     */
    private static final ConcurrentHashMap<Integer, String> BUCKET_MAPPING_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认数据源（当映射未初始化时使用）
     */
    private static volatile String DEFAULT_DATA_SOURCE = "ds0";

    /**
     * 是否已初始化
     */
    private static volatile boolean initialized = false;

    @Override
    public String doSharding(Collection<String> availableTargetNames,
                             PreciseShardingValue<Long> shardingValue) {
        // 确保已初始化
        ensureInitialized();

        //获取
        Object keyId = shardingValue.getValue();

        // 第一层：计算桶编号
        int bucketId = Math.abs((int) (keyId.hashCode() % BUCKET_COUNT));

        // 第二层：获取桶对应的数据源
        String dataSource = BUCKET_MAPPING_CACHE.get(bucketId);

        if (dataSource == null) {
            log.warn("桶 {} 未找到映射，使用默认数据源 {}", bucketId, DEFAULT_DATA_SOURCE);
            dataSource = DEFAULT_DATA_SOURCE;
        }

        log.debug("分片键 {} → 桶 {} → 数据源 {}", keyId, bucketId, dataSource);

        return dataSource;
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        return collection;
    }

    /**
     * 确保映射已初始化（懒加载）
     */
    private void ensureInitialized() {
        if (!initialized) {
            synchronized (BucketShardingAlgorithm.class) {
                if (!initialized) {
                    initializeFromDatabase();
                    initialized = true;
                }
            }
        }
    }

    /**
     * 从数据库加载桶映射配置
     */
    private void initializeFromDatabase() {
        try {
            // 注意：这里需要通过 Spring 上下文获取 Mapper
            // 实际使用时通过 initMapping() 方法注入
            log.info("哈希桶映射初始化完成，共 {} 个桶", BUCKET_COUNT);
        } catch (Exception e) {
            log.error("哈希桶映射初始化失败", e);
        }
    }

    /**
     * 初始化桶映射（由 Spring 容器调用）
     *
     * @param mappings 桶映射列表
     */
    public static void initMapping(Map<Integer, String> mappings) {
        BUCKET_MAPPING_CACHE.clear();

        for (Map.Entry<Integer, String> entry : mappings.entrySet()) {
            BUCKET_MAPPING_CACHE.put(entry.getKey(), entry.getValue());
        }

        log.info("桶映射初始化成功，共 {} 个映射", mappings.size());
    }

    /**
     * 动态更新桶映射（支持在线扩容）
     *
     * @param startBucket 起始桶编号
     * @param endBucket   结束桶编号
     * @param dataSource  目标数据源
     */
    public static void updateBucketMapping(int startBucket, int endBucket, String dataSource) {
        for (int i = startBucket; i <= endBucket; i++) {
            BUCKET_MAPPING_CACHE.put(i, dataSource);
        }
        log.info("✅ 桶映射更新成功：[{}-{}] → {}", startBucket, endBucket, dataSource);
    }

    /**
     * 批量更新桶映射
     *
     * @param mappings 新的映射关系
     */
    public static void updateBatchMapping(Map<Integer, String> mappings) {
        BUCKET_MAPPING_CACHE.putAll(mappings);
        log.info("✅ 批量更新桶映射成功，共 {} 个映射", mappings.size());
    }

    /**
     * 获取当前桶映射快照
     */
    public static Map<Integer, String> getCurrentMapping() {
        return new ConcurrentHashMap<>(BUCKET_MAPPING_CACHE);
    }

    /**
     * 重置为默认配置（用于测试）
     */
    public static void resetToDefault() {
        BUCKET_MAPPING_CACHE.clear();
//        for (int i = 0; i < BUCKET_COUNT; i++) {
//            if (i < 128) {
//                BUCKET_MAPPING_CACHE.put(i, "ds0");
//            } else {
//                BUCKET_MAPPING_CACHE.put(i, "ds1");
//            }
//        }
        IntStream.range(0, BUCKET_COUNT)
                .forEach(index -> {
                    BUCKET_MAPPING_CACHE.put(index, "ds0");
                    System.out.println("处理第 " + index + " 个任务，索引: " + index);
                });
        DEFAULT_DATA_SOURCE = "ds0";
        log.info("桶映射已重置为默认配置");
    }

    @Override
    public String getType() {
        return "Cluster";
    }
}
