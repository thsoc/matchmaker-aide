package com.aide.infrastructure.remote.service;

import com.aide.common.Result.Result;
import com.aide.common.dto.member.MemberTypeConfig;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 远程会员服务
 * @date 2026/6/9
 * @date 17:43
 */
public interface RemoteMemberService {

    Result buyMember(Long userId, Integer memberType, BigDecimal amount);

    Result<MemberTypeConfig> getMemberAmount(Integer memberType);
}
