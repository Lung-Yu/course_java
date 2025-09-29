# UC-010: TaskStatisticsUseCase 實作文檔

## 概述

本實作展示了進階的任務統計分析功能，重點展現**函數式程式設計**和**效能優化**技術。

## 🚀 核心功能特色

### 1. Stream API 複雜統計計算
```java
// 多維度統計計算示例
private Map<String, Long> groupByPriority(List<TaskDTO> tasks) {
    return tasks.stream()
        .collect(Collectors.groupingBy(
            task -> task.getPriority().getDisplayName(),
            // 自訂排序 - 按優先級等級排序
            () -> new TreeMap<>(Comparator.comparing(key -> 
                Arrays.stream(Priority.values())
                    .filter(p -> p.getDisplayName().equals(key))
                    .mapToInt(Priority::getLevel)
                    .findFirst()
                    .orElse(0)
            )),
            Collectors.counting()
        ));
}
```

**函數式特色：**
- 複雜的 `collect()` 操作
- 自訂 `TreeMap` 排序邏輯
- 方法鏈組合 (Method chaining)

### 2. 高階函數與函數組合
```java
// 時間分組函數工廠
private Function<TaskDTO, String> createTimeGroupingFunction(StatisticsRequest.Dimension timeDimension) {
    return switch (timeDimension) {
        case TIME_DAILY -> task -> task.getCreatedAt().format(DAILY_FORMAT);
        case TIME_WEEKLY -> task -> {
            LocalDateTime date = task.getCreatedAt();
            LocalDateTime weekStart = date.with(DayOfWeek.MONDAY);
            return weekStart.format(WEEKLY_FORMAT);
        };
        case TIME_MONTHLY -> task -> task.getCreatedAt().format(MONTHLY_FORMAT);
        case TIME_QUARTERLY -> task -> {
            LocalDateTime date = task.getCreatedAt();
            int quarter = (date.getMonthValue() - 1) / 3 + 1;
            return date.getYear() + "-Q" + quarter;
        };
        default -> task -> "其他";
    };
}
```

**函數式特色：**
- **高階函數** (Higher-order function) 返回函數
- **函數工廠模式**
- **Switch Expression** 與 Lambda 結合

### 3. 記憶體優化大資料處理
```java
// 自動選擇串行/並行處理
Stream<TaskDTO> taskStream = request.isEnableMemoryOptimization() && tasks.size() > 1000
    ? tasks.parallelStream()
    : tasks.stream();
```

**效能優化特色：**
- 根據資料量自動選擇處理策略
- 並行 Stream 處理大資料集
- 記憶體使用優化

### 4. 進階快取機制
```java
public class StatisticsCache {
    private static class CacheEntry {
        private final StatisticsReport report;
        private final LocalDateTime createdAt;
        private final long ttlMinutes;
        private volatile LocalDateTime lastAccessed;
        private final AtomicLong accessCount = new AtomicLong(0);
    }
    
    // LRU 驅逐策略
    private void evictLeastRecentlyUsed() {
        // 找出最久未使用的項目並移除
    }
}
```

**效能優化特色：**
- **TTL** (Time To Live) 過期機制
- **LRU** (Least Recently Used) 驅逐策略
- 執行緒安全的 `ConcurrentHashMap`
- 原子操作計數器

## 📊 多維度分析架構

### 維度配置枚舉
```java
public enum Dimension {
    STATUS("status", "按狀態統計"),
    PRIORITY("priority", "按優先級統計"),
    TIME_DAILY("time_daily", "按日統計"),
    TIME_WEEKLY("time_weekly", "按週統計"),
    TIME_MONTHLY("time_monthly", "按月統計"),
    TIME_QUARTERLY("time_quarterly", "按季統計"),
    COMPLETION_TIME("completion_time", "完成時間分析"),
    OVERDUE_ANALYSIS("overdue_analysis", "逾期分析");
}
```

### 彈性的圖表配置
```java
public enum ChartPreference {
    AUTO("auto", "自動選擇"),
    PIE_CHARTS("pie", "餅圖優先"),
    BAR_CHARTS("bar", "柱狀圖優先"),
    LINE_CHARTS("line", "折線圖優先"),
    MIXED("mixed", "混合圖表");
}
```

## 🔧 函數式程式設計展示

### 1. 不可變物件設計
```java
public class StatisticsReport {
    // 所有欄位都是 final
    private final LocalDateTime generatedAt;
    private final long totalTasks;
    private final Map<String, Long> tasksByStatus;
    
    // 防禦性複製
    this.tasksByStatus = Map.copyOf(builder.tasksByStatus);
}
```

### 2. 建造者模式與流暢介面
```java
StatisticsReport report = StatisticsReport.builder()
    .period(request.getStartDate(), request.getEndDate(), description)
    .totalTasks(totalCount)
    .completedTasks(basicStats.get("completed"))
    .completionRate(calculateCompletionRate(basicStats))
    .build();
```

### 3. Stream 管道操作
```java
// 複雜的篩選和轉換管道
return taskRepository.findAll().stream()
    .filter(task -> !task.isDeleted() || request.isIncludeDeleted())
    .map(task -> TaskDTO.fromTask(task))
    .filter(task -> isTaskInDateRange(task, request))
    .filter(task -> matchesStatusFilter(task, request))
    .filter(task -> matchesPriorityFilter(task, request))
    .limit(request.getMaxResults())
    .collect(Collectors.toList());
```

### 4. 條件式函數組合
```java
// 計算平均完成天數
private double calculateAverageCompletionDays(List<TaskDTO> tasks) {
    return tasks.stream()
        .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
        .filter(task -> task.getUpdatedAt() != null)
        .mapToDouble(task -> ChronoUnit.DAYS.between(task.getCreatedAt(), task.getUpdatedAt()))
        .average()
        .orElse(0.0);
}
```

## 🎯 效能優化策略

### 1. 智慧快取機制
- **多層快取鍵生成**：根據查詢參數生成唯一快取鍵
- **自動清理**：定期清理過期項目
- **效能監控**：記錄命中率和查詢時間

### 2. 記憶體管理
- **流式處理**：避免載入全部資料到記憶體
- **並行處理**：大資料集自動啟用並行 Stream
- **資料分頁**：`maxResults` 限制處理資料量

### 3. 非同步處理支援
```java
public CompletableFuture<StatisticsReport> generateReportAsync(StatisticsRequest request) {
    return CompletableFuture.supplyAsync(() -> generateReport(request));
}
```

## 📈 圖表資料結構

### 統一的資料點格式
```java
public static class DataPoint {
    private final String label;
    private final double value;
    private final String category;
    private final Map<String, Object> metadata;
}
```

### 多圖表類型支援
- **餅圖** (PIE)：狀態分佈
- **柱狀圖** (BAR)：優先級統計
- **折線圖** (LINE)：時間趨勢
- **面積圖** (AREA)：累積統計
- **甜甜圈圖** (DONUT)：階層資料

## 🧪 測試策略

### 1. 功能測試
- 基本統計計算正確性
- 多維度分析結果驗證
- 篩選條件邏輯測試

### 2. 效能測試
- 快取命中率驗證
- 記憶體優化效果測試
- 並行處理效能比較

### 3. 邊界測試
- 空資料集處理
- 大資料集壓力測試
- 異常情況處理

## 💡 使用範例

### 基本週報統計
```java
StatisticsReport weeklyReport = taskStatisticsUseCase.generateReport(
    StatisticsRequest.defaultWeekly()
);
```

### 自訂多維度分析
```java
StatisticsRequest request = StatisticsRequest.builder()
    .lastMonths(3)
    .statusFilter(Set.of(TaskStatus.COMPLETED, TaskStatus.IN_PROGRESS))
    .priorityFilter(Set.of(Priority.HIGH, Priority.URGENT))
    .dimensions(Set.of(
        StatisticsRequest.Dimension.STATUS,
        StatisticsRequest.Dimension.TIME_MONTHLY
    ))
    .chartPreference(StatisticsRequest.ChartPreference.MIXED)
    .enableMemoryOptimization(true)
    .build();

StatisticsReport report = taskStatisticsUseCase.generateReport(request);
```

### 非同步處理
```java
CompletableFuture<StatisticsReport> future = 
    taskStatisticsUseCase.generateReportAsync(request);

future.thenAccept(report -> {
    System.out.println("統計完成: " + report.getTotalTasks() + " 個任務");
});
```

## 🎓 教學重點

### 函數式程式設計概念
1. **純函數** (Pure Functions)
2. **不可變性** (Immutability)
3. **高階函數** (Higher-order Functions)
4. **函數組合** (Function Composition)
5. **遞歸與遲延評估** (Recursion & Lazy Evaluation)

### Java 進階特性應用
1. **Stream API** 進階用法
2. **Optional** 處理空值
3. **CompletableFuture** 非同步程式設計
4. **併發集合類別** 執行緒安全
5. **泛型與型別推導**

### 效能優化技巧
1. **時間複雜度分析**
2. **空間複雜度優化**
3. **快取策略設計**
4. **並行處理最佳化**
5. **記憶體洩漏預防**

## 🔍 擴展性設計

此實作具備良好的擴展性：

1. **新統計維度**：輕鬆添加新的 `Dimension`
2. **圖表類型**：支援新的圖表格式
3. **快取策略**：可替換不同快取實作
4. **資料來源**：可支援不同資料庫
5. **輸出格式**：可擴展 JSON、XML 等格式

## 📋 編譯與執行

```bash
# 編譯專案
cd advance_projects/task-list
mvn compile

# 執行測試
mvn test -Dtest=TaskStatisticsUseCaseTest

# 執行示範程式
mvn exec:java -Dexec.mainClass="com.tygrus.task_list.application.usecase.TaskStatisticsUseCaseDemo"
```

---

**本實作展示了 Java 函數式程式設計的精髓，結合現代效能優化技術，是學習進階 Java 開發的絕佳範例。**