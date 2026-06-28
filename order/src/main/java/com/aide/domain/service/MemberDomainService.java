package com.aide.domain.service;

import com.aide.common.Result.Result;
import com.aide.common.dto.feign.member.MemberTypeConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.aide.infrastructure.remote.service.RemoteMemberService;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 会员领域服务
 * @date 2026/6/9
 * @date 17:51
 */
@Service
@RequiredArgsConstructor
public class MemberDomainService {
    private final RemoteMemberService RemoteMemberService;

    /**
     * @author mazg
     * @description 扣款
     * @date 17:52 2026/6/9
     * @return
     **/
    public Result createMember(Long userId, Integer memberType, BigDecimal amount){
        return RemoteMemberService.buyMember(userId, memberType, amount);
    }

    /**
     * @author mazg
     * @description 获取会员金额
     * @date 17:52 2026/6/9
     * @return
     **/
    public Result<MemberTypeConfig> getMemberAmount(Integer memberType) {
        return RemoteMemberService.getMemberAmount(memberType);
    }

}
