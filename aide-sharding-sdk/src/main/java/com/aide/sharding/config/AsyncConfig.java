package com.aide.sharding.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * @author mazg
 * @description TODO
 * @date 2026/6/30
 * @date 14:23
 */
@Configuration("shardingAsyncConfig")
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Autowired
    private Executor shardingdefaultExecutor;  // 注入另一个配置类定义的 Bean

    @Override
    public Executor getAsyncExecutor() {
        return shardingdefaultExecutor;
    }
}
