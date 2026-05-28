package com.aide.infrastructure.listener;

import com.aide.domain.event.PaymentSuccessEvent;
import com.aide.domain.model.RechargeRecordDo;
import com.aide.domain.service.MoneyDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author mazg
 * @description 支付成功事件监听器
 *
 * 职责：
 * 1. 监听支付成功事件
 * 2. 异步更新用户账户余额
 * 3. 记录充值完成日志
 * @date 2026/5/28
 * @date 13:59
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessListener {

    private final MoneyDomainService moneyDomainService;

    /**
     * 监听支付成功事件，更新用户余额
     *
     * 使用 @Async 异步处理，避免阻塞回调响应
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("paymentCallbackExecutor")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        RechargeRecordDo rechargeRecord = event.getRechargeRecord();
        String transactionId = event.getTransactionId();

        log.info("收到支付成功事件，订单号: {}, 交易号: {}, 用户ID: {}, 金额: {}",
                rechargeRecord.getOrderNo(), transactionId,
                rechargeRecord.getUserId(), rechargeRecord.getAmount());

        try {
            // 更新用户账户余额
            moneyDomainService.rechargeAccount(
                    rechargeRecord.getUserId(),
                    rechargeRecord.getAmount()
            );

            log.info("用户余额更新成功，用户ID: {}, 充值金额: {}",
                    rechargeRecord.getUserId(), rechargeRecord.getAmount());

        } catch (Exception e) {
            log.error("更新用户余额失败，用户ID: {}, 订单号: {}",
                    rechargeRecord.getUserId(), rechargeRecord.getOrderNo(), e);
            // 这里可以添加补偿机制或告警
            throw e; // 重新抛出异常触发重试
        }
    }
}
