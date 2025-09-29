# UC-008: 批次更新任務 (BatchUpdateTasksUseCase)

## 概述
批次更新任務用例展示了並行處理、錯誤恢復和執行緒安全的核心概念。這是一個複雜的企業級功能，涵蓋了分散式系統設計的關鍵要素。

## 學習目標
- 掌握 CompletableFuture 和 ExecutorService 的並行程式設計
- 理解樂觀鎖和悲觀鎖的並發控制機制
- 實作重試機制和指數退避策略
- 設計進度回報和即時監控系統
- 掌握執行緒安全和原子操作
- 學習批次操作的性能優化技巧

## 核心特性

### 1. 並行處理架構
```java
// 使用CompletableFuture進行並行處理
List<CompletableFuture<BatchResult>> futures = batches.stream()
    .map(batch -> processBatchAsync(batch, request))
    .collect(Collectors.toList());

// 等待所有批次完成
CompletableFuture<Void> allBatches = CompletableFuture.allOf(
    futures.toArray(new CompletableFuture[0])
);
```

### 2. 錯誤恢復機制
- **重試策略**: 指數退避重試機制
- **衝突處理**: 樂觀鎖衝突自動重試
- **隔離失敗**: 部分失敗不影響其他任務
- **錯誤分類**: 區分可重試和永久性錯誤

### 3. 進度回報系統
```java
// 即時進度更新
BatchProgressUpdate progress = BatchProgressUpdate.builder()
    .totalTasks(totalTasks)
    .processedTasks(processedCount)
    .successfulTasks(successCount)
    .failedTasks(failedCount)
    .currentTaskId(currentTask)
    .currentOperation(operation)
    .build();

progressCallback.accept(progress);
```

### 4. 執行緒安全保障
- **原子計數器**: AtomicInteger 確保計數正確性
- **讀寫鎖**: ReentrantReadWriteLock 保護共享資源
- **並發集合**: ConcurrentHashMap 存儲性能指標
- **交易隔離**: 每個任務獨立交易避免死鎖

## 關鍵設計模式

### 1. Builder Pattern
所有的 DTO 都使用 Builder 模式，提供靈活的物件構建：
```java
BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
    .taskIds(taskIds)
    .newStatus(TaskStatus.IN_PROGRESS)
    .updatedBy("user")
    .batchSize(10)
    .maxRetries(3)
    .progressCallback(this::handleProgress)
    .build();
```

### 2. Strategy Pattern
不同的重試策略可以靈活切換：
- 指數退避策略
- 固定間隔策略
- 自適應策略

### 3. Observer Pattern
進度回報機制使用觀察者模式：
```java
request.getProgressCallback().accept(progressUpdate);
```

## 並發控制策略

### 1. 樂觀鎖控制
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public TaskDTO updateSingleTask(String taskId, BatchUpdateTaskRequest request, int attemptNumber) {
    // 每個任務獨立交易
    // 樂觀鎖衝突時自動重試
}
```

### 2. 執行緒池管理
```java
// 根據CPU核心數調整執行緒池大小
this.executorService = Executors.newFixedThreadPool(
    Math.min(Runtime.getRuntime().availableProcessors() * 2, 10)
);
```

### 3. 資源隔離
- 每個批次獨立處理
- 失敗隔離不影響其他批次
- 超時機制防止資源掛起

## 性能優化技巧

### 1. 批次大小調優
- 小批次：低延遲，高並發
- 大批次：高吞吐量，減少網路開銷
- 動態調整：根據系統負載自適應

### 2. 記憶體管理
- 流式處理避免大量物件創建
- 及時清理 CompletableFuture 引用
- 使用物件池重用昂貴物件

### 3. 資料庫優化
- 批次查詢減少資料庫往返
- 連接池配置優化
- 索引優化支援批次操作

## 錯誤分類和處理

### 1. 可重試錯誤
- **樂觀鎖衝突**: OptimisticLockException
- **暫時性網路錯誤**: ConnectionException
- **資料庫鎖等待**: LockTimeoutException

### 2. 不可重試錯誤
- **任務不存在**: TaskNotFoundException
- **業務規則違反**: IllegalStatusTransitionException
- **權限不足**: AccessDeniedException

### 3. 重試策略
```java
// 指數退避重試
for (int attempt = 1; attempt <= maxRetries + 1; attempt++) {
    try {
        return updateSingleTask(taskId, request, attempt);
    } catch (RetryableException e) {
        if (attempt <= maxRetries) {
            Thread.sleep(100L * (1L << (attempt - 1))); // 100ms, 200ms, 400ms...
        }
    }
}
```

## 監控和度量

### 1. 性能指標
- **吞吐量**: 每秒處理任務數
- **延遲**: 平均任務處理時間
- **成功率**: 操作成功百分比
- **重試率**: 重試操作比例

### 2. 業務指標
- **批次成功率**: 完全成功的批次比例
- **錯誤分佈**: 各類錯誤的佔比
- **資源使用率**: CPU、記憶體使用情況

### 3. 報警機制
- 成功率低於閾值時報警
- 執行時間超過預期時報警
- 重試率異常時報警

## 測試策略

### 1. 單元測試
- 正常流程測試
- 異常處理測試
- 邊界條件測試

### 2. 並發測試
- 多執行緒並行執行
- 競態條件檢測
- 死鎖檢測

### 3. 性能測試
- 大批量資料處理
- 高並發場景測試
- 記憶體洩漏檢測

### 4. 混沌工程
- 隨機注入網路延遲
- 隨機資料庫連接失敗
- 隨機服務節點故障

## 最佳實踐

### 1. 設計原則
- **單一職責**: 每個類只負責一個關注點
- **開閉原則**: 對擴展開放，對修改封閉
- **依賴注入**: 降低耦合度，提高可測試性

### 2. 錯誤處理
- **快速失敗**: 儘早發現和報告錯誤
- **優雅降級**: 部分失敗時保持系統可用
- **詳細日誌**: 記錄足夠的上下文資訊

### 3. 性能優化
- **批次操作**: 減少資料庫往返次數
- **連接池**: 重用資料庫連接
- **異步處理**: 提高系統吞吐量

### 4. 監控和報警
- **健康檢查**: 定期檢查系統狀態
- **性能監控**: 追蹤關鍵性能指標
- **錯誤報警**: 及時發現和處理問題

## 使用範例

### 基本用法
```java
// 創建批次更新請求
BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
    .taskIds(Arrays.asList("task-1", "task-2", "task-3"))
    .newStatus(TaskStatus.IN_PROGRESS)
    .updatedBy("user-id")
    .reason("Bulk status update")
    .build();

// 執行批次更新
BatchOperationResult result = batchUpdateUseCase.execute(request);

// 檢查結果
if (result.isCompletelySuccessful()) {
    System.out.println("所有任務更新成功");
} else {
    System.out.println("部分任務更新失敗: " + result.getFailureCount());
}
```

### 進階用法
```java
// 帶進度監控的批次更新
BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
    .taskIds(largeTaskList)
    .newStatus(TaskStatus.COMPLETED)
    .updatedBy("batch-processor")
    .batchSize(50)
    .maxRetries(3)
    .progressCallback(progress -> {
        System.out.printf("進度: %.1f%% (%d/%d)\n",
            progress.getProgressPercentage(),
            progress.getProcessedTasks(),
            progress.getTotalTasks());
    })
    .build();

BatchOperationResult result = batchUpdateUseCase.execute(request);

// 分析結果
analyzeResults(result);
```

## 編譯和執行

### 編譯
```bash
# 編譯整個項目
mvn clean compile

# 只編譯測試
mvn test-compile
```

### 運行測試
```bash
# 運行所有測試
mvn test

# 運行特定測試類
mvn test -Dtest=BatchUpdateTasksUseCaseTest

# 運行特定測試方法
mvn test -Dtest=BatchUpdateTasksUseCaseTest#testSuccessfulBatchUpdate
```

### 運行示範
```bash
# 運行示範程式
java -cp target/classes com.tygrus.task_list.application.usecase.BatchUpdateTasksUseCaseDemo
```

## 擴展點

### 1. 自定義重試策略
實作 RetryStrategy 介面來定義自己的重試邏輯

### 2. 自定義進度報告
實作 ProgressReporter 介面來自定義進度報告格式

### 3. 自定義錯誤處理
實作 ErrorHandler 介面來處理特定類型的錯誤

### 4. 自定義性能監控
整合 Micrometer 或其他監控框架

## 故障排除

### 常見問題

1. **記憶體溢出**: 調整批次大小和執行緒池大小
2. **死鎖**: 檢查交易邊界和鎖定順序
3. **性能差**: 分析瓶頸，優化資料庫查詢
4. **不一致性**: 檢查交易隔離級別和並發控制

### 除錯技巧

1. 啟用詳細日誌記錄
2. 使用分析器檢查性能瓶頸
3. 監控執行緒池狀態
4. 追蹤資料庫連接使用情況

這個 Use Case 是學習企業級並行處理和分散式系統設計的絕佳範例，涵蓋了從基本的並發控制到複雜的錯誤恢復策略的各個方面。