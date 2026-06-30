package com.aide.common.exception;


import com.aide.common.Result.BizCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author mazg
 * @description 表示无权限访问（403 Forbidden）
 * @date 2026/6/26
 * @date 19:21
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // 可选注解，简化全局处理
public class ForbiddenException extends RuntimeException {

    private final BizCodeEnum bizCodeEnum;

    public ForbiddenException() {
        super("Forbidden");
        this.bizCodeEnum = BizCodeEnum.NO_PERMISSION;
    }

    public ForbiddenException(String message) {
        super(message);
        this.bizCodeEnum = BizCodeEnum.NO_PERMISSION;
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
        this.bizCodeEnum = BizCodeEnum.NO_PERMISSION;
    }

    public BizCodeEnum getCode() {
        return bizCodeEnum;
    }
}