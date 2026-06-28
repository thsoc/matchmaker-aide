package com.aide.service.impl;

import com.aide.common.Result.Result;
import com.aide.common.auth.context.UserContext;
import com.aide.common.dto.feign.order.OrderRequest;
import com.aide.common.dto.type.OrderTypeEnum;
import com.aide.common.dto.type.PayTypeEnum;
import com.aide.domain.event.MemberPurchasedEvent;
import com.aide.domain.model.OrderDo;
import com.aide.domain.service.MemberDomainService;
import com.aide.domain.service.MoneyDomainService;
import com.aide.domain.service.OrderDomainService;
import com.aide.service.OrderPayStrategy;
import com.aide.service.OrderServiceStrategy;
import com.aide.service.OrderTypeFactory;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description 阿里支付
 * @date 2026/6/28
 * @date 15:40
 */
@Slf4j
@AllArgsConstructor
@Service
public class AliPayStrategy implements OrderPayStrategy {
    private final OrderDomainService orderDomainService;
    private final MemberDomainService memberDomainService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderTypeFactory orderTypeFactory;
    @Override
    public PayTypeEnum getPayType() {
        return PayTypeEnum.BALANCE;
    }

    @GlobalTransactional
    @Override
    public void changeOrderStatus(OrderDo orderDo) {

        Long userId = UserContext.getUserId();
        orderDomainService.changeOrderStatus(userId, orderDo.getOrderNo());

        OrderServiceStrategy strategy = orderTypeFactory.getStrategy(OrderTypeEnum.getByCode(orderDo.getOrderType()));
        strategy.nextStep(orderDo);

        //扣款(第三方已经扣除)


        //发布领域事件，发放积分,更新订单状态
        MemberPurchasedEvent event = new MemberPurchasedEvent(
                this,
                userId,
                orderDo.getOrderNo(),
                orderDo.getAmount()
        );
        eventPublisher.publishEvent(event);
        log.info(">>> createOrder end xid={}", RootContext.getXID());
    }
}
