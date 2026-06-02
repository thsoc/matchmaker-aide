//package com.aide.config;
//
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.codec.JsonJacksonCodec;
//import org.redisson.config.Config;
//import org.redisson.config.ReadMode;
//import org.redisson.config.SubscriptionMode;
//import org.redisson.connection.balancer.RoundRobinLoadBalancer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author mazg
// * @description redis集群配置
// * @date 2026/6/2
// * @date 16:16
// */
//@Configuration
//public class RedissonClusterConfig {
//
//    @Value("${spring.redis.cluster.nodes}")
//    private List<String> clusterNodes;
//
//    @Value("${spring.redis.password}")
//    private String password;
//
//    @Value("${spring.redis.timeout:3000}")
//    private int timeout;
//
//    @Bean(destroyMethod = "shutdown")
//    public RedissonClient redissonClient() {
//        Config config = new Config();
//
//        // 转换节点地址格式
//        List<String> nodeAddresses = clusterNodes.stream()
//                .map(node -> "redis://" + node)
//                .collect(Collectors.toList());
//
//        config.useClusterServers()
//                .addNodeAddress(nodeAddresses.toArray(new String[0]))
//                // 密码
//                .setPassword(password)
//                // 主节点连接池
//                .setMasterConnectionPoolSize(64)
//                .setMasterConnectionMinimumIdleSize(10)
//                // 从节点连接池
//                .setSlaveConnectionPoolSize(64)
//                .setSlaveConnectionMinimumIdleSize(10)
//                // 扫描间隔（毫秒）
//                .setScanInterval(2000)
//                // 重试配置
//                .setRetryAttempts(3)
//                .setRetryInterval(1500)
//                // 超时配置
//                .setIdleConnectionTimeout(10000)
//                .setConnectTimeout(timeout)
//                .setTimeout(timeout)
//                // 从节点读取模式
//                .setReadMode(ReadMode.SLAVE)
//                // 负载均衡算法
//                .setLoadBalancer(new RoundRobinLoadBalancer())
//                // 订阅相关
//                .setSubscriptionConnectionPoolSize(50)
//                .setSubscriptionMode(SubscriptionMode.MASTER)
//                // 失败节点重连间隔
//                .setFailedSlaveReconnectionInterval(3000)
//                .setFailedSlaveCheckInterval(180000);
//
//        // 序列化配置
//        config.setCodec(new JsonJacksonCodec());
//
//        return Redisson.create(config);
//    }
//}