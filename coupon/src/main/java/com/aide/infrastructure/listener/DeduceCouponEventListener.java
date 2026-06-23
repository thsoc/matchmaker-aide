package com.aide.infrastructure.listener;

import com.aide.domain.event.ReceiveCouponEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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

    /**
     * 监听会员购买成功事件，在事务提交后异步执行
     */
    @Async
    @EventListener
    public void handleMemberPurchased(ReceiveCouponEvent event) {
        log.info("优惠券购买，用户ID: {}, 优惠券id: {}", event.getUserId(), event.getId());
        try {
            //假设发送mq，更改订单状态
            sendMqOrder(event);

        } catch (Exception e) {
            log.error("MQ发送失败，用户ID: {}, 优惠券id: {}", event.getUserId(), event.getId(), e);
            // 兜底补偿：写入本地消息表（outbox_event），由定时任务重新发送
//            saveToLocalMessageTable(event);
        }
    }

    private void sendMqOrder(ReceiveCouponEvent event) {
        log.info("创建订单开始，用户ID: {}, 优惠券id: {}", event.getUserId(), event.getId());
        log.info("创建订单开始，用户ID: {}, 优惠券id: {}", event.getUserId(), event.getId());
    }
}
