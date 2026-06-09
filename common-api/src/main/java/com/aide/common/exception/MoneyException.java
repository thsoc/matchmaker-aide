package com.aide.common.exception;

/**
 * @author mazg
 * @description 金额异常类
 * @date 2026/6/7
 * @date 18:20
 */
public class MoneyException extends BusinessException {
    public static final int MONEY_RECHARGE_ERROR = 50001;//todo，后面需要定义统一枚举
    public static final int MONEY_DEDUCE_ERROR = 50002;//todo，后面需要定义统一枚举

    public MoneyException(int code, String message) {
        super(code, message);
    }

    // 静态工厂方法：快速创建常见异常
    public static MoneyException moneyRechargeError(Long userId) {
        return new MoneyException(MONEY_RECHARGE_ERROR, "充值金额异常: " + userId);
    }

    // 静态工厂方法：快速创建常见异常
    public static MoneyException moneyDeduceError(Long userId) {
        return new MoneyException(MONEY_DEDUCE_ERROR, "余额不足或用户不存在: " + userId);
    }
}
