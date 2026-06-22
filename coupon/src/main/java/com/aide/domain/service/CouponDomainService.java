package com.aide.domain.service;

import com.aide.common.auth.context.UserContext;
import com.aide.domain.model.CouponDo;
import com.aide.domain.repository.CouponRepository;
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
     * @author mazg
     * @description 创建优惠券
     * @date 18:08 2026/6/9
     * @return 
     **/
    public String createCoupon(CouponDo couponDo) {

        log.info(">>> createCoupon START userid={}, request={}",UserContext.getUser().getId(), couponDo);
        //校验优惠券信息
        couponDo.validateCoupon();
        //初始化优惠券信息
        couponDo.initCoupon(UserContext.getUser().getId());
        return couponRepository.createCoupon(couponDo);
    }
}
