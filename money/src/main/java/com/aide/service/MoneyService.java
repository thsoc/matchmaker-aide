package com.aide.service;

import com.aide.adapter.dto.RechargeRequestDTO;
import com.aide.adapter.dto.RechargeResponseDTO;
import com.aide.common.exception.MoneyException;
import com.aide.infrastructure.persistence.entity.Money;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * @author mazg
 * @description 金额服务接口
 * @date 2026/5/28  15:53
 */
public interface MoneyService{
    /**
     * 获取用户余额
     */
    Long getMoney(String account);

    /**
     * 用户充值
     *
     * @param request 充值请求
     * @return 充值结果
     */
    RechargeResponseDTO recharge(RechargeRequestDTO request) throws MoneyException;

    /**
     * 查询充值记录
     *
     * @param rechargeId 充值记录ID
     * @return 充值记录详情
     */
    RechargeResponseDTO getRechargeRecord(Long rechargeId);

    /**
     * 处理支付回调
     *
     * @param orderNo          订单号
     * @param transactionId    交易ID
     */
    void handlePaymentCallback(String orderNo, String transactionId);

    /**
     * 处理支付失败
     *
     * @param orderNo          订单号
     * @param failureReason    失败原因
     */
    void handlePaymentFailure(String orderNo, String failureReason);

    /**
     * 扣款
     *
     * @param userId      用户ID
     * @param amount      扣款金额
     * @param description 扣款描述
     */
    void deduct(Long userId, BigDecimal amount, String description);
}
