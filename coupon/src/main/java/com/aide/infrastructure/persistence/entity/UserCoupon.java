package com.aide.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 用户优惠券实体类
 * @date 2026/6/14
 * @date 16:05
 */
@Data
@Builder
@Getter
@TableName("aide_user_coupon")
public class UserCoupon {
    /**
     * 用户优惠券ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 优惠券ID
     */
    @TableField("coupon_id")
    private Long couponId;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;


    /**
     * 优惠券名称
     */
    @TableField("coupon_name")
    private String couponName;

    /**
     * 优惠券购买时间
     */
    @TableField("buy_time")
    private LocalDateTime buyTime;

    /**
     * 优惠券生效时间
     */
    @TableField("effective_time")
    private LocalDateTime effectiveTime;

    /**
     * 优惠券失效时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;


    /**
     * 优惠券折扣方式 0-折扣券 1-满减券 2-代金券
     */
    @TableField("coupon_discount_type")
    private Integer couponDiscountType;

    /**
     * 对于代金券，直接存储固定抵扣金额（如 20 元）；对于折扣券，存储折扣比例（如 0.85 代表 85 折）
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 使用门槛。满减券和折扣券需要填写（如满 100 可用），代金券如果无门槛则填 0。
     */
    @TableField("condition_amount")
    private BigDecimal conditionAmount ;

    /**
     * 折扣上限
     */
    @TableField("max_discount")
    private BigDecimal maxDiscount;

    /**
     * 规则json,用于存储更复杂的扩展规则,暂时不用
     */
    @TableField("rule_json")
    private String ruleJson;



    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 0-未使用 1-已使用 2-已过期
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
