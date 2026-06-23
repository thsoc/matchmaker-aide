package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.*;
import com.aide.domain.repository.CouponRepository;
import com.aide.infrastructure.converter.CouponConverter;
import com.aide.infrastructure.persistence.entity.Coupon;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import com.aide.infrastructure.persistence.mapper.CouponMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 优惠仓储实现类，基础仓储实现类
 * @date 2026/6/14
 * @date 16:35
 */
@Slf4j
@Component
@AllArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponMapper couponMapper;
    private final CouponConverter couponConverter;

    @Override
    public String createCoupon(CouponDo couponDo) {
        log.info("创建优惠券");
        //将领域对象转换成持久化对象
        Coupon coupon = fromCouponDo(couponDo);
        couponMapper.insert(coupon);
        return coupon.getId().toString();
    }

    @Override
    public IPage<CouponDo> getPageCoupon(Page<Coupon> objectPage, CouponDo couponDo) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Coupon::getExpireTime)
                .orderByDesc(Coupon::getEffectiveTime);
        if (couponDo.getCouponRule().getStatus() != null) {
            wrapper.eq(Coupon::getStatus, couponDo.getCouponRule().getStatus());
        }
        if (couponDo.getCouponRule().getDiscountType() != null){
            wrapper.eq(Coupon::getCouponDiscountType, couponDo.getCouponRule().getDiscountType().getCode());
        }
        //优惠券名称,前后都有%
        if (couponDo.getCouponName() != null){
            wrapper.like(Coupon::getCouponName, couponDo.getCouponName());
//            //后%
//            wrapper.likeRight(CouponDo::getCouponName, couponDo.getCouponName());
        }
        Page<Coupon> page = couponMapper.selectPage(objectPage, wrapper);
        return couponConverter.convertCouponPage(page);
    }

    private Coupon fromCouponDo(CouponDo couponDo) {
        return Coupon.builder()
                .id(couponDo.getId())
                .couponName(couponDo.getCouponName())
                .effectiveTime(couponDo.getCouponRule().getEffectiveTime())
                .expireTime(couponDo.getCouponRule().getExpireTime())
                .couponDiscountType(couponDo.getCouponRule().getDiscountType().getCode())
                .totalCount(couponDo.getCouponRule().getTotalCount())
                .availableStock(couponDo.getCouponRule().getAvailableStock())
                .amount(couponDo.getCouponRule().getAmount())
                .conditionAmount(couponDo.getCouponRule().getConditionAmount())
                .maxDiscount(couponDo.getCouponRule().getMaxDiscount())
                .ruleJson(couponDo.getCouponRule().getRuleJson())
                .description(couponDo.getCouponQuota().getDescription())
                .status(couponDo.getCouponRule().getStatus())
                .createTime(couponDo.getCouponQuota().getCreateTime())
                .updateTime(couponDo.getCouponQuota().getUpdateTime())
                .deleteTime(couponDo.getCouponQuota().getDeleteTime())
                .createBy(couponDo.getCouponQuota().getCreateBy())
                .updateBy(couponDo.getCouponQuota().getUpdateBy())
                .remark(couponDo.getCouponQuota().getRemark())
                .build();
    }
}
