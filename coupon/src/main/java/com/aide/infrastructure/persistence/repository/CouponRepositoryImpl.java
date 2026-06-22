package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.CouponDo;
import com.aide.domain.repository.CouponRepository;
import com.aide.infrastructure.persistence.entity.Coupon;
import com.aide.infrastructure.persistence.mapper.CouponMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 订单仓储实现类，基础仓储实现类
 * @date 2026/6/14
 * @date 16:35
 */
@Slf4j
@Component
@AllArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponMapper couponMapper;

    @Override
    public String createCoupon(CouponDo couponDo) {
        log.info("创建优惠券");
        //将领域对象转换成持久化对象
        Coupon coupon = fromCouponDo(couponDo);
        couponMapper.insert(coupon);
        return coupon.getId().toString();
    }

    private Coupon fromCouponDo(CouponDo orderDo) {
        return Coupon.builder()
                .id(orderDo.getId())
                .couponName(orderDo.getCouponName())
                .effectiveTime(orderDo.getEffectiveTime())
                .expireTime(orderDo.getExpireTime())
                .couponDiscountType(orderDo.getCouponDiscountType())
                .totalCount(orderDo.getTotalCount())
                .availableStock(orderDo.getAvailableStock())
                .amount(orderDo.getAmount())
                .conditionAmount(orderDo.getConditionAmount())
                .maxDiscount(orderDo.getMaxDiscount())
                .ruleJson(orderDo.getRuleJson())
                .description(orderDo.getDescription())
                .status(orderDo.getStatus())
                .createTime(orderDo.getCreateTime())
                .updateTime(orderDo.getUpdateTime())
                .deleteTime(orderDo.getDeleteTime())
                .createBy(orderDo.getCreateBy())
                .updateBy(orderDo.getUpdateBy())
                .remark(orderDo.getRemark())
                .build();
    }
}
