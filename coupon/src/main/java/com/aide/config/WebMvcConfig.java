package com.aide.config;


import com.aide.common.auth.interceptor.AuthInterceptor;
import com.aide.common.auth.interceptor.UserContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description 用户上下文拦截器配置：只是获取信息，不进行权限校验，权限校验在网关中进行
 * @author mazg
 * @date 2026/5/18
 * @date 11:08
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserContextInterceptor userContextInterceptor;
    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(UserContextInterceptor userContextInterceptor, AuthInterceptor authInterceptor) {
        this.userContextInterceptor = userContextInterceptor;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 第一个拦截器：用户上下文,判断登录
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/coupon/**")
                .excludePathPatterns("/coupon/getCouponList")
                .excludePathPatterns("/error"); // 排除错误路径

        // 第二个拦截器：管理员权限校验
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/coupon/admin/**");

//        // 第三个拦截器：请求日志记录（拦截所有路径）
//        registry.addInterceptor(logInterceptor)
//                .addPathPatterns("/**");
    }
}
