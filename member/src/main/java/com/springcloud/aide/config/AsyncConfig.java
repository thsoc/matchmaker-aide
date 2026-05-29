package com.springcloud.aide.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * 备用专用线程池
     * 
     * 为什么需要专用线程池？
     * 1. 避免与其他异步任务竞争资源
     * 2. 可以针对登录场景独立调优
     * 3. 便于监控和问题排查
     */
    @Bean("TestEventExecutor")
    public Executor TestEventExecutor() {
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
        executor.setThreadNamePrefix("login-event-");
        
        // 拒绝策略：队列满时由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);  // 最多等待30秒

        executor.initialize();
        return executor;
    }
}
