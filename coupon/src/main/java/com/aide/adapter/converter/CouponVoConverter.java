package com.aide.adapter.converter;

import com.aide.domain.model.CouponDo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 订单VO转换器实现
 * @date 2026/6/14
 * @date 16:18
 */
@Component
public class CouponVoConverter {

    public CouponDo fromOrderRequest(Long userId, Integer orderType, BigDecimal amount, String description) {
        return null;
    }
}
