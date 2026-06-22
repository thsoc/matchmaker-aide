package com.aide.domain.model.strategy;


import com.aide.adapter.dto.CouponRequest;
import com.aide.domain.model.CouponDiscountType;
import com.aide.domain.model.CouponDo;
import com.aide.domain.model.CouponQuota;
import com.aide.domain.model.CouponRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 代金券转换器 (couponDiscountType = 2)
 * @date 2026/6/21
 * @date 18:36
 */
@Component
public class CashCouponConverter implements CouponConverterStrategy {

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
                        .discountType(CouponDiscountType.VOUCHER) // 明确指定为代金券
                        .amount(request.getCashCouponAmount()) // 代金券的 amount 字段存储面额
                        .conditionAmount(BigDecimal.ZERO) // 代金券通常无门槛，或门槛为0
                        .maxDiscount(null) // 代金券没有折扣上限
                        .build())
                .build();
    }

    @Override
    public CouponDiscountType getCouponType() {
            return CouponDiscountType.VOUCHER;
    }
}