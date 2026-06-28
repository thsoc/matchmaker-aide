package com.aide.domain.service;

import com.aide.domain.factory.MemberTypeFactory;
import com.aide.domain.model.MemberDo;
import com.aide.common.dto.feign.member.MemberTypeConfig;
import com.aide.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description  会员领域服务
 *
 * 职责：处理涉及多个领域对象的业务规则
 * @date 2026/5/29
 * @date 13:10
 */
@Service
@RequiredArgsConstructor
public class MemberDomainService {

    private final MemberRepository memberRepository;
    private final MemberTypeFactory memberTypeFactory;

    /**
     * 购买会员（核心领域逻辑）
     *
     * @param userId 用户ID
     * @param memberType 会员类型
     * @return 会员领域对象
     */
    public MemberDo purchaseMembership(Long userId, Integer memberType) {
        // 1. 通过工厂获取会员类型配置（值对象）
        MemberTypeConfig config = memberTypeFactory.getConfig(memberType);

        // 2. 查询现有会员
        MemberDo existingMember = memberRepository.findByUserId(userId);

        // 3. 应用业务规则：新购或续费
        if (existingMember != null) {
            existingMember.renew(config);
            memberRepository.save(existingMember);
            return existingMember;
        } else {
            MemberDo newMember = MemberDo.builder()
                    .userId(userId)
                    .status(0) // 初始状态：未激活
                    .build();
            newMember.activate(config);
            memberRepository.save(newMember);
            return newMember;
        }
    }
}
