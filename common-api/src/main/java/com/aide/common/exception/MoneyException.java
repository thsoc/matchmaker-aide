package com.aide.common.exception;

import com.aide.common.Result.BizCodeEnum;

/**
 * @author mazg
 * @description 金额异常类
 * @date 2026/6/7
 * @date 18:20
 */
public class MoneyException extends BusinessException {

    public MoneyException(BizCodeEnum code, String message) {
        super(code, message);
    }

    // 静态工厂方法：快速创建常见异常
    public static MoneyException moneyRechargeError(Long userId) {
        return new MoneyException(BizCodeEnum.MONEY_RECHARGE_ERROR, "充值金额异常: " + userId);
    }

    // 静态工厂方法：快速创建常见异常
    public static MoneyException moneyDeduceError(Long userId) {
        return new MoneyException(BizCodeEnum.MONEY_DEDUCE_ERROR, "余额不足或用户不存在: " + userId);
    }
}
