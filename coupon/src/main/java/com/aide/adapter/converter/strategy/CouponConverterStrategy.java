package com.aide.adapter.converter.strategy;


import com.aide.adapter.VO.CouponRequest;
import com.aide.domain.model.CouponDo;

/**
 * @author mazg
 * @description 优惠券转换策略接口
 * @date 2026/6/21
 * @date 18:34
 */
public interface CouponConverterStrategy {
    /**
     * 将请求对象转换为领域对象
     * @param request 请求参数
     * @return 领域对象
     */
    CouponDo convert(CouponRequest request);
    /**
     * 获取优惠券类型
     * @return 优惠券类型
     */
    Integer getCouponType();
}
