package com.aide.adapter.controller;

import com.aide.common.Result.Result;
import com.aide.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aide.adapter.VO.CouponRequest;

import javax.validation.Valid;

/**
 * @author mazg
 * @description 券控制器
 * @date 2026/5/29
 * @date 11:30
 */
@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    /**
     * 创建优惠券
     */
    @PostMapping("/createCoupon")
    public Result<String> buyMember(@Valid @RequestBody CouponRequest request) {
        String result = couponService.createCoupon(request);
        return Result.success(result);
    }
}
