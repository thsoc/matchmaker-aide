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
    private final CouponVoConverter couponVoConverter;

    /**
     * @author mazg
     * @description 创建订单
     * @date 18:08 2026/6/9
     * @return 
     **/
    public String createCoupon(Long userId, Integer orderType, BigDecimal amount, String description) {

        //将订单转换为领域对象
        CouponDo orderDo = couponVoConverter.fromOrderRequest(userId, orderType, amount, description);
        //校验订单
        orderDo.validateFromBuyMenber();
        //初始化订单信息
        orderDo.initFromBuyMenber();
        return couponRepository.createOrder(orderDo);
    }
}
