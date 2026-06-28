package com.aide.adapter.controller;

import com.aide.adapter.VO.CouponVo;
import com.aide.adapter.VO.UserCouponVo;
import com.aide.adapter.dto.UserCouponRequest;
import com.aide.common.Result.Result;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.service.CouponService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.aide.adapter.dto.CouponRequest;

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
    @PostMapping("/admin/createCoupon")
    public Result<String> buyMember(@Valid @RequestBody CouponRequest request) {
        String result = couponService.createCoupon(request);
        return Result.success(result);
    }

    /**
     * 获取用户优惠券列表
     */
    @PostMapping("/getUserCoupon")
    public Result<Page<UserCouponVo>> getPageUserCoupon(@Valid @RequestBody UserCouponRequest userCouponRequest) {
        Page<UserCouponVo> page = couponService.getPageUserCoupon(userCouponRequest);
        return Result.success(page);
    }
    /**
     * //优惠券列表（未生效，待抢购）
     */
    @PostMapping("/getCouponList")
    public Result<Page<CouponVo>> getCouponList(@Valid @RequestBody CouponRequest request) {
        Page<CouponVo> page = couponService.getPageCoupon(request);
        return Result.success(page);
    }

    /**
     * 优惠券详情
     */
    @PostMapping("/getCouponInfo/{id}")
    public Result<CouponInfo> getCouponDetail(@PathVariable Long id) {
        CouponInfo info  = couponService.getCouponInfo(id);
        return Result.success(info);
    }

    /**
     * 1.一人一单，
     * 2.库存-1
     * 3.异步下单待支付
     * 4.取消支付，更新redis中的库存,异步MQ删除订单，
     * 5.余额支付：确认支付，更新redis中的库存 ,异步MQ更新订单状态+扣款
     * 6.支付宝等支付：异步回调确认支付，更新redis中的库存 ,异步MQ更新订单状态
     */
    @PostMapping("/receiveCoupon/{id}")
    public Result<String> receiveCoupon(@PathVariable Long id) {
        if(id == null){
            return Result.error("优惠券ID不能为空");
        }
        couponService.receiveCoupon(id);
        return Result.success("领取优惠券成功");
    }

    /**
     * @author mazg
     * @description 用户购买购物券成功
     * @date 18:48 2026/6/28
     * @return 
     **/
    @PostMapping("/buyCouPon/{id}")
    public Result<String> buyCouPon(@PathVariable Long id) {
        if(id == null){
            return Result.error("优惠券ID不能为空");
        }
//        couponService.buyCouPon(id); //todo
        return Result.success("用户购买购物券成功");
    }


}
