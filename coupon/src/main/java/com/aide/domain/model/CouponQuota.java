package com.aide.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 优惠券其他字段
 * @date 2026/6/23
 * @date 03:15
 */
@Getter
@Builder(toBuilder = true)
public class CouponQuota {

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 描述
     */
    private String description;

    /**
     * 备注
     */
    private String remark;
}
