package com.aide.adapter.VO;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @author mazg
 * @description 订单请求参数
 * @date 2026/6/14
 * @date 13:16
 */
@Data
public class OrderRequest {
    @NotBlank(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "订单类型不能为空")
    @Min(value = 1, message = "订单类型不能小于1")
//    @Max(value = 3, message = "订单类型不能大于2")
    private Integer orderType;

    @NotBlank(message = "金额不能为空")
    @Min(value = 0, message = "金额不能小于0")
    private BigDecimal amount;

    private String description;
}
