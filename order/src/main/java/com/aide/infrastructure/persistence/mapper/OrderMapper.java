package com.aide.infrastructure.persistence.mapper;

import com.aide.infrastructure.persistence.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mazg
 * @description 订单 MAP
 * @date 2026/6/14
 * @date 16:39
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
