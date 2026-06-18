package com.aide.service.impl;

import com.aide.adapter.VO.CouponRequest;
import com.aide.common.auth.context.UserContext;
import com.aide.domain.service.CouponDomainService;
import com.aide.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mazg
 * @description 会员服务实现类
 * @date 2026/5/29
 * @date 11:32
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    private final CouponDomainService couponDomainService;
    @Override
    @Transactional
    public String createCoupon(CouponRequest request) {
        Long userId = UserContext.getUser().getId();
        //打印request所有字段信息
        log.info(">>> createCoupon START userid={}, request={}", userId, request);
        return couponDomainService.createCoupon(userId, request.getCouponType(), request.getAmount(), request.getDescription());
    }

}
