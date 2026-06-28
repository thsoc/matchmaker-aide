package com.aide.service;

import com.aide.common.dto.feign.order.OrderRequest;
import com.aide.common.dto.type.OrderTypeEnum;
import com.aide.domain.model.OrderDo;

public interface OrderServiceStrategy {

    String createOrder(OrderRequest request);

    OrderTypeEnum getOrderType();

    void nextStep(OrderDo orderDo);
}
