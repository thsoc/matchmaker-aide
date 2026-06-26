package com.aide.common.auth.interceptor;


import com.aide.common.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author mazg
 * @description 管理员权限验证拦截器
 * 如果每个服务都做验证，考虑使用网关做统一验证，这个方式性能不高，并且不利于服务间调用
 * 这边不做认证拦截器，只做是否是管理员的权限
 * @date 2026/5/18
 * @date 10:48
 */
@Slf4j
@Component
//@Conditional(JwtAuthenticationFilter.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET) //web应用下才生效, 非web应用下不生效,gateway下不生效
public class AuthInterceptor implements HandlerInterceptor {
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_ROLE_ADMIN = "ADMIN";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求中获取用户角色信息
        String userRole = request.getHeader(USER_ROLE_HEADER);
        if (userRole == null || !userRole.equalsIgnoreCase(USER_ROLE_ADMIN)) {
            log.info("用户角色不是admin，无法操作，当前角色是：{}", userRole);
            throw new ForbiddenException("Admin only"); // 自定义异常

        }
        log.info("当前角色是：{}", userRole);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }
}
