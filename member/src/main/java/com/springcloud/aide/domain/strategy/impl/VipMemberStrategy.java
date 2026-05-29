package com.springcloud.aide.domain.strategy.impl;


import com.springcloud.aide.domain.model.MemberTypeConfig;
import com.springcloud.aide.domain.strategy.MemberTypeStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description VIP会员策略
 * @date 2026/5/29
 * @date 13:24
 */
@Component
public class VipMemberStrategy implements MemberTypeStrategy {

    @Override
    public Integer getTypeCode() {
        return 3;
    }

    @Override
    public MemberTypeConfig getConfig() {
        return MemberTypeConfig.builder()
                .memberType(3)
                .name("VIP会员")
                .price(new BigDecimal("399.00"))
                .validityDays(365)
                .build();
    }
}
