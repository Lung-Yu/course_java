package com.tygrus.task_list.infrastructure.repository;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.repository.TaskRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 記憶體內任務儲存庫實作
 * 用於演示和測試目的
 */
@Repository
public class InMemoryTaskRepository implements TaskRepository {
    
    private final Map<TaskId, Task> tasks = new ConcurrentHashMap<>();
    
    @Override
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        tasks.put(task.getId(), task);
        return task;
    }
    
    @Override
    public Optional<Task> findById(TaskId taskId) {
        if (taskId == null) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(tasks.get(taskId));
    }
    
    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }
    
    public List<Task> findByStatus(TaskStatus... statuses) {
        if (statuses == null || statuses.length == 0) {
            return new ArrayList<>();
        }
        
        Set<TaskStatus> statusSet = Set.of(statuses);
        
        return tasks.values().stream()
            .filter(task -> statusSet.contains(task.getStatus()))
            .collect(Collectors.toList());
    }
    
    public List<Task> findTasksWithDueDateBetween(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return new ArrayList<>();
        }
        
        return tasks.values().stream()
            .filter(task -> task.getDueDate() != null)
            .filter(task -> !task.getDueDate().isBefore(startTime) && !task.getDueDate().isAfter(endTime))
            .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
            .collect(Collectors.toList());
    }
    
    public List<Task> findOverdueTasks(LocalDateTime currentTime) {
        if (currentTime == null) {
            return new ArrayList<>();
        }
        
        return tasks.values().stream()
            .filter(task -> task.getDueDate() != null)
            .filter(task -> task.getDueDate().isBefore(currentTime))
            .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
            .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
            .collect(Collectors.toList());
    }
    
    /**
     * 刪除任務（返回 boolean 版本，用於舊有程式碼相容性）
     */
    public boolean deleteByIdWithResult(TaskId taskId) {
        if (taskId == null) {
            return false;
        }
        
        return tasks.remove(taskId) != null;
    }
    
    @Override
    public boolean existsById(TaskId taskId) {
        if (taskId == null) {
            return false;
        }
        
        return tasks.containsKey(taskId);
    }
    
    public long count() {
        return tasks.size();
    }
    
    public long countByStatus(TaskStatus status) {
        if (status == null) {
            return 0;
        }
        
        return tasks.values().stream()
            .filter(task -> task.getStatus() == status)
            .count();
    }
    
    /**
     * 清空所有任務 (僅用於測試)
     */
    public void clear() {
        tasks.clear();
    }
    
    /**
     * 批量儲存任務
     */
    @Override
    public List<Task> saveAll(List<Task> taskList) {
        if (taskList == null) {
            return new ArrayList<>();
        }
        
        List<Task> savedTasks = new ArrayList<>();
        for (Task task : taskList) {
            savedTasks.add(save(task));
        }
        return savedTasks;
    }
    
    // === 實作 domain.repository.TaskRepository 的額外方法 ===
    
    /**
     * 批次查詢任務
     */
    @Override
    public Map<TaskId, Task> findByIds(List<TaskId> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<TaskId, Task> result = new HashMap<>();
        for (TaskId taskId : taskIds) {
            Task task = tasks.get(taskId);
            if (task != null) {
                result.put(taskId, task);
            }
        }
        return result;
    }
    
    /**
     * 檢查任務是否存在（批次版本）
     */
    @Override
    public Map<TaskId, Boolean> existsByIds(List<TaskId> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<TaskId, Boolean> result = new HashMap<>();
        for (TaskId taskId : taskIds) {
            result.put(taskId, tasks.containsKey(taskId));
        }
        return result;
    }
    
    /**
     * 使用樂觀鎖更新任務
     * 注意：此實作簡化了樂觀鎖邏輯，實際生產環境需要更複雜的版本控制
     */
    @Override
    public Task saveWithOptimisticLock(Task task, Long expectedVersion) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        // 簡化版樂觀鎖：實際專案中需要在Task實體中維護version欄位
        Task existingTask = tasks.get(task.getId());
        if (existingTask != null && expectedVersion != null) {
            // 這裡應該檢查版本號，簡化處理
            // throw new OptimisticLockException("Version conflict");
        }
        
        return save(task);
    }
    
    /**
     * 刪除任務（實作 domain repository 介面）
     */
    @Override
    public void deleteById(TaskId taskId) {
        if (taskId != null) {
            tasks.remove(taskId);
        }
    }
}