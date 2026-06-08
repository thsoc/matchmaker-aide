package com.aide.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    @TableField("money")
    private BigDecimal money;
}
