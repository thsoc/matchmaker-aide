package com.aide.common.dto.type;

import lombok.Getter;

/**
 * @author mazg
 * @description 订单状态1-待支付 2-支付成功 3-支付失败 4-取消订单 5-订单完成 6-订单关闭
 * @date 13:17 2026/6/28
 * @return 
 **/
@Getter
public enum OrderStatusEnum {
    /** 待支付 - 用户下单后未完成支付 */
    PENDING_PAYMENT("1", "待支付", false, true),

    /** 支付成功 - 已完成资金结算 */
    PAYMENT_SUCCESS("2", "支付成功", true, false),

    /** 支付失败 - 支付渠道明确返回失败 */
    PAYMENT_FAILED("3", "支付失败", true, true),

    /** 用户主动取消订单 */
    CANCELLED_BY_USER("4", "取消订单", true, false),

    /** 订单完成 - 已履约（含确认收货/自动完成） */
    COMPLETED("5", "订单完成", true, false),

    /** 系统关闭订单（超时未支付/风控拦截等） */
    CLOSED_BY_SYSTEM("6", "订单关闭", true, false);

    ;


    // 1. 定义成员变量


    /** 状态码（与数据库/接口保持一致） */
    private final String code;

    /** 业务描述（前端展示用） */
    private final String description;

    /** 是否为终态（不可再变更） */
    private final boolean isTerminal;

    /** 是否允许发起支付（仅待支付状态可支付） */
    private final boolean allowPayment;

    OrderStatusEnum(String code, String description, boolean isTerminal, boolean allowPayment) {
        this.code = code;
        this.description = description;
        this.isTerminal = isTerminal;
        this.allowPayment = allowPayment;
    }


    //根据code获取枚举
    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum value : OrderStatusEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
