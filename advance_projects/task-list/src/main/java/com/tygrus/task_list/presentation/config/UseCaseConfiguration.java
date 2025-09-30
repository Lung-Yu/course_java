package com.tygrus.task_list.presentation.config;

import com.tygrus.task_list.application.usecase.*;
import com.tygrus.task_list.domain.repository.TaskRepository;
import com.tygrus.task_list.infrastructure.cache.StatisticsCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Use Case配置類
 * 
 * 配置Application層的Use Case Bean
 * 遵循依賴注入原則，將業務邏輯與框架解耦
 */
@Configuration
public class UseCaseConfiguration {
    
    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository) {
        return new CreateTaskUseCase(taskRepository);
    }
    
    @Bean
    public QueryTaskListUseCase queryTaskListUseCase(TaskRepository taskRepository) {
        return new QueryTaskListUseCase(taskRepository);
    }
    
    @Bean
    public UpdateTaskStatusUseCase updateTaskStatusUseCase(TaskRepository taskRepository) {
        return new UpdateTaskStatusUseCase(taskRepository);
    }
    
    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskRepository taskRepository) {
        return new DeleteTaskUseCase(taskRepository);
    }
    
    @Bean
    public TaskStatisticsUseCase taskStatisticsUseCase(
            TaskRepository taskRepository,
            StatisticsCache statisticsCache) {
        return new TaskStatisticsUseCase(taskRepository, statisticsCache);
    }
}