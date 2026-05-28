package com.aide.domain.strategy.impl;

import com.aide.domain.model.RechargeRecordDo;
import com.aide.domain.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mazg
 * @description 测试策略实现
 *
 * 职责：
 * 1. 调用测试API
 * 2. 处理测试回调
 * 3. 查询test支付状态
 * @date 2026/5/28
 * @date 13:10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestPaymentStrategy implements PaymentStrategy {


    private static final Integer PAYMENT_TYPE = 3; // test支付类型编码

    @Override
    public String executePayment(RechargeRecordDo rechargeRecord) {
        log.info("发起test支付，订单号: {}, 金额: {}",
                rechargeRecord.getOrderNo(), rechargeRecord.getAmount());

        try {
            // 1. 构建test请求参数
            Map<String, Object> payParams = buildAlipayParams(rechargeRecord);

            // 2. 调用test统一下单 API
            // AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            // request.setOutTradeNo(rechargeRecord.getOrderNo());
            // request.setTotalAmount(rechargeRecord.getAmount().toString());
            // request.setSubject("账户充值");
            // AlipayTradePagePayResponse response = alipayClient.execute(request);

            // 模拟返回表单内容
            String formContent = "<form>alipay_form_" + System.currentTimeMillis() + "</form>";

            log.info("test下单成功，订单号: {}", rechargeRecord.getOrderNo());

            return formContent;

        } catch (Exception e) {
            log.error("test支付失败，订单号: {}", rechargeRecord.getOrderNo(), e);
            throw new RuntimeException("test支付失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean queryPaymentStatus(String orderNo) {
        log.info("查询test支付状态，订单号: {}", orderNo);

        try {
            // 调用test查询 API
            // AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            // request.setOutTradeNo(orderNo);
            // AlipayTradeQueryResponse response = alipayClient.execute(request);
            // return "TRADE_SUCCESS".equals(response.getTradeStatus());

            // 模拟查询结果
            return false;

        } catch (Exception e) {
            log.error("查询test支付状态失败，订单号: {}", orderNo, e);
            return false;
        }
    }

    @Override
    public void cancelPayment(String orderNo) {
        log.info("取消test支付，订单号: {}", orderNo);

        try {
            // 调用test关闭订单 API
            // AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            // request.setOutTradeNo(orderNo);
            // alipayClient.execute(request);

            log.info("test订单已取消，订单号: {}", orderNo);

        } catch (Exception e) {
            log.error("取消test支付失败，订单号: {}", orderNo, e);
        }
    }

    @Override
    public Integer getPaymentType() {
        return PAYMENT_TYPE;
    }

    @Override
    public boolean verifyCallback(Object callbackData) {
        // 验证test回调签名
        // Map<String, String> params = convertToMap(callbackData);
        // return AlipaySignature.rsaCheckV1(params, alipayPublicKey, charset, signType);

        log.debug("验证test回调签名");
        return true; // 简化实现
    }

    /**
     * 构建test请求参数
     */
    private Map<String, Object> buildAlipayParams(RechargeRecordDo rechargeRecord) {
        Map<String, Object> params = new HashMap<>();
        params.put("out_trade_no", rechargeRecord.getOrderNo());
        params.put("total_amount", rechargeRecord.getAmount().toString());
        params.put("subject", "账户充值");
        params.put("product_code", "FAST_INSTANT_TRADE_PAY");
        params.put("notify_url", "/api/payment/alipay/callback");
        return params;
    }
}
