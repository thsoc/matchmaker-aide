package com.aide.infrastructure.listener;

import com.aide.common.dto.feign.points.AddPointsRequest;
import com.aide.common.dto.mq.order.OrderDto;
import com.aide.common.dto.type.OrderTypeEnum;
import com.aide.domain.event.MemberPurchasedEvent;
import com.aide.infrastructure.remote.feign.PointsFeignClient;
import com.aide.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class MemberPurchasedEventListener {

    private final PointsFeignClient pointsFeignClient;
//    private final OrderFeignClient orderFeignClient;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 监听会员购买成功事件，在事务提交后异步执行
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberPurchased(MemberPurchasedEvent event) {
        log.info("处理会员购买赠送积分事件，用户ID: {}, 订单编号: {}", event.getUserId(), event.getOrderNo());
        try {

            //假设发送mq，赠送积分
            sendMqPoints(event);
        } catch (Exception e) {
            log.error("MQ发送失败，用户ID: {}, 订单编号: {}", event.getUserId(), event.getOrderNo(), e);
            // 兜底补偿：写入本地消息表（outbox_event），由定时任务重新发送
//            saveToLocalMessageTable(event);
        }
    }

    private void sendMqPoints(MemberPurchasedEvent event) {
        //假设一元钱一积分,实际上业务要放在订单服务
        //金额取正整数
        BigDecimal abs = event.getAmount().setScale(0, RoundingMode.DOWN).abs();
        int giftPoints = abs.intValue();
        AddPointsRequest dto = AddPointsRequest.builder()
                .userId(event.getUserId())
                .points(giftPoints)
                .remark("购买会员赠送积分").build();
        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("point.exchange", "point.routingKey", dto, cd);
        //局部 confirm,使用全局confirm
//        cd.getFuture().addCallback(
//                result -> {
//                    if (!result.isAck()) {
//                        log.error("消息未送达 exchange");
//                        // 兜底补偿：写入本地消息表（outbox_event），由定时任务重新发送
////                        saveToLocalMessageTable(event);
//                    }
//                    if (result.isAck()){
//                        log.info("积分赠送开始，用户ID: {}, 积分: {}", event.getUserId(), giftPoints);
//                    }
//                },
//                ex -> log.error("MQ发送失败，用户ID: {}, 积分: {}", event.getUserId(), giftPoints, ex)
//        );
    }
}
