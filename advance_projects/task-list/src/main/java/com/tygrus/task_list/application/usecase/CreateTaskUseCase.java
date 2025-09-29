package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.CreateTaskRequest;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 創建任務Use Case
 * 
 * 實現UC-001: 創建任務的業務邏輯
 * 整合Domain模型進行任務創建
 */
public class CreateTaskUseCase {
    
    private final TaskRepository taskRepository;
    
    public CreateTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = Objects.requireNonNull(taskRepository, "TaskRepository cannot be null");
    }
    
    /**
     * 執行創建任務業務邏輯
     */
    public TaskDTO execute(CreateTaskRequest request) {
        // 驗證請求
        validateRequest(request);
        
        // 建構Task實體
        Task task = buildTaskFromRequest(request);
        
        // 儲存到資料庫
        Task savedTask = taskRepository.save(task);
        
        // 轉換為DTO並回傳
        return TaskDTO.fromTask(savedTask);
    }
    
    private void validateRequest(CreateTaskRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateTaskRequest cannot be null");
        }
        
        validateTitle(request.getTitle());
        validateDescription(request.getDescription());
        validateDueDate(request.getDueDate());
    }
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("Task title cannot exceed 255 characters");
        }
    }
    
    private void validateDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Task description cannot exceed 1000 characters");
        }
    }
    
    private void validateDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
    }
    
    private Task buildTaskFromRequest(CreateTaskRequest request) {
        return Task.builder()
            .id(TaskId.generate())
            .title(request.getTitle())
            .description(request.getDescription())
            .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
            .dueDate(request.getDueDate())
            .createdAt(LocalDateTime.now())
            .build();
    }
}