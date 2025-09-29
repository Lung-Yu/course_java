# UC-008: 批次更新任務 (BatchUpdateTasksUseCase) - 使用指南

## 概述

BatchUpdateTasksUseCase 是一個企業級的並行批次處理系統，展示了現代 Java 應用程式中的並發程式設計、錯誤恢復和性能優化技術。

## 核心特性

### ✨ 並行處理
- 使用 `CompletableFuture` 實現非同步處理
- 智能執行緒池管理，根據 CPU 核心數自動調整
- 批次分割策略，平衡延遲和吞吐量

### 🔄 錯誤恢復
- 指數退避重試機制
- 樂觀鎖衝突自動處理
- 部分失敗隔離，不影響其他任務

### 📊 進度監控
- 即時進度回報
- 詳細性能指標統計
- 自定義回調函數支援

### 🛡️ 執行緒安全
- 原子操作確保資料一致性
- 讀寫鎖保護共享資源
- 無鎖資料結構優化性能

## 快速開始

### 1. 基本使用

```java
// 創建批次更新請求
List<String> taskIds = Arrays.asList("task-1", "task-2", "task-3");

BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
    .taskIds(taskIds)
    .newStatus(TaskStatus.IN_PROGRESS)
    .updatedBy("user-001")
    .reason("批次啟動任務處理")
    .build();

// 執行批次更新
BatchOperationResult result = batchUpdateUseCase.execute(request);

// 檢查結果
if (result.isCompletelySuccessful()) {
    System.out.println("所有任務更新成功");
} else {
    System.out.printf("成功: %d, 失敗: %d\n", 
        result.getSuccessCount(), result.getFailureCount());
}
```

### 2. 帶進度監控的批次更新

```java
BatchUpdateTaskRequest request = BatchUpdateTaskRequest.builder()
    .taskIds(largeTaskList)
    .newStatus(TaskStatus.IN_PROGRESS)
    .updatedBy("batch-processor")
    .batchSize(20) // 每批處理20個任務
    .maxRetries(3) // 最多重試3次
    .progressCallback(progress -> {
        System.out.printf("進度: %.1f%% (%d/%d) - 成功: %d, 失敗: %d\n",
            progress.getProgressPercentage(),
            progress.getProcessedTasks(),
            progress.getTotalTasks(),
            progress.getSuccessfulTasks(),
            progress.getFailedTasks());
    })
    .build();

BatchOperationResult result = batchUpdateUseCase.execute(request);
```

### 3. 錯誤處理和恢復

```java
BatchOperationResult result = batchUpdateUseCase.execute(request);

if (result.hasErrors()) {
    System.out.println("發現錯誤，開始分析:");
    
    // 分析錯誤類型
    Map<String, Long> errorTypes = result.getErrors().stream()
        .collect(Collectors.groupingBy(
            BatchOperationError::getErrorType,
            Collectors.counting()
        ));
    
    errorTypes.forEach((type, count) -> {
        System.out.printf("  %s: %d 次\n", type, count);
    });
    
    // 收集可重試的任務
    List<String> retryableTasks = result.getErrors().stream()
        .filter(error -> error.isConcurrencyError() && !error.isFailedAfterRetry())
        .map(BatchOperationError::getTaskId)
        .collect(Collectors.toList());
    
    if (!retryableTasks.isEmpty()) {
        System.out.println("準備重試任務: " + retryableTasks);
        // 實施重試邏輯...
    }
}
```

## 配置選項

### BatchUpdateTaskRequest 參數

| 參數 | 類型 | 默認值 | 說明 |
|------|------|--------|------|
| `taskIds` | `List<String>` | - | 要更新的任務ID列表（必填） |
| `newStatus` | `TaskStatus` | - | 新的任務狀態（必填） |
| `updatedBy` | `String` | - | 更新操作的執行者（必填） |
| `reason` | `String` | `null` | 更新原因（可選） |
| `batchSize` | `int` | `10` | 每批處理的任務數 |
| `maxRetries` | `int` | `3` | 最大重試次數 |
| `progressCallback` | `Consumer<BatchProgressUpdate>` | `null` | 進度回調函數 |

### 批次大小調優指南

- **小批次 (1-5)**: 適合高優先級任務，低延遲要求
- **中批次 (10-20)**: 平衡延遲和吞吐量，適合一般用途
- **大批次 (50-100)**: 高吞吐量處理，適合後台批次作業

## 性能指標

### BatchOperationResult 提供的指標

```java
BatchOperationResult result = batchUpdateUseCase.execute(request);

// 基本統計
System.out.println("成功率: " + result.getSuccessRate() + "%");
System.out.println("執行時間: " + result.getExecutionTime().toMillis() + "ms");
System.out.println("吞吐量: " + result.getThroughputPerSecond() + " tasks/sec");

// 詳細性能指標
Map<String, Object> metrics = result.getPerformanceMetrics();
System.out.println("平均處理時間: " + metrics.get("averageProcessingTimeMs") + "ms");
System.out.println("重試率: " + metrics.get("retryRate") + "%");
System.out.println("並發執行緒數: " + metrics.get("concurrentThreads"));
```

## 錯誤類型和處理策略

### 可重試錯誤
- `OptimisticLockException`: 樂觀鎖衝突，自動重試
- `IllegalStatusTransitionException`: 狀態轉換衝突，檢查業務邏輯後重試

### 不可重試錯誤
- `TaskNotFoundException`: 任務不存在，需要檢查資料完整性
- `IllegalArgumentException`: 參數錯誤，需要修正輸入

### 重試策略
```java
// 指數退避重試間隔
// 第1次重試: 100ms
// 第2次重試: 200ms  
// 第3次重試: 400ms
// ...

int retryDelay = 100 * (1 << (attemptNumber - 1));
```

## 併發控制

### 樂觀鎖處理
系統使用樂觀鎖來處理併發更新衝突：

```java
@Override
public Task saveWithOptimisticLock(Task task, Long expectedVersion) {
    // 檢查版本號
    if (currentVersion != expectedVersion) {
        throw new OptimisticLockException("Version conflict");
    }
    // 保存並增加版本號
    return save(task.withVersion(expectedVersion + 1));
}
```

### 執行緒池配置
```java
// 執行緒池大小 = min(CPU核心數 * 2, 10)
int threadPoolSize = Math.min(
    Runtime.getRuntime().availableProcessors() * 2, 
    10
);
```

## 監控和警報

### 關鍵監控指標
- **成功率**: 應該 > 95%
- **平均執行時間**: 根據批次大小調整期望值
- **重試率**: 應該 < 10%
- **併發錯誤率**: 應該 < 5%

### 警報條件
```java
// 成功率過低警報
if (result.getSuccessRate() < 95.0) {
    alertService.sendAlert("批次操作成功率過低: " + result.getSuccessRate() + "%");
}

// 執行時間過長警報
if (result.getExecutionTime().toSeconds() > 60) {
    alertService.sendAlert("批次操作執行時間過長: " + result.getExecutionTime());
}

// 重試率過高警報
double retryRate = (double) result.getRetryCount() / result.getTotalCount() * 100;
if (retryRate > 10.0) {
    alertService.sendAlert("批次操作重試率過高: " + retryRate + "%");
}
```

## 最佳實踐

### 1. 批次大小選擇
```java
public int calculateOptimalBatchSize(int totalTasks, int availableMemory) {
    // 根據可用記憶體和任務總數計算最佳批次大小
    int memoryBasedSize = availableMemory / TASK_MEMORY_FOOTPRINT;
    int computeBasedSize = Runtime.getRuntime().availableProcessors() * 2;
    
    return Math.min(Math.max(memoryBasedSize, computeBasedSize), MAX_BATCH_SIZE);
}
```

### 2. 錯誤處理策略
```java
public void handleBatchErrors(BatchOperationResult result) {
    if (!result.hasErrors()) {
        return;
    }
    
    // 按錯誤類型分組處理
    Map<String, List<BatchOperationError>> errorsByType = result.getErrors()
        .stream()
        .collect(Collectors.groupingBy(BatchOperationError::getErrorType));
    
    // 處理併發錯誤
    List<BatchOperationError> concurrencyErrors = errorsByType.get("OptimisticLockException");
    if (concurrencyErrors != null) {
        scheduleRetryBatch(concurrencyErrors);
    }
    
    // 處理業務錯誤
    List<BatchOperationError> businessErrors = errorsByType.get("IllegalStatusTransitionException");
    if (businessErrors != null) {
        logBusinessRuleViolations(businessErrors);
    }
    
    // 處理系統錯誤
    List<BatchOperationError> systemErrors = errorsByType.get("TaskNotFoundException");
    if (systemErrors != null) {
        reportDataInconsistency(systemErrors);
    }
}
```

### 3. 性能調優
```java
// 使用 JVM 參數優化垃圾收集
// -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:+UnlockExperimentalVMOptions

// 監控記憶體使用
MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
long beforeMemory = memoryBean.getHeapMemoryUsage().getUsed();

BatchOperationResult result = batchUpdateUseCase.execute(request);

long afterMemory = memoryBean.getHeapMemoryUsage().getUsed();
long memoryUsed = afterMemory - beforeMemory;

System.out.println("記憶體使用量: " + memoryUsed / 1024 / 1024 + " MB");
```

## 故障排除

### 常見問題和解決方案

#### 1. 記憶體不足 (OutOfMemoryError)
**原因**: 批次大小過大或任務物件過重
**解決方案**:
```java
// 減少批次大小
request = request.toBuilder().batchSize(5).build();

// 或使用流式處理
// 實現分頁處理大批次任務
```

#### 2. 執行時間過長
**原因**: 資料庫效能瓶頸或網路延遲
**解決方案**:
```java
// 增加連接池大小
// 使用批次查詢減少資料庫往返
List<Task> tasks = repository.findByIds(taskIds);

// 並行度調優
executorService = Executors.newFixedThreadPool(
    Math.min(availableCpuCores, databaseConnectionPoolSize)
);
```

#### 3. 死鎖問題
**原因**: 多個執行緒競爭相同資源
**解決方案**:
```java
// 使用統一的鎖定順序
List<String> sortedTaskIds = taskIds.stream()
    .sorted()
    .collect(Collectors.toList());

// 或使用更細粒度的鎖
// 每個任務使用獨立的鎖
```

## 編譯和運行

### 編譯專案
```bash
cd advance_projects/task-list
mvn clean compile
```

### 運行測試
```bash
# 運行所有測試
mvn test

# 運行特定測試
mvn test -Dtest=BatchUpdateTasksUseCaseSimpleTest

# 運行並產生測試報告
mvn test -Dmaven.test.failure.ignore=true surefire-report:report
```

### 運行示範程式
```bash
# 運行基本示範
java -cp target/classes com.tygrus.task_list.BatchUpdateDemo

# 運行詳細示範
java -cp target/classes com.tygrus.task_list.application.usecase.BatchUpdateTasksUseCaseDemo
```

### 性能測試
```bash
# 使用 JProfiler 進行性能分析
java -agentpath:/path/to/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849 \
     -cp target/classes com.tygrus.task_list.BatchUpdateDemo

# 使用 JVM 內建工具監控
java -XX:+PrintGCDetails -XX:+PrintGCTimeStamps \
     -cp target/classes com.tygrus.task_list.BatchUpdateDemo
```

## 擴展和自定義

### 1. 自定義重試策略
```java
public interface RetryStrategy {
    boolean shouldRetry(Exception exception, int attemptNumber);
    long getRetryDelay(int attemptNumber);
}

public class ExponentialBackoffStrategy implements RetryStrategy {
    @Override
    public boolean shouldRetry(Exception exception, int attemptNumber) {
        return exception instanceof OptimisticLockException && attemptNumber <= 3;
    }
    
    @Override
    public long getRetryDelay(int attemptNumber) {
        return 100L * (1L << (attemptNumber - 1));
    }
}
```

### 2. 自定義進度報告器
```java
public interface ProgressReporter {
    void reportProgress(BatchProgressUpdate update);
}

public class LoggingProgressReporter implements ProgressReporter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingProgressReporter.class);
    
    @Override
    public void reportProgress(BatchProgressUpdate update) {
        logger.info("批次進度: {}/{} ({}%) - 成功: {}, 失敗: {}",
            update.getProcessedTasks(), update.getTotalTasks(),
            update.getProgressPercentage(), update.getSuccessfulTasks(),
            update.getFailedTasks());
    }
}
```

### 3. 整合監控系統
```java
// 整合 Micrometer 指標
@Component
public class BatchUpdateMetrics {
    private final MeterRegistry meterRegistry;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer executionTimer;
    
    public BatchUpdateMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.successCounter = Counter.builder("batch_update_success")
            .description("Successful batch updates")
            .register(meterRegistry);
        this.failureCounter = Counter.builder("batch_update_failure")
            .description("Failed batch updates")
            .register(meterRegistry);
        this.executionTimer = Timer.builder("batch_update_duration")
            .description("Batch update execution time")
            .register(meterRegistry);
    }
    
    public void recordBatchResult(BatchOperationResult result) {
        successCounter.increment(result.getSuccessCount());
        failureCounter.increment(result.getFailureCount());
        executionTimer.record(result.getExecutionTime());
    }
}
```

這個批次更新系統展示了企業級 Java 應用程式中並發處理的最佳實踐，是學習分散式系統設計和性能優化的絕佳範例。