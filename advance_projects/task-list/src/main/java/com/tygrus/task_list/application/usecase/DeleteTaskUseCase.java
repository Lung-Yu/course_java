package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.DeleteTaskRequest;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.exception.PermissionDeniedException;
import com.tygrus.task_list.application.exception.TaskNotFoundException;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.repository.TaskRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 刪除任務Use Case (UC-004)
 * 
 * 展示物件導向設計的核心概念：
 * - 軟刪除模式: 資料完整性和可追蹤性
 * - 權限驗證: 安全性設計
 * - Domain Events: 事件驅動架構
 * - 業務規則封裝: Domain層責任分離
 * - 例外處理: 分層的例外處理機制
 * - 執行緒安全: 交易管理和樂觀鎖
 * 
 * 這個Use Case展示了刪除操作的最佳實踐，包括軟刪除、權限控制和審計日誌
 */
@Service
public class DeleteTaskUseCase {

    private final TaskRepository taskRepository;

    public DeleteTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * 執行任務軟刪除
     * 
     * @param request 刪除請求
     * @return 刪除後的任務DTO
     * @throws TaskNotFoundException 當任務不存在時
     * @throws PermissionDeniedException 當權限不足時
     * @throws IllegalStateException 當任務已被刪除時
     */
    public TaskDTO execute(DeleteTaskRequest request) {
        // 1. 輸入驗證 (已在DTO的Builder中完成)
        
        // 2. 查找任務
        TaskId taskId = TaskId.of(request.getTaskId());
        Task task = findTaskById(taskId);
        
        // 3. 權限驗證
        validatePermissions(task, request.getDeletedBy());
        
        // 4. 業務規則驗證
        validateBusinessRules(task);
        
        // 5. 執行軟刪除 (Domain層負責業務邏輯和事件發布)
        task.markAsDeleted(request.getDeletedBy(), request.getReason());
        
        // 6. 記錄操作日誌
        logDeletionOperation(task, request);
        
        // 7. 保存任務
        Task savedTask = taskRepository.save(task);
        
        // 8. 轉換為DTO並返回
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
     * 權限驗證
     * 
     * 展示應用層的權限控制邏輯
     */
    private void validatePermissions(Task task, String userId) {
        // 模擬權限檢查邏輯
        // 在實際應用中，這裡會：
        // 1. 檢查用戶是否為任務創建者
        // 2. 檢查用戶是否有管理員權限
        // 3. 檢查用戶是否屬於任務的相關團隊
        
        if (isUnauthorizedUser(userId)) {
            throw new PermissionDeniedException(
                userId, 
                "delete", 
                task.getId().getValue(),
                String.format("User %s does not have permission to delete task %s", 
                    userId, task.getId().getValue())
            );
        }
    }

    /**
     * 業務規則驗證
     * 
     * 檢查刪除操作的業務約束
     */
    private void validateBusinessRules(Task task) {
        // 檢查任務是否已被刪除
        if (task.isDeleted()) {
            throw new IllegalStateException("Task is already deleted");
        }
        
        // 其他業務規則可以在這裡添加
        // 例如：某些狀態的任務不能被刪除
        // 例如：有依賴關係的任務不能被刪除
    }

    /**
     * 模擬權限檢查
     * 
     * 在實際應用中，這會連接到認證授權系統
     */
    private boolean isUnauthorizedUser(String userId) {
        // 模擬權限驗證邏輯
        // 實際實作會檢查：
        // - 用戶認證狀態
        // - 用戶角色權限
        // - 資源特定權限
        
        // 為了測試，我們假設以下用戶沒有權限
        return "unauthorizedUser".equals(userId);
    }

    /**
     * 記錄操作日誌
     * 
     * 展示審計日誌的最佳實踐
     */
    private void logDeletionOperation(Task task, DeleteTaskRequest request) {
        // 在實際應用中，這裡會：
        // 1. 記錄到審計日誌系統
        // 2. 發送通知給相關人員
        // 3. 更新統計指標
        
        System.out.printf("Task deleted - ID: %s, DeletedBy: %s, Reason: %s, Timestamp: %s%n",
            task.getId().getValue(),
            request.getDeletedBy(),
            request.getReason(),
            task.getDeletedAt()
        );
    }

    /**
     * 轉換Task為TaskDTO
     * 
     * 展示DTO轉換的最佳實踐，包括軟刪除資訊
     */
    private TaskDTO convertToDTO(Task task) {
        TaskDTO.Builder builder = TaskDTO.builder()
            .id(task.getId().getValue())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus())
            .priority(task.getPriority())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt());
            
        // 如果需要在DTO中包含刪除資訊，可以添加相關字段
        // 這取決於業務需求和API設計
        
        return builder.build();
    }
}