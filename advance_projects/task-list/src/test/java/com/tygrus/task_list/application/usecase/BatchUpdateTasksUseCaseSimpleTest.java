package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.*;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BatchUpdateTasksUseCase 簡化測試
 * 
 * 使用內存實現來測試基本功能
 */
class BatchUpdateTasksUseCaseSimpleTest {

    private BatchUpdateTasksUseCase useCase;
    private InMemoryTaskRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
        useCase = new BatchUpdateTasksUseCase(repository);
    }

    @Test
    void testSuccessfulBatchUpdate() {
        // Given: 準備測試資料
        List<String> taskIds = Arrays.asList("task-1", "task-2", "task-3");
        
        // 創建並保存測試任務
        for (String taskId : taskIds) {
            Task task = createTestTask(taskId);
            repository.save(task);
        }
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("test-user")
            .reason("Batch update test")
            .batchSize(2)
            .build();

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 驗證結果
        assertNotNull(result);
        assertEquals(3, result.getTotalCount());
        assertEquals(3, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertTrue(result.isCompletelySuccessful());
        assertFalse(result.hasErrors());
        
        // 驗證性能指標
        assertTrue(result.getExecutionTime().toMillis() >= 0);
        assertEquals(100.0, result.getSuccessRate());
    }

    @Test
    void testPartialFailureBatchUpdate() {
        // Given: 部分任務失敗的情況
        List<String> taskIds = Arrays.asList("task-1", "task-2", "non-existent");
        
        // 創建並保存存在的任務
        Task task1 = createTestTask("task-1");
        Task task2 = createTestTask("task-2");
        repository.save(task1);
        repository.save(task2);
        // non-existent 任務不保存，模擬不存在的情況
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS) // 改為合法的狀態轉換
            .updatedBy("test-user")
            .reason("Partial failure test")
            .build();

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 驗證結果
        assertEquals(3, result.getTotalCount());
        assertEquals(2, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertFalse(result.isCompletelySuccessful());
        assertTrue(result.hasErrors());
        
        // 驗證錯誤詳情
        List<BatchOperationError> errors = result.getErrors();
        assertEquals(1, errors.size());
        assertEquals("non-existent", errors.get(0).getTaskId());
    }

    @Test
    void testProgressCallback() {
        // Given: 設置進度回調
        List<String> taskIds = Arrays.asList("task-1", "task-2", "task-3");
        List<BatchProgressUpdate> progressUpdates = new ArrayList<>();
        
        // 創建並保存測試任務
        for (String taskId : taskIds) {
            Task task = createTestTask(taskId);
            repository.save(task);
        }
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("test-user")
            .progressCallback(progressUpdates::add) // 收集進度更新
            .build();

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 驗證進度回調被調用
        assertFalse(progressUpdates.isEmpty());
        assertEquals(3, result.getSuccessCount());
        
        // 驗證進度更新的內容
        for (BatchProgressUpdate progress : progressUpdates) {
            assertTrue(progress.getTotalTasks() > 0);
            assertTrue(progress.getProcessedTasks() >= 0);
            assertNotNull(progress.getCurrentTaskId());
            assertNotNull(progress.getTimestamp());
        }
    }

    @Test
    void testEmptyTaskList() {
        // Given & When & Then: 空的任務列表應該在構建請求時拋出異常
        assertThrows(IllegalArgumentException.class, () -> {
            BatchUpdateTaskRequest.builder()
                .taskIds(Collections.emptyList())
                .newStatus(TaskStatus.IN_PROGRESS)
                .updatedBy("test-user")
                .build();
        });
    }

    // Helper methods
    private Task createTestTask(String taskId) {
        return Task.builder()
            .id(TaskId.of(taskId))
            .title("Test Task " + taskId)
            .description("Test Description")
            .priority(Priority.MEDIUM)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * 簡單的內存任務儲存庫實作，用於測試
     */
    private static class InMemoryTaskRepository implements TaskRepository {
        private final Map<String, Task> tasks = new ConcurrentHashMap<>();
        
        @Override
        public Task save(Task task) {
            tasks.put(task.getId().getValue(), task);
            return task;
        }
        
        @Override
        public Optional<Task> findById(TaskId taskId) {
            return Optional.ofNullable(tasks.get(taskId.getValue()));
        }
        
        @Override
        public boolean existsById(TaskId taskId) {
            return tasks.containsKey(taskId.getValue());
        }
        
        @Override
        public void deleteById(TaskId taskId) {
            tasks.remove(taskId.getValue());
        }
        
        @Override
        public List<Task> findAll() {
            return new ArrayList<>(tasks.values());
        }
        
        @Override
        public Map<TaskId, Task> findByIds(List<TaskId> taskIds) {
            Map<TaskId, Task> result = new HashMap<>();
            for (TaskId taskId : taskIds) {
                Task task = tasks.get(taskId.getValue());
                if (task != null) {
                    result.put(taskId, task);
                }
            }
            return result;
        }
        
        @Override
        public List<Task> saveAll(List<Task> tasksToSave) {
            List<Task> savedTasks = new ArrayList<>();
            for (Task task : tasksToSave) {
                Task savedTask = save(task);
                savedTasks.add(savedTask);
            }
            return savedTasks;
        }
        
        @Override
        public Map<TaskId, Boolean> existsByIds(List<TaskId> taskIds) {
            Map<TaskId, Boolean> result = new HashMap<>();
            for (TaskId taskId : taskIds) {
                result.put(taskId, existsById(taskId));
            }
            return result;
        }
        
        @Override
        public Task saveWithOptimisticLock(Task task, Long expectedVersion) {
            return save(task);
        }
    }
}