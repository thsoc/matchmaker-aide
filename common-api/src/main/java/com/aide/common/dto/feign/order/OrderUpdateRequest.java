package com.aide.common.dto.feign.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author mazg
 * @description 订单更新请求参数
 * @date 2026/6/14
 * @date 13:16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequest {
    @NotNull(message = "订单编号不能为空")
    private String orderNo;

}
