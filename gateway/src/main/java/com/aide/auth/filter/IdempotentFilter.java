package com.aide.auth.filter;

import com.aide.auth.config.GatewayFingerprintGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;


/**
 * @author mazg
 * @description 防重试过滤器,防“短时间内的重复请求”,
 * 如果第一次请求成功但网络超时，第二次请求在 10 秒后到达，网关的 TTL 已经过期，网关会放行。
 * @date 2026/6/20
 * @date 20:51
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> dedupScript; // 注入 Lua 脚本
    private static final int MAX_BODY_CACHE_SIZE = 64 * 1024; // 限制最大缓存 64KB，防止 OOM

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String userId = request.getHeaders().getFirst("X-User-ID");
        if (userId == null) userId = "anonymous";

        // 1. 判断是否需要读取 Body（仅针对 JSON/XML 等包含 Body 的请求）
        MediaType contentType = request.getHeaders().getContentType();
        boolean hasBody = contentType != null &&
                (contentType.includes(MediaType.APPLICATION_JSON) || contentType.includes(MediaType.APPLICATION_XML));

        if (!hasBody) {
            // 无 Body 场景（如 GET 请求）：直接同步执行防重校验
            String fingerprint = GatewayFingerprintGenerator.generate(userId, path, request.getQueryParams(), null);
            return checkAndProceed(exchange, chain, fingerprint);
        }

        // 2. 有 Body 场景：异步读取 Body -> 缓存并重新包装请求 -> 执行防重校验
        String finalUserId = userId;
        return DataBufferUtils.join(request.getBody())
                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                .flatMap(dataBuffer -> {
                    // 限制读取大小，防止恶意超大请求撑爆网关内存
                    // 将流中的数据提取到 byte[] 数组中（相当于在内存中缓存）
                    byte[] bytes = new byte[Math.min(dataBuffer.readableByteCount(), MAX_BODY_CACHE_SIZE)];
                    dataBuffer.read(bytes);
                    // 必须释放原始 buffer，防止内存泄漏
                    DataBufferUtils.release(dataBuffer);

                    String bodyStr = new String(bytes, StandardCharsets.UTF_8);
                    String fingerprint = GatewayFingerprintGenerator.generate(finalUserId, path, request.getQueryParams(), bodyStr);

                    // 重新包装请求，把读取过的 Body 回填，保证下游微服务能正常接收
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
                        }
                    };

                    // 先校验防重，通过后再将包装后的请求传递给下游
                    return checkAndProceed(exchange.mutate().request(decorator).build(), chain, fingerprint);
                });
    }

    /**
     * 统一执行 Redis 防重校验逻辑
     */
    private Mono<Void> checkAndProceed(ServerWebExchange exchange, GatewayFilterChain chain, String fingerprint) {
        // 假设 Lua 脚本返回 1 表示首次请求，返回 0 表示重复请求
        Long result = redisTemplate.execute(dedupScript, Collections.singletonList(fingerprint), "5000");

        if (result != null && result == 0L) {
            log.warn("网关拦截到重复请求，指纹: {}", fingerprint);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 优先级尽量靠前，确保在业务过滤器之前执行，在鉴权之后
        return -100;
    }
}