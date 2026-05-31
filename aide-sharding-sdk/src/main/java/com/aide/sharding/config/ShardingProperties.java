package com.aide.sharding.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author mazg
 * @description sharding配置属性
 * @date 2026/5/31
 * @date 20:33
 */

@Data
@ConfigurationProperties(prefix = "sharding.bucket")
public class ShardingProperties {

    /**
     * 是否启用分片
     */
    private boolean enabled = false;

    /**
     * 桶数量（默认256）
     */
    private int bucketCount = 256;

    /**
     * 分片键字段名
     */
    private String shardingColumn = "account";

    /**
     * 是否启用数据迁移接口
     */
    private boolean enableMigrationApi = true;
}
