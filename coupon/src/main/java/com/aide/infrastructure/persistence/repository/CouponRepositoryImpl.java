package com.aide.infrastructure.persistence.repository;

import com.aide.common.constant.coupon.Constant;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.domain.model.CouponDo;
import com.aide.domain.repository.CouponRepository;
import com.aide.infrastructure.converter.CouponConverter;
import com.aide.infrastructure.persistence.entity.Coupon;
import com.aide.infrastructure.persistence.mapper.CouponMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String createCoupon(CouponDo couponDo) {
        log.info("创建优惠券");
        //将领域对象转换成持久化对象
        Coupon coupon = fromCouponDo(couponDo);
        couponMapper.insert(coupon);
        return coupon.getId().toString();
    }

    @Override
    public Page<CouponDo> getPageCoupon(Page<Coupon> objectPage, CouponDo couponDo) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Coupon::getExpireTime)
                .orderByDesc(Coupon::getEffectiveTime);
        if (couponDo.getCouponRule().getStatus() != null) {
            wrapper.eq(Coupon::getStatus, couponDo.getCouponRule().getStatus());
        }
        if (couponDo.getCouponRule().getDiscountType() != null) {
            wrapper.eq(Coupon::getCouponDiscountType, couponDo.getCouponRule().getDiscountType().getCode());
        }
        //优惠券名称,前后都有%
        if (couponDo.getCouponName() != null) {
            wrapper.like(Coupon::getCouponName, couponDo.getCouponName());
//            //后%
//            wrapper.likeRight(CouponDo::getCouponName, couponDo.getCouponName());
        }
        Page<Coupon> page = couponMapper.selectPage(objectPage, wrapper);
        return couponConverter.convertCouponPage(page);
    }

    @Override
    public CouponInfo getCouponInfo(Long id) {
        LambdaQueryChainWrapper<Coupon> couponLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(couponMapper);
        couponLambdaQueryChainWrapper.eq(Coupon::getStatus, "0");
        Coupon one = couponLambdaQueryChainWrapper.one();
        if (one == null) {
            return null;
        }
        CouponInfo couponInfo = fromCouponToCouponInfo(one);
        //存入redis
        redisTemplate.opsForValue().set(Constant.COUPON_MONEY_KEY_PREFIX + couponInfo.getId(), couponInfo, 10000, TimeUnit.SECONDS);
        return couponInfo;
    }

    private CouponInfo fromCouponToCouponInfo(Coupon one) {
        return CouponInfo.builder()
                .id(one.getId())
                .couponName(one.getCouponName())
                .effectiveTime(one.getEffectiveTime())
                .expireTime(one.getExpireTime())
                .discountType(one.getCouponDiscountType())
                .totalCount(one.getTotalCount())
                .availableStock(one.getAvailableStock())
                .amount(one.getAmount())
                .conditionAmount(one.getConditionAmount())
                .maxDiscount(one.getMaxDiscount())
                .ruleJson(one.getRuleJson())
                .description(one.getDescription())
                .status(one.getStatus())
                .remark(one.getRemark())
                .build();
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
