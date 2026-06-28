package com.aide.infrastructure.listener;

import com.aide.common.dto.mq.order.OrderDto;
import com.aide.common.dto.type.OrderTypeEnum;
import com.aide.domain.event.ReceiveCouponEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author mazg
 * @description 会员购买事件监听器
 *
 *  职责：处理会员购买后的副作用（跨服务调用）
 * @date 2026/5/29
 * @date 13:14
 */


@Slf4j
@Component
@RequiredArgsConstructor
public class DeduceCouponEventListener {
    private final RabbitTemplate rabbitTemplate;

    /**
     * 监听会员购买成功事件，在事务提交后异步执行
     */
    @Async
    @EventListener
    public void handleMemberPurchased(ReceiveCouponEvent event) {
        log.info("优惠券购买,创建订单开始发送MQ，用户ID: {}, 优惠券id: {}", event.getUserId(), event.getId());
        OrderDto orderDto = OrderDto.builder()
                .couponId(event.getId())
                .userId(event.getUserId())
                .orderType(OrderTypeEnum.BUY_COUPON)
                .build();
        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("order.exchange", "order.routingKey", orderDto, cd);
        cd.getFuture().addCallback(
                result -> {
                    if (!result.isAck()) {
                        log.error("消息未送达 exchange");
                        // 兜底补偿：写入本地消息表（outbox_event），由定时任务重新发送
//                        saveToLocalMessageTable(event);
                    }
                    if (result.isAck()){
                        log.info("创建订单消息送达exchange，用户ID: {}, 优惠券id: {}", event.getUserId(), event.getId());
                    }
                },
                ex -> log.error("MQ发送失败，用户ID: {}, 优惠券id: {}", event.getUserId(), event.getId(), ex)
        );
    }

}
