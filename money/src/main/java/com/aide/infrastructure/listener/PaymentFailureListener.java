package com.aide.infrastructure.listener;

import com.aide.domain.event.PaymentFailureEvent;
import com.aide.domain.model.RechargeRecordDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author mazg
 * @description 支付失败事件监听器
 *
 * 职责：
 * 1. 监听支付失败事件
 * 2. 记录失败日志
 * 3. 发送告警通知（可选）
 * @date 2026/5/28
 * @date 14:02
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailureListener {

    /**
     * 监听支付失败事件
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("paymentCallbackExecutor")
    public void handlePaymentFailure(PaymentFailureEvent event) {
        RechargeRecordDo rechargeRecord = event.getRechargeRecord();
        String failureReason = event.getFailureReason();

        log.error("支付失败，订单号: {}, 用户ID: {}, 金额: {}, 原因: {}",
                rechargeRecord.getOrderNo(),
                rechargeRecord.getUserId(),
                rechargeRecord.getAmount(),
                failureReason);

        // TODO: 可以在这里添加告警通知逻辑
        // 例如：发送短信、邮件通知管理员
    }
}

