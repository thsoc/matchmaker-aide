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

    /**
     * //todo 获取用户优惠券
     */
    @PostMapping("/getUserCoupon")
    public Result<String> getUserCoupon() {
        return Result.success("获取用户优惠券成功");
    }
    /**
     * //todo 优惠券列表（未生效，待抢购）
     */
    @PostMapping("/getCouponList")
    public Result<String> getCouponList() {
        return Result.success("获取优惠券列表成功");
    }

    /**
     * //todo 定时任务，预热快生效的优惠券，保存到redis
     */
    @PostMapping("/preheatCoupon")
    public Result<String> preheatCoupon() {
        return Result.success("预热快生效的优惠券成功");
    }
    /**
     * //todo 优惠券详情
     */
    @PostMapping("/getCouponDetail")
    public Result<String> getCouponDetail() {
        return Result.success("获取优惠券详情成功");
    }
}
