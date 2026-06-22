package com.aide.domain.model.strategy;


import com.aide.adapter.dto.CouponRequest;
import com.aide.domain.model.CouponDiscountType;
import com.aide.domain.model.CouponDo;
import com.aide.domain.model.CouponQuota;
import com.aide.domain.model.CouponRule;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 折扣券转换器 (couponDiscountType = 0)
 * @date 2026/6/21
 * @date 18:35
 */
@Component
public class DiscountCouponConverter implements CouponConverterStrategy {

    @Override
    public CouponDo convert(CouponRequest request) {
        return CouponDo.builder()
                .couponQuota(CouponQuota.builder()
                        .couponName(request.getCouponName())
                        .description(request.getDescription())
                        .build())
                .couponRule(CouponRule.builder()
                        .expireTime(request.getExpireTime())
                        .discountType(CouponDiscountType.DISCOUNT) // 明确指定为折扣券
                        .amount(request.getDiscount()) // 折扣券的 amount 字段存储折扣比例，如 0.85
                        .conditionAmount(request.getFullDiscountAmount()) // 使用门槛
                        .maxDiscount(request.getMaxDiscount()) // 折扣上限
                        .build())
                .build();
    }

    @Override
    public CouponDiscountType getCouponType() {
        return CouponDiscountType.DISCOUNT;
    }
}