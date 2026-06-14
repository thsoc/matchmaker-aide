package com.aide.adapter.VO;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author mazg
 * @description 发放积分请求参数
 * @date 2026/6/14
 * @date 20:34
 */
@Data
public class AddPointsRequest {
    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private Long userId;
    /**
     * 积分数
     */
    @NotBlank(message = "积分数不能为空")
    @Min(value = 0, message = "积分数不能小于0")
    private Integer points;
    /**
     * 备注
     */
    private String remark;
}
