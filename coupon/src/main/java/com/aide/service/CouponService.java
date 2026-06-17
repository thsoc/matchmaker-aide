package com.aide.service;

import com.aide.adapter.VO.CouponRequest;
import com.aide.common.dto.order.OrderRequest;

public interface CouponService {
    /**
     * 创建优惠券
     *
     * @param request 优惠券请求参数
     * @return 优惠券ID
     */
    String createCoupon(CouponRequest request);
}
