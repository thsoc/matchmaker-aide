package com.aide.common.dto.feign.points;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author mazg
 * @description 发放积分请求参数
 * @date 2026/6/14
 * @date 20:34
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddPointsRequest {
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    /**
     * 积分数
     */
    @NotNull(message = "积分数不能为空")
    @Min(value = 0, message = "积分数不能小于0")
    private Integer points;
    /**
     * 备注
     */
    private String remark;
}
