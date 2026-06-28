package com.aide.service;

import com.aide.common.dto.feign.order.OrderRequest;
import com.aide.common.dto.type.OrderTypeEnum;
import com.aide.common.dto.type.PayTypeEnum;
import com.aide.domain.model.OrderDo;

public interface OrderPayStrategy {

    PayTypeEnum getPayType();

    void changeOrderStatus(OrderDo orderDo);
}
