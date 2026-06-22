package com.aide.domain.model.strategy;


import com.aide.adapter.dto.UserCouponRequest;
import com.aide.domain.model.UserCouponDo;

/**
 * @author mazg
 * @description 用户优惠券转换策略接口
 * @date 2026/6/21
 * @date 18:34
 */
public interface UserCouponConverterStrategy {
    /**
     * 将请求对象转换为领域对象
     * @param request 请求参数
     * @return 领域对象
     */
    UserCouponDo convert(UserCouponRequest request);
    /**
     * 获取优惠券类型
     * @return 优惠券类型
     */
    Integer getCouponType();
}
