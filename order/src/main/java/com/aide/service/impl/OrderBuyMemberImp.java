package com.aide.service.impl;

import com.aide.common.Result.Result;
import com.aide.common.auth.context.UserContext;
import com.aide.common.dto.feign.member.MemberTypeConfig;
import com.aide.common.dto.feign.order.OrderRequest;
import com.aide.common.dto.type.OrderTypeEnum;
import com.aide.domain.event.MemberPurchasedEvent;
import com.aide.domain.model.OrderDo;
import com.aide.domain.service.MemberDomainService;
import com.aide.domain.service.MoneyDomainService;
import com.aide.domain.service.OrderDomainService;
import com.aide.service.OrderServiceStrategy;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mazg
 * @description 秒杀优惠券
 * @date 2026/6/28
 * @date 12:57
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderBuyMemberImp implements OrderServiceStrategy {
    private final OrderDomainService orderDomainService;
    private final MemberDomainService memberDomainService;

    @Override
    @Transactional
    public String createOrder(OrderRequest request) {
        log.info(">>> createOrder START xid={}", RootContext.getXID());
        if(request.getUserId() == null){ //兼容消息队列
            request.setUserId(UserContext.getUser().getId());
        }

        //获取会员金额
        log.info("获取会员金额，用户ID: {}", request.getDescription());
        Result<MemberTypeConfig> resultMember = memberDomainService.getMemberAmount(request.getMemberType());
        MemberTypeConfig memberInfo = resultMember.getData();
        log.info("会员金额: {}", memberInfo.getPrice());

        //下单
        log.info("创建订单，用户ID: {}, 订单类型: {}, 金额: {}, 描述: {}", request.getUserId(), OrderTypeEnum.BUY_MEMBER.getCode(), memberInfo.getPrice(), request.getMemberType());
        //todo 应该有个订单详情保存会员类型，这边放在描述中
        String orderNo = orderDomainService.createOrder(request.getUserId(), OrderTypeEnum.BUY_MEMBER.getCode(), memberInfo.getPrice(), request.getMemberType().toString());
        log.info("下单成功，订单编号: {}", orderNo);
        return orderNo;
    }

    @Override
    public OrderTypeEnum getOrderType() {
        return OrderTypeEnum.BUY_MEMBER;
    }

    @Override
    public void nextStep(OrderDo orderDo) {
        Long userId = UserContext.getUserId();
        //创建用户优惠券，//todo 这一步可以发MQ，使用orderNo保证幂等性(要在会员服务中新增会员购买记录表)
        log.info("创建用户优惠券，用户ID: {}, 金额: {}", userId, orderDo.getAmount());
        //todo 应该有个订单详情保存会员类型，这边放在描述中
        Result memberResult = memberDomainService.createMember(userId, Integer.valueOf(orderDo.getDescription()), orderDo.getAmount());
        log.info("创建用户优惠券，用户ID: {}", userId);
        if (!memberResult.isSuccess()){
            log.error("创建用户优惠券，用户ID: {}", userId);
            throw new RuntimeException("创建用户优惠券");
        }
    }

}
