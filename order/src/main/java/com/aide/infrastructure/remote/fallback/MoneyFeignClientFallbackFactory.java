package com.aide.infrastructure.remote.fallback;

import com.aide.common.Result.Result;
import com.aide.common.dto.feign.member.MemberTypeConfig;
import com.aide.common.dto.feign.money.DeductRequest;
import com.aide.infrastructure.remote.feign.MemberFeignClient;
import com.aide.infrastructure.remote.feign.MoneyFeignClient;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/30
 * @date 16:24
 */
public class MoneyFeignClientFallbackFactory extends AbstractFallbackFactory<MoneyFeignClient>{
    @Override
    public MoneyFeignClient create(Throwable cause) {
        return new MoneyFeignClient() {
            @Override
            public Result deduct(DeductRequest build) {
                //这边是余额扣款全局事务回滚
                return defaultFail("money-service", "deduct", cause);
            }
        };
    }
}
