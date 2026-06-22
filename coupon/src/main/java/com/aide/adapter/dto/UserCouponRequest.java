package com.aide.adapter.dto;

import com.aide.common.util.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 用户优惠券请求参数
 * @date 2026/6/17
 * @date 18:56
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponRequest extends BasePageQuery {
    /**
     * 用户优惠券ID
     */
    private Long id;

//    @NotNull(message = "用户ID不能为空")
//    private Long userId;
    /**
     * 优惠券类型：1-会员优惠券 2-普通优惠券 3-VIP优惠券
     */
    @NotNull(message = "优惠券类型不能为空")
    @Min(value = 1, message = "优惠券类型不能小于1")
    private Integer couponType;

    /**
     * 优惠券状态：1-未生效 2-已生效 3-已过期
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

//    /**
//     * 优惠券生效时间
//     */
//    @NotNull(message = "优惠券生效时间不能为空")
//    private LocalDateTime effectiveTime;
//    /**
//     * 优惠券失效时间
//     */
//    @NotNull(message = "优惠券失效时间不能为空")
//    private LocalDateTime expireTime;
//
//    /**
//     * 优惠券折扣方式 0-折扣券 1-满减券 2-代金券
//     */
//    @NotNull(message = "优惠券折扣方式不能为空")
//    @Min(value = 0, message = "优惠券折扣方式不能小于0")
//    @Max(value = 2, message = "优惠券折扣方式不能大于2")
//    private Integer couponDiscountType;
//
//    /**
//     * 优惠券折扣
//     */
////    @NotNull(message = "优惠券折扣不能为空")
//    @DecimalMin(value = "0", message = "优惠券折扣不能小于0")
//    @DecimalMax(value = "1", message = "优惠券折扣不能大于1")
//    @Digits(integer = 10, fraction = 2, message = "优惠券折扣最多只能有2位小数")
//    private BigDecimal discount;
//
//    /**
//     * 折扣券使用门槛
//     */
//    @DecimalMin(value = "0", message = "折扣券使用门槛不能小于0")
//    @Digits(integer = 10, fraction = 2, message = "折扣券使用门槛最多只能有2位小数")
//    private BigDecimal fullDiscountAmount;
//
//    /**
//     * 折扣券使用上限
//     */
//    @DecimalMin(value = "0", message = "折扣券使用上限不能小于0")
//    @Digits(integer = 10, fraction = 2, message = "折扣券使用上限最多只能有2位小数")
//    private BigDecimal maxDiscount;
//
//
//    /**
//     * 满减券使用门槛
//     */
////    @NotNull(message = "优惠券金额不能为空")
//    @DecimalMin(value = "0", message = "满减券使用门槛不能小于0")
//    private BigDecimal fullReductionAmount;
//
//    /**
//     * 满减券抵扣金额
//     */
//    @DecimalMin(value = "0", message = "满减券使用金额不能小于0")
//    @Digits(integer = 10, fraction = 2, message = "满减券使用金额最多只能有2位小数")
//    private BigDecimal reductionAmount;
//
//    /**
//     * 代金券面额
//     */
//    @DecimalMin(value = "0", message = "代金券面额不能小于0")
//    @Digits(integer = 10, fraction = 2, message = "代金券面额最多只能有2位小数")
//    private BigDecimal cashCouponAmount;
//
//
//    /**
//     * 优惠券描述
//     */
//    private String description;
}
