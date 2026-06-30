package com.aide.infrastructure.remote.fallback;

import com.aide.common.Result.Result;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.infrastructure.remote.feign.CouponFeignClient;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/30
 * @date 16:24
 */
public class CouponFeignClientFallbackFactory extends AbstractFallbackFactory<CouponFeignClient>{
    @Override
    public CouponFeignClient create(Throwable cause) {
        return new CouponFeignClient() {
            @Override
            public Result buyCouPon(Long id) {
                return defaultFail("coupon-service", "buyCouPon", cause);
            }

            @Override
            public Result<CouponInfo> getCouPonInfo(Long id) {
                return defaultFail("coupon-service", "getCouPonInfo", cause);
            }
        };
    }
}
