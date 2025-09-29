package com.tygrus.task_list;

import com.tygrus.task_list.application.dto.*;
import com.tygrus.task_list.application.usecase.BatchUpdateTasksUseCase;
import com.tygrus.task_list.domain.model.*;
import com.tygrus.task_list.domain.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UC-008 BatchUpdateTasksUseCase 功能示範
 * 
 * 展示並行批次更新任務的核心功能：
 * - 基本批次更新操作
 * - 進度監控和回報
 * - 錯誤處理和恢復
 * - 性能指標統計
 */
public class BatchUpdateDemo {

    public static void main(String[] args) {
        System.out.println("=== UC-008 BatchUpdateTasksUseCase 功能示範 ===\n");
        
        // 初始化系統組件
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        BatchUpdateTasksUseCase batchUpdateUseCase = new BatchUpdateTasksUseCase(repository);
        
        BatchUpdateDemo demo = new BatchUpdateDemo(repository, batchUpdateUseCase);
        
        try {
            // 準備測試資料
            demo.prepareTestData();
            
            // 示範各種批次更新場景
            demo.demonstrateBasicBatchUpdate();
            demo.demonstrateBatchUpdateWithProgress();
            demo.demonstrateErrorHandling();
            demo.demonstratePerformanceOptimization();
            
        } finally {
            // 清理資源
            batchUpdateUseCase.shutdown();
        }
        
        System.out.println("\n=== 示範完成 ===");
    }

    private final InMemoryTaskRepository repository;
    private final BatchUpdateTasksUseCase batchUpdateUseCase;

    public BatchUpdateDemo(InMemoryTaskRepository repository, BatchUpdateTasksUseCase batchUpdateUseCase) {
        this.repository = repository;
        this.batchUpdateUseCase = batchUpdateUseCase;
    }

    /**
     * 準備測試資料
     */
    private void prepareTestData() {
        System.out.println("準備測試資料...");
        
        // 創建30個測試任務
        for (int i = 1; i <= 30; i++) {
            Task task = Task.builder()
                .id(TaskId.of("task-" + String.format("%03d", i)))
                .title("測試任務 " + i)
                .description("這是第 " + i + " 個測試任務")
                .priority(Priority.values()[i % Priority.values().length])
                .createdAt(LocalDateTime.now().minusDays(i))
                .build();
            
            repository.save(task);
        }
        
        System.out.printf("✓ 已創建 %d 個測試任務\n", repository.size());
    }

    /**
     * 示範1: 基本批次更新操作
     */
    private void demonstrateBasicBatchUpdate() {
        System.out.println("\n--- 示範1: 基本批次更新操作 ---");
        
        // 選擇前10個任務進行批次更新
        List<String> taskIds = Arrays.asList(
            "task-001", "task-002", "task-003", "task-004", "task-005",
            "task-006", "task-007", "task-008", "task-009", "task-010"
        );
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("demo-user")
            .reason("批次啟動任務處理")
            .batchSize(3) // 每批處理3個任務
            .build();
        
        System.out.println("開始執行批次更新...");
        long startTime = System.currentTimeMillis();
        
        BatchOperationResult result = batchUpdateUseCase.execute(request);
        
        long endTime = System.currentTimeMillis();
        
        // 輸出結果
        System.out.printf("✓ 批次更新完成，耗時: %dms\n", endTime - startTime);
        printBasicResults(result);
    }

    /**
     * 示範2: 帶進度監控的批次更新
     */
    private void demonstrateBatchUpdateWithProgress() {
        System.out.println("\n--- 示範2: 帶進度監控的批次更新 ---");
        
        // 選擇接下來的15個任務
        List<String> taskIds = new ArrayList<>();
        for (int i = 11; i <= 25; i++) {
            taskIds.add("task-" + String.format("%03d", i));
        }
        
        System.out.println("開始執行帶進度監控的批次更新...");
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("progress-demo-user")
            .reason("進度監控示範")
            .batchSize(5)
            .progressCallback(this::handleProgressUpdate) // 設置進度回調
            .build();
        
        BatchOperationResult result = batchUpdateUseCase.execute(request);
        
        System.out.println("\n✓ 帶進度監控的批次更新完成");
        printDetailedResults(result);
    }

    /**
     * 示範3: 錯誤處理
     */
    private void demonstrateErrorHandling() {
        System.out.println("\n--- 示範3: 錯誤處理 ---");
        
        // 包含一些不存在的任務ID來模擬錯誤
        List<String> taskIds = Arrays.asList(
            "task-026", "task-027", "task-028",
            "non-existent-1", "non-existent-2"  // 不存在的任務
        );
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("error-demo-user")
            .reason("錯誤處理示範")
            .maxRetries(2) // 設置重試次數
            .build();
        
        System.out.println("開始執行包含錯誤的批次更新...");
        
        BatchOperationResult result = batchUpdateUseCase.execute(request);
        
        System.out.println("✓ 錯誤處理示範完成");
        printErrorAnalysis(result);
    }

