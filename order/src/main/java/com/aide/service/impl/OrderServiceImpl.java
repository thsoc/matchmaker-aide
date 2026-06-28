package com.aide.service.impl;

import com.aide.common.auth.context.UserContext;
import com.aide.common.dto.feign.order.OrderRequest;
import com.aide.common.dto.feign.order.OrderUpdateRequest;
import com.aide.common.dto.type.OrderTypeEnum;
import com.aide.common.dto.type.PayTypeEnum;
import com.aide.domain.model.OrderDo;
import com.aide.domain.service.OrderDomainService;
import com.aide.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author mazg
 * @description 订单服务实现类
 * @date 2026/5/29
 * @date 11:32
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderTypeFactory orderTypeFactory;
    private final OrderPayTypeFactory orderPayTypeFactory;
    private final OrderDomainService orderDomainService;


    @Override
    public String createOrder(OrderRequest request) {
        //获取订单类型
        OrderServiceStrategy strategy = orderTypeFactory.getStrategy(OrderTypeEnum.getByCode(request.getOrderType()));
        String order = strategy.createOrder(request);
        return order;
    }

    @Override
    public void changeOrderStatus(OrderUpdateRequest request) {
        //查询订单领域对象
        OrderDo orderDo = orderDomainService.getOrderDo(request.getOrderNo());
        //获取支付类型
        OrderPayStrategy strategy = orderPayTypeFactory.getStrategy(PayTypeEnum.getByCode(orderDo.getOrderType()));
        strategy.changeOrderStatus(orderDo);
    }
}
