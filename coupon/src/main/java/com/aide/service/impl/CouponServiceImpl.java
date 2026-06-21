package com.aide.service.impl;

import com.aide.adapter.VO.CouponRequest;
import com.aide.adapter.converter.strategy.CouponConverterFactory;
import com.aide.adapter.converter.strategy.CouponConverterStrategy;
import com.aide.common.auth.context.UserContext;
import com.aide.domain.model.CouponDo;
import com.aide.domain.service.CouponDomainService;
import com.aide.service.CouponService;
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
    private final CouponConverterFactory couponConverterFactory;
    @Override
    @Transactional
    public String createCoupon(CouponRequest request) {
        Long userId = UserContext.getUser().getId();
        //打印request所有字段信息
        log.info(">>> createCoupon START userid={}, request={}", userId, request);
        //获取优惠券领域对象
        CouponConverterStrategy converter = couponConverterFactory.getConverter(request.getCouponType());
        CouponDo couponDo = converter.convert(request);
        return couponDomainService.createCoupon(couponDo);
    }

}
