package com.aide.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceAsyncTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration("paymentAsyncConfig")
@EnableAsync
public class ThreadPoolConfig {

    /**
     * 默认线程池（供 @Async 不带名称时使用）
     */
    @Bean("defaultExecutor")
    @Primary
    public Executor defaultExecutor(BeanFactory beanFactory) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("default-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return new LazyTraceAsyncTaskExecutor(beanFactory, executor);
    }


    /**
     * 支付回调专用线程池
     *
     * 什么需要专用线程池？
     * 1. 支付回调是关键业务，需要独立资源保障
     * 2. 避免与其他异步任务竞争
     * 3. 可以针对支付场景独立调优和监控
     */
    @Bean("paymentCallbackExecutor")
    public Executor paymentCallbackExecutor(BeanFactory beanFactory) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：支付回调通常不需要太多并发
        executor.setCorePoolSize(5);

        // 最大线程数：应对高峰期
        executor.setMaxPoolSize(20);

        // 队列容量：缓冲峰值回调请求
        executor.setQueueCapacity(200);

        // 线程存活时间（秒）
        executor.setKeepAliveSeconds(60);

        // 线程名称前缀（便于调试）
        executor.setThreadNamePrefix("payment-callback-");

        // 拒绝策略：队列满时由调用线程执行（保证不丢失）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();
        return new LazyTraceAsyncTaskExecutor(beanFactory, executor);
    }
}
