package com.aide.service;

import com.aide.adapter.VO.OrderRequest;

public interface OrderService {
    /**
     * 创建订单
     * @param request 订单请求参数
     * @return 订单ID
     */
    String createOrder(OrderRequest request);
}
