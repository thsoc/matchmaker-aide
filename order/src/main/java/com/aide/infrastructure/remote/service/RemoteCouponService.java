package com.aide.infrastructure.remote.service;

import com.aide.common.Result.Result;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.common.dto.feign.member.MemberTypeConfig;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 远程优惠券服务
 * @date 2026/6/9
 * @date 17:43
 */
public interface RemoteCouponService {

    Result buyCouPon(Long id);

    Result<CouponInfo> getCouPonInfo(Long id);
}
