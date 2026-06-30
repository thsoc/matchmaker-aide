package com.aide.common.Result;

/**
 * @author mazg
 * @description 返回状态枚举
 * @date 2026/6/30
 * @date 11:45
 */
public enum BizCodeEnum {
    SUCCESS(20000, "成功"),


    PARAM_INVALID(40010, "参数校验失败"),



    BUSINESS_ERROR(40020, "业务逻辑异常"),
    MONEY_RECHARGE_ERROR(40021, "充值金额异常"),
    MONEY_DEDUCE_ERROR(40022, "余额不足或用户不存在"),
    USER_NOT_FOUND(40023, "用户没有找到"),




    NO_PERMISSION(40030, "无权限"),



    SYSTEM_ERROR(50000, "系统内部错误"),
    CLIENT_ERROR(50001, "客户端错误"),


    ;

    private final int code;
    private final String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
