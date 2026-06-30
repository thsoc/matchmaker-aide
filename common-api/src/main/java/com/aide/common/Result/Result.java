package com.aide.common.Result;

import com.aide.common.context.TraceContext;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private String traceId;
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(BizCodeEnum.SUCCESS.getMessage());
        result.setTraceId(TraceContext.getTraceId());
        result.setData(null);
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(BizCodeEnum.SUCCESS.getCode());
        result.setMessage(BizCodeEnum.SUCCESS.getMessage());
        result.setTraceId(TraceContext.getTraceId());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(BizCodeEnum.SUCCESS.getCode());
        result.setMessage(message);
        result.setTraceId(TraceContext.getTraceId());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(BizCodeEnum.SYSTEM_ERROR.getCode());
        result.setMessage(message);
        result.setTraceId(TraceContext.getTraceId());
        result.setData(null);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTraceId(TraceContext.getTraceId());
        result.setData(null);
        return result;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == BizCodeEnum.SUCCESS.getCode();
    }
}
