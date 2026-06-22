package com.aide.adapter.VO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 用户优惠券返回模型
 * @date 2026/6/22
 * @date 20:12
 */
@Data
@Builder
public class UserCouponVo {
    /**
     * 用户优惠券ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 订单编号
     */
    private String orderNo;


    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券购买时间
     */
    private LocalDateTime buyTime;

    /**
     * 优惠券生效时间
     */
    private LocalDateTime effectiveTime;

    /**
     * 优惠券失效时间
     */
    private LocalDateTime expireTime;


    /**
     * 优惠券折扣方式 0-折扣券 1-满减券 2-代金券
     */
    private Integer couponDiscountType;

    /**
     * 对于代金券，直接存储固定抵扣金额（如 20 元）；对于折扣券，存储折扣比例（如 0.85 代表 85 折）
     */
    private BigDecimal amount;

    /**
     * 使用门槛。满减券和折扣券需要填写（如满 100 可用），代金券如果无门槛则填 0。
     */
    private BigDecimal conditionAmount ;

    /**
     * 折扣上限
     */
    private BigDecimal maxDiscount;

    /**
     * 规则json,用于存储更复杂的扩展规则,暂时不用
     */
    private String ruleJson;



    /**
     * 描述
     */
    private String description;

    /**
     * 0-未使用 1-已使用 2-已过期
     */
    private String status;
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
     * 备注
     */
    private String remark;
}
