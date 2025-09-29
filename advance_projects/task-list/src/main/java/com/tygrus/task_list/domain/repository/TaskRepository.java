package com.tygrus.task_list.domain.repository;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;

import java.util.List;
import java.util.Optional;

/**
 * Task Repository接口
 * 
 * 定義Task實體的資料存取抽象
 * 遵循Repository模式，隔離Domain和Infrastructure
 */
public interface TaskRepository {
    
    /**
     * 儲存任務
     */
    Task save(Task task);
    
    /**
     * 根據ID查詢任務
     */
    Optional<Task> findById(TaskId taskId);
    
    /**
     * 檢查任務是否存在
     */
    boolean existsById(TaskId taskId);
    
    /**
     * 刪除任務
     */
    void deleteById(TaskId taskId);
    
    /**
     * 查詢所有任務
     * 為QueryTaskListUseCase提供基礎資料
     * 實際的過濾、排序、分頁邏輯在Application層實現
     */
    List<Task> findAll();
}