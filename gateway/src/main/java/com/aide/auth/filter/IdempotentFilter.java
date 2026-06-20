package com.aide.auth.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * @author mazg
 * @description 防重试过滤器,防“短时间内的重复请求”,
 * 如果第一次请求成功但网络超时，第二次请求在 10 秒后到达，网关的 TTL 已经过期，网关会放行。
 * @date 2026/6/20
 * @date 20:51
 */
@Component
public class IdempotentFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 默认防重窗口 3秒
    private static final long DEFAULT_TTL_MS = 3000L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod().name();

        // 仅拦截写操作
        if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        // 1. 构建请求指纹 (这里简化为 URI + User-ID，实际应包含 Body Hash)
        String userId = request.getHeaders().getFirst("X-User-ID");
        String fingerprint = String.format("dedup:%s:%s", userId, request.getURI().getPath());

        // 2. 执行 Lua 脚本进行原子去重
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/dedup.lua"));
        script.setResultType(Long.class);

        Long result = redisTemplate.execute(script, Collections.singletonList(fingerprint), String.valueOf(DEFAULT_TTL_MS));

        // 3. 判断结果，拦截或放行
        if (result != null && result == 0L) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete(); // 直接返回 429
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1; // 保证在鉴权等过滤器之后执行
    }
}