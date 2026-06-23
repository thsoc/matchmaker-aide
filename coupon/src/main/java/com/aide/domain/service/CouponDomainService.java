package com.aide.domain.service;

import com.aide.adapter.dto.CouponRequest;
import com.aide.common.auth.context.UserContext;
import com.aide.common.domain.IClock;
import com.aide.common.util.PageUtil;
import com.aide.domain.model.CouponDo;
import com.aide.domain.repository.CouponRepository;
import com.aide.infrastructure.persistence.entity.Coupon;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description 优惠券领域类
 * @date 2026/6/9
 * @date 18:05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CouponDomainService {
    private final CouponRepository couponRepository;


    /**
     * @return
     * @author mazg
     * @description 创建优惠券
     * @date 18:08 2026/6/9
     **/
    public CouponDo createCouponDo(CouponRequest request, Long userId, String status, IClock clock) {
        log.info(">>> createCoupon START userid={}, request={}", userId, request);
        CouponDo couponDo = CouponDo.createFromDTO(request, userId, clock, status);
        //校验优惠券信息
        couponDo.validateCoupon();
        return couponDo;
    }

    public IPage<CouponDo> getPageCoupon(CouponRequest request) {
        log.info(">>> getPageCoupon START userId={}, request={}", UserContext.getUser().getId(), request);
        CouponDo couponDo = this.createCouponDo(request, null, "0", null);
        Page<Coupon> objectPage = PageUtil.buildPage(request);

        IPage<CouponDo> pageUserCoupon = couponRepository.getPageCoupon(objectPage, couponDo);
        log.info(">>> getPageCoupon END userId={}, request={}, response={}", UserContext.getUser().getId(), couponDo, pageUserCoupon);
        return pageUserCoupon;
    }
}
