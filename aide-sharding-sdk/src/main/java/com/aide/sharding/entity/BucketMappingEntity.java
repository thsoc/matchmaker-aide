package com.aide.sharding.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 哈希桶映射配置实体
 * @date 2026/5/31
 * @date 19:46
 */
@Data
@TableName("bucket_mapping")
public class BucketMappingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 桶起始编号
     */
    @TableField("bucket_start")
    private Integer bucketStart;

    /**
     * 桶结束编号
     */
    @TableField("bucket_end")
    private Integer bucketEnd;

    /**
     * 数据源名称（ds0, ds1, ds2...）
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 状态：0-停用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;
}
