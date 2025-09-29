package com.tygrus.task_list.config;

import com.tygrus.task_list.infrastructure.repository.TaskRepository;
import com.tygrus.task_list.infrastructure.repository.InMemoryTaskRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 任務提醒系統配置
 * 配置必要的 Bean 和啟用異步處理與調度功能
 */
@Configuration
@EnableAsync
@EnableScheduling
public class TaskReminderConfig {
    
    /**
     * 提供預設的 TaskRepository 實作
     * 只有在沒有其他 TaskRepository Bean 時才會創建
     */
    @Bean
    @ConditionalOnMissingBean(TaskRepository.class)
    public TaskRepository taskRepository() {
        return new InMemoryTaskRepository();
    }
    
    /**
     * 配置異步執行器
     * 用於異步通知處理
     */
    @Bean("taskReminderExecutor")
    public Executor taskReminderExecutor() {
        return Executors.newFixedThreadPool(10);
    }
}