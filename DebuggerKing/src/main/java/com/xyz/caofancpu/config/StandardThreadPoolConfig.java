package com.xyz.caofancpu.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * FileName: GlobalThreadPoolConfig
 * Author:   caofanCPU
 * Date:     2018/9/5 9:20
 */

@Configuration
@EnableAsync
public class StandardThreadPoolConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(StandardThreadPoolConfig.class);
    
    @Bean(name = "standardThreadPool")
    public ThreadPoolTaskExecutor standardThreadPool() {
        logger.info("初始化服务线程池");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(16);
        // 指定最大线程数
        executor.setMaxPoolSize(64);
        // 队列中最大的数目
        executor.setQueueCapacity(16);
        // 线程名称前缀
        executor.setThreadNamePrefix("debugger_");
        // 线程池拒绝策略
        executor.setRejectedExecutionHandler(rejectedExecutionHandler());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 初始化
        executor.initialize();
        logger.info("完成服务线程池启动");
        return executor;
    }
    
    @Bean
    public RejectedExecutionHandler rejectedExecutionHandler() {
        //rejection-policy：当pool已经达到max size的时候，如何处理新任务
        //CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        //对拒绝task的处理策略
        return new ThreadPoolExecutor.CallerRunsPolicy();
    }
    
    
}
