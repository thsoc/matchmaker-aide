package com.aide.service;

import com.aide.common.dto.order.OrderRequest;

public interface OrderService {
    /**
     * 创建订单
     * @param request 订单请求参数
     * @return 订单ID
     */
    String createOrder(OrderRequest request);

    /**
     * 更新订单状态
     *
     * @param userId  用户ID
     * @param orderNo 订单编号
     */
    void changeOrderStatus(Long userId, String orderNo);
}
