package com.lzb.tester.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncThreadPoolConfiguration {

    @Bean("taskExecutor")
    public Executor getThreadPool(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(50);
        executor.setCorePoolSize(1);
        executor.setKeepAliveSeconds(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("worker-service-");
        // 超出MaxPoolSize的任务直接拒绝，抛异常
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }
}
