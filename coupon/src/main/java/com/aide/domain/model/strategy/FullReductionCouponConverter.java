package com.aide.domain.model.strategy;


import com.aide.adapter.dto.CouponRequest;
import com.aide.domain.model.CouponDiscountType;
import com.aide.domain.model.CouponDo;
import com.aide.domain.model.CouponQuota;
import com.aide.domain.model.CouponRule;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 满减券转换器 (couponDiscountType = 1)
 * @date 2026/6/21
 * @date 18:36
 */
@Component
public class FullReductionCouponConverter implements CouponConverterStrategy {

    @Override
    public CouponDo convert(CouponRequest request) {
        return CouponDo.builder()
                .couponQuota(CouponQuota.builder()
                        .couponName(request.getCouponName())
                        .description(request.getDescription())
                        .build())
                .couponRule(CouponRule.builder()
                        .effectiveTime(request.getEffectiveTime())
                        .expireTime(request.getExpireTime())
                        .discountType(CouponDiscountType.FULL_REDUCTION) // 明确指定为满减券
                        .amount(request.getReductionAmount()) // 满减券的 amount 字段存储抵扣金额
                        .conditionAmount(request.getFullReductionAmount()) // 使用门槛，即“满”的金额
                        .maxDiscount(null) // 满减券通常没有折扣上限
                        .build())
                .build();
    }

    @Override
    public CouponDiscountType getCouponType() {
        return CouponDiscountType.FULL_REDUCTION;
    }
}