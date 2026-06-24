package com.aide.domain.model;

import com.aide.adapter.dto.CouponRequest;
import com.aide.common.domain.IClock;
import com.aide.infrastructure.persistence.entity.Coupon;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 优惠券领域对象
 * @date 2026/6/14
 * @date 16:20
 */
@Getter
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponDo {
    /**
     * 优惠券ID
     */
    private Long id;

    /**
     * 优惠券名称
     */
    private String couponName;


    /**
     * 优惠券规则
     */
    private CouponRule couponRule;


    /**
     * 优惠券其他聚合根
     */
    private CouponQuota couponQuota;


    public static CouponDoBuilder rebuildBuilder() {
        return new CouponDoBuilder();
    }

    /**
     * 校验优惠券信息
     */
    public void validateCoupon() {
        if (couponName == null) {
            throw new RuntimeException("优惠券名称不能为空");
        }
        if (couponRule == null) {
            throw new RuntimeException("优惠券规则不能为空");
        }
        if (couponRule.getExpireTime() == null) {
            throw new RuntimeException("优惠券失效时间不能为空");
        }
        if (couponRule.getDiscountType() == null) {
            throw new RuntimeException("优惠券折扣方式不能为空");
        }
        if (couponRule.getTotalCount() == null) {
            throw new RuntimeException("发行总量不能为空");
        }
        if (couponRule.getAmount() == null) {
            throw new RuntimeException("优惠券金额不能为空");
        }
        if (couponRule.getConditionAmount() == null) {
            throw new RuntimeException("使用门槛不能为空");
        }
        if (couponRule.getMaxDiscount() == null) {
            throw new RuntimeException("折扣上限不能为空");
        }
    }

    // 自定义 Builder 逻辑，确保状态计算一定会被触发
    public static class CouponDoBuilder {
        private Long id;
        private String couponName;
        private CouponRule couponRule;
        private CouponQuota couponQuota;

        private CouponDoBuilder() {

        }

        public CouponDoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CouponDoBuilder couponName(String couponName) {
            this.couponName = couponName;
            return this;
        }

        public CouponDoBuilder couponRule(CouponRule couponRule) {
            this.couponRule = couponRule;
            return this;
        }

        public CouponDoBuilder couponQuota(CouponQuota couponQuota) {
            this.couponQuota = couponQuota;
            return this;
        }


        public CouponDo rebuild() {
            CouponDo coupon = new CouponDo();
            coupon.id = this.id;
            coupon.couponName = this.couponName;
            coupon.couponRule = this.couponRule;
            coupon.couponQuota = this.couponQuota;

            // 在对象构建完成的瞬间，自动触发内部状态推导
            coupon.couponRule.calculateLifeCycleStatus(null);
            return coupon;
        }


    }


    public static CouponDo createFromDTO(CouponRequest request, Long userId, IClock clock, String status) {
        CouponDoBuilder couponDoBuilder = CouponDo.builder()
                .couponName(request.getCouponName());
        CouponQuota.CouponQuotaBuilder couponQuotaBuilder = CouponQuota.builder()
                .remark("创建优惠券")
                .description(request.getDescription());
        CouponRule.CouponRuleBuilder couponRuleBuilder = CouponRule.builder()
                .totalCount(request.getCouponCount())
                .availableStock(request.getCouponCount())
                .effectiveTime(request.getEffectiveTime())
                .expireTime(request.getExpireTime());
        if (CouponDiscountType.VOUCHER.getCode().equals(request.getCouponDiscountType())) {
            couponRuleBuilder
                    .discountType(CouponDiscountType.VOUCHER) // 明确指定为代金券
                    .amount(request.getCashCouponAmount()) // 代金券的 amount 字段存储面额
                    .conditionAmount(BigDecimal.ZERO) // 代金券通常无门槛，或门槛为0
                    .maxDiscount(null);// 代金券没有折扣上限
        }

        if (CouponDiscountType.DISCOUNT.getCode().equals(request.getCouponDiscountType())) {
            couponRuleBuilder
                    .discountType(CouponDiscountType.DISCOUNT) // 明确指定为折扣券
                    .amount(request.getDiscount()) // 折扣券的 amount 字段存储折扣比例，如 0.85
                    .conditionAmount(request.getFullDiscountAmount()) // 使用门槛
                    .maxDiscount(request.getMaxDiscount());// 折扣上限
        }

        if (CouponDiscountType.FULL_REDUCTION.getCode().equals(request.getCouponDiscountType())) {
            couponRuleBuilder
                    .discountType(CouponDiscountType.FULL_REDUCTION) // 明确指定为满减券
                    .amount(request.getReductionAmount()) // 满减券的 amount 字段存储抵扣金额
                    .conditionAmount(request.getFullReductionAmount()) // 使用门槛，即“满”的金额
                    .maxDiscount(null);// 满减券通常没有折扣上限
        }
        if (userId != null) {
            couponQuotaBuilder.createBy(userId.toString()).updateBy(userId.toString());
        }
        if (clock != null) {
            //将clock转为LocalDateTime
            LocalDateTime currentTime = clock.getCurrentTime();
            couponQuotaBuilder.createTime(currentTime);
        }
        if (status != null) {
            couponRuleBuilder.status(status);
        }
        if (status == null) {
            couponRuleBuilder.status("0");
        }
        CouponDo couponDo = couponDoBuilder.couponQuota(couponQuotaBuilder.build()).couponRule(couponRuleBuilder.build()).build();
        return couponDo;
    }

    public static CouponDo fromPOJO(Coupon entity){
        return CouponDo.builder()
                .id(entity.getId())
                .couponName(entity.getCouponName())
                .couponRule(CouponRule.builder()
                        .amount(entity.getAmount())
                        .discountType(CouponDiscountType.getByCode(entity.getCouponDiscountType()))
                        .conditionAmount(entity.getConditionAmount())
                        .effectiveTime(entity.getEffectiveTime())
                        .expireTime(entity.getExpireTime())
                        .maxDiscount(entity.getMaxDiscount())
                        .ruleJson(entity.getRuleJson())
                        .status(entity.getStatus())
                        .build())
                .couponQuota(CouponQuota.builder()
                        .description(entity.getDescription())
                        .createTime(entity.getCreateTime())
                        .updateTime(entity.getUpdateTime())
                        .deleteTime(entity.getDeleteTime())
                        .createBy(entity.getCreateBy())
                        .updateBy(entity.getUpdateBy())
                        .remark(entity.getRemark())
                        .build())
                .rebuild();
    }
}
