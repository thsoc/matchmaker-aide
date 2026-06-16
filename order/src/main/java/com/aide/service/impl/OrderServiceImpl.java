package com.aide.service.impl;

import com.aide.adapter.VO.OrderRequest;
import com.aide.common.auth.context.UserContext;
import com.aide.domain.service.OrderDomainService;
import com.aide.service.OrderService;
import io.seata.core.context.RootContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Override
    @Transactional
    public String createOrder(OrderRequest request) {
        log.info(">>> createOrder START xid={}", RootContext.getXID());
//        Long userId = UserContext.getUser().getId();
        log.info("创建订单，用户ID: {}, 订单类型: {}, 金额: {}, 描述: {}", request.getUserId(), request.getOrderType(), request.getAmount(), request.getDescription());
        String orderId = orderDomainService.createOrder(request.getUserId(), request.getOrderType(), request.getAmount(), request.getDescription());
        log.info(">>> createOrder end xid={}", RootContext.getXID());
        return orderId;
    }
}
