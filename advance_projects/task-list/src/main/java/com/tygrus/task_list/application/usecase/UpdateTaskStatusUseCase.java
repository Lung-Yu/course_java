package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.dto.UpdateTaskStatusRequest;
import com.tygrus.task_list.application.exception.TaskNotFoundException;
import com.tygrus.task_list.domain.event.TaskCompletedEvent;
import com.tygrus.task_list.domain.event.TaskStatusChangedEvent;
import com.tygrus.task_list.domain.exception.IllegalStatusTransitionException;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 更新任務狀態Use Case (UC-003)
 * 
 * 展示物件導向設計的核心概念：
 * - State Pattern: 狀態轉換邏輯封裝
 * - Domain Events: 事件驅動架構
 * - 業務規則驗證: 封裝在Domain層
 * - 例外處理: 分層的例外處理機制
 * - 交易管理: 確保資料一致性
 * 
 * 這個Use Case是學習OOP封裝、多態和事件處理的絕佳範例
 */
@Service
public class UpdateTaskStatusUseCase {

    private final TaskRepository taskRepository;

    public UpdateTaskStatusUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * 執行任務狀態更新
     * 
     * @param request 狀態更新請求
     * @return 更新後的任務DTO
     * @throws TaskNotFoundException 當任務不存在時
     * @throws IllegalStatusTransitionException 當狀態轉換不合法時
     */
    public TaskDTO execute(UpdateTaskStatusRequest request) {
        // 1. 輸入驗證 (已在DTO的Builder中完成)
        
        // 2. 查找任務
        TaskId taskId = TaskId.of(request.getTaskId());
        Task task = findTaskById(taskId);
        
        // 3. 記錄原始狀態（用於Domain Events）
        TaskStatus originalStatus = task.getStatus();
        
        // 4. 執行狀態轉換（Domain層會驗證業務規則）
        try {
            task.updateStatus(request.getNewStatus());
        } catch (IllegalStateException e) {
            throw new IllegalStatusTransitionException(
                originalStatus.name(), 
                request.getNewStatus().name(),
                e.getMessage()
            );
        }
        
        // 5. 添加Domain Events
        addDomainEvents(task, originalStatus, request);
        
        // 6. 保存任務
        Task savedTask = taskRepository.save(task);
        
        // 7. 轉換為DTO並返回
        return convertToDTO(savedTask);
    }

    /**
     * 查找任務，不存在則拋出例外
     */
    private Task findTaskById(TaskId taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        return taskOptional.orElseThrow(() -> 
            new TaskNotFoundException(taskId.getValue()));
    }

    /**
     * 添加Domain Events
     * 展示事件驅動架構的應用
     */
    private void addDomainEvents(Task task, TaskStatus originalStatus, UpdateTaskStatusRequest request) {
        // 基本狀態變更事件
        TaskStatusChangedEvent statusChangedEvent = new TaskStatusChangedEvent(
            task.getId(),
            originalStatus,
            task.getStatus(),
            request.getReason(),
            request.getUpdatedBy()
        );
        
        task.addDomainEvent(statusChangedEvent);
        
        // 特殊事件：任務完成
        if (task.getStatus() == TaskStatus.COMPLETED) {
            TaskCompletedEvent completedEvent = new TaskCompletedEvent(
                task.getId(),
                request.getUpdatedBy()
            );
            
            task.addDomainEvent(completedEvent);
        }
    }

    /**
     * 轉換Task為TaskDTO
     * 展示DTO轉換的最佳實踐
     */
    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
            .id(task.getId().getValue())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus())
            .priority(task.getPriority())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }
}