package com.aide.infrastructure.persistence.mapper;

import com.aide.infrastructure.persistence.entity.Points;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mazg
 * @description 积分持久层接口
 * @date 2026/6/14
 * @date 21:17
 */
@Mapper
public interface PointsMapper extends BaseMapper<Points> {
}
