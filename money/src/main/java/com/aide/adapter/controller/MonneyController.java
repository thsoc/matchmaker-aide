package com.aide.adapter.controller;

import com.aide.adapter.dto.RechargeRequestDTO;
import com.aide.adapter.dto.RechargeResponseDTO;
import com.aide.common.Result.Result;
import com.aide.domain.service.PaymentContext;
import com.aide.service.MoneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/money") //
@RequiredArgsConstructor
public class MonneyController {

    private final MoneyService moneyService;
    private final PaymentContext paymentContext;


    /**
     * 获取用户金额
     */
    @RequestMapping("/getMoney/{account}")
    public Result getMoney(@PathVariable("account") String account) {
        if (account == null || account.trim().isEmpty()) {
            return Result.error("账户不能为空");
        }
        return Result.success(moneyService.getMoney(account));
    }

    /**
     * 用户充值
     */
    @PostMapping("/recharge")
    public Result<RechargeResponseDTO> recharge(@Valid @RequestBody RechargeRequestDTO request) {
        try {
            RechargeResponseDTO response = moneyService.recharge(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("充值失败: " + e.getMessage());
        }
    }

    /**
     * 微信支付回调接口
     *
     * 注意：
     * 1. 此接口由微信服务器调用，不需要认证
     * 2. 必须快速响应，复杂逻辑异步处理
     * 3. 返回 success 表示接收成功
     */
    @PostMapping("/notify/wechat")
    public String wechatNotify(@RequestBody Map<String, String> notifyData) {
        log.info("收到微信支付回调: {}", notifyData);

        try {
            // 1. 解析回调数据
            String orderNo = notifyData.get("out_trade_no"); // 商户订单号
            String transactionId = notifyData.get("transaction_id"); // 微信交易号
            String resultCode = notifyData.get("result_code"); // 业务结果

            // 2. 判断支付结果
            if ("SUCCESS".equals(resultCode)) {
                // 支付成功
                moneyService.handlePaymentCallback(orderNo, transactionId);
                return "success";
            } else {
                // 支付失败
                String failReason = notifyData.getOrDefault("err_code_des", "支付失败");
                moneyService.handlePaymentFailure(orderNo, failReason);
                return "fail";
            }

        } catch (Exception e) {
            log.error("处理微信支付回调异常", e);
            return "fail"; // 返回 fail 会让微信重试
        }
    }

    /**
     * 支付宝支付回调接口
     *
     * 注意：
     * 1. 支付宝使用表单参数格式
     * 2. 需要验证签名防止伪造
     */
    @PostMapping("/notify/alipay")
    public String alipayNotify(@RequestParam Map<String, String> notifyData) {
        log.info("收到支付宝支付回调: {}", notifyData);

        try {
            // 1. 验证签名（重要！防止伪造请求）
            // boolean signVerified = AlipaySignature.verifySign(notifyData, alipayPublicKey);
            // if (!signVerified) {
            //     log.error("支付宝签名验证失败");
            //     return "fail";
            // }

            // 2. 解析回调数据
            String orderNo = notifyData.get("out_trade_no"); // 商户订单号
            String tradeNo = notifyData.get("trade_no"); // 支付宝交易号
            String tradeStatus = notifyData.get("trade_status"); // 交易状态

            // 3. 判断支付结果
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                // 支付成功
                moneyService.handlePaymentCallback(orderNo, tradeNo);
                return "success";
            } else {
                // 支付失败
                moneyService.handlePaymentFailure(orderNo, "交易状态: " + tradeStatus);
                return "fail";
            }

        } catch (Exception e) {
            log.error("处理支付宝支付回调异常", e);
            return "fail";
        }
    }

    /**
     * 主动查询支付状态（备用方案）
     *
     * 如果回调未收到，前端可以轮询此接口
     */
    @GetMapping("/query/{orderNo}")
    public Result<Map<String, Object>> queryPaymentStatus(@PathVariable String orderNo,
                                                          @RequestParam Integer payType) {
        // 请求示例：GET /money/query/RC123?payType=1
        log.info("查询支付状态，订单号: {}", orderNo);

        try {
            // 调用支付上下文查询状态
             boolean isPaid = paymentContext.queryPaymentStatus(orderNo, payType);

            // 模拟返回
            Map<String, Object> result = new HashMap<>();
            result.put("orderNo", orderNo);
            result.put("status", "PAID");
            result.put("message", "支付成功");


            return Result.success(result);

        } catch (Exception e) {
            log.error("查询支付状态异常，订单号: {}", orderNo, e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }


    /**
     * 扣款
     */
    @PostMapping("/deduct")
    public Result deduct(@RequestParam("userId") Long userId,
                         @RequestParam("amount") BigDecimal amount,
                         @RequestParam("description") String description) {
        try {
            moneyService.deduct(userId, amount, description);
            return Result.success("扣款成功");
        } catch (Exception e) {
            log.error("扣款失败，用户ID: {}, 金额: {}", userId, amount, e);
            return Result.error("扣款失败: " + e.getMessage());
        }
    }



}
