package com.aide.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 用户资金表
 * @date 2026/5/25
 * @date 16:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("aide_money")
public class Money {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("available_money")
    private BigDecimal availableMoney;

    @TableField("frozen_money")
    private BigDecimal frozenMoney;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 修改人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    /**
     * 版本号
     */
    @Version
    private String version;


    /**
     * 预留字段
     */
    @TableField("reserved1")
    private String reserved1;

    @TableField("reserved2")
    private String reserved2;

    @TableField("reserved3")
    private String reserved3;

    @TableField("reserved4")
    private String reserved4;

    @TableField("reserved5")
    private Integer reserved5;

    @TableField("reserved6")
    private Integer reserved6;

    @TableField("reserved7")
    private Integer reserved7;

    @TableField("reserved8")
    private Integer reserved8;
}
