package com.lzb.tester;

import com.lzb.tester.common.JdbcPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class WorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class,args);
    }

//    private static Map<Integer,ClientInfo> container = new HashMap<>();
//
//    @Bean("clientContainer")
//    public Map<Integer,ClientInfo> getContainer(){
//        return container;
//    }
//
    @Scheduled(cron = "0 0 8 * * ?")
    public void clearContainer(){
        JdbcPool.taskQueue.clear();
        JdbcPool.variableMap.clear();
        JdbcPool.pool().clear();
    }
}
