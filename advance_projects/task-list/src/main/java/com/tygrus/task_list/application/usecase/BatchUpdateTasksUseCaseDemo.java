package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.*;
import com.tygrus.task_list.domain.model.TaskStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BatchUpdateTasksUseCase 使用示範
 * 
 * 展示如何使用批次更新任務功能：
 * - 基本批次操作
 * - 進度監控
 * - 錯誤處理
 * - 性能調優
 */
public class BatchUpdateTasksUseCaseDemo {

    private static final Logger logger = LogManager.getLogger(BatchUpdateTasksUseCaseDemo.class);
    private final BatchUpdateTasksUseCase batchUpdateUseCase;

    public BatchUpdateTasksUseCaseDemo(BatchUpdateTasksUseCase batchUpdateUseCase) {
        this.batchUpdateUseCase = batchUpdateUseCase;
    }

    /**
     * 示範1: 基本批次更新操作
     */
    public void demonstrateBasicBatchUpdate() {
        System.out.println("=== 基本批次更新示範 ===");
        
        // 準備要更新的任務ID清單
        List<String> taskIds = Arrays.asList(
            "task-001", "task-002", "task-003", "task-004", "task-005"
        );
        
        // 建立批次更新請求
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("batch-demo-user")
            .reason("Batch update demonstration")
            .batchSize(2) // 每批處理2個任務
            .maxRetries(3) // 最多重試3次
            .build();
        
        try {
            // 執行批次更新
            BatchOperationResult result = batchUpdateUseCase.execute(request);
            
            // 輸出結果統計
            printBasicResults(result);
            
        } catch (Exception e) {
            logger.error("批次更新失敗: {}", e.getMessage());
        }
    }

    /**
     * 示範2: 帶進度監控的批次更新
     */
    public void demonstrateBatchUpdateWithProgress() {
        System.out.println("\n=== 帶進度監控的批次更新示範 ===");
        
        List<String> taskIds = Arrays.asList(
            "task-101", "task-102", "task-103", "task-104", "task-105",
            "task-106", "task-107", "task-108", "task-109", "task-110"
        );
        
        // 進度追蹤器
        AtomicInteger progressUpdateCount = new AtomicInteger(0);
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.COMPLETED)
            .updatedBy("progress-demo-user")
            .reason("Progress monitoring demonstration")
            .batchSize(3)
            .progressCallback(this::handleProgressUpdate) // 設置進度回調
            .build();
        
        try {
            System.out.println("開始執行批次更新，總計 " + taskIds.size() + " 個任務...");
            
            BatchOperationResult result = batchUpdateUseCase.execute(request);
            
            System.out.println("\n批次更新完成！");
            printDetailedResults(result);
            
        } catch (Exception e) {
            logger.error("批次更新失敗: {}", e.getMessage());
        }
    }

    /**
     * 示範3: 大批量任務處理
     */
    public void demonstrateLargeBatchProcessing() {
        System.out.println("\n=== 大批量任務處理示範 ===");
        
        // 模擬1000個任務
        List<String> taskIds = java.util.stream.IntStream.range(1, 1001)
            .mapToObj(i -> String.format("large-task-%04d", i))
            .toList();
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.IN_PROGRESS)
            .updatedBy("large-batch-user")
            .reason("Large batch processing demonstration")
            .batchSize(50) // 增大批次大小以提高效率
            .maxRetries(2)
            .progressCallback(progress -> {
                // 每10%進度輸出一次
                if (progress.getProcessedTasks() % 100 == 0) {
                    System.out.printf("進度: %d/%d (%.1f%%) - 成功: %d, 失敗: %d\n",
                        progress.getProcessedTasks(), progress.getTotalTasks(),
                        progress.getProgressPercentage(), progress.getSuccessfulTasks(),
                        progress.getFailedTasks());
                }
            })
            .build();
        
        try {
            long startTime = System.currentTimeMillis();
            
            BatchOperationResult result = batchUpdateUseCase.execute(request);
            
            long endTime = System.currentTimeMillis();
            
            System.out.println("\n大批量處理完成！");
            printPerformanceResults(result, endTime - startTime);
            
        } catch (Exception e) {
            logger.error("大批量處理失敗: {}", e.getMessage());
        }
    }

    /**
     * 示範4: 錯誤處理和恢復
     */
    public void demonstrateErrorHandlingAndRecovery() {
        System.out.println("\n=== 錯誤處理和恢復示範 ===");
        
        // 包含一些會失敗的任務ID
        List<String> taskIds = Arrays.asList(
            "valid-task-1", "non-existent-task", "valid-task-2",
            "conflicted-task", "valid-task-3"
        );
        
        BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
            .taskIds(taskIds)
            .newStatus(TaskStatus.COMPLETED)
            .updatedBy("error-demo-user")
            .reason("Error handling demonstration")
            .maxRetries(2)
            .build();
        
        try {
            BatchOperationResult result = batchUpdateUseCase.execute(request);
            
            System.out.println("批次操作完成（部分成功）");
            printErrorAnalysis(result);
            
            // 示範錯誤恢復策略
            if (result.hasErrors()) {
                demonstrateErrorRecovery(result);
            }
            
        } catch (Exception e) {
            logger.error("批次操作完全失敗: {}", e.getMessage());
        }
    }

    /**
     * 進度更新處理器
     */
    private void handleProgressUpdate(BatchProgressUpdate progress) {
        System.out.printf("\r進度更新: %d/%d (%.1f%%) | 目前處理: %s | 操作: %s",
            progress.getProcessedTasks(), progress.getTotalTasks(),
            progress.getProgressPercentage(), progress.getCurrentTaskId(),
            progress.getCurrentOperation());
        
        if (progress.isCompleted()) {
            System.out.println(); // 換行
        }
    }

    /**
     * 輸出基本結果
     */
    private void printBasicResults(BatchOperationResult result) {
        System.out.println("批次更新結果:");
        System.out.println("- 總任務數: " + result.getTotalCount());
        System.out.println("- 成功數: " + result.getSuccessCount());
        System.out.println("- 失敗數: " + result.getFailureCount());
        System.out.println("- 成功率: " + String.format("%.2f%%", result.getSuccessRate()));
        System.out.println("- 執行時間: " + result.getExecutionTime().toMillis() + "ms");
    }

    /**
     * 輸出詳細結果
     */
    private void printDetailedResults(BatchOperationResult result) {
        printBasicResults(result);
        
        System.out.println("- 重試次數: " + result.getRetryCount());
        System.out.println("- 平均處理時間: " + String.format("%.2fms", result.getAverageProcessingTimeMs()));
        System.out.println("- 吞吐量: " + String.format("%.2f tasks/sec", result.getThroughputPerSecond()));
        
        if (result.hasErrors()) {
            System.out.println("\n錯誤詳情:");
            result.getErrors().forEach(error -> {
                System.out.println("  - 任務 " + error.getTaskId() + ": " + error.getErrorMessage());
            });
        }
    }

    /**
     * 输出性能結果
     */
    private void printPerformanceResults(BatchOperationResult result, long actualTime) {
        System.out.println("性能統計:");
        System.out.println("- 實際執行時間: " + actualTime + "ms");
        System.out.println("- 記錄執行時間: " + result.getExecutionTime().toMillis() + "ms");
        System.out.println("- 處理的任務數: " + result.getTotalCount());
        System.out.println("- 成功率: " + String.format("%.2f%%", result.getSuccessRate()));
        System.out.println("- 平均每任務時間: " + String.format("%.3fms", result.getAverageProcessingTimeMs()));
        System.out.println("- 吞吐量: " + String.format("%.2f tasks/sec", result.getThroughputPerSecond()));
        System.out.println("- 重試率: " + String.format("%.2f%%", 
            result.getTotalCount() > 0 ? (double) result.getRetryCount() / result.getTotalCount() * 100 : 0));
        
        // 輸出性能指標
        System.out.println("\n詳細性能指標:");
        result.getPerformanceMetrics().forEach((key, value) -> {
            System.out.println("  - " + key + ": " + value);
        });
    }

    /**
     * 輸出錯誤分析
     */
    private void printErrorAnalysis(BatchOperationResult result) {
        printBasicResults(result);
        
        if (result.hasErrors()) {
            System.out.println("\n錯誤分析:");
            
            long concurrencyErrors = result.getErrors().stream()
                .mapToLong(error -> error.isConcurrencyError() ? 1 : 0)
                .sum();
            
            long businessRuleErrors = result.getErrors().stream()
                .mapToLong(error -> error.isBusinessRuleViolation() ? 1 : 0)
                .sum();
            
            long retriedErrors = result.getErrors().stream()
                .mapToLong(error -> error.isFailedAfterRetry() ? 1 : 0)
                .sum();
            
            System.out.println("- 並發衝突錯誤: " + concurrencyErrors);
            System.out.println("- 業務規則違反: " + businessRuleErrors);
            System.out.println("- 重試後仍失敗: " + retriedErrors);
            
            System.out.println("\n失敗任務詳情:");
            result.getErrors().forEach(error -> {
                System.out.printf("  - %s: %s (%s) [重試: %d次]\n",
                    error.getTaskId(), error.getErrorMessage(), 
                    error.getErrorType(), error.getRetryAttempts());
            });
        }
    }

    /**
     * 示範錯誤恢復策略
     */
    private void demonstrateErrorRecovery(BatchOperationResult result) {
        System.out.println("\n=== 錯誤恢復策略示範 ===");
        
        // 收集可重試的錯誤
        List<String> retryableTaskIds = result.getErrors().stream()
            .filter(error -> error.isConcurrencyError() && !error.isFailedAfterRetry())
            .map(BatchOperationError::getTaskId)
            .toList();
        
        if (!retryableTaskIds.isEmpty()) {
            System.out.println("發現 " + retryableTaskIds.size() + " 個可重試的任務");
            System.out.println("實際應用中，這些任務可以:");
            System.out.println("1. 排程延遲重試");
            System.out.println("2. 放入死信佇列");
            System.out.println("3. 通知管理員手動處理");
            System.out.println("4. 記錄到錯誤日誌以供後續分析");
            
            retryableTaskIds.forEach(taskId -> {
                System.out.println("  - 任務 " + taskId + " 已標記為待重試");
            });
        }
        
        // 收集不可重試的錯誤
        List<String> permanentFailures = result.getErrors().stream()
            .filter(error -> !error.isConcurrencyError() || error.isFailedAfterRetry())
            .map(BatchOperationError::getTaskId)
            .toList();
        
        if (!permanentFailures.isEmpty()) {
            System.out.println("\n永久失敗的任務 (" + permanentFailures.size() + " 個):");
            permanentFailures.forEach(taskId -> {
                System.out.println("  - 任務 " + taskId + " 需要手動檢查");
            });
        }
    }

    /**
     * 主要示範方法
     */
    public void runAllDemonstrations() {
        System.out.println("BatchUpdateTasksUseCase 功能示範");
        System.out.println("=====================================");
        
        demonstrateBasicBatchUpdate();
        demonstrateBatchUpdateWithProgress();
        demonstrateLargeBatchProcessing();
        demonstrateErrorHandlingAndRecovery();
        
        System.out.println("\n=====================================");
        System.out.println("所有示範完成！");
    }
}