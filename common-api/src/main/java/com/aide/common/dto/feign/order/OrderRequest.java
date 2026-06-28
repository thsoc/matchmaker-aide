package com.aide.common.dto.feign.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author mazg
 * @description 订单创建请求参数
 * @date 2026/6/14
 * @date 13:16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long id;

    private Long userId;

    /**
     * 订单类型：1-会员购买,2-抢购优惠券
     */
    @NotNull(message = "订单类型不能为空")
    @Min(value = 1, message = "订单类型不能小于1")
//    @Max(value = 3, message = "订单类型不能大于2")
    private Integer orderType;

    /**
     * 会员类型
     */
    @NotNull(message = "会员类型不能为空")
    @Min(value = 1, message = "会员类型不能小于1")
    @Max(value = 3, message = "会员类型不能大于3")
    private Integer memberType;

//    @NotNull(message = "金额不能为空")
//    @Min(value = 0, message = "金额不能小于0")
//    private BigDecimal amount;

    private String description;
}
