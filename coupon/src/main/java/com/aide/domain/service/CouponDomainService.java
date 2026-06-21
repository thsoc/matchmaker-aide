package com.aide.domain.service;

import com.aide.adapter.converter.CouponVoConverter;
import com.aide.domain.model.CouponDo;
import com.aide.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
     * @author mazg
     * @description 创建优惠券
     * @date 18:08 2026/6/9
     * @return 
     **/
    public String createCoupon(CouponDo couponDo) {
        //校验优惠券信息
        couponDo.validateCoupon();
        //初始化优惠券信息
        couponDo.initCoupon();
        return couponRepository.createOrder(couponDo);
    }
}
