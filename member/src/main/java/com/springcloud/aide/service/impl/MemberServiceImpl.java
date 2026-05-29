package com.springcloud.aide.service.impl;

import com.springcloud.aide.domain.event.MemberPurchasedEvent;
import com.springcloud.aide.domain.factory.MemberTypeFactory;
import com.springcloud.aide.domain.model.MemberDo;
import com.springcloud.aide.domain.model.MemberTypeConfig;
import com.springcloud.aide.domain.service.MemberDomainService;
import com.springcloud.aide.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberDomainService memberDomainService;
    private final MemberTypeFactory memberTypeFactory;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String buyMember(Long userId, Integer memberType) {
        log.info("开始购买会员，用户ID: {}, 会员类型: {}", userId, memberType);

        // 1. 通过工厂获取会员配置（值对象）
        MemberTypeConfig config = memberTypeFactory.getConfig(memberType);

        // 2. 调用领域服务执行核心业务逻辑
        MemberDo member = memberDomainService.purchaseMembership(userId, memberType);
        log.info("会员信息更新成功，会员ID: {}", member.getId());

        // 3. 发布领域事件（异步处理跨服务调用）
        MemberPurchasedEvent event = new MemberPurchasedEvent(
                this,
                userId,
                member.getId(),
                memberType,
                config.getPrice(),
                member.calculateGiftPoints()
        );
        eventPublisher.publishEvent(event);

        String message = String.format("购买%s成功，有效期至%s",
                config.getName(), member.getEndTime().toLocalDate());
        log.info(message);

        return message;
    }
}

