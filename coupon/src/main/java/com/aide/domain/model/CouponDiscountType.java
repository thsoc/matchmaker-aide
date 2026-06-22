package com.aide.domain.model;

/**
 * 优惠券类型枚举
 */
public enum CouponDiscountType {
    DISCOUNT(0, "折扣券"),
    FULL_REDUCTION(1, "满减券"),
    VOUCHER(2, "代金券");

    // 1. 定义成员变量
    private final Integer code;
    private final String desc;

    CouponDiscountType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    //根据code获取枚举
    public static CouponDiscountType getByCode(Integer code) {
        for (CouponDiscountType value : CouponDiscountType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}