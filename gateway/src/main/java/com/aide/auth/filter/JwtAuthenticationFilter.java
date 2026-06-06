package com.aide.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mazg
 * @description 权限认证过滤器
 * @date 2026/5/25
 * @date 17:44
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final SecretKey jwtSecretKey;
    private final ObjectMapper objectMapper;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ACCOUNT_HEADER = "X-User-Account";
    private static final String USER_NAME_HEADER = "X-User-Username";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_SEX_HEADER = "X-User-Sex";
    private static final String USER_CACHE_PREFIX = "user:cache:";

    private RedisTemplate<String, Object> redisTemplate;

    public JwtAuthenticationFilter(SecretKey jwtSecretKey, ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        this.jwtSecretKey = jwtSecretKey;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("开始进行权限认证");

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 放行公开接口（如登录、注册）
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            return chain.filter(exchange);
        }

        log.info("网关请求: {}", path);

        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("请求未携带有效的Token，路径: {}", path);
            return setUnauthorizedResponse(exchange, "未登录或登录已过期");
        }
        String cacheKey = USER_CACHE_PREFIX + token;
        //先判断redis里面有没有token
        if (redisTemplate.opsForValue().get(cacheKey) == null) {
            log.warn("请求未携带有效的Token，路径: {}", path);
            return setUnauthorizedResponse(exchange, "未登录或登录已过期");
        }

        try {

            Claims claims = parseToken(token);

            if (claims == null) {
                log.warn("Token解析失败或已过期，路径: {}", path);
                return setUnauthorizedResponse(exchange, "Token无效或已过期");
            }

            String userId = claims.getSubject();
            String account = claims.get("account", String.class);
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            String sex = claims.get("sex", String.class);

            log.info("Token验证成功 - 用户ID: {}, 账号: {}, 路径: {}", userId, account, path);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(USER_ID_HEADER, userId)
                    .header(USER_ACCOUNT_HEADER, account)
                    .header(USER_NAME_HEADER, username)
                    .header(USER_ROLE_HEADER, role)
                    .header(USER_SEX_HEADER, sex)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        }catch (ExpiredJwtException e) {
            // 明确知道是 Token 过期了
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } catch (Exception e) {
            log.error("Token验证异常，路径: {}", path, e);
            return setUnauthorizedResponse(exchange, "Token验证失败");
        }
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", 401);
        errorResponse.put("message", message);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            log.error("写入响应失败", e);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
