package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.*;
import com.tygrus.task_list.application.exception.BatchOperationException;
import com.tygrus.task_list.application.exception.ConcurrencyConflictException;
import com.tygrus.task_list.application.exception.TaskNotFoundException;
import com.tygrus.task_list.domain.exception.IllegalStatusTransitionException;
import com.tygrus.task_list.domain.exception.OptimisticLockException;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * BatchUpdateTasksUseCase TDD 測試
 * 
 * 測試重點：
 * - 並行處理正確性
 * - 錯誤恢復機制
 * - 進度回報功能
 * - 執行緒安全性
 * - 性能指標統計
 */
@ExtendWith(MockitoExtension.class)
class BatchUpdateTasksUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private BatchUpdateTasksUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BatchUpdateTasksUseCase(taskRepository);
    }

    @Test
    void testSuccessfulBatchUpdate() {
        // Given: 準備測試資料
        List<String> taskIds = Arrays.asList("task-1", "task-2", "task-3");
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("test-user")
            .reason("Batch update test")
            .batchSize(2)
            .build();

        // 模擬任務存在且可以更新
        setupSuccessfulTasks(taskIds);

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
        assertTrue(result.getSuccessRate() == 100.0);
        
        // 驗證Repository互動
        verify(taskRepository, times(3)).findById(any(TaskId.class));
        verify(taskRepository, times(3)).save(any(Task.class));
    }

    @Test
    void testPartialFailureBatchUpdate() {
        // Given: 部分任務失敗的情況
        List<String> taskIds = Arrays.asList("task-1", "task-2", "task-3", "task-4");
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.COMPLETED)
            .updatedBy("test-user")
            .reason("Partial failure test")
            .build();

        // 設置前兩個任務成功，後兩個失敗
        setupMixedResultTasks();

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 驗證結果
        assertEquals(4, result.getTotalCount());
        assertEquals(2, result.getSuccessCount());
        assertEquals(2, result.getFailureCount());
        assertFalse(result.isCompletelySuccessful());
        assertTrue(result.hasErrors());
        assertEquals(50.0, result.getSuccessRate());
        
        // 驗證錯誤詳情
        List<BatchOperationError> errors = result.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.stream().anyMatch(error -> "task-3".equals(error.getTaskId())));
        assertTrue(errors.stream().anyMatch(error -> "task-4".equals(error.getTaskId())));
    }

    @Test
    void testConcurrencyConflictWithRetry() {
        // Given: 模擬樂觀鎖衝突
        List<String> taskIds = Arrays.asList("task-1");
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("test-user")
            .maxRetries(2)
            .build();

        Task task = createTestTask("task-1", TaskStatus.PENDING);
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(task));
        
        // 第一次和第二次調用拋出OptimisticLockException，第三次成功
        when(taskRepository.save(any(Task.class)))
            .thenThrow(new OptimisticLockException("Version conflict"))
            .thenThrow(new OptimisticLockException("Version conflict"))
            .thenReturn(task);

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 驗證重試成功
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertTrue(result.getRetryCount() > 0);
        
        // 驗證重試次數
        verify(taskRepository, times(3)).save(any(Task.class));
    }

    @Test
    void testMaxRetriesExceeded() {
        // Given: 超過最大重試次數
        List<String> taskIds = Arrays.asList("task-1");
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("test-user")
            .maxRetries(1)
            .build();

        Task task = createTestTask("task-1", TaskStatus.PENDING);
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(task));
        
        // 所有嘗試都失敗
        when(taskRepository.save(any(Task.class)))
            .thenThrow(new OptimisticLockException("Persistent conflict"));

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 驗證最終失敗
        assertEquals(1, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertTrue(result.hasErrors());
        
        BatchOperationError error = result.getErrors().get(0);
        assertTrue(error.isFailedAfterRetry());
        assertTrue(error.isConcurrencyError());
    }

    @Test
    void testProgressCallback() throws InterruptedException {
        // Given: 設置進度回調
        List<String> taskIds = Arrays.asList("task-1", "task-2", "task-3");
        AtomicInteger progressCallbackCount = new AtomicInteger(0);
        CountDownLatch progressLatch = new CountDownLatch(3);
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("test-user")
            .progressCallback(progress -> {
                progressCallbackCount.incrementAndGet();
                progressLatch.countDown();
                
                // 驗證進度資訊
                assertTrue(progress.getTotalTasks() > 0);
                assertTrue(progress.getProcessedTasks() >= 0);
                assertTrue(progress.getProgressPercentage() >= 0);
                assertNotNull(progress.getCurrentTaskId());
                assertNotNull(progress.getTimestamp());
            })
            .build();

        setupSuccessfulTasks(taskIds);

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 等待進度回調完成
        assertTrue(progressLatch.await(5, TimeUnit.SECONDS));
        
        // 驗證進度回調被調用
        assertTrue(progressCallbackCount.get() >= taskIds.size());
        assertEquals(3, result.getSuccessCount());
    }

    @Test
    void testLargeBatchPerformance() {
        // Given: 大批量任務測試
        int taskCount = 100;
        List<String> taskIds = IntStream.range(1, taskCount + 1)
            .mapToObj(i -> "task-" + i)
            .toList();
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.COMPLETED)
            .updatedBy("performance-test")
            .batchSize(10)
            .build();

        // 模擬所有任務成功
        setupSuccessfulTasks(taskIds);

        // When: 執行批次更新
        long startTime = System.currentTimeMillis();
        BatchOperationResult result = useCase.execute(request);
        long endTime = System.currentTimeMillis();

        // Then: 驗證性能
        assertEquals(taskCount, result.getSuccessCount());
        assertTrue(result.isCompletelySuccessful());
        
        // 驗證執行時間合理（應該在並行處理下相對較快）
        long executionTime = endTime - startTime;
        assertTrue(executionTime < 10000, "Execution time should be less than 10 seconds");
        
        // 驗證性能指標
        Map<String, Object> metrics = result.getPerformanceMetrics();
        assertNotNull(metrics.get("throughputPerSecond"));
        assertNotNull(metrics.get("averageProcessingTimeMs"));
        assertNotNull(metrics.get("concurrentThreads"));
        
        double throughput = (Double) metrics.get("throughputPerSecond");
        assertTrue(throughput > 0, "Throughput should be positive");
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        // Given: 並發執行相同的批次更新
        List<String> taskIds = Arrays.asList("task-1", "task-2", "task-3");
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("concurrency-test")
            .build();

        setupSuccessfulTasks(taskIds);

        int threadCount = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threadCount);
        List<BatchOperationResult> results = Collections.synchronizedList(new ArrayList<>());
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        // When: 多執行緒並行執行
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    BatchOperationResult result = useCase.execute(request);
                    results.add(result);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    completionLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown(); // 開始所有執行緒
        assertTrue(completionLatch.await(10, TimeUnit.SECONDS));

        // Then: 驗證執行緒安全性
        assertTrue(exceptions.isEmpty(), "No exceptions should occur in concurrent execution");
        assertEquals(threadCount, results.size());
        
        // 所有結果都應該成功（如果沒有衝突）
        results.forEach(result -> {
            assertEquals(3, result.getTotalCount());
            assertTrue(result.getSuccessCount() + result.getFailureCount() == 3);
        });
    }

    @Test
    void testInvalidStatusTransition() {
        // Given: 無效的狀態轉換
        List<String> taskIds = Arrays.asList("task-1");
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.PENDING) // 嘗試從COMPLETED轉回PENDING
            .updatedBy("test-user")
            .build();

        Task completedTask = createTestTaskWithStatus("task-1", TaskStatus.COMPLETED);
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(completedTask));

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 驗證狀態轉換錯誤
        assertEquals(1, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        
        BatchOperationError error = result.getErrors().get(0);
        assertTrue(error.isBusinessRuleViolation());
        assertEquals("task-1", error.getTaskId());
    }

    @Test
    void testTaskNotFound() {
        // Given: 任務不存在
        List<String> taskIds = Arrays.asList("non-existent-task");
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("test-user")
            .build();

        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.empty());

        // When: 執行批次更新
        BatchOperationResult result = useCase.execute(request);

        // Then: 驗證任務不存在錯誤
        assertEquals(1, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        
        BatchOperationError error = result.getErrors().get(0);
        assertEquals("non-existent-task", error.getTaskId());
        assertFalse(error.isConcurrencyError());
        assertFalse(error.isBusinessRuleViolation());
    }

    // Helper methods

    private void setupSuccessfulTasks(List<String> taskIds) {
        for (String taskId : taskIds) {
            Task task = createTestTask(taskId, TaskStatus.PENDING);
            when(taskRepository.findById(TaskId.of(taskId))).thenReturn(Optional.of(task));
            when(taskRepository.save(eq(task))).thenReturn(task);
        }
    }

    private void setupMixedResultTasks() {
        // task-1, task-2 成功
        Task task1 = createTestTask("task-1", TaskStatus.PENDING);
        Task task2 = createTestTask("task-2", TaskStatus.PENDING);
        when(taskRepository.findById(TaskId.of("task-1"))).thenReturn(Optional.of(task1));
        when(taskRepository.findById(TaskId.of("task-2"))).thenReturn(Optional.of(task2));
        when(taskRepository.save(eq(task1))).thenReturn(task1);
        when(taskRepository.save(eq(task2))).thenReturn(task2);
        
        // task-3 不存在
        when(taskRepository.findById(TaskId.of("task-3"))).thenReturn(Optional.empty());
        
        // task-4 存在但處於已完成狀態，無法轉換到其他狀態（業務規則違反）
        Task task4 = createTestTaskWithStatus("task-4", TaskStatus.COMPLETED);
        when(taskRepository.findById(TaskId.of("task-4"))).thenReturn(Optional.of(task4));
        // 不需要設置 save 的 mock，因為狀態轉換會失敗
    }

    private Task createTestTask(String taskId, TaskStatus status) {
        Task task = Task.builder()
            .id(TaskId.of(taskId))
            .title("Test Task " + taskId)
            .description("Test Description")
            .priority(Priority.MEDIUM)
            .createdAt(LocalDateTime.now())
            .build();
        
        return task;
    }
    
    /**
     * 創建具有特定狀態的測試任務
     * 使用反射或其他機制來設置狀態，因為正常業務邏輯不允許直接設置任意狀態
     */
    private Task createTestTaskWithStatus(String taskId, TaskStatus status) {
        Task task = Task.builder()
            .id(TaskId.of(taskId))
            .title("Test Task " + taskId)
            .description("Test Description")
            .priority(Priority.MEDIUM)
            .createdAt(LocalDateTime.now())
            .build();
        
        // 如果需要設置為 COMPLETED 狀態，我們模擬正常的狀態轉換流程
        if (status == TaskStatus.COMPLETED) {
            try {
                task.updateStatus(TaskStatus.IN_PROGRESS); // 先轉到進行中
                task.updateStatus(TaskStatus.COMPLETED);    // 再轉到完成
            } catch (Exception e) {
                // 如果轉換失敗，使用反射設置狀態
                try {
                    java.lang.reflect.Field statusField = Task.class.getDeclaredField("status");
                    statusField.setAccessible(true);
                    statusField.set(task, status);
                } catch (Exception reflectionException) {
                    throw new RuntimeException("Failed to set task status for testing", reflectionException);
                }
            }
        }
        
        return task;
    }
}