package com.aide.service;

import com.aide.adapter.VO.CouponVo;
import com.aide.adapter.VO.UserCouponVo;
import com.aide.adapter.dto.CouponRequest;
import com.aide.adapter.dto.UserCouponRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface CouponService {
    /**
     * 创建优惠券
     *
     * @param request 优惠券请求参数
     * @return 优惠券ID
     */
    String createCoupon(CouponRequest request);

    Page<UserCouponVo> getPageUserCoupon(UserCouponRequest userCouponRequest);

    Page<CouponVo> getPageCoupon(CouponRequest request);

    void receiveCoupon(Long id);

    void preheatCoupon(String param);
}
