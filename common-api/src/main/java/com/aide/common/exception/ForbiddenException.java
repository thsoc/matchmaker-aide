package com.aide.common.exception;


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

    private final int code;

    public ForbiddenException() {
        super("Forbidden");
        this.code = HttpStatus.FORBIDDEN.value();
    }

    public ForbiddenException(String message) {
        super(message);
        this.code = HttpStatus.FORBIDDEN.value();
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
        this.code = HttpStatus.FORBIDDEN.value();
    }

    public int getCode() {
        return code;
    }
}