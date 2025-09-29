package com.tygrus.task_list.infrastructure.repository;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Domain TaskRepository 適配器
 * 將 InMemoryTaskRepository 適配為 domain.repository.TaskRepository 介面
 */
@Component
public class DomainTaskRepositoryAdapter implements TaskRepository {
    
    private final InMemoryTaskRepository inMemoryTaskRepository;
    
    public DomainTaskRepositoryAdapter(InMemoryTaskRepository inMemoryTaskRepository) {
        this.inMemoryTaskRepository = inMemoryTaskRepository;
    }
    
    @Override
    public Task save(Task task) {
        return inMemoryTaskRepository.save(task);
    }
    
    @Override
    public Optional<Task> findById(TaskId taskId) {
        return inMemoryTaskRepository.findById(taskId);
    }
    
    @Override
    public boolean existsById(TaskId taskId) {
        return inMemoryTaskRepository.existsById(taskId);
    }
    
    @Override
    public void deleteById(TaskId taskId) {
        inMemoryTaskRepository.deleteById(taskId);
    }
    
    @Override
    public List<Task> findAll() {
        return inMemoryTaskRepository.findAll();
    }
    
    @Override
    public Map<TaskId, Task> findByIds(List<TaskId> taskIds) {
        return inMemoryTaskRepository.findByIds(taskIds);
    }
    
    @Override
    public List<Task> saveAll(List<Task> tasks) {
        return inMemoryTaskRepository.saveAll(tasks);
    }
    
    @Override
    public Map<TaskId, Boolean> existsByIds(List<TaskId> taskIds) {
        return inMemoryTaskRepository.existsByIds(taskIds);
    }
    
    @Override
    public Task saveWithOptimisticLock(Task task, Long expectedVersion) {
        return inMemoryTaskRepository.saveWithOptimisticLock(task, expectedVersion);
    }
}