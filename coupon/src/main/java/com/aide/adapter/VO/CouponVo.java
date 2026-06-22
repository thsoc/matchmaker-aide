package com.aide.adapter.VO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 优惠券返回模型
 * @date 2026/6/22
 * @date 20:12
 */
@Data
@Builder
public class CouponVo {
    /**
     * 优惠券ID
     */
    private Long id;


    /**
     * 优惠券名称
     */
    private String couponName;

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
     * 发行总量
     */
    private Integer totalCount;

    /**
     * 优惠券剩余数量
     */
    private Integer availableStock ;


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
     * 规则json
     */
    private String ruleJson;



    /**
     * 描述
     */
    private String description;
    /**
     *
     */
    private String status;


    /**
     * 备注
     */
    private String remark;
}
