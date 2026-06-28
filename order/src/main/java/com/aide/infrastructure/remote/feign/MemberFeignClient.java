package com.aide.infrastructure.remote.feign;


import com.aide.common.Result.Result;
import com.aide.common.dto.feign.member.MemberTypeConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

/**
 * 会员服务Feign客户端
 */
@FeignClient(name = "member-service", path = "/member")
public interface MemberFeignClient {

    /**

    /**
     * @author 购买会员
     **/
    @PostMapping("/buyMember")
    Result buyMember(Long userId, Integer memberType, BigDecimal amount);

    /**
     * 获取会员金额
     * @param memberType 会员类型：1-普通会员 2-高级会员 3-VIP会员
     * @return 金额
     */
    @GetMapping("/getMemberPrice/{memberType}")
    Result<MemberTypeConfig> getMemberAmount(Integer memberType);
}

