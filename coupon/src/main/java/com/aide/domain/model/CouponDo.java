package com.aide.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author mazg
 * @description 订单领域对象
 * @date 2026/6/14
 * @date 16:20
 */
@Data
@Builder
@Getter
public class CouponDo {
    /**
     * 订单ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 订单类型：1-会员购买
     */
    private Integer orderType;
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 描述
     */
    private String description;
    /**
     * 订单状态：1-待支付 2-支付成功 3-支付失败 4-取消订单 5-订单完成 6-订单关闭
     */
    private String status;
    /**
     * 创建时间
     */
    @TableField("create_time")
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
     * 备注
     */
    private String remark;
    /**
     * 版本号
     */
    private String version;

    /**
     * @description 初始化订单信息(购买会员)
     * @date 2026/6/14
     * @date 16:23
     */
    public void initFromBuyMenber() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.status = "2";
        this.remark = "购买会员";
        this.createBy = this.userId.toString();
        this.updateBy = this.userId.toString();
    }

    public void validateFromBuyMenber() {
        if (this.userId == null || this.userId <= 0) {
            throw new IllegalArgumentException("用户ID无效");
        }
        if (this.orderType == null || this.orderType < 1 || this.orderType > 3) {
            throw new IllegalArgumentException("会员类型无效，请选择1-3");
        }
        if (this.orderNo == null || this.orderNo.isEmpty()) {
            throw new IllegalArgumentException("订单编号无效");
        }
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金额无效");
        }
    }

}
