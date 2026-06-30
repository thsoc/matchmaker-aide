package com.aide.common.exception;

import com.aide.common.Result.BizCodeEnum;

/**
 * @author mazg
 * @description 业务异常基类
 * @date 2026/6/7
 * @date 18:15
 */
public class BusinessException extends Exception{
    private static final long serialVersionUID = 1L;

    /**
     * 业务错误码
     */
    private final BizCodeEnum bizCodeEnum;



    // 构造方法：仅传递错误码和信息
    public BusinessException(BizCodeEnum bizCodeEnum, String message) {
        super(message);
        this.bizCodeEnum = bizCodeEnum;
    }

    // 构造方法：支持自定义HTTP状态码
    public BusinessException(BizCodeEnum bizCodeEnum, String message, int httpStatus) {
        super(message);
        this.bizCodeEnum = bizCodeEnum;
    }

    public BusinessException(String message) {
        super(message);
        this.bizCodeEnum = BizCodeEnum.BUSINESS_ERROR;
    }

    public BizCodeEnum getBizCode() {
        return bizCodeEnum;
    }
}
