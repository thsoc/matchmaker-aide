package com.aide.domain.model.strategy;


import com.aide.adapter.dto.UserCouponRequest;
import com.aide.domain.model.UserCouponDo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 用固话代金券转换器 (couponDiscountType = 2)
 * @date 2026/6/21
 * @date 18:36
 */
@Component
public class UserCashCouponConverter implements UserCouponConverterStrategy {

    @Override
    public UserCouponDo convert(UserCouponRequest request) {
        return UserCouponDo.builder()
                .couponName(request.getCouponName())
//                .effectiveTime(request.getEffectiveTime())
//                .expireTime(request.getExpireTime())
                .couponDiscountType(2) // 明确指定为代金券
//                .amount(request.getCashCouponAmount()) // 代金券的 amount 字段存储面额
                .conditionAmount(BigDecimal.ZERO) // 代金券通常无门槛，或门槛为0
                .maxDiscount(null) // 代金券没有折扣上限
//                .description(request.getDescription())
                .build();
    }

    @Override
    public Integer getCouponType() {
            return 2;
    }
}