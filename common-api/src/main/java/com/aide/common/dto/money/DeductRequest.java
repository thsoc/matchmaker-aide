package com.aide.common.dto.money;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author mazg
 * @description 扣款请求参数
 * @date 2026/6/17
 * @date 16:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeductRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotNull(message = "金额不能为空")
    @Min(value = 0, message = "金额不能小于0")
    private BigDecimal amount;
    private String description;
}
