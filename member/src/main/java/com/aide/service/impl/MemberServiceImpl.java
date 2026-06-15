package com.aide.service.impl;

import com.aide.domain.event.MemberPurchasedEvent;
import com.aide.domain.factory.MemberTypeFactory;
import com.aide.domain.model.MemberDo;
import com.aide.domain.model.MemberTypeConfig;
import com.aide.domain.service.MemberDomainService;
import com.aide.domain.service.MoneyDomainService;
import com.aide.domain.service.OrderDomainService;
import com.aide.service.MemberService;
import io.seata.spring.annotation.GlobalTransactional;
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
    private final MoneyDomainService moneyDomainService;
    private final OrderDomainService orderDomainService;

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public String buyMember(Long userId, Integer memberType) {
        log.info("开始购买会员，用户ID: {}, 会员类型: {}", userId, memberType);

        // 1. 通过工厂获取会员配置（值对象）
        MemberTypeConfig config = memberTypeFactory.getConfig(memberType);

        // 2. 调用领域服务执行核心业务逻辑
        MemberDo member = memberDomainService.purchaseMembership(userId, memberType);
        log.info("会员信息更新成功，会员ID: {}", member.getId());

        //3.扣款
        moneyDomainService.dudeceMoney(userId, config.getPrice(), "购买会员");

        //4.保存订单
        orderDomainService.createOrder(userId,
                1, // 订单类型：1-会员购买
                config.getPrice(),
                "购买" + config.getName());

        // 5. 发布领域事件，发放积分
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

