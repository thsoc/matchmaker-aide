package com.aide.common.dto.type;

import lombok.Getter;

@Getter
public enum PayTypeEnum {
    BALANCE(1, "余额"),
    ALIPAY(2, "支付宝"),
    WECHAT(3, "微信"),

    ;


    // 1. 定义成员变量
    private final Integer code;
    private final String desc;

    PayTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    //根据code获取枚举
    public static PayTypeEnum getByCode(Integer code) {
        for (PayTypeEnum value : PayTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