    /**
     * 示範4: 性能優化
     */
    private void demonstratePerformanceOptimization() {
        System.out.println("\n--- 示範4: 性能優化 ---");
        
        // 創建更多任務來測試性能
        List<String> largeTaskIds = new ArrayList<>();
        for (int i = 101; i <= 200; i++) {
            String taskId = "perf-task-" + i;
            largeTaskIds.add(taskId);
            
            // 創建任務
            Task task = Task.builder()
                .id(TaskId.of(taskId))
                .title("性能測試任務 " + i)
                .description("用於性能測試的任務")
                .priority(Priority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .build();
            
            repository.save(task);
        }
        
        System.out.println("測試不同批次大小的性能影響...");
        
        // 測試小批次大小
        testBatchPerformance("小批次 (batch=5)", largeTaskIds.subList(0, 50), 5);
        
        // 測試中等批次大小
        testBatchPerformance("中批次 (batch=10)", largeTaskIds.subList(50, 100), 10);
        
        System.out.println("✓ 性能優化示範完成");
    }

    /**
     * 測試批次性能
     */
    private void testBatchPerformance(String testName, List<String> taskIds, int batchSize) {
        System.out.printf("\n%s - 處理 %d 個任務:\n", testName, taskIds.size());
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("perf-test-user")
            .reason("性能測試")
            .batchSize(batchSize)
            .build();
        
        long startTime = System.currentTimeMillis();
        BatchOperationResult result = batchUpdateUseCase.execute(request);
        long endTime = System.currentTimeMillis();
        
        System.out.printf("  - 實際執行時間: %dms\n", endTime - startTime);
        System.out.printf("  - 成功率: %.1f%%\n", result.getSuccessRate());
        System.out.printf("  - 吞吐量: %.1f tasks/sec\n", result.getThroughputPerSecond());
        System.out.printf("  - 平均處理時間: %.2fms per task\n", result.getAverageProcessingTimeMs());
    }

    /**
     * 進度更新處理器
     */
    private void handleProgressUpdate(BatchProgressUpdate progress) {
        // 使用 \r 讓進度在同一行更新
        System.out.printf("\r  進度: %d/%d (%.1f%%) | 成功: %d, 失敗: %d",
            progress.getProcessedTasks(), progress.getTotalTasks(),
            progress.getProgressPercentage(), progress.getSuccessfulTasks(),
            progress.getFailedTasks());
        
        if (progress.isCompleted()) {
            System.out.println(); // 完成時換行
        }
    }

    /**
     * 輸出基本結果
     */
    private void printBasicResults(BatchOperationResult result) {
        System.out.println("  結果統計:");
        System.out.printf("    總任務數: %d\n", result.getTotalCount());
        System.out.printf("    成功數: %d\n", result.getSuccessCount());
        System.out.printf("    失敗數: %d\n", result.getFailureCount());
        System.out.printf("    成功率: %.1f%%\n", result.getSuccessRate());
        System.out.printf("    執行時間: %dms\n", result.getExecutionTime().toMillis());
    }

    /**
     * 輸出詳細結果
     */
    private void printDetailedResults(BatchOperationResult result) {
        printBasicResults(result);
        System.out.printf("    重試次數: %d\n", result.getRetryCount());
        System.out.printf("    平均處理時間: %.2fms\n", result.getAverageProcessingTimeMs());
        System.out.printf("    吞吐量: %.1f tasks/sec\n", result.getThroughputPerSecond());
        
        if (!result.getPerformanceMetrics().isEmpty()) {
            System.out.println("  性能指標:");
            result.getPerformanceMetrics().forEach((key, value) -> {
                System.out.printf("    %s: %s\n", key, value);
            });
        }
    }

    /**
     * 輸出錯誤分析
     */
    private void printErrorAnalysis(BatchOperationResult result) {
        printBasicResults(result);
        
        if (result.hasErrors()) {
            System.out.println("  錯誤分析:");
            
            Map<String, Long> errorTypeCount = new HashMap<>();
            result.getErrors().forEach(error -> {
                String errorType = error.getErrorType();
                errorTypeCount.merge(errorType, 1L, Long::sum);
            });
            
            errorTypeCount.forEach((type, count) -> {
                System.out.printf("    %s: %d 次\n", type, count);
            });
            
            System.out.println("  失敗任務詳情:");
            result.getErrors().forEach(error -> {
                System.out.printf("    - %s: %s\n", error.getTaskId(), error.getErrorMessage());
            });
        }
    }

    /**
     * 簡單的內存任務儲存庫實作，用於示範
     */
    private static class InMemoryTaskRepository implements TaskRepository {
        private final Map<String, Task> tasks = new ConcurrentHashMap<>();
        private final AtomicInteger sequenceGenerator = new AtomicInteger(1);
        
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
        
        public void clear() {
            tasks.clear();
        }
        
        public int size() {
            return tasks.size();
        }
    }
}