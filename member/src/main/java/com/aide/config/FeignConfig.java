package com.aide.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mazg
 * @description openfeign 配置类
 * @date 2026/6/17
 * @date 12:46
 */
//@Configuration
public class FeignConfig {

    /**
     * 添加 seata 拦截器,多个拦截器时，显示注入
     * @return
     */
//    @Bean
    public ErrorDecoder seataFeignErrorDecoder() {
        return new SeataFeignErrorDecoder();
    }
}
