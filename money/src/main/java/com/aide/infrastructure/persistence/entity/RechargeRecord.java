package com.aide.infrastructure.persistence.entity;

/**
 * @author mazg
 * @description TODO
 * @date 2026/5/28
 * @date 10:19
 */
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 充值记录表
 * @date 2026/5/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("aide_recharge_record")
public class RechargeRecord {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("status")
    private Integer status;

    @TableField("order_no")
    private String orderNo;

    @TableField("pay_type")
    private Integer payType;

    /**
     * 支付结果（预支付ID或支付表单）
     */
    private String paymentResult;

    @TableField("recharge_time")
    private LocalDateTime rechargeTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("remark")
    private String remark;
}

