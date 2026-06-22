package com.aide.domain.model;

import com.aide.adapter.dto.UserCouponRequest;
import com.aide.domain.model.strategy.CouponConverterFactory;
import com.aide.domain.model.strategy.CouponConverterStrategy;
import com.aide.domain.model.strategy.UserCouponConverterFactory;
import com.aide.domain.model.strategy.UserCouponConverterStrategy;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 用户优惠券领域模型
 * @date 2026/6/14
 * @date 16:05
 */
@Builder(toBuilder = true)
@Getter
public class UserCouponDo {
    /**
     * 用户优惠券ID
     */
    private final Long id;

    /**
     * 用户ID
     */
    private final Long userId;

    /**
     * 优惠券ID
     */
    private final Long couponId;

    /**
     * 订单编号
     */
    private final String orderNo;


    /**
     * 优惠券名称
     */
    private final String couponName;

    /**
     * 优惠券购买时间
     */
    private final LocalDateTime buyTime;

    /**
     * 优惠券生效时间
     */
    private final LocalDateTime effectiveTime;

    /**
     * 优惠券失效时间
     */
    private final LocalDateTime expireTime;


    /**
     * 优惠券折扣方式 0-折扣券 1-满减券 2-代金券
     */
    private final Integer couponDiscountType;

    /**
     * 对于代金券，直接存储固定抵扣金额（如 20 元）；对于折扣券，存储折扣比例（如 0.85 代表 85 折）
     */
    private final BigDecimal amount;

    /**
     * 使用门槛。满减券和折扣券需要填写（如满 100 可用），代金券如果无门槛则填 0。
     */
    private final BigDecimal conditionAmount;

    /**
     * 折扣上限
     */
    private final BigDecimal maxDiscount;

    /**
     * 规则json,用于存储更复杂的扩展规则,暂时不用
     */
    private final String ruleJson;


    /**
     * 描述
     */
    private final String description;

    /**
     * 0-未使用 1-已使用 2-已过期
     */
    private final String status;
    /**
     * 创建时间
     */
    private final LocalDateTime createTime;
    /**
     * 更新时间
     */
    private final LocalDateTime updateTime;
    /**
     * 删除时间
     */
    private final LocalDateTime deleteTime;

    /**
     * 创建人
     */
    private final String createBy;

    /**
     * 修改人
     */
    private final String updateBy;

    /**
     * 备注
     */
    private final String remark;

    // 1. 持有一个静态的工厂引用，由外部（应用层）在启动时注入
    private static UserCouponConverterFactory factory;

    // 2. 提供一个静态方法，供应用层在启动时调用，完成依赖注入
    public static void injectFactory(UserCouponConverterFactory couponFactory) {
        UserCouponDo.factory = couponFactory;
    }

    public static UserCouponDo createFromDTO(UserCouponRequest userCouponRequest, Long userId) {
        // 防御性编程，确保工厂已注入
        if (factory == null) {
            throw new IllegalStateException("UserCouponConverterFactory 未注入到 CouponDo 中！");
        }

        // 4. 委托给策略工厂去处理具体的创建逻辑
        UserCouponConverterStrategy converter = factory.getConverter(userCouponRequest.getCouponType());
        UserCouponDo userCouponDo = converter.convert(userCouponRequest);

        return userCouponDo;
    }

}
