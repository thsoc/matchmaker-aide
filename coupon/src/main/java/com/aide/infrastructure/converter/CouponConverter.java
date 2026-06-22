package com.aide.infrastructure.converter;

import com.aide.domain.model.*;
import com.aide.infrastructure.persistence.entity.Coupon;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/23
 * @date 04:06
 */
@Component
public class CouponConverter {

    /**
     * 将 PO 转换为 DO
     * 只有基础设施层知道 PO 的存在，领域层保持绝对纯洁
     */
    public CouponDo toDomain(Coupon po) {
        if (po == null) return null;

        // 1. 组装值对象
        CouponRule period = CouponRule.builder()
                .amount(po.getAmount())
                .conditionAmount(po.getConditionAmount())
                .discountType(CouponDiscountType.getByCode(po.getCouponDiscountType()))
                .effectiveTime(po.getEffectiveTime())
                .expireTime(po.getExpireTime())
                .maxDiscount(po.getMaxDiscount())
                .ruleJson(po.getRuleJson())
                .status(po.getStatus())
                .totalCount(po.getTotalCount())
                .availableStock(po.getAvailableStock())
//                .couponLifeCycleStatus(CouponLifeCycleStatus.ACTIVE)
                .build();
        CouponQuota quota = CouponQuota.builder()
                .couponName(po.getCouponName())
                .description(po.getDescription())
                .createBy(po.getCreateBy())
                .updateBy(po.getUpdateBy())
                .remark(po.getRemark())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .deleteTime(po.getDeleteTime())
                .build();

        // 2. 调用领域对象的 Builder 进行重建
        return CouponDo.rebuildBuilder()
                .id(po.getId())
                .couponQuota(quota)
                .couponRule(period)
                .build(); // build 时会自动触发 calculateCurrentStatus()
    }
}