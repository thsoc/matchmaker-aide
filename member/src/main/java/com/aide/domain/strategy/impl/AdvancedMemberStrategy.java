package com.aide.domain.strategy.impl;

import com.aide.common.dto.feign.member.MemberTypeConfig;
import com.aide.domain.strategy.MemberTypeStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 高级会员策略
 * @date 2026/5/29
 * @date 13:23
 */
@Component
public class AdvancedMemberStrategy implements MemberTypeStrategy {

    @Override
    public Integer getTypeCode() {
        return 2;
    }

    @Override
    public MemberTypeConfig getConfig() {
        return MemberTypeConfig.builder()
                .memberType(2)
                .name("高级会员")
                .price(new BigDecimal("199.00"))
                .validityDays(90)
                .build();
    }
}
