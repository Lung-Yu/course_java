package com.tygrus.task_list.infrastructure.repository;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
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
    
    @Override
    public List<Task> findByStatus(TaskStatus... statuses) {
        if (statuses == null || statuses.length == 0) {
            return new ArrayList<>();
        }
        
        Set<TaskStatus> statusSet = Set.of(statuses);
        
        return tasks.values().stream()
            .filter(task -> statusSet.contains(task.getStatus()))
            .collect(Collectors.toList());
    }
    
    @Override
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
    
    @Override
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
    
    @Override
    public boolean deleteById(TaskId taskId) {
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
    
    @Override
    public long count() {
        return tasks.size();
    }
    
    @Override
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
}