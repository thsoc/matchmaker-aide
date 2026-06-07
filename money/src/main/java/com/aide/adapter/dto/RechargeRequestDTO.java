package com.aide.adapter.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * @description 充值请求DTO
 * @author mazg
 * @date 2026/5/28
 * @date 10:25
 */
@Data
public class RechargeRequestDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 充值金额（最小0.01）
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;

    /**
     * 支付方式：1-微信 2-支付宝 3-银行卡
     */
    @NotNull(message = "支付方式不能为空")
//    @Pattern(regexp = "^[1-3]$", message = "支付类型1-3") 只能用于string
    @Min(value = 1, message = "支付类型最小值为1")
    @Max(value = 3, message = "支付类型最大值为3")
    private Integer payType;

    /**
     * 备注
     */
    private String remark;
}
