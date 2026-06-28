package com.aide.domain.strategy.impl;

/**
 * @author mazg
 * @description 普通会员策略
 * @date 2026/5/29
 * @date 13:22
 */

import com.aide.common.dto.feign.member.MemberTypeConfig;
import com.aide.domain.strategy.MemberTypeStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NormalMemberStrategy implements MemberTypeStrategy {

    @Override
    public Integer getTypeCode() {
        return 1;
    }

    @Override
    public MemberTypeConfig getConfig() {
        return MemberTypeConfig.builder()
                .memberType(1)
                .name("普通会员")
                .price(new BigDecimal("99.00"))
                .validityDays(30)
                .build();
    }
}
