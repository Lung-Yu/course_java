package com.tygrus.task_list.infrastructure.repository;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 任務儲存庫介面
 * 定義任務資料存取的方法
 */
public interface TaskRepository {
    
    /**
     * 儲存任務
     * 
     * @param task 要儲存的任務
     * @return 儲存後的任務
     */
    Task save(Task task);
    
    /**
     * 根據ID查找任務
     * 
     * @param taskId 任務ID
     * @return 任務（如果存在）
     */
    Optional<Task> findById(TaskId taskId);
    
    /**
     * 查找所有任務
     * 
     * @return 所有任務列表
     */
    List<Task> findAll();
    
    /**
     * 根據狀態查找任務
     * 
     * @param statuses 要查找的狀態列表
     * @return 符合狀態的任務列表
     */
    List<Task> findByStatus(TaskStatus... statuses);
    
    /**
     * 查找在指定時間範圍內到期的任務
     * 
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @return 符合條件的任務列表
     */
    List<Task> findTasksWithDueDateBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找逾期任務
     * 
     * @param currentTime 當前時間
     * @return 逾期任務列表
     */
    List<Task> findOverdueTasks(LocalDateTime currentTime);
    
    /**
     * 刪除任務
     * 
     * @param taskId 要刪除的任務ID
     * @return 是否刪除成功
     */
    boolean deleteById(TaskId taskId);
    
    /**
     * 檢查任務是否存在
     * 
     * @param taskId 任務ID
     * @return 是否存在
     */
    boolean existsById(TaskId taskId);
    
    /**
     * 計算任務總數
     * 
     * @return 任務總數
     */
    long count();
    
    /**
     * 根據狀態計算任務數量
     * 
     * @param status 任務狀態
     * @return 該狀態的任務數量
     */
    long countByStatus(TaskStatus status);
}