package com.aide.infrastructure.remote.feign;


import com.aide.common.Result.Result;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.common.dto.feign.member.MemberTypeConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

/**
 * 优惠券服务Feign客户端
 */
@FeignClient(name = "coupon-service", path = "/coupon")
public interface CouponFeignClient {

    @PostMapping("/buyCouPon/{id}")
    Result buyCouPon(Long id);

    @GetMapping("/getCouPonInfo/{id}")
    Result<CouponInfo> getCouPonInfo(Long id);
}

