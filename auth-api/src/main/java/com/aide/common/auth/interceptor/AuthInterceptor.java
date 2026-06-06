//package com.aide.common.auth.interceptor;
//
//
//import com.aide.common.Result.Result;
//import com.aide.common.auth.context.UserContext;
//import com.aide.common.auth.entity.UserInfo;
//import com.aide.common.auth.service.CacheService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.crypto.SecretKey;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//
///**
// * @author mazg
// * @description 认证拦截器
// * 如果每个服务都做验证，考虑使用网关做统一验证，这个方式性能不高，并且不利于服务间调用
// * @date 2026/5/18
// * @date 10:48
// */
//@Slf4j
//@Component
//public class AuthInterceptor implements HandlerInterceptor {
//
//    private final CacheService cacheService;
//    private final SecretKey jwtSecretKey;
//    private final ObjectMapper objectMapper;
//
//    public AuthInterceptor(CacheService cacheService,
//                          SecretKey jwtSecretKey,
//                          ObjectMapper objectMapper) {
//        this.cacheService = cacheService;
//        this.jwtSecretKey = jwtSecretKey;
//        this.objectMapper = objectMapper;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = extractToken(request);
//
//        if (!StringUtils.hasText(token)) {
//            log.warn("请求未携带Token，路径: {}", request.getRequestURI());
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json;charset=UTF-8");
//            response.getWriter().write(objectMapper.writeValueAsString(
//                Result.error(401, "未登录或登录已过期")
//            ));
//            return false;
//        }
//
//        try {
//            UserInfo user = validateTokenAndGetUser(token);
//
//            if (user == null) {
//                log.warn("Token无效或已过期，路径: {}", request.getRequestURI());
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                response.setContentType("application/json;charset=UTF-8");
//                response.getWriter().write(objectMapper.writeValueAsString(
//                    Result.error(401, "登录已过期，请重新登录")
//                ));
//                return false;
//            }
//
//            UserContext.setUser(user);
//            log.debug("用户认证成功，用户ID: {}, 路径: {}", user.getId(), request.getRequestURI());
//            return true;
//
//        } catch (Exception e) {
//            log.error("Token验证失败", e);
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json;charset=UTF-8");
//            response.getWriter().write(objectMapper.writeValueAsString(
//                Result.error(401, "Token验证失败")
//            ));
//            return false;
//        }
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
//                               Object handler, Exception ex) throws Exception {
//        UserContext.clear();
//    }
//
//    /**
//     * 从请求中提取Token
//     */
//    private String extractToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return request.getParameter("token");
//    }
//
//    /**
//     * 验证Token并获取用户信息
//     */
//    private UserInfo validateTokenAndGetUser(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(jwtSecretKey)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String userId = claims.getSubject();
//            if (!StringUtils.hasText(userId)) {
//                return null;
//            }
//
//            UserInfo cachedUser = cacheService.getUserCache(token, UserInfo.class);
//            if (cachedUser != null) {
//                return cachedUser;
//            }
//
//            log.warn("缓存中未找到用户信息，Token: {}", token);
//            return null;
//
//        } catch (Exception e) {
//            log.error("解析Token失败", e);
//            return null;
//        }
//    }
//}
