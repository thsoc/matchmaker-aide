package com.aide.domain.model;

import com.aide.adapter.dto.UserCouponRequest;
import com.aide.infrastructure.persistence.entity.UserCoupon;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 用户优惠券领域模型
 * @date 2026/6/14
 * @date 16:05
 */
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserCouponDo {
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
    private BigDecimal conditionAmount;

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


    public static UserCouponDo createFromDTO(UserCouponRequest request, Long userId) {
        UserCouponDoBuilder builder = UserCouponDo.builder().couponName(request.getCouponName());
        // 明确指定为代金券
        // 代金券通常无门槛，或门槛为0
        // 代金券没有折扣上限
        if (2 == request.getCouponType()) {
            builder.couponDiscountType(2).conditionAmount(BigDecimal.ZERO).maxDiscount(null);
        }
        // 明确指定为折扣券
        if (0 == request.getCouponType()) {
            builder.couponDiscountType(0);
        }
        // 明确指定为满减券
        // 满减券通常没有折扣上限
        if (1 == request.getCouponType()) {
            builder.couponDiscountType(1).maxDiscount(null);
        }
        UserCouponDo userCouponDo = builder.build();
        return userCouponDo;
    }

    public static UserCouponDo fromPOJO(UserCoupon entity) {
        return UserCouponDo.builder()
                .amount(entity.getAmount())
                .buyTime(entity.getBuyTime())
                .couponDiscountType(entity.getCouponDiscountType())
                .couponId(entity.getCouponId())
                .couponName(entity.getCouponName())
                .couponDiscountType(entity.getCouponDiscountType())
                .conditionAmount(entity.getConditionAmount())
                .createTime(entity.getCreateTime())
                .description(entity.getDescription())
                .effectiveTime(entity.getEffectiveTime())
                .expireTime(entity.getExpireTime())
                .id(entity.getId())
                .maxDiscount(entity.getMaxDiscount())
                .orderNo(entity.getOrderNo())
                .ruleJson(entity.getRuleJson())
                .status(entity.getStatus())
                .updateTime(entity.getUpdateTime())
                .userId(entity.getUserId())
                .build();
    }
}
