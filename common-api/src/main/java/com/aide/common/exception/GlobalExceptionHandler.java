package com.aide.common.exception;

import com.aide.common.Result.BizCodeEnum;
import com.aide.common.Result.Result;
import com.aide.common.context.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数异常, traceId: {}", TraceContext.getTraceId(), e);
        return Result.error(BizCodeEnum.PARAM_INVALID.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.warn("实际异常类型: {}", e.getClass().getName());
        log.error("未预期异常, traceId: {}", TraceContext.getTraceId(), e);
        return Result.error("系统异常，请稍后重试");
    }



    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Result<Void> BusinessException(BusinessException e) {
        // 记录warn日志，带上traceId
        log.warn("业务异常, traceId: {}, code: {}, msg: {}",
                TraceContext.getTraceId(), e.getBizCode().getCode(), e.getMessage());
//        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("请求体参数校验失败", e);  // 打印完整异常堆栈
        // 获取第一个校验失败的错误信息
        String message = e.getBindingResult().getFieldError().getDefaultMessage();

        // 返回统一的失败响应
        return Result.error(message);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<?> handleForbidden(ForbiddenException e) {
        log.warn("实际异常类型: {}", e.getClass().getName());
        log.warn("权限不足", e);
        return Result.error(e.getMessage());
    }
}
