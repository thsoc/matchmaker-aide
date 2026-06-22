package com.aide.domain.model.strategy;


import com.aide.adapter.dto.UserCouponRequest;
import com.aide.domain.model.UserCouponDo;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 折扣券转换器 (couponDiscountType = 0)
 * @date 2026/6/21
 * @date 18:35
 */
@Component
public class UserDiscountCouponConverter implements UserCouponConverterStrategy {

    @Override
    public UserCouponDo convert(UserCouponRequest request) {
        return UserCouponDo.builder()
                .couponName(request.getCouponName())
//                .effectiveTime(request.getEffectiveTime())
//                .expireTime(request.getExpireTime())
                .couponDiscountType(0) // 明确指定为折扣券
//                .amount(request.getDiscount()) // 折扣券的 amount 字段存储折扣比例，如 0.85
//                .conditionAmount(request.getFullDiscountAmount()) // 使用门槛
//                .maxDiscount(request.getMaxDiscount()) // 折扣上限
//                .description(request.getDescription())
                .build();
    }

    @Override
    public Integer getCouponType() {
        return 0;
    }
}