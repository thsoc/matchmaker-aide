package com.aide.domain.repository;


import com.aide.domain.model.CouponDo;

/**
 * @author mazg
 * @description 订单仓储接口 - 定义领域对象的持久化契约
 * @date 13:28 2026/6/14
 * @return 
 **/
public interface CouponRepository {

    /**
     * 创建订单
     * @param orderDo 订单领域对象
     * @return 订单ID
     */
    String createOrder(CouponDo orderDo);
}
