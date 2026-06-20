package com.aide.common.aop;

import com.aide.common.aspect.Idempotent;
import com.aide.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author mazg
 * @description 幂等AOP,兜底
 * @date 2026/6/20
 * @date 20:57
 */
@Aspect
@Component
public class IdempotentAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;
//    @Autowired
//    private SpelKeyGenerator spelKeyGenerator;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // 1. 解析 SpEL 获取具体的业务 Key
        String keyValue = SpelKeyGenerator.parseKey(idempotent.key(), joinPoint);
        String redisKey = "biz:idempotent:" + keyValue;

        // 2. 尝试设置 Key (利用 setIfAbsent 实现原子性)
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "1", idempotent.expire(), TimeUnit.SECONDS);

        // 3. 如果设置失败，说明是重复请求
        if (Boolean.FALSE.equals(success)) {
            throw new BusinessException(idempotent.message());
        }

        try {
            // 4. 执行业务逻辑
            return joinPoint.proceed();
        } catch (Exception e) {
            // 5. 业务执行失败，删除 Key 允许重试 (可选：根据异常类型决定是否删除)
            redisTemplate.delete(redisKey);
            throw e;
        }
    }
}



