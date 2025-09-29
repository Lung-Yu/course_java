package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.ChartData;
import com.tygrus.task_list.application.dto.StatisticsReport;
import com.tygrus.task_list.application.dto.StatisticsRequest;
import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.infrastructure.cache.StatisticsCache;
import com.tygrus.task_list.infrastructure.repository.InMemoryTaskRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * UC-010: 任務統計分析 UseCase 示範程式
 * 
 * 展示功能：
 * 1. Stream API 複雜統計計算
 * 2. 多維度分析（狀態、優先級、時間）
 * 3. 多種圖表格式支援
 * 4. 記憶體優化大資料處理
 * 5. 可配置統計維度
 * 6. 快取機制效能提升
 * 7. 函數式程式設計展示
 */
public class TaskStatisticsUseCaseDemo {
    
    private final TaskStatisticsUseCase taskStatisticsUseCase;
    private final InMemoryTaskRepository taskRepository;
    private final StatisticsCache cache;
    
    public TaskStatisticsUseCaseDemo() {
        this.taskRepository = new InMemoryTaskRepository();
        com.tygrus.task_list.domain.repository.TaskRepository domainRepo = 
            new com.tygrus.task_list.infrastructure.repository.DomainTaskRepositoryAdapter(taskRepository);
        this.cache = new StatisticsCache(15, 100); // 15分鐘TTL, 100項目上限
        this.taskStatisticsUseCase = new TaskStatisticsUseCase(domainRepo, cache);
        
        // 初始化測試資料
        initializeTestData();
    }
    
    public static void main(String[] args) {
        TaskStatisticsUseCaseDemo demo = new TaskStatisticsUseCaseDemo();
        demo.runAllDemos();
    }
    
    /**
     * 執行所有示範
     */
    public void runAllDemos() {
        System.out.println("=".repeat(80));
        System.out.println("UC-010 任務統計分析 UseCase 功能示範");
        System.out.println("=".repeat(80));
        
        // 1. 基本統計報告
        demonstrateBasicStatistics();
        
        // 2. 多維度分析
        demonstrateMultiDimensionAnalysis();
        
        // 3. 時間範圍分析
        demonstrateTimeRangeAnalysis();
        
        // 4. 圖表資料生成
        demonstrateChartGeneration();
        
        // 5. 快取機制
        demonstrateCachePerformance();
        
        // 6. 大資料集優化
        demonstrateBigDataOptimization();
        
        // 7. 非同步處理
        demonstrateAsyncProcessing();
        
        // 8. 函數式程式設計特性
        demonstrateFunctionalProgramming();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("所有示範完成！");
        System.out.println("=".repeat(80));
    }
    
    /**
     * 示範1：基本統計報告
     */
    private void demonstrateBasicStatistics() {
        System.out.println("\n【示範1】基本統計報告");
        System.out.println("-".repeat(50));
        
        // 創建基本統計請求
        StatisticsRequest request = StatisticsRequest.defaultWeekly();
        
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        System.out.printf("報告期間: %s\n", report.getPeriodDescription());
        System.out.printf("總任務數: %d\n", report.getTotalTasks());
        System.out.printf("已完成: %d\n", report.getCompletedTasks());
        System.out.printf("進行中: %d\n", report.getInProgressTasks());
        System.out.printf("待處理: %d\n", report.getPendingTasks());
        System.out.printf("已取消: %d\n", report.getCancelledTasks());
        System.out.printf("完成率: %.1f%%\n", report.getCompletionRate() * 100);
        System.out.printf("平均完成天數: %.1f 天\n", report.getAvgCompletionDays());
        System.out.printf("逾期任務: %d\n", report.getOverdueTasks());
        System.out.printf("處理時間: %d ms\n", report.getProcessingTimeMs());
        System.out.printf("來自快取: %s\n", report.isFromCache() ? "是" : "否");
    }
    
