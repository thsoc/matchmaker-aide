package com.aide.domain.model;

/**
 * @author mazg
 * @description 优惠券生命周期状态（领域枚举）
 * @date 2026/6/22
 * @date 23:48
 */
public enum CouponLifeCycleStatus {

    PENDING("未生效"),
    ACTIVE("可抢购"),
    EXHAUSTED("已抢光"),
    EXPIRED("已过期"),
    DELETED("已删除");

    private final String desc;

    CouponLifeCycleStatus(String desc) {
        this.desc = desc;
    }

    // 领域行为：判断当前状态是否允许被领取
    public boolean canClaimable() {
        return this == ACTIVE;
    }
}