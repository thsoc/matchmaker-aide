package com.aide.adapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 充值响应DTO
 * @date 2026/5/28
 * @date 10:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeResponseDTO {

    /**
     * 充值记录ID
     */
    private Long rechargeId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 充值状态
     */
    private Integer status;

    /**
     * 充值订单号
     */
    private String orderNo;

    /**
     * 充值时间
     */
    private LocalDateTime rechargeTime;

    /**
     * 支付结果（预支付ID或支付表单）
     */
    private String paymentResult;
}
