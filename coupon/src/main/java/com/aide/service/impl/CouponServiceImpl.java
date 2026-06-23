package com.aide.service.impl;

import com.aide.adapter.VO.CouponVo;
import com.aide.adapter.VO.UserCouponVo;
import com.aide.adapter.dto.CouponRequest;
import com.aide.adapter.dto.UserCouponRequest;
import com.aide.common.auth.context.UserContext;
import com.aide.common.domain.IClock;
import com.aide.common.domain.SystemClock;
import com.aide.common.util.PageUtil;
import com.aide.domain.model.CouponDo;
import com.aide.domain.model.UserCouponDo;
import com.aide.domain.repository.CouponRepository;
import com.aide.domain.service.CouponDomainService;
import com.aide.domain.service.UserCouponDomainService;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import com.aide.service.CouponService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mazg
 * @description 优惠券服务实现类
 * @date 2026/5/29
 * @date 11:32
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    private final CouponDomainService couponDomainService;
    private final UserCouponDomainService userCouponDomainService;
    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public String createCoupon(CouponRequest request) {
        Long userId = UserContext.getUser().getId();
        //打印request所有字段信息
        log.info(">>> createCoupon START userid={}, request={}", userId, request);
        //获取优惠券领域对象
        IClock clock = new SystemClock();
        CouponDo couponDo = couponDomainService.createCouponDo(request, userId, null, clock);

        return couponRepository.createCoupon(couponDo);
    }

    @Override
    public Page<UserCouponVo> getPageUserCoupon(UserCouponRequest userCouponRequest) {
        Long userId = UserContext.getUserId();
        log.info(">>> getPageUserCoupon START userId={}, request={}", UserContext.getUserId(), userCouponRequest);
        //将用户优惠券请求对象转换领域对象
        UserCouponDo userCouponDo = userCouponDomainService.createUserCouponDo(userCouponRequest, userId);
        Page<UserCoupon> objectPage = PageUtil.buildPage(userCouponRequest);
        IPage<UserCouponDo> pageUserCoupon = userCouponDomainService.getPageUserCoupon(objectPage, userCouponDo, userId);

        //根据需要转换返回对象（加密等）
        IPage<UserCouponVo> convert = pageUserCoupon.convert(entity -> UserCouponVo.builder()
                .amount(entity.getAmount())
                .buyTime(entity.getBuyTime())
                .couponDiscountType(entity.getCouponDiscountType())
                .couponId(entity.getCouponId())
                .couponName(entity.getCouponName())
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
                .deleteTime(entity.getDeleteTime())
                .remark(entity.getRemark())
                .build());
        return (Page<UserCouponVo>) convert;
    }

    @Override
    public Page<CouponVo> getPageCoupon(CouponRequest request) {
        log.info(">>> getPageCoupon START request={}", request);
        //获取优惠券领域对象
        IPage<CouponDo> pageCoupon = couponDomainService.getPageCoupon(request);

        //根据需要转换返回对象（加密等）
        IPage<CouponVo> convert = pageCoupon.convert(entity -> CouponVo.builder()
                .amount(entity.getCouponRule().getAmount())
                .couponDiscountType(entity.getCouponRule().getDiscountType().getCode())
                .couponName(entity.getCouponName())
                .conditionAmount(entity.getCouponRule().getConditionAmount())
                .description(entity.getCouponQuota().getDescription())
                .effectiveTime(entity.getCouponRule().getEffectiveTime())
                .expireTime(entity.getCouponRule().getExpireTime())
                .id(entity.getId())
                .maxDiscount(entity.getCouponRule().getMaxDiscount())
                .ruleJson(entity.getCouponRule().getRuleJson())
                .status(entity.getCouponRule().getStatus())
                .remark(entity.getCouponQuota().getRemark())
                .build());
        return (Page<CouponVo>) convert;
    }

}
