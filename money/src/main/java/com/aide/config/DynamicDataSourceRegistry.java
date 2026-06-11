package com.aide.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


/**
 * @author mazg
 * @description * 动态数据源注册中心
 * * 支持动态扩容和数据源自动发现
 * 暂不使用
 * @date 2026/6/10
 * @date 19:05
 */
//@Component
//@ConfigurationProperties(prefix = "sharding")
public class DynamicDataSourceRegistry {

    // 数据源配置映射
    private final Map<String, ShardConfig> shardConfigs = new ConcurrentHashMap<>();

    // 一致性哈希环
    private final SortedMap<Integer, String> consistentHashRing = new TreeMap<>();

    // 虚拟节点数
    private int virtualNodes = 160;    // 对应 virtual-nodes

    private String shardKey;

    private List<ShardConfig> nodes;   // 对应 nodes 列表

    // 分片列表
    private List<String> shardList = new CopyOnWriteArrayList<>();

    /**
     * 分片配置
     */
    public static class ShardConfig {
        private String id;
        private String url;
        private String username;
        private String password;
        private int weight = 1;
        private boolean active = true;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    /**
     * 初始化一致性哈希环
     */
    @PostConstruct
    public void initConsistentHashRing() {
        // 1. 构建分片配置映射
        for (ShardConfig config : nodes) {
            if (config != null && config.isActive()) {
                shardConfigs.put(config.getId(), config);
            }
        }
        rebuildConsistentHashRing();
    }

    /**
     * 重建一致性哈希环
     */
    public synchronized void rebuildConsistentHashRing() {
        consistentHashRing.clear();
        shardList.clear();

        // 只添加活跃的分片
        List<ShardConfig> activeShards = shardConfigs.values().stream()
                .filter(ShardConfig::isActive)
                .collect(Collectors.toList());

        for (ShardConfig shard : activeShards) {
            shardList.add(shard.getId());

            // 根据权重添加虚拟节点
            for (int i = 0; i < virtualNodes * shard.getWeight(); i++) {
                String virtualNodeKey = shard.getId() + "#VN" + i;
                int hash = getHash(virtualNodeKey);
                consistentHashRing.put(hash, shard.getId());
            }
        }
    }

    /**
     * 根据分片键获取目标分片
     */
    public String getShardForKey(String key) {
        if (consistentHashRing.isEmpty()) {
            throw new IllegalStateException("一致性哈希环为空");
        }

        int hash = getHash(key);
        SortedMap<Integer, String> tailMap = consistentHashRing.tailMap(hash);

        if (tailMap.isEmpty()) {
            // 返回环的第一个节点
            return consistentHashRing.get(consistentHashRing.firstKey());
        }

        return tailMap.get(tailMap.firstKey());
    }

    /**
     * 计算哈希值
     */
    private int getHash(String key) {
        return Math.abs(key.hashCode());
    }

    /**
     * 添加分片
     */
    public synchronized void addShard(ShardConfig shardConfig) {
        shardConfigs.put(shardConfig.getId(), shardConfig);
        rebuildConsistentHashRing();
    }

    /**
     * 移除分片
     */
    public synchronized void removeShard(String shardId) {
        ShardConfig config = shardConfigs.get(shardId);
        if (config != null) {
            config.setActive(false);
            rebuildConsistentHashRing();
        }
    }

    /**
     * 获取分片配置
     */
    public ShardConfig getShardConfig(String shardId) {
        return shardConfigs.get(shardId);
    }

    /**
     * 获取所有活跃分片
     */
    public List<String> getActiveShards() {
        return new ArrayList<>(shardList);
    }

    /**
     * 检查分片是否存在
     */
    public boolean containsShard(String shardId) {
        return shardConfigs.containsKey(shardId);
    }

    /**
     * 获取分片数量
     */
    public int getShardCount() {
        return shardConfigs.size();
    }

    /**
     * 获取活跃分片数量
     */
    public int getActiveShardCount() {
        return (int) shardConfigs.values().stream()
                .filter(ShardConfig::isActive)
                .count();
    }

    /**
     * 获取一致性哈希环状态
     */
    public String getConsistentHashRingStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("一致性哈希环状态：\n");
        sb.append("总节点数：").append(consistentHashRing.size()).append("\n");
        sb.append("物理分片：").append(String.join(", ", shardList)).append("\n");

        // 显示哈希环分布
        sb.append("哈希环分布：\n");
        for (Map.Entry<Integer, String> entry : consistentHashRing.entrySet()) {
            sb.append("  Hash: ").append(entry.getKey())
                    .append(" -> Shard: ").append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }

//    /**
//     * 测试一致性哈希
//     */
//    public static void main(String[] args) {
//        DynamicDataSourceRegistry registry = new DynamicDataSourceRegistry();
//        registry.setVirtualNodes(3);  // 测试用，减少虚拟节点
//
//        // 添加测试分片
//        ShardConfig shard1 = new ShardConfig();
//        shard1.setId("shard_0");
//        shard1.setWeight(1);
//        shard1.setActive(true);
//
//        ShardConfig shard2 = new ShardConfig();
//        shard2.setId("shard_1");
//        shard2.setWeight(2);  // 权重更高
//        shard2.setActive(true);
//
//        registry.addShard(shard1);
//        registry.addShard(shard2);
//
//        // 测试路由
//        System.out.println("=== 一致性哈希测试 ===");
//        System.out.println("分片列表: " + registry.getActiveShards());
//        System.out.println("\n路由测试：");
//
//        String[] testKeys = {"user_1001", "user_1002", "user_1003", "order_1001", "order_1002"};
//        for (String key : testKeys) {
//            String shard = registry.getShardForKey(key);
//            System.out.println("Key: " + key + " -> Shard: " + shard);
//        }
//
//        // 添加新分片
//        System.out.println("\n=== 动态添加分片 ===");
//        ShardConfig shard3 = new ShardConfig();
//        shard3.setId("shard_2");
//        shard3.setWeight(1);
//        shard3.setActive(true);
//        registry.addShard(shard3);
//
//        System.out.println("新分片列表: " + registry.getActiveShards());
//        System.out.println("\n路由测试（新增分片后）：");
//        for (String key : testKeys) {
//            String shard = registry.getShardForKey(key);
//            System.out.println("Key: " + key + " -> Shard: " + shard);
//        }
//
//        // 查看哈希环状态
//        System.out.println("\n" + registry.getConsistentHashRingStatus());
//    }

    // Setter for properties
    public void setVirtualNodes(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    /**
     * 打印配置信息
     */
    private void printConfiguration() {
        System.out.println("=== 动态分片配置信息 ===");
        System.out.println("分片键: " + shardKey);
        System.out.println("虚拟节点数: " + virtualNodes);
        System.out.println("真实节点数: " + nodes.size());
        System.out.println("活跃节点数: " + getActiveShards().size());
        System.out.println("哈希环大小: " + consistentHashRing.size());

        for (ShardConfig config : nodes) {
            System.out.println("  - 分片: " + config.getId() +
                    ", 权重: " + config.getWeight() +
                    ", 状态: " + (config.isActive() ? "活跃" : "不活跃"));
        }
    }

}
