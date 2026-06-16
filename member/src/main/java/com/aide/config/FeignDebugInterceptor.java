package com.aide.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import org.springframework.stereotype.Component;

/**
 * @author mazg
 * @description 验证码服务调用链路追踪拦截器
 * @date 2026/6/16
 * @date 21:21
 */
@Component
public class FeignDebugInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        System.err.println(">>> Feign Headers: " + template.headers());
        System.err.println(">>> Current XID: " + RootContext.getXID());
    }
}