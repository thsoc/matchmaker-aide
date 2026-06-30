package com.aide.infrastructure.remote.fallback;

import com.aide.common.Result.BizCodeEnum;
import com.aide.common.Result.Result;
import com.aide.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/30
 * @date 16:19
 */
@Slf4j
public abstract class AbstractFallbackFactory<T> implements FallbackFactory<T> {

    /** 读操作降级：返回兜底 Result，不抛异常（读操作不影响事务） */
    protected <R> Result<R> defaultFail(String serviceName, String method, Throwable cause) {
        log.warn("[{}] 服务降级, method={}, cause={}", serviceName, method,
                cause != null ? cause.getMessage() : "unknown", cause);
        throw new RuntimeException(serviceName + " 服务不可用，触发全局回滚", cause);
//        return Result.error(BizCodeEnum.SYSTEM_ERROR.getCode(),
//                serviceName + " 服务暂不可用");
    }

//    /** 写操作降级：抛异常，让调用方 @GlobalTransactional 回滚 */
//    protected RuntimeException seataRollback(String serviceName, String method, Throwable cause) {
//        log.warn("[{}] 写操作降级触发全局回滚, method={}, cause={}", serviceName, method,
//                cause != null ? cause.getMessage() : "unknown", cause);
//        return new RuntimeException(serviceName + " 服务不可用，触发全局回滚", cause);
//    }
    // 子类根据需要重写 FallbackFactory的create 方法
}
