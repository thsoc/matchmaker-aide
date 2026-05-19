package com.aide.configuration;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author mazg
 * @description TODO
 * @date 2025/12/8
 * @date 14:34
 */
//@Order(-1)
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final Logger log = LogManager.getLogger(AuthorizeFilter.class);
    final private String authParamKey = "Authorization";
    final private String authParamValue = "admin";
    final private String truthHead = "Truth";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String truthHeadValue = headers.get(truthHead) != null ? headers.get(truthHead).get(0) : null;
        log.info("truth:{}", truthHeadValue);

        request.mutate().headers((header) -> {
            header.set(truthHead, "test3");
        }).build();

        //1.获取请求参数
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        String authorization = queryParams.getFirst(authParamKey);
        //2.判断参数值
        if (StringUtils.equals(authorization, authParamValue)) {
            //3。是，放行
            return chain.filter(exchange);
        }
//        //设置返回码
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        //4.否。拦截
//        return exchange.getResponse().setComplete();
        //3。是，放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