    /**
     * 示範2：多維度分析
     */
    private void demonstrateMultiDimensionAnalysis() {
        System.out.println("\n【示範2】多維度分析");
        System.out.println("-".repeat(50));
        
        // 配置多維度分析
        StatisticsRequest request = StatisticsRequest.builder()
            .lastMonths(1)
            .dimensions(Set.of(
                StatisticsRequest.Dimension.STATUS,
                StatisticsRequest.Dimension.PRIORITY,
                StatisticsRequest.Dimension.TIME_WEEKLY
            ))
            .reportTitle("月度多維度分析報告")
            .build();
        
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        // 按狀態統計
        System.out.println("\n按狀態分組:");
        report.getTasksByStatus().forEach((status, count) -> 
            System.out.printf("  %s: %d\n", status, count)
        );
        
        // 按優先級統計
        System.out.println("\n按優先級分組:");
        report.getTasksByPriority().forEach((priority, count) -> 
            System.out.printf("  %s: %d\n", priority, count)
        );
        
        // 時間趨勢分析
        System.out.println("\n時間趨勢分析:");
        report.getTasksByTimeGroup().forEach((timeGroup, count) -> 
            System.out.printf("  %s: %d\n", timeGroup, count)
        );
    }
    
    /**
     * 示範3：時間範圍分析
     */
    private void demonstrateTimeRangeAnalysis() {
        System.out.println("\n【示範3】時間範圍分析");
        System.out.println("-".repeat(50));
        
        LocalDateTime now = LocalDateTime.now();
        
        // 最近7天分析
        StatisticsRequest weeklyRequest = StatisticsRequest.builder()
            .timeRange(now.minusDays(7), now)
            .dimensions(Set.of(StatisticsRequest.Dimension.TIME_DAILY))
            .reportTitle("最近7天日報")
            .build();
        
        StatisticsReport weeklyReport = taskStatisticsUseCase.generateReport(weeklyRequest);
        
        System.out.println("最近7天每日任務統計:");
        weeklyReport.getTasksByTimeGroup().forEach((day, count) -> 
            System.out.printf("  %s: %d 個任務\n", day, count)
        );
        
        // 季度分析
        StatisticsRequest quarterlyRequest = StatisticsRequest.builder()
            .lastMonths(3)
            .dimensions(Set.of(StatisticsRequest.Dimension.TIME_MONTHLY))
            .reportTitle("季度報告")
            .build();
        
        StatisticsReport quarterlyReport = taskStatisticsUseCase.generateReport(quarterlyRequest);
        
        System.out.println("\n季度月份統計:");
        quarterlyReport.getTasksByTimeGroup().forEach((month, count) -> 
            System.out.printf("  %s: %d 個任務\n", month, count)
        );
    }
    
    /**
     * 示範4：圖表資料生成
     */
    private void demonstrateChartGeneration() {
        System.out.println("\n【示範4】圖表資料生成");
        System.out.println("-".repeat(50));
        
        // 生成不同類型的圖表
        StatisticsRequest request = StatisticsRequest.builder()
            .lastMonths(1)
            .chartPreference(StatisticsRequest.ChartPreference.MIXED)
            .generateCharts(true)
            .build();
        
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        System.out.printf("生成圖表數量: %d\n", report.getChartDataList().size());
        
        for (ChartData chart : report.getChartDataList()) {
            System.out.printf("\n圖表: %s (%s)\n", chart.getTitle(), chart.getType().getDisplayName());
            System.out.printf("  副標題: %s\n", chart.getSubtitle());
            System.out.printf("  資料點數量: %d\n", chart.getDataPointCount());
            System.out.printf("  資料總和: %.0f\n", chart.getTotalValue());
            
            // 顯示前3個資料點
            chart.getDataPoints().stream()
                .limit(3)
                .forEach(point -> 
                    System.out.printf("    %s: %.0f\n", point.getLabel(), point.getValue())
                );
            
            if (chart.getDataPointCount() > 3) {
                System.out.println("    ...");
            }
        }
    }
    
