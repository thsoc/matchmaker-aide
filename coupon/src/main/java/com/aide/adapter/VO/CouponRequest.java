package com.aide.adapter.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 优惠券请求参数
 * @date 2026/6/17
 * @date 18:56
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponRequest {
//    @NotNull(message = "用户ID不能为空")
//    private Long userId;
    /**
     * 优惠券类型：1-会员优惠券 2-普通优惠券 3-VIP优惠券
     */
    @NotNull(message = "优惠券类型不能为空")
    @Min(value = 1, message = "优惠券类型不能小于1")
    private Integer couponType;
    /**
     * 优惠券折扣方式 0-折扣券 1-满减券 2-代金券
     */
    @NotNull(message = "优惠券折扣方式不能为空")
    @Min(value = 0, message = "优惠券折扣方式不能小于0")
    @Max(value = 2, message = "优惠券折扣方式不能大于2")
    private Integer couponDiscountType;

    /**
     * 优惠券状态：1-待使用 2-已使用 3-已过期
     */
    @NotNull(message = "优惠券状态不能为空")
    @Min(value = 1, message = "优惠券状态不能小于1")
    @Max(value = 3, message = "优惠券状态不能大于3")
    private Integer couponStatus;

    /**
     * 优惠券名称
     */
    @NotNull(message = "优惠券名称不能为空")
    private String couponName;

    /**
     * 优惠券生效时间
     */
    @NotNull(message = "优惠券生效时间不能为空")
    private LocalDateTime effectiveTime;
    /**
     * 优惠券失效时间
     */
    @NotNull(message = "优惠券失效时间不能为空")
    private LocalDateTime expireTime;

    /**
     * 优惠券金额
     */
//    @NotNull(message = "优惠券金额不能为空")
    @Min(value = 0, message = "优惠券金额不能小于0")
    private BigDecimal amount;

    /**
     * 优惠券折扣
     */
//    @NotNull(message = "优惠券折扣不能为空")
    @Min(value = 0, message = "优惠券折扣不能小于0")
    @Max(value = 1, message = "优惠券折扣不能大于1")
    private BigDecimal discount;

    /**
     * 优惠券使用限制：1-无使用限制 2-指定商品可用 3-指定会员可用
     */
    @NotNull(message = "优惠券使用限制不能为空")
    @Min(value = 1, message = "优惠券使用限制不能小于1")
    @Max(value = 3, message = "优惠券使用限制不能大于3")
    private Integer couponUseLimit;


    /**
     * 优惠券描述
     */
    private String description;
}
