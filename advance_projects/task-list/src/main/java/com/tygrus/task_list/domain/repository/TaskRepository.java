package com.tygrus.task_list.domain.repository;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Task Repository接口
 * 
 * 定義Task實體的資料存取抽象
 * 遵循Repository模式，隔離Domain和Infrastructure
 * 擴展支援批次操作以提升並行處理效能
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
    
    /**
     * 批次查詢任務
     * 為批次操作提供效能優化
     * 
     * @param taskIds 任務ID列表
     * @return 找到的任務映射表 (TaskId -> Task)
     */
    Map<TaskId, Task> findByIds(List<TaskId> taskIds);
    
    /**
     * 批次儲存任務
     * 提供批次更新的原子性操作
     * 
     * @param tasks 要儲存的任務列表
     * @return 儲存後的任務列表
     */
    List<Task> saveAll(List<Task> tasks);
    
    /**
     * 檢查任務是否存在（批次版本）
     * 
     * @param taskIds 任務ID列表
     * @return 存在性映射表 (TaskId -> Boolean)
     */
    Map<TaskId, Boolean> existsByIds(List<TaskId> taskIds);
    
    /**
     * 使用樂觀鎖更新任務
     * 防止並發修改衝突
     * 
     * @param task 要更新的任務
     * @param expectedVersion 期望的版本號
     * @return 更新後的任務
     * @throws com.tygrus.task_list.domain.exception.OptimisticLockException 當版本衝突時
     */
    Task saveWithOptimisticLock(Task task, Long expectedVersion);
}