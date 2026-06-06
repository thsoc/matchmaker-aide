package com.aide.config;

import com.aide.common.auth.interceptor.UserContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description 用户上下文拦截器配置：只是获取信息，不进行权限校验，权限校验在网关中进行
 * @author mazg 
 * @date 2026/5/25
 * @date 17:24 
 */
@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {
    private final UserContextInterceptor userContextInterceptor;

    public WebMvcConfig(UserContextInterceptor userContextInterceptor) {
        this.userContextInterceptor = userContextInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/error"); // 排除错误路径
    }
}
