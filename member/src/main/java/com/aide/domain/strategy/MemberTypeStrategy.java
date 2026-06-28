package com.aide.domain.strategy;


import com.aide.common.dto.feign.member.MemberTypeConfig;

/**
 * 会员类型策略接口
 */
public interface MemberTypeStrategy {

    /**
     * 获取会员类型编码
     */
    Integer getTypeCode();

    /**
     * 获取会员类型配置
     */
    MemberTypeConfig getConfig();
}
