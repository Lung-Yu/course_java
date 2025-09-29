package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.*;
import com.tygrus.task_list.application.exception.BatchOperationException;
import com.tygrus.task_list.application.exception.ConcurrencyConflictException;
import com.tygrus.task_list.application.exception.TaskNotFoundException;
import com.tygrus.task_list.domain.exception.IllegalStatusTransitionException;
import com.tygrus.task_list.domain.exception.OptimisticLockException;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.repository.TaskRepository;

// 移除 Spring 依賴，使用純 Java 實現

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 批次更新任務Use Case (UC-008)
 * 
 * 展示並行處理的核心概念：
 * - CompletableFuture: 非同步程式設計
 * - ExecutorService: 執行緒池管理
 * - 並發控制: 樂觀鎖和悲觀鎖
 * - 錯誤恢復: 重試機制和回滾策略
 * - 進度回報: 即時狀態監控
 * - 執行緒安全: 原子操作和同步機制
 * 
 * 這個Use Case是學習並發程式設計和分散式系統設計的絕佳範例
 */
public class BatchUpdateTasksUseCase {

    private final TaskRepository taskRepository;
    private final ExecutorService executorService;
    private final ReentrantReadWriteLock progressLock = new ReentrantReadWriteLock();
    
    // 並發統計計數器
    private final AtomicInteger totalProcessed = new AtomicInteger(0);
    private final AtomicInteger totalSuccessful = new AtomicInteger(0);
    private final AtomicInteger totalFailed = new AtomicInteger(0);
    private final AtomicInteger totalRetries = new AtomicInteger(0);

