package com.aide.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author mazg
 * @description 注入Lua 脚本
 * @date 2026/6/21
 * @date 16:33
 */
@Configuration
public class RedisScriptConfig {

    @Bean
    public DefaultRedisScript<Long> deducedCouponScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        // 指定脚本文件路径（相对于 classpath）
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/deducedCoupon.lua")));
        // 指定返回值类型
        script.setResultType(Long.class);
        return script;
    }
}