package com.aide.sharding.mapper;


import com.aide.sharding.entity.BucketMappingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BucketMappingMapper extends BaseMapper<BucketMappingEntity> {

    /**
     * 查询所有启用的桶映射
     */
    @Select("SELECT * FROM bucket_mapping WHERE status = 1 ORDER BY bucket_start")
    List<BucketMappingEntity> selectActiveMappings();

    /**
     * 根据桶编号查询数据源
     */
    @Select("SELECT data_source FROM bucket_mapping WHERE ? BETWEEN bucket_start AND bucket_end AND status = 1 LIMIT 1")
    String getDataSourceByBucket(@Param("bucketId") int bucketId);
}