    public BatchUpdateTasksUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        // 創建固定大小的執行緒池，適合CPU密集型任務
        this.executorService = Executors.newFixedThreadPool(
            Math.min(Runtime.getRuntime().availableProcessors() * 2, 10)
        );
    }

    /**
     * 執行批次任務更新
     * 
     * @param request 批次更新請求
     * @return 批次操作結果
     * @throws BatchOperationException 當批次操作部分或全部失敗時
     */
    public BatchOperationResult execute(BatchUpdateTaskRequest request) {
        LocalDateTime startTime = LocalDateTime.now();
        
        // 重置計數器
        resetCounters();
        
        // 建立結果構建器
        BatchOperationResult.Builder resultBuilder = BatchOperationResult.builder()
            .startTime(startTime)
            .totalCount(request.getTaskIds().size());
        
        try {
            // 分批處理任務
            List<List<String>> batches = partitionTasks(request.getTaskIds(), request.getBatchSize());
            
            // 使用CompletableFuture進行並行處理
            List<CompletableFuture<BatchResult>> futures = batches.stream()
                .map(batch -> processBatchAsync(batch, request))
                .collect(Collectors.toList());
            
            // 等待所有批次完成並收集結果
            CompletableFuture<Void> allBatches = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            // 設置超時時間防止無限等待
            try {
                allBatches.get(5, TimeUnit.MINUTES);
            } catch (TimeoutException e) {
                // 取消未完成的任務
                futures.forEach(future -> future.cancel(true));
                throw new BatchOperationException("Batch operation timed out after 5 minutes", 
                    Collections.emptyList());
            }
            
            // 收集所有結果
            List<BatchResult> batchResults = futures.stream()
                .map(this::safelyGetResult)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            // 彙總結果
            return aggregateResults(batchResults, resultBuilder, startTime);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BatchOperationException("Batch operation was interrupted", 
                Collections.emptyList());
        } catch (ExecutionException e) {
            throw new BatchOperationException("Batch operation failed", 
                Collections.emptyList(), e);
        }
    }

    /**
     * 非同步處理單個批次
     */
    private CompletableFuture<BatchResult> processBatchAsync(List<String> taskIds, 
                                                           BatchUpdateTaskRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            return processBatch(taskIds, request);
        }, executorService);
    }

    /**
     * 處理單個批次
     */
    private BatchResult processBatch(List<String> taskIds, BatchUpdateTaskRequest request) {
        List<TaskDTO> successfulTasks = new ArrayList<>();
        List<BatchOperationError> errors = new ArrayList<>();
        
        for (String taskId : taskIds) {
            try {
                TaskDTO updatedTask = updateSingleTaskWithRetry(taskId, request);
                successfulTasks.add(updatedTask);
                totalSuccessful.incrementAndGet();
                
                // 更新進度
                updateProgress(request, taskId, "Completed successfully");
                
            } catch (Exception e) {
                BatchOperationError error = new BatchOperationError(taskId, e.getMessage(), e);
                errors.add(error);
                totalFailed.incrementAndGet();
                
                // 更新進度
                updateProgress(request, taskId, "Failed: " + e.getMessage());
            }
            
            totalProcessed.incrementAndGet();
        }
        
        return new BatchResult(successfulTasks, errors);
    }

    /**
     * 使用重試機制更新單個任務
     */
    private TaskDTO updateSingleTaskWithRetry(String taskId, BatchUpdateTaskRequest request) {
        int maxRetries = request.getMaxRetries();
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries + 1; attempt++) {
            try {
                return updateSingleTask(taskId, request, attempt);
            } catch (OptimisticLockException e) {
                lastException = e;
                totalRetries.incrementAndGet();
                
                if (attempt <= maxRetries) {
                    // 指數退避策略
                    try {
                        Thread.sleep(100L * (1L << (attempt - 1))); // 100ms, 200ms, 400ms...
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ConcurrencyConflictException(taskId, "INTERRUPTED", 
                            "Thread was interrupted during retry", ie, attempt);
                    }
                } else {
                    // 最後一次嘗試失敗
                    throw new ConcurrencyConflictException(taskId, "OPTIMISTIC_LOCK",
                        "Failed after " + maxRetries + " retries: " + e.getMessage(), e, attempt);
                }
            } catch (TaskNotFoundException | IllegalStatusTransitionException e) {
                // 任務不存在或業務規則違反不需要重試
                throw e;
            }
        }
        
        // 理論上不會到達這裡
        throw new RuntimeException("Unexpected error in retry logic", lastException);
    }

    /**
     * 更新單個任務
     * 注意：在實際應用中，此方法應該包在交易中執行
     */
    public TaskDTO updateSingleTask(String taskId, BatchUpdateTaskRequest request, int attemptNumber) {
        // 查找任務
        TaskId id = TaskId.of(taskId);
        Optional<Task> taskOptional = taskRepository.findById(id);
        
        if (taskOptional.isEmpty()) {
            throw new TaskNotFoundException(taskId);
        }
        
        Task task = taskOptional.get();
        TaskStatus originalStatus = task.getStatus();
        
        // 執行狀態轉換
        try {
            task.updateStatus(request.getNewStatus());
        } catch (IllegalStateException e) {
            throw new IllegalStatusTransitionException(
                originalStatus.name(), 
                request.getNewStatus().name(),
                e.getMessage()
            );
        }
        
        // 保存任務
        Task savedTask = taskRepository.save(task);
        
        return TaskDTO.fromTask(savedTask);
    }

    /**
     * 更新進度回報
     */
    private void updateProgress(BatchUpdateTaskRequest request, String taskId, String operation) {
        Consumer<BatchProgressUpdate> callback = request.getProgressCallback();
        if (callback != null) {
            progressLock.readLock().lock();
            try {
                BatchProgressUpdate progress = BatchProgressUpdate.builder()
                    .totalTasks(request.getTaskIds().size())
                    .processedTasks(totalProcessed.get())
                    .successfulTasks(totalSuccessful.get())
                    .failedTasks(totalFailed.get())
                    .currentTaskId(taskId)
                    .currentOperation(operation)
                    .build();
                
                callback.accept(progress);
            } catch (Exception e) {
                // 進度回報失敗不應該影響主要處理流程
                System.err.println("Progress callback failed: " + e.getMessage());
            } finally {
                progressLock.readLock().unlock();
            }
        }
    }

    /**
     * 將任務ID列表分割成批次
     */
    private List<List<String>> partitionTasks(List<String> taskIds, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < taskIds.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, taskIds.size());
            batches.add(new ArrayList<>(taskIds.subList(i, endIndex)));
        }
        return batches;
    }

    /**
     * 安全地獲取CompletableFuture結果
     */
    private BatchResult safelyGetResult(CompletableFuture<BatchResult> future) {
        try {
            return future.get();
        } catch (Exception e) {
            // 記錄錯誤但不拋出異常，讓其他批次繼續處理
            System.err.println("Failed to get batch result: " + e.getMessage());
            return new BatchResult(Collections.emptyList(), 
                Collections.singletonList(new BatchOperationError("BATCH_ERROR", e.getMessage(), e)));
        }
    }

    /**
     * 彙總所有批次結果
     */
    private BatchOperationResult aggregateResults(List<BatchResult> batchResults, 
                                                BatchOperationResult.Builder resultBuilder,
                                                LocalDateTime startTime) {
        // 合併所有成功任務
        List<TaskDTO> allSuccessfulTasks = batchResults.stream()
            .flatMap(result -> result.getSuccessfulTasks().stream())
            .collect(Collectors.toList());
        
        // 合併所有錯誤
        List<BatchOperationError> allErrors = batchResults.stream()
            .flatMap(result -> result.getErrors().stream())
            .collect(Collectors.toList());
        
        // 計算性能指標
        LocalDateTime endTime = LocalDateTime.now();
        Map<String, Object> performanceMetrics = calculatePerformanceMetrics(
            startTime, endTime, allSuccessfulTasks.size(), allErrors.size());
        
        return resultBuilder
            .successCount(allSuccessfulTasks.size())
            .failureCount(allErrors.size())
            .retryCount(totalRetries.get())
            .successfulTasks(allSuccessfulTasks)
            .errors(allErrors)
            .endTime(endTime)
            .performanceMetrics(performanceMetrics)
            .build();
    }

    /**
     * 計算性能指標
     */
    private Map<String, Object> calculatePerformanceMetrics(LocalDateTime startTime, LocalDateTime endTime,
                                                          int successCount, int errorCount) {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        
        long executionTimeMs = java.time.Duration.between(startTime, endTime).toMillis();
        int totalTasks = successCount + errorCount;
        
        metrics.put("executionTimeMs", executionTimeMs);
        metrics.put("totalRetries", totalRetries.get());
        metrics.put("averageProcessingTimeMs", totalTasks > 0 ? (double) executionTimeMs / totalTasks : 0.0);
        metrics.put("throughputPerSecond", executionTimeMs > 0 ? (double) totalTasks * 1000 / executionTimeMs : 0.0);
        metrics.put("retryRate", totalTasks > 0 ? (double) totalRetries.get() / totalTasks * 100.0 : 0.0);
        metrics.put("concurrentThreads", ((ThreadPoolExecutor) executorService).getCorePoolSize());
        
        return metrics;
    }

    /**
     * 重置計數器
     */
    private void resetCounters() {
        totalProcessed.set(0);
        totalSuccessful.set(0);
        totalFailed.set(0);
        totalRetries.set(0);
    }

    /**
     * 清理資源
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 批次處理結果內部類
     */
    private static class BatchResult {
        private final List<TaskDTO> successfulTasks;
        private final List<BatchOperationError> errors;
        
        public BatchResult(List<TaskDTO> successfulTasks, List<BatchOperationError> errors) {
            this.successfulTasks = successfulTasks;
            this.errors = errors;
        }
        
        public List<TaskDTO> getSuccessfulTasks() {
            return successfulTasks;
        }
        
        public List<BatchOperationError> getErrors() {
            return errors;
        }
    }
}