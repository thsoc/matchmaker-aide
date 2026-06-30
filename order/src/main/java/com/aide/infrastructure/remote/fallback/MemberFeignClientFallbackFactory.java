package com.aide.infrastructure.remote.fallback;

import com.aide.common.Result.Result;
import com.aide.common.dto.feign.coupon.CouponInfo;
import com.aide.common.dto.feign.member.MemberTypeConfig;
import com.aide.infrastructure.remote.feign.CouponFeignClient;
import com.aide.infrastructure.remote.feign.MemberFeignClient;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/30
 * @date 16:24
 */
public class MemberFeignClientFallbackFactory extends AbstractFallbackFactory<MemberFeignClient>{
    @Override
    public MemberFeignClient create(Throwable cause) {
        return new MemberFeignClient() {
            @Override
            public Result buyMember(Long userId, Integer memberType, BigDecimal amount) {
                return defaultFail("member-service", "buyMember", cause);
            }

            @Override
            public Result<MemberTypeConfig> getMemberAmount(Integer memberType) {
                return defaultFail("member-service", "getMemberAmount", cause);
            }
        };
    }
}
