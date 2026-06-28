package com.aide.infrastructure.remote.service.imp;

import com.aide.common.Result.Result;
import com.aide.common.dto.feign.member.MemberTypeConfig;
import com.aide.infrastructure.remote.feign.MemberFeignClient;
import com.aide.infrastructure.remote.service.RemoteMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 远程会员服务实现类
 * @date 2026/6/9
 * @date 17:46
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteMemberServiceImp implements RemoteMemberService {

    private final MemberFeignClient memberFeignClient;



    @Override
    public Result buyMember(Long userId, Integer memberType, BigDecimal amount) {
        return memberFeignClient.buyMember(userId,memberType, amount);
    }

    @Override
    public Result<MemberTypeConfig> getMemberAmount(Integer memberType) {
        return memberFeignClient.getMemberAmount(memberType);
    }
}
