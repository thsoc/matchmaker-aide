package com.aide.infrastructure.listener;

import com.aide.common.dto.points.AddPointsRequest;
import com.aide.domain.event.MemberPurchasedEvent;
import com.aide.infrastructure.remote.feign.PointsFeignClient;
import com.aide.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private final OrderService orderService;

    /**
     * 监听会员购买成功事件，在事务提交后异步执行
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberPurchased(MemberPurchasedEvent event) {
        log.info("处理会员购买赠送积分事件，用户ID: {}, 订单编号: {}", event.getUserId(), event.getOrderNo());
        //假设发送mq，更改订单状态
        sendMqOrder(event);

        //假设发送mq，赠送积分
        sendMqPoints(event);
    }

    private void sendMqOrder(MemberPurchasedEvent event) {
        log.info("订单状态更新开始，用户ID: {}, 订单编号: {}", event.getUserId(), event.getOrderNo());
        orderService.changeOrderStatus(event.getUserId(), event.getOrderNo());
        log.info("订单状态更新成功，用户ID: {}, 订单编号: {}", event.getUserId(), event.getOrderNo());
    }

    private void sendMqPoints(MemberPurchasedEvent event) {
        //假设一元钱一积分
        //金额取正整数
        BigDecimal abs = event.getAmount().setScale(0, RoundingMode.DOWN).abs();
        int giftPoints = abs.intValue();

        log.info("积分赠送开始，用户ID: {}, 积分: {}", event.getUserId(), giftPoints);
        pointsFeignClient.addPoints(AddPointsRequest.builder()
                .userId(event.getUserId())
                .points(giftPoints)
                .remark("购买会员赠送积分").build());
        log.info("积分赠送成功，用户ID: {}, 积分: {}", event.getUserId(), giftPoints);
    }
}
