package com.aide.common.dto.type;

import lombok.Getter;

/**
 * @author mazg
 * @description 订单类型
 * @date 13:17 2026/6/28
 * @return 
 **/
@Getter
public enum OrderTypeEnum {
    BUY_MEMBER(1, "购买会员"),
    BUY_COUPON(2, "购买优惠券"),

    ;


    // 1. 定义成员变量
    private final Integer code;
    private final String desc;

    OrderTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    //根据code获取枚举
    public static OrderTypeEnum getByCode(Integer code) {
        for (OrderTypeEnum value : OrderTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
