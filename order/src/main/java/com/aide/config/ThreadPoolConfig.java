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

@Component
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
     * 备用专用线程池
     * 
     * 为什么需要专用线程池？
     * 1. 避免与其他异步任务竞争资源
     * 2. 可以针对登录场景独立调优
     * 3. 便于监控和问题排查
     */
    @Bean("TestEventExecutor")
    public Executor TestEventExecutor(BeanFactory beanFactory) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数：根据 CPU 核数和业务特点调整
        executor.setCorePoolSize(10);
        
        // 最大线程数：应对突发流量
        executor.setMaxPoolSize(50);
        
        // 队列容量：缓冲峰值请求
        executor.setQueueCapacity(500);
        
        // 线程存活时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 线程名称前缀（便于调试）
        executor.setThreadNamePrefix("order-event-");

        // 拒绝策略：队列满时由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);  // 最多等待30秒

        executor.initialize();
        return new LazyTraceAsyncTaskExecutor(beanFactory, executor);
    }
}
