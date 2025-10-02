package com.tygrus.task_list.infrastructure.persistence.repository;

import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.repository.TaskRepository;
import com.tygrus.task_list.infrastructure.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PostgreSQL implementation of TaskRepository
 * 將 JPA Repository 適配為 Domain Repository
 */
@Repository("postgresqlTaskRepository")
@Primary
@Transactional
public class PostgreSQLTaskRepository implements TaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLTaskRepository.class);

    private final JpaTaskRepository jpaTaskRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public PostgreSQLTaskRepository(JpaTaskRepository jpaTaskRepository) {
        this.jpaTaskRepository = jpaTaskRepository;
    }

    @Override
    public Task save(Task task) {
        logger.debug("Saving task with ID: {}", task.getId().getValue());
        
        try {
            // 檢查是否為已存在的實體
            Optional<TaskEntity> existingEntity = jpaTaskRepository.findById(task.getId().getValue());
            
            TaskEntity entity;
            if (existingEntity.isPresent()) {
                // 更新現有實體，保留 version 等 JPA 管理的字段
                entity = existingEntity.get();
                entity.updateFromDomain(task);
            } else {
                // 新建實體
                entity = TaskEntity.fromDomain(task);
            }
            
            TaskEntity savedEntity = jpaTaskRepository.save(entity);
            
            logger.debug("Successfully saved task with ID: {}", savedEntity.getId());
            return savedEntity.toDomain();
        } catch (Exception e) {
            logger.error("Failed to save task with ID: {}", task.getId().getValue(), e);
            throw new RuntimeException("Failed to save task", e);
        }
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        logger.debug("Finding task by ID: {}", id.getValue());
        
        try {
            Optional<TaskEntity> entity = jpaTaskRepository.findByIdAndNotDeleted(id.getValue());
            Optional<Task> result = entity.map(TaskEntity::toDomain);
            
            if (result.isPresent()) {
                logger.debug("Found task with ID: {}", id.getValue());
            } else {
                logger.debug("No task found with ID: {}", id.getValue());
            }
            
            return result;
        } catch (Exception e) {
            logger.error("Failed to find task by ID: {}", id.getValue(), e);
            throw new RuntimeException("Failed to find task", e);
        }
    }

    @Override
    public List<Task> findAll() {
        logger.debug("Finding all active tasks");
        
        try {
            List<TaskEntity> entities = jpaTaskRepository.findAllActive();
            List<Task> tasks = entities.stream()
                .map(TaskEntity::toDomain)
                .collect(Collectors.toList());
            
            logger.debug("Found {} active tasks", tasks.size());
            return tasks;
        } catch (Exception e) {
            logger.error("Failed to find all tasks", e);
            throw new RuntimeException("Failed to find tasks", e);
        }
    }

    @Override
    public void deleteById(TaskId id) {
        logger.debug("Soft deleting task with ID: {}", id.getValue());
        
        try {
            LocalDateTime now = LocalDateTime.now();
            int updated = jpaTaskRepository.softDeleteById(id.getValue(), now, now);
            
            if (updated > 0) {
                logger.debug("Successfully soft deleted task with ID: {}", id.getValue());
            } else {
                logger.warn("No task found to delete with ID: {}", id.getValue());
            }
        } catch (Exception e) {
            logger.error("Failed to delete task with ID: {}", id.getValue(), e);
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    @Override
    public boolean existsById(TaskId id) {
        logger.debug("Checking if task exists with ID: {}", id.getValue());
        
        try {
            boolean exists = jpaTaskRepository.existsByIdAndNotDeleted(id.getValue());
            logger.debug("Task exists with ID {}: {}", id.getValue(), exists);
            return exists;
        } catch (Exception e) {
            logger.error("Failed to check task existence with ID: {}", id.getValue(), e);
            throw new RuntimeException("Failed to check task existence", e);
        }
    }

    @Override
    public Map<TaskId, Task> findByIds(List<TaskId> taskIds) {
        logger.debug("Finding tasks by IDs: {}", taskIds.size());
        
        try {
            List<String> ids = taskIds.stream()
                .map(TaskId::getValue)
                .collect(Collectors.toList());
            
            List<TaskEntity> entities = jpaTaskRepository.findAllById(ids);
            Map<TaskId, Task> result = entities.stream()
                .filter(entity -> !entity.getDeleted())
                .collect(Collectors.toMap(
                    entity -> TaskId.of(entity.getId()),
                    TaskEntity::toDomain
                ));
            
            logger.debug("Found {} tasks for {} requested IDs", result.size(), taskIds.size());
            return result;
        } catch (Exception e) {
            logger.error("Failed to find tasks by IDs", e);
            throw new RuntimeException("Failed to find tasks by IDs", e);
        }
    }

    @Override
    public List<Task> saveAll(List<Task> tasks) {
        logger.debug("Batch saving {} tasks", tasks.size());
        
        try {
            List<TaskEntity> entities = tasks.stream()
                .map(TaskEntity::fromDomain)
                .collect(Collectors.toList());
            
            List<TaskEntity> savedEntities = jpaTaskRepository.saveAll(entities);
            List<Task> savedTasks = savedEntities.stream()
                .map(TaskEntity::toDomain)
                .collect(Collectors.toList());
            
            logger.debug("Successfully batch saved {} tasks", savedTasks.size());
            return savedTasks;
        } catch (Exception e) {
            logger.error("Failed to batch save tasks", e);
            throw new RuntimeException("Failed to batch save tasks", e);
        }
    }

    @Override
    public Map<TaskId, Boolean> existsByIds(List<TaskId> taskIds) {
        logger.debug("Checking existence for {} task IDs", taskIds.size());
        
        try {
            List<String> ids = taskIds.stream()
                .map(TaskId::getValue)
                .collect(Collectors.toList());
            
            List<TaskEntity> existingEntities = jpaTaskRepository.findAllById(ids);
            Map<String, Boolean> existingMap = existingEntities.stream()
                .filter(entity -> !entity.getDeleted())
                .collect(Collectors.toMap(
                    TaskEntity::getId,
                    entity -> true
                ));
            
            Map<TaskId, Boolean> result = taskIds.stream()
                .collect(Collectors.toMap(
                    taskId -> taskId,
                    taskId -> existingMap.getOrDefault(taskId.getValue(), false)
                ));
            
            logger.debug("Checked existence for {} task IDs", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Failed to check task existence by IDs", e);
            throw new RuntimeException("Failed to check task existence by IDs", e);
        }
    }

    @Override
    public Task saveWithOptimisticLock(Task task, Long expectedVersion) {
        logger.debug("Saving task with optimistic lock: ID={}, expectedVersion={}", 
                    task.getId().getValue(), expectedVersion);
        
        try {
            // 先檢查當前版本
            Optional<TaskEntity> existingEntity = jpaTaskRepository.findById(task.getId().getValue());
            if (existingEntity.isEmpty()) {
                throw new RuntimeException("Task not found for optimistic lock update");
            }
            
            if (!existingEntity.get().getVersion().equals(expectedVersion)) {
                throw new RuntimeException("Optimistic lock exception: version mismatch");
            }
            
            // 執行更新
            TaskEntity entity = TaskEntity.fromDomain(task);
            entity.setVersion(expectedVersion);
            TaskEntity savedEntity = jpaTaskRepository.save(entity);
            
            logger.debug("Successfully saved task with optimistic lock: ID={}, newVersion={}", 
                        savedEntity.getId(), savedEntity.getVersion());
            return savedEntity.toDomain();
        } catch (Exception e) {
            logger.error("Failed to save task with optimistic lock: ID={}", task.getId().getValue(), e);
            throw new RuntimeException("Failed to save task with optimistic lock", e);
        }
    }

    /**
     * 查找指定狀態的任務
     */
    public List<Task> findByStatus(TaskStatus status) {
        logger.debug("Finding tasks by status: {}", status);
        
        try {
            List<TaskEntity> entities = jpaTaskRepository.findByStatusAndNotDeleted(status);
            List<Task> tasks = entities.stream()
                .map(TaskEntity::toDomain)
                .collect(Collectors.toList());
            
            logger.debug("Found {} tasks with status: {}", tasks.size(), status);
            return tasks;
        } catch (Exception e) {
            logger.error("Failed to find tasks by status: {}", status, e);
            throw new RuntimeException("Failed to find tasks by status", e);
        }
    }

    /**
     * 根據標題搜尋任務
     */
    public List<Task> findByTitle(String title) {
        logger.debug("Finding tasks by title containing: {}", title);
        
        try {
            List<TaskEntity> entities = jpaTaskRepository.findByTitleContainingAndNotDeleted(title);
            List<Task> tasks = entities.stream()
                .map(TaskEntity::toDomain)
                .collect(Collectors.toList());
            
            logger.debug("Found {} tasks with title containing: {}", tasks.size(), title);
            return tasks;
        } catch (Exception e) {
            logger.error("Failed to find tasks by title: {}", title, e);
            throw new RuntimeException("Failed to find tasks by title", e);
        }
    }

    /**
     * 統計任務數量
     */
    public long count() {
        logger.debug("Counting all active tasks");
        
        try {
            long count = jpaTaskRepository.findAllActive().size();
            logger.debug("Total active task count: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Failed to count tasks", e);
            throw new RuntimeException("Failed to count tasks", e);
        }
    }

    /**
     * 進階查詢方法 - 查找到期的任務
     */
    public List<Task> findOverdueTasks() {
        logger.debug("Finding overdue tasks");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            List<TaskEntity> entities = jpaTaskRepository.findOverdueTasks(now);
            List<Task> tasks = entities.stream()
                .map(TaskEntity::toDomain)
                .collect(Collectors.toList());
            
            logger.debug("Found {} overdue tasks", tasks.size());
            return tasks;
        } catch (Exception e) {
            logger.error("Failed to find overdue tasks", e);
            throw new RuntimeException("Failed to find overdue tasks", e);
        }
    }

    /**
     * 進階查詢方法 - 查找即將到期的任務
     */
    public List<Task> findTasksDueSoon(int hoursAhead) {
        logger.debug("Finding tasks due within {} hours", hoursAhead);
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime futureTime = now.plusHours(hoursAhead);
            
            List<TaskEntity> entities = jpaTaskRepository.findTasksDueBetween(now, futureTime);
            List<Task> tasks = entities.stream()
                .map(TaskEntity::toDomain)
                .collect(Collectors.toList());
            
            logger.debug("Found {} tasks due within {} hours", tasks.size(), hoursAhead);
            return tasks;
        } catch (Exception e) {
            logger.error("Failed to find tasks due soon", e);
            throw new RuntimeException("Failed to find tasks due soon", e);
        }
    }

    /**
     * 批次操作 - 軟刪除多個任務
     */
    public int softDeleteTasks(List<TaskId> taskIds) {
        logger.debug("Soft deleting {} tasks", taskIds.size());
        
        try {
            List<String> ids = taskIds.stream()
                .map(TaskId::getValue)
                .collect(Collectors.toList());
            
            LocalDateTime now = LocalDateTime.now();
            int deleted = jpaTaskRepository.softDeleteByIds(ids, now, now);
            
            // 清除實體管理器快取以確保後續查詢能讀取到最新資料
            entityManager.flush();
            entityManager.clear();
            
            logger.debug("Successfully soft deleted {} tasks", deleted);
            return deleted;
        } catch (Exception e) {
            logger.error("Failed to soft delete tasks", e);
            throw new RuntimeException("Failed to soft delete tasks", e);
        }
    }

    /**
     * 批次操作 - 更新多個任務的狀態
     */
    public int updateTaskStatus(List<TaskId> taskIds, TaskStatus status) {
        logger.debug("Updating status of {} tasks to {}", taskIds.size(), status);
        
        try {
            List<String> ids = taskIds.stream()
                .map(TaskId::getValue)
                .collect(Collectors.toList());
            
            LocalDateTime now = LocalDateTime.now();
            int updated = jpaTaskRepository.updateStatusByIds(ids, status, now);
            
            // 清除實體管理器快取以確保後續查詢能讀取到最新資料
            entityManager.flush();
            entityManager.clear();
            
            logger.debug("Successfully updated status of {} tasks", updated);
            return updated;
        } catch (Exception e) {
            logger.error("Failed to update task status", e);
            throw new RuntimeException("Failed to update task status", e);
        }
    }

    /**
     * 分頁查詢
     */
    public List<Task> findAllWithPaging(int page, int size, String sortBy, String sortDirection) {
        logger.debug("Finding tasks with paging: page={}, size={}, sortBy={}, direction={}", 
                    page, size, sortBy, sortDirection);
        
        try {
            Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<TaskEntity> entities = jpaTaskRepository.findAllActive(pageRequest);
            
            List<Task> tasks = entities.getContent().stream()
                .map(TaskEntity::toDomain)
                .collect(Collectors.toList());
            
            logger.debug("Found {} tasks on page {} of {}", tasks.size(), page + 1, entities.getTotalPages());
            return tasks;
        } catch (Exception e) {
            logger.error("Failed to find tasks with paging", e);
            throw new RuntimeException("Failed to find tasks with paging", e);
        }
    }

    /**
     * 統計查詢
     */
    public List<Object[]> getTaskStatisticsByStatus() {
        logger.debug("Getting task statistics by status");
        
        try {
            List<Object[]> statistics = jpaTaskRepository.countByStatus();
            logger.debug("Retrieved statistics for {} status types", statistics.size());
            return statistics;
        } catch (Exception e) {
            logger.error("Failed to get task statistics", e);
            throw new RuntimeException("Failed to get task statistics", e);
        }
    }
}