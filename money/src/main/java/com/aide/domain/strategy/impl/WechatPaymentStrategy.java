package com.aide.domain.strategy.impl;


import com.aide.domain.model.RechargeRecordDo;
import com.aide.domain.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mazg
 * @description 微信支付策略实现
 *
 * 职责：
 * 1. 调用微信支付 API
 * 2. 处理微信支付回调
 * 3. 查询微信支付状态
 * @date 2026/5/28
 * @date 13:08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatPaymentStrategy implements PaymentStrategy {

    // TODO: 注入微信支付 SDK Client
    // private final WxPayService wxPayService;

    private static final Integer PAYMENT_TYPE = 1; // 微信支付类型编码

    @Override
    public String executePayment(RechargeRecordDo rechargeRecord) {
        log.info("发起微信支付，订单号: {}, 金额: {}",
                rechargeRecord.getOrderNo(), rechargeRecord.getAmount());

        try {
            // 1. 构建微信支付请求参数
            Map<String, Object> payParams = buildWechatPayParams(rechargeRecord);

            // 2. 调用微信支付统一下单 API
            // WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
            // request.setOutTradeNo(rechargeRecord.getOrderNo());
            // request.setTotalFee(rechargeRecord.getAmount().multiply(new BigDecimal("100")).intValue());
            // request.setBody("账户充值");
            // WxPayUnifiedOrderResult result = wxPayService.unifiedOrder(request);

            // 模拟返回预支付 ID
            String prepayId = "wx_prepay_id_" + System.currentTimeMillis();

            log.info("微信支付下单成功，订单号: {}, prepayId: {}",
                    rechargeRecord.getOrderNo(), prepayId);

            return prepayId;

        } catch (Exception e) {
            log.error("微信支付失败，订单号: {}", rechargeRecord.getOrderNo(), e);
            throw new RuntimeException("微信支付失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean queryPaymentStatus(String orderNo) {
        log.info("查询微信支付状态，订单号: {}", orderNo);

        try {
            // 调用微信支付查询 API
            // WxPayOrderQueryResult result = wxPayService.queryOrder(orderNo, null);
            // return "SUCCESS".equals(result.getTradeState());

            // 模拟查询结果
            return false;

        } catch (Exception e) {
            log.error("查询微信支付状态失败，订单号: {}", orderNo, e);
            return false;
        }
    }

    @Override
    public void cancelPayment(String orderNo) {
        log.info("取消微信支付，订单号: {}", orderNo);

        try {
            // 调用微信支付关闭订单 API
            // wxPayService.closeOrder(orderNo);

            log.info("微信支付订单已取消，订单号: {}", orderNo);

        } catch (Exception e) {
            log.error("取消微信支付失败，订单号: {}", orderNo, e);
        }
    }

    @Override
    public Integer getPaymentType() {
        return PAYMENT_TYPE;
    }

    @Override
    public boolean verifyCallback(Object callbackData) {
        // 验证微信支付回调签名
        // return wxPayService.parseOrderNotifyResult(callbackData.toString()) != null;

        log.debug("验证微信支付回调签名");
        return true; // 简化实现
    }

    /**
     * 构建微信支付请求参数
     */
    private Map<String, Object> buildWechatPayParams(RechargeRecordDo rechargeRecord) {
        Map<String, Object> params = new HashMap<>();
        params.put("out_trade_no", rechargeRecord.getOrderNo());
        params.put("total_fee", rechargeRecord.getAmount().multiply(new BigDecimal("100")).intValue());
        params.put("body", "账户充值");
        params.put("notify_url", "/api/payment/wechat/callback");
        return params;
    }
}
