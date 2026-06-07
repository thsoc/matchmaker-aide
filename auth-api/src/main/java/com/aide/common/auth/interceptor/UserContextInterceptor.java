package com.aide.common.auth.interceptor;

import com.aide.common.auth.context.UserContext;
import com.aide.common.auth.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author mazg
 * @description 用户上下文拦截器
 * 每个服务接收网关请求时，会携带用户信息
 * @date 2026/6/6
 * @date 19:25
 */
@Slf4j
@Configuration
public class UserContextInterceptor implements HandlerInterceptor {
    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求中获取用户信息
        String userId = request.getHeader("X-User-Id");
        String userAccount = request.getHeader("X-User-Account");
        String userUsername = request.getHeader("X-User-Username");
        String userRole = request.getHeader("user-role");
        String userSex = request.getHeader("X-User-Sex");

        // 2. 封装为本地上下文对象，存入 ThreadLocal
        if (userId != null) {
            UserInfo userInfo = UserInfo.builder()
                    .id(Long.parseLong(userId))
                    .account(userAccount)
                    .username(userUsername)
                    .role(userRole)
                    .sex(userSex)
                    .build();
            UserContext.setUser(userInfo);
        }
        return true;

    }
    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
