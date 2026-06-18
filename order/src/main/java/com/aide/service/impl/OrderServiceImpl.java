package com.aide.service.impl;

import com.aide.common.Result.Result;
import com.aide.common.auth.context.UserContext;
import com.aide.common.dto.member.MemberTypeConfig;
import com.aide.common.dto.order.OrderRequest;
import com.aide.domain.event.MemberPurchasedEvent;
import com.aide.domain.service.MemberDomainService;
import com.aide.domain.service.MoneyDomainService;
import com.aide.domain.service.OrderDomainService;
import com.aide.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 会员服务实现类
 * @date 2026/5/29
 * @date 11:32
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderDomainService orderDomainService;
    private final MoneyDomainService moneyDomainService;
    private final MemberDomainService memberDomainService;
    private final ApplicationEventPublisher eventPublisher;
    @Override
    @GlobalTransactional
    public String createOrder(OrderRequest request) {
        log.info(">>> createOrder START xid={}", RootContext.getXID());
        Long userId = UserContext.getUser().getId();
        //获取会员金额
        log.info("获取会员金额，用户ID: {}", request.getUserId());
        Result<MemberTypeConfig> resultMember = memberDomainService.getMemberAmount(request.getMemberType());
        MemberTypeConfig memberInfo = resultMember.getData();
        log.info("会员金额: {}", memberInfo.getPrice());

        //下单
        log.info("创建订单，用户ID: {}, 订单类型: {}, 金额: {}, 描述: {}", request.getUserId(), request.getOrderType(), memberInfo.getPrice(), request.getDescription());
        String orderNo = orderDomainService.createOrder(request.getUserId(), request.getOrderType(), memberInfo.getPrice(), request.getDescription());
        log.info("下单成功，订单编号: {}", orderNo);

        //扣款
        log.info("开始扣款，用户ID: {}, 金额: {}", request.getUserId(), memberInfo.getPrice());
        Result result = moneyDomainService.deductMoney(request.getUserId(), memberInfo.getPrice(), "下单");
        if (!result.isSuccess()) {
            log.error("扣款失败，用户ID: {}, 金额: {}", request.getUserId(), memberInfo.getPrice());
            throw new RuntimeException("扣款失败");
        }

        //创建会员，//todo 这一步可以发MQ，使用orderNo保证幂等性(要在会员服务中新增会员购买记录表)
        log.info("创建会员，用户ID: {}, 金额: {}", request.getUserId(), memberInfo.getPrice());
        Result memberResult = memberDomainService.createMember(request.getUserId(), request.getMemberType(), memberInfo.getPrice());
        log.info("创建会员成功，用户ID: {}", request.getUserId());
        if (!memberResult.isSuccess()){
            log.error("创建会员失败，用户ID: {}", request.getUserId());
            throw new RuntimeException("创建会员失败");
        }


        //发布领域事件，发放积分,更新订单状态
        MemberPurchasedEvent event = new MemberPurchasedEvent(
                this,
                userId,
                orderNo,
                memberInfo.getPrice()
        );
        eventPublisher.publishEvent(event);
        log.info(">>> createOrder end xid={}", RootContext.getXID());
        return orderNo;
    }

    @Override
    public void changeOrderStatus(Long userId, String orderNo) {
        orderDomainService.changeOrderStatus(userId, orderNo);
    }
}
