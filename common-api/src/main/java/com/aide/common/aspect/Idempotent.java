package com.aide.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    // 幂等Key的SpEL表达式，例如 "#orderCreateDTO.orderNo"
    String key();
    // 过期时间，单位秒
    int expire() default 60;
    // 提示信息
    String message() default "请勿重复提交";
}