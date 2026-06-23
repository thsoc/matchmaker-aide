package com.aide.domain.model;

import com.aide.common.domain.IClock;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mazg
 * @description 时间聚合根
 * @date 2026/6/23
 * @date 02:47
 */
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class CouponRule {
    /**
     * 0-创建 1-已抢光 2-已删除
     */
    private String status;
    /**
     * 优惠券折扣方式 0-折扣券 1-满减券 2-代金券
     */
    private CouponDiscountType discountType;

    /**
     * 优惠券生效时间
     */
    private LocalDateTime effectiveTime;

    /**
     * 优惠券失效时间
     */
    private LocalDateTime expireTime;

    /**
     * 发行总量
     */
    private Integer totalCount;

    /**
     * 优惠券剩余数量
     */
    private Integer availableStock;


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
     * 规则json
     */
    private String ruleJson;

    private CouponLifeCycleStatus couponLifeCycleStatus;

    /**
     * 【核心领域行为】：动态计算当前的生命周期状态
     * 这是一个纯粹的查询/计算方法，不修改对象自身的任何状态（无副作用）
     */
    public CouponLifeCycleStatus calculateLifeCycleStatus(IClock clock) {
        // 1. 人工干预优先（如被删除/下架）
        if ("-1".equals(status)) {
            couponLifeCycleStatus = CouponLifeCycleStatus.DELETED;
            return CouponLifeCycleStatus.DELETED;
        }
        // 2. 库存耗尽
        if (this.availableStock != null && this.availableStock <= 0) {
            couponLifeCycleStatus = CouponLifeCycleStatus.EXHAUSTED;
            return CouponLifeCycleStatus.EXHAUSTED;
        }
        LocalDateTime now;
        if (clock != null)
            //将clock转为LocalDateTime
            now = clock.getCurrentTime();
        else now = LocalDateTime.now();

        // 3. 时间驱动的状态流转
        if (now.isBefore(this.effectiveTime)) {
            couponLifeCycleStatus = CouponLifeCycleStatus.PENDING;
            return CouponLifeCycleStatus.PENDING;
        } else if (now.isAfter(this.expireTime)) {
            couponLifeCycleStatus = CouponLifeCycleStatus.EXPIRED;
            return CouponLifeCycleStatus.EXPIRED;
        } else {
            couponLifeCycleStatus = CouponLifeCycleStatus.ACTIVE;
            return CouponLifeCycleStatus.ACTIVE;
        }
    }

    /**
     * 【核心领域行为】：判断优惠券是否可用于某笔订单
     * 将业务规则封装在领域对象内部，而不是散落在 Service 的 if-else 中
     */
    public boolean canApplicable(BigDecimal orderAmount, IClock clock) {
        CouponLifeCycleStatus currentStatus = calculateLifeCycleStatus(clock);
        if (currentStatus != CouponLifeCycleStatus.ACTIVE) {
            return false;
        }
        return orderAmount.compareTo(this.conditionAmount) >= 0;
    }

    /**
     * 优惠券是否有库存
     */
    public boolean exhausted() {
        // 2. 库存耗尽
        if (this.availableStock != null && this.availableStock <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 优惠券库存减一
     */
    public void decrease() {
        if (this.availableStock > 0) {
            this.availableStock -= 1;
        }
    }


}
