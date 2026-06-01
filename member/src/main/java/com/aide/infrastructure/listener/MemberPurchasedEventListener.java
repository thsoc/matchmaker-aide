package com.aide.infrastructure.listener;

import com.aide.adapter.feign.MoneyFeignClient;
import com.aide.adapter.feign.OrderFeignClient;
import com.aide.adapter.feign.PointsFeignClient;
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
    private final MoneyFeignClient moneyFeignClient;
    private final OrderFeignClient orderFeignClient;
    private final PointsFeignClient pointsFeignClient;

    /**
     * 监听会员购买成功事件，在事务提交后异步执行
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberPurchased(MemberPurchasedEvent event) {
        log.info("处理会员购买事件，用户ID: {}, 会员ID: {}", event.getUserId(), event.getMemberId());

        try {
            // 1. 扣款
            moneyFeignClient.deduct(
                    event.getUserId(),
                    event.getAmount(),
                    "购买会员"
            );
            log.info("扣款成功，用户ID: {}, 金额: {}", event.getUserId(), event.getAmount());

            // 2. 创建订单 - 通过工厂获取配置
            MemberTypeConfig config = memberTypeFactory.getConfig(event.getMemberType());
            Long orderId = orderFeignClient.createOrder(
                    event.getUserId(),
                    1, // 订单类型：1-会员购买
                    event.getAmount(),
                    "购买" + config.getName()
            );
            log.info("订单创建成功，订单ID: {}", orderId);

            // 3. 赠送积分
            String remark = String.format("购买%s赠送%d积分", config.getName(), event.getGiftPoints());
            pointsFeignClient.addPoints(event.getUserId(), event.getGiftPoints(), remark);
            log.info("积分赠送成功，用户ID: {}, 积分: {}", event.getUserId(), event.getGiftPoints());

        } catch (Exception e) {
            log.error("处理会员购买事件失败，用户ID: {}", event.getUserId(), e);
            // TODO: 这里可以添加补偿机制或重试逻辑，确保事件处理成功 1.发送mq，2.直接重试 3.告警 4.补偿，5.本地消息表+定时任务 6.注释@Async使用同步
//            throw e; //防止无限重试
        }
    }
}
