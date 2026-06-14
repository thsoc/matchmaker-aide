package com.aide.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 积分实体类
 * @date 2026/6/14
 * @date 20:49
 */
@Data
@Getter
@Builder
@TableName("aide_points")
public class Points {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;
    /**
     * 积分类型，1-会员购买
     */
    @TableField("points_type")
    private Integer pointsType;
    /**
     * 积分
     */
    @TableField("points")
    private Integer points;
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
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
     * 版本号
     */
    @Version
    @TableField("version")
    private Integer version;

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
