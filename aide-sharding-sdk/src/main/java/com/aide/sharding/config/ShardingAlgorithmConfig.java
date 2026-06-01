//package com.aide.sharding.config;
//
//import com.aide.sharding.algorithm.BucketShardingAlgorithm;
//import com.aide.sharding.entity.BucketMappingEntity;
//import com.aide.sharding.mapper.BucketMappingMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @description 分片算法配置
// * @author mazg
// * @date 2026/5/31
// * @date 20:41
// */
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class ShardingAlgorithmConfig {
//
//    private final BucketMappingMapper bucketMappingMapper;
//
//    /**
//     * 注册自定义分片算法 Bean
//     */
//    @Bean
//    public BucketShardingAlgorithm bucketShardingAlgorithm() {
//        return new BucketShardingAlgorithm();
//    }
//
//    /**
//     * 应用启动时初始化桶映射
//     */
//    @PostConstruct
//    public void initBucketMapping() {
//        try {
//            List<BucketMappingEntity> mappings =
//                    bucketMappingMapper.selectActiveMappings();
//
//            if (mappings == null || mappings.isEmpty()) {
//                log.warn("未找到桶映射配置，使用默认配置");
//                BucketShardingAlgorithm.resetToDefault();
//                return;
//            }
//
//            // 构建桶编号到数据源的映射
//            Map<Integer, String> bucketMap = new HashMap<>();
//            for (BucketMappingEntity mapping : mappings) {
//                for (int i = mapping.getBucketStart(); i <= mapping.getBucketEnd(); i++) {
//                    bucketMap.put(i, mapping.getDataSource());
//                }
//            }
//
//            BucketShardingAlgorithm.initMapping(bucketMap);
//            log.info("桶映射初始化成功，共 {} 个桶", bucketMap.size());
//
//        } catch (Exception e) {
//            log.error("桶映射初始化失败，使用默认配置", e);
//            BucketShardingAlgorithm.resetToDefault();
//        }
//    }
//}
