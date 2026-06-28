package com.aide.common.dto.mq.order;

import com.aide.common.dto.type.OrderTypeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @author mazg
 * @description MQ消息需要的对象：订单对象
 * @date 2026/6/28
 * @date 11:52
 */
@Data
@Builder
public class OrderDto {
    private Long couponId;
    private Long userId;
    /**
     * 订单类型
     * 1.抢购优惠券
     */
    private OrderTypeEnum orderType;
}
