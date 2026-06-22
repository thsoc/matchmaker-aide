package com.aide.infrastructure.persistence.repository;

import com.aide.domain.model.UserCouponDo;
import com.aide.domain.repository.UserCouponRepository;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import com.aide.infrastructure.persistence.mapper.UserCouponMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 用户优惠券仓储实现类，基础仓储实现类
 * @date 2026/6/14
 * @date 16:35
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponMapper userCouponMapper;

    @Override
    public IPage<UserCouponDo> getPageUserCoupon(Page<UserCoupon> objectPage, UserCouponDo userCouponDo, Long userId) {
        log.info(">>> getPageUserCoupon START userId={}, request={}", userId, userCouponDo);
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
                .orderByAsc(UserCoupon::getExpireTime)
                .orderByDesc(UserCoupon::getEffectiveTime);
        if (userCouponDo.getStatus() != null) {
            wrapper.eq(UserCoupon::getStatus, userCouponDo.getStatus());
        }
        if (userCouponDo.getCouponDiscountType() != null){
            wrapper.eq(UserCoupon::getCouponDiscountType, userCouponDo.getCouponDiscountType());
        }
        //优惠券名称,前后都有%
        if (userCouponDo.getCouponName() != null){
            wrapper.like(UserCoupon::getCouponName, userCouponDo.getCouponName());
//            //后%
//            wrapper.likeRight(UserCoupon::getCouponName, userCouponDo.getCouponName());
        }
        IPage<UserCoupon> page = userCouponMapper.selectPage(objectPage, wrapper);
        return page.convert(entity ->
                UserCouponDo.builder()
                        .amount(entity.getAmount())
                        .buyTime(entity.getBuyTime())
                        .couponDiscountType(entity.getCouponDiscountType())
                        .couponId(entity.getCouponId())
                        .couponName(entity.getCouponName())
                        .couponDiscountType(entity.getCouponDiscountType())
                        .conditionAmount(entity.getConditionAmount())
                        .createTime(entity.getCreateTime())
                        .description(entity.getDescription())
                        .effectiveTime(entity.getEffectiveTime())
                        .expireTime(entity.getExpireTime())
                        .id(entity.getId())
                        .maxDiscount(entity.getMaxDiscount())
                        .orderNo(entity.getOrderNo())
                        .ruleJson(entity.getRuleJson())
                        .status(entity.getStatus())
                        .updateTime(entity.getUpdateTime())
                        .userId(entity.getUserId())
                        .build());


    }
}
