package com.aide.infrastructure.listener;

import com.aide.common.dto.points.AddPointsRequest;
import com.aide.infrastructure.remote.feign.MoneyFeignClient;
import com.aide.infrastructure.remote.feign.OrderFeignClient;
import com.aide.infrastructure.remote.feign.PointsFeignClient;
import com.aide.domain.event.MemberPurchasedEvent;
import com.aide.domain.factory.MemberTypeFactory;
import com.aide.domain.model.MemberTypeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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

    private final MemberTypeFactory memberTypeFactory;
    private final PointsFeignClient pointsFeignClient;

    /**
     * 监听会员购买成功事件，在事务提交后异步执行
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberPurchased(MemberPurchasedEvent event) {
        log.info("处理会员购买赠送积分事件，用户ID: {}, 会员ID: {}", event.getUserId(), event.getMemberId());

        // 这里可以添加补偿机制或重试逻辑，确保事件处理成功 1.发送mq，2.直接重试 3.告警 4.补偿，5.本地消息表+定时任务 6.注释@Async使用同步
        MemberTypeConfig config = memberTypeFactory.getConfig(event.getMemberType());
        //假设发送mq
        sendMqPoints(config, event);
    }

    private void sendMqPoints(MemberTypeConfig config, MemberPurchasedEvent event) {
        // 4. 赠送积分
        String remark = String.format("购买%s赠送%d积分", config.getName(), event.getGiftPoints());
        AddPointsRequest build = AddPointsRequest.builder().userId(event.getUserId()).points(event.getGiftPoints()).remark(remark).build();
        pointsFeignClient.addPoints(build);
        log.info("积分赠送成功，用户ID: {}, 积分: {}", event.getUserId(), event.getGiftPoints());
    }
}
