package com.aide.infrastructure.remote.service.imp;

import com.aide.common.Result.Result;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.infrastructure.remote.feign.CouponFeignClient;
import com.aide.infrastructure.remote.service.RemoteCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description 远程优惠券服务实现类
 * @date 2026/6/9
 * @date 17:46
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteCouponServiceImp implements RemoteCouponService {

    private final CouponFeignClient couponFeignClient;


    @Override
    public Result buyCouPon(Long id) {
        return couponFeignClient.buyCouPon(id);
    }

    @Override
    public Result<CouponInfo> getCouPonInfo(Long id) {
        return couponFeignClient.getCouPonInfo(id);
    }
}
