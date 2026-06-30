package com.aide.common.exception;

import com.aide.common.Result.BizCodeEnum;

/**
 * @author mazg
 * @description 用户异常类
 * @date 2026/6/7
 * @date 18:20
 */
public class UserException extends BusinessException {

    public UserException(BizCodeEnum code, String message) {
        super(code, message);
    }

    // 静态工厂方法：快速创建常见异常
    public static UserException userNotFound(Long userId) {
        return new UserException(BizCodeEnum.USER_NOT_FOUND, "用户没有找到: " + userId);
    }
}