    /**
     * 示範5：快取機制效能
     */
    private void demonstrateCachePerformance() {
        System.out.println("\n【示範5】快取機制效能測試");
        System.out.println("-".repeat(50));
        
        StatisticsRequest request = StatisticsRequest.defaultMonthly();
        
        // 第一次查詢（無快取）
        long startTime1 = System.currentTimeMillis();
        StatisticsReport report1 = taskStatisticsUseCase.generateReport(request);
        long time1 = System.currentTimeMillis() - startTime1;
        
        // 第二次查詢（使用快取）
        long startTime2 = System.currentTimeMillis();
        StatisticsReport report2 = taskStatisticsUseCase.generateReport(request);
        long time2 = System.currentTimeMillis() - startTime2;
        
        System.out.printf("第一次查詢時間: %d ms (快取狀態: %s)\n", time1, report1.isFromCache() ? "命中" : "未命中");
        System.out.printf("第二次查詢時間: %d ms (快取狀態: %s)\n", time2, report2.isFromCache() ? "命中" : "未命中");
        System.out.printf("效能提升: %.1fx\n", (double) time1 / time2);
        
        // 顯示快取統計
        StatisticsCache.CacheStats cacheStats = taskStatisticsUseCase.getCacheStats();
        System.out.println("\n快取統計資訊:");
        System.out.printf("  %s\n", cacheStats.toString());
    }
    
    /**
     * 示範6：大資料集記憶體優化
     */
    private void demonstrateBigDataOptimization() {
        System.out.println("\n【示範6】大資料集記憶體優化");
        System.out.println("-".repeat(50));
        
        // 創建更多測試資料模擬大資料集
        generateLargeDataset(1500);
        
        // 啟用記憶體優化
        StatisticsRequest optimizedRequest = StatisticsRequest.builder()
            .lastMonths(2)
            .enableMemoryOptimization(true)
            .maxResults(1000)
            .reportTitle("大資料集分析（優化版）")
            .build();
        
        long startTime = System.currentTimeMillis();
        StatisticsReport optimizedReport = taskStatisticsUseCase.generateReport(optimizedRequest);
        long optimizedTime = System.currentTimeMillis() - startTime;
        
        // 停用記憶體優化比較
        StatisticsRequest normalRequest = StatisticsRequest.builder()
            .lastMonths(2)
            .enableMemoryOptimization(false)
            .maxResults(1000)
            .useCache(false) // 避免快取影響
            .reportTitle("大資料集分析（一般版）")
            .build();
        
        long startTime2 = System.currentTimeMillis();
        StatisticsReport normalReport = taskStatisticsUseCase.generateReport(normalRequest);
        long normalTime = System.currentTimeMillis() - startTime2;
        
        System.out.printf("處理任務數量: %d\n", optimizedReport.getTotalTasks());
        System.out.printf("記憶體優化處理時間: %d ms\n", optimizedTime);
        System.out.printf("一般處理時間: %d ms\n", normalTime);
        
        if (normalTime > optimizedTime) {
            System.out.printf("效能提升: %.1fx\n", (double) normalTime / optimizedTime);
        } else {
            System.out.println("在此資料量下，優化效果不明顯");
        }
    }
    
    /**
     * 示範7：非同步處理
     */
    private void demonstrateAsyncProcessing() {
        System.out.println("\n【示範7】非同步處理");
        System.out.println("-".repeat(50));
        
        StatisticsRequest request = StatisticsRequest.builder()
            .lastMonths(1)
            .useCache(false) // 確保真實處理
            .reportTitle("非同步統計分析")
            .build();
        
        System.out.println("開始非同步統計分析...");
        
        CompletableFuture<StatisticsReport> future = taskStatisticsUseCase.generateReportAsync(request);
        
        // 模擬其他工作
        System.out.println("執行其他工作...");
        simulateOtherWork();
        
        // 等待結果
        future.thenAccept(report -> {
            System.out.println("非同步分析完成！");
            System.out.printf("報告標題: %s\n", report.getPeriodDescription());
            System.out.printf("總任務數: %d\n", report.getTotalTasks());
            System.out.printf("完成率: %.1f%%\n", report.getCompletionRate() * 100);
        }).join();
    }
    
