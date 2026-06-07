package com.aide.common.exception;

/**
 * @author mazg
 * @description 业务异常基类
 * @date 2026/6/7
 * @date 18:15
 */
public class BusinessException extends Exception{
    private static final long serialVersionUID = 1L;

    /**
     * 业务错误码（例如：40001-参数错误，50001-业务逻辑异常）
     */
    private final int code;

    /**
     * HTTP状态码（适配响应状态，默认400）
     */
    private final int httpStatus;

    // 构造方法：仅传递错误码和信息
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = 400; // 业务异常通常属于客户端请求问题，默认返回400 Bad Request
    }

    // 构造方法：支持自定义HTTP状态码
    public BusinessException(int code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

}
