package com.aide.domain.strategy;

import com.aide.domain.model.RechargeRecordDo;


/**
 * @author mazg
 * @description 支付策略接口 - 定义不同支付方式的统一契约
 *
 * 职责：
 * 1. 统一定义支付行为
 * 2. 支持多种支付方式扩展
 * 3. 符合开闭原则（OCP）
 *
 * @date 2026/5/28
 */
public interface PaymentStrategy {

    /**
     * 执行支付
     *
     * @param rechargeRecord 充值记录
     * @return 支付结果（第三方交易号或预支付ID）
     */
    String executePayment(RechargeRecordDo rechargeRecord);

    /**
     * 查询支付状态
     *
     * @param orderNo 订单号
     * @return 支付状态（true-已支付，false-未支付）
     */
    boolean queryPaymentStatus(String orderNo);

    /**
     * 取消支付
     *
     * @param orderNo 订单号
     */
    void cancelPayment(String orderNo);

    /**
     * 获取支付方式类型
     *
     * @return 支付方式编码
     */
    Integer getPaymentType();

    /**
     * 验证支付回调签名
     *
     * @param callbackData 回调数据
     * @return 是否有效
     */
    boolean verifyCallback(Object callbackData);
}