    /**
     * 示範8：函數式程式設計特性
     */
    private void demonstrateFunctionalProgramming() {
        System.out.println("\n【示範8】函數式程式設計特性展示");
        System.out.println("-".repeat(50));
        
        System.out.println("以下功能展示了函數式程式設計的特性：");
        System.out.println("1. Stream API 進行資料轉換和聚合");
        System.out.println("2. 高階函數 (Higher-order functions)");
        System.out.println("3. Lambda 表達式和方法參考");
        System.out.println("4. 不可變物件設計");
        System.out.println("5. 函數組合 (Function composition)");
        
        // 展示複雜的 Stream 操作鏈
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(30)
            .statusFilter(Set.of(TaskStatus.COMPLETED, TaskStatus.IN_PROGRESS))
            .priorityFilter(Set.of(Priority.HIGH, Priority.URGENT))
            .build();
        
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        System.out.println("\n篩選條件:");
        System.out.println("  狀態: 已完成 + 進行中");
        System.out.println("  優先級: 高 + 緊急");
        System.out.printf("  結果: %d 個任務符合條件\n", report.getTotalTasks());
        
        // 展示圖表資料的函數式處理
        report.getChartDataList().stream()
            .filter(chart -> chart.getDataPointCount() > 0)
            .map(chart -> String.format("%s: %.0f", chart.getTitle(), chart.getTotalValue()))
            .forEach(summary -> System.out.println("  " + summary));
    }
    
    /**
     * 初始化測試資料
     */
    private void initializeTestData() {
        LocalDateTime now = LocalDateTime.now();
        
        // 創建各種狀態和優先級的任務
        TaskStatus[] statuses = TaskStatus.values();
        Priority[] priorities = Priority.values();
        
        for (int i = 0; i < 50; i++) {
            TaskStatus status = statuses[i % statuses.length];
            Priority priority = priorities[i % priorities.length];
            
            LocalDateTime createdAt = now.minusDays((long) (Math.random() * 30));
            LocalDateTime dueDate = createdAt.plusDays((long) (Math.random() * 14 + 1));
            
            Task task = Task.builder()
                .id(TaskId.of("task-" + (i + 1)))
                .title("測試任務 " + (i + 1))
                .description("這是第 " + (i + 1) + " 個測試任務")
                .priority(priority)
                .createdAt(createdAt)
                .dueDate(dueDate)
                .build();
            
            // 設定任務狀態（根據有效轉換路徑）
            if (status == TaskStatus.COMPLETED) {
                // PENDING -> IN_PROGRESS -> COMPLETED
                task.updateStatus(TaskStatus.IN_PROGRESS);
                task.updateStatus(TaskStatus.COMPLETED);
            } else if (status == TaskStatus.IN_PROGRESS) {
                task.updateStatus(TaskStatus.IN_PROGRESS);
            } else if (status == TaskStatus.CANCELLED) {
                task.updateStatus(TaskStatus.CANCELLED);
            }
            // PENDING, TODO 狀態不需要額外操作
            
            taskRepository.save(task);
        }
        
        System.out.println("初始化測試資料完成：50 個任務");
    }
    
    /**
     * 生成大資料集
     */
    private void generateLargeDataset(int count) {
        LocalDateTime now = LocalDateTime.now();
        TaskStatus[] statuses = TaskStatus.values();
        Priority[] priorities = Priority.values();
        
        for (int i = 0; i < count; i++) {
            TaskStatus status = statuses[i % statuses.length];
            Priority priority = priorities[i % priorities.length];
            
            LocalDateTime createdAt = now.minusDays((long) (Math.random() * 60));
            
            Task task = Task.builder()
                .id(TaskId.of("bulk-task-" + i))
                .title("大資料集任務 " + i)
                .description("批量生成的測試任務")
                .priority(priority)
                .createdAt(createdAt)
                .build();
            
            if (status == TaskStatus.COMPLETED) {
                // PENDING -> IN_PROGRESS -> COMPLETED
                task.updateStatus(TaskStatus.IN_PROGRESS);
                task.updateStatus(TaskStatus.COMPLETED);
            } else if (status == TaskStatus.IN_PROGRESS) {
                task.updateStatus(TaskStatus.IN_PROGRESS);
            } else if (status == TaskStatus.CANCELLED) {
                task.updateStatus(TaskStatus.CANCELLED);
            }
            // PENDING, TODO 狀態不需要額外操作
            
            taskRepository.save(task);
        }
        
        System.out.printf("生成大資料集: %d 個任務\n", count);
    }
    
    /**
     * 模擬其他工作
     */
    private void simulateOtherWork() {
        try {
            Thread.sleep(500); // 模擬工作500ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("其他工作完成");
    }
}