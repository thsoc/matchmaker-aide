package com.aide.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 订单实体类
 * @date 2026/6/14
 * @date 16:05
 */
@Data
@Builder
@Getter
@TableName("aide_order")
public class Coupon {
    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 订单类型：1-会员购买
     */
    @TableField("order_type")
    private Integer orderType;
    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;
    /**
     * 描述
     */
    @TableField("description")
    private String description;
    /**
     * 订单状态：1-待支付 2-支付成功 3-支付失败 4-取消订单 5-订单完成 6-订单关闭
     */
     @TableField("status")
    private String status;
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
     * 删除时间
     */
     @TableField("delete_time")
    private LocalDateTime deleteTime;

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
