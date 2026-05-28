package com.aide.domain.service;


import com.aide.domain.factory.PaymentStrategyFactory;
import com.aide.domain.model.RechargeRecordDo;
import com.aide.domain.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 支付上下文 - 协调支付策略的执行
 *
 * 职责：
 * 1. 封装支付流程
 * 2. 统一处理支付异常
 * 3. 记录支付日志
 * @date 2026/5/28
 * @date 13:13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentContext {

    private final PaymentStrategyFactory strategyFactory;

    /**
     * 执行支付
     *
     * @param rechargeRecord 充值记录
     * @return 支付结果（预支付ID或表单）
     */
    public String executePayment(RechargeRecordDo rechargeRecord) {
        Integer payType = rechargeRecord.getPayType();

        log.info("开始执行支付，订单号: {}, 支付方式: {}",
                rechargeRecord.getOrderNo(), payType);

        try {
            // 1. 获取对应的支付策略
            PaymentStrategy strategy = strategyFactory.getStrategy(payType);

            // 2. 执行支付
            String paymentResult = strategy.executePayment(rechargeRecord);

            log.info("支付执行成功，订单号: {}, 结果: {}",
                    rechargeRecord.getOrderNo(), paymentResult);

            return paymentResult;

        } catch (Exception e) {
            log.error("支付执行失败，订单号: {}", rechargeRecord.getOrderNo(), e);
            throw new RuntimeException("支付失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询支付状态
     *
     * @param orderNo 订单号
     * @param payType 支付方式
     * @return 支付状态
     */
    public boolean queryPaymentStatus(String orderNo, Integer payType) {
        log.info("查询支付状态，订单号: {}, 支付方式: {}", orderNo, payType);

        PaymentStrategy strategy = strategyFactory.getStrategy(payType);
        return strategy.queryPaymentStatus(orderNo);
    }

    /**
     * 取消支付
     *
     * @param orderNo 订单号
     * @param payType 支付方式
     */
    public void cancelPayment(String orderNo, Integer payType) {
        log.info("取消支付，订单号: {}, 支付方式: {}", orderNo, payType);

        PaymentStrategy strategy = strategyFactory.getStrategy(payType);
        strategy.cancelPayment(orderNo);
    }

    /**
     * 验证支付回调
     *
     * @param callbackData 回调数据
     * @param payType 支付方式
     * @return 是否有效
     */
    public boolean verifyCallback(Object callbackData, Integer payType) {
        PaymentStrategy strategy = strategyFactory.getStrategy(payType);
        return strategy.verifyCallback(callbackData);
    }
}
