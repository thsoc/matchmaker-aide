package com.aide.common.auth.interceptor;


import com.aide.common.auth.context.UserContext;
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


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求中获取用户角色信息
        String userRole = request.getHeader("user-role");
        if (userRole != null && userRole.equals("admin")) {
            return true;
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
