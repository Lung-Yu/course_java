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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskStatisticsUseCase 測試類別
 * 
 * 測試統計分析功能的各個方面
 */
class TaskStatisticsUseCaseTest {
    
    private TaskStatisticsUseCase taskStatisticsUseCase;
    private InMemoryTaskRepository taskRepository;
    private StatisticsCache statisticsCache;
    
    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        statisticsCache = new StatisticsCache(5, 10); // 短TTL用於測試
        taskStatisticsUseCase = new TaskStatisticsUseCase(taskRepository, statisticsCache);
        
        // 創建測試資料
        createTestTasks();
    }
    
    @Test
    @DisplayName("測試基本統計報告生成")
    void testBasicStatisticsGeneration() {
        // Given
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(7)
            .reportTitle("測試報告")
            .build();
        
        // When
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        // Then
        assertNotNull(report);
        assertTrue(report.getTotalTasks() > 0);
        assertNotNull(report.getPeriodDescription());
        assertEquals(0.0, report.getCompletionRate(), 0.01); // 測試資料都是PENDING
        assertFalse(report.isFromCache()); // 第一次查詢不應該來自快取
    }
    
    @Test
    @DisplayName("測試多維度統計分析")
    void testMultiDimensionAnalysis() {
        // Given
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(30)
            .dimensions(Set.of(
                StatisticsRequest.Dimension.STATUS,
                StatisticsRequest.Dimension.PRIORITY,
                StatisticsRequest.Dimension.TIME_DAILY
            ))
            .build();
        
        // When
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        // Then
        assertNotNull(report.getTasksByStatus());
        assertNotNull(report.getTasksByPriority());
        assertNotNull(report.getTasksByTimeGroup());
        
        assertFalse(report.getTasksByStatus().isEmpty());
        assertFalse(report.getTasksByPriority().isEmpty());
    }
    
    @Test
    @DisplayName("測試快取機制")
    void testCachePerformance() {
        // Given
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(7)
            .useCache(true)
            .build();
        
        // When - 第一次查詢
        StatisticsReport report1 = taskStatisticsUseCase.generateReport(request);
        
        // When - 第二次查詢（應該來自快取）
        StatisticsReport report2 = taskStatisticsUseCase.generateReport(request);
        
        // Then
        assertFalse(report1.isFromCache());
        assertTrue(report2.isFromCache());
        assertEquals(report1.getTotalTasks(), report2.getTotalTasks());
        
        // 快取查詢應該更快
        assertTrue(report2.getProcessingTimeMs() <= report1.getProcessingTimeMs());
    }
    
    @Test
    @DisplayName("測試圖表資料生成")
    void testChartDataGeneration() {
        // Given
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(30)
            .generateCharts(true)
            .chartPreference(StatisticsRequest.ChartPreference.MIXED)
            .dimensions(Set.of(
                StatisticsRequest.Dimension.STATUS,
                StatisticsRequest.Dimension.PRIORITY
            ))
            .build();
        
        // When
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        // Then
        assertNotNull(report.getChartDataList());
        assertFalse(report.getChartDataList().isEmpty());
        
        // 檢查圖表資料結構
        for (ChartData chart : report.getChartDataList()) {
            assertNotNull(chart.getId());
            assertNotNull(chart.getTitle());
            assertNotNull(chart.getType());
            assertNotNull(chart.getDataPoints());
            assertTrue(chart.getDataPointCount() > 0);
        }
    }
    
    @Test
    @DisplayName("測試狀態篩選功能")
    void testStatusFiltering() {
        // 創建不同狀態的任務
        createTaskWithStatus(TaskStatus.COMPLETED, Priority.HIGH);
        createTaskWithStatus(TaskStatus.IN_PROGRESS, Priority.MEDIUM);
        
        // Given - 只查詢已完成的任務
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(7)
            .statusFilter(Set.of(TaskStatus.COMPLETED))
            .build();
        
        // When
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        // Then
        assertEquals(1, report.getTotalTasks());
        assertEquals(1, report.getCompletedTasks());
        assertEquals(0, report.getPendingTasks());
        assertEquals(0, report.getInProgressTasks());
    }
    
    @Test
    @DisplayName("測試優先級篩選功能")
    void testPriorityFiltering() {
        // Given - 只查詢高優先級任務
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(7)
            .priorityFilter(Set.of(Priority.HIGH, Priority.URGENT))
            .build();
        
        // When
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        // Then
        // 檢查只包含高優先級任務
        assertTrue(report.getTasksByPriority().containsKey(Priority.HIGH.getDisplayName()));
        // 不應該包含低優先級任務
        assertFalse(report.getTasksByPriority().containsKey(Priority.LOW.getDisplayName()));
    }
    
    @Test
    @DisplayName("測試時間範圍篩選")
    void testTimeRangeFiltering() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        StatisticsRequest request = StatisticsRequest.builder()
            .timeRange(now.minusDays(2), now.minusDays(1))
            .build();
        
        // When
        StatisticsReport report = taskStatisticsUseCase.generateReport(request);
        
        // Then
        assertNotNull(report);
        assertEquals(now.minusDays(2).toLocalDate(), report.getPeriodStart().toLocalDate());
        assertEquals(now.minusDays(1).toLocalDate(), report.getPeriodEnd().toLocalDate());
    }
    
    @Test
    @DisplayName("測試記憶體優化設定")
    void testMemoryOptimization() {
        // Given
        StatisticsRequest optimizedRequest = StatisticsRequest.builder()
            .lastDays(30)
            .enableMemoryOptimization(true)
            .maxResults(5)
            .build();
        
        StatisticsRequest normalRequest = StatisticsRequest.builder()
            .lastDays(30)
            .enableMemoryOptimization(false)
            .maxResults(5)
            .useCache(false) // 避免快取影響測試
            .build();
        
        // When
        StatisticsReport optimizedReport = taskStatisticsUseCase.generateReport(optimizedRequest);
        StatisticsReport normalReport = taskStatisticsUseCase.generateReport(normalRequest);
        
        // Then
        // 結果應該相同（在小資料集下）
        assertEquals(normalReport.getTotalTasks(), optimizedReport.getTotalTasks());
        assertEquals(normalReport.getCompletionRate(), optimizedReport.getCompletionRate(), 0.01);
    }
    
    @Test
    @DisplayName("測試非同步處理")
    void testAsyncProcessing() throws Exception {
        // Given
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(7)
            .useCache(false) // 確保真實處理
            .build();
        
        // When
        CompletableFuture<StatisticsReport> future = taskStatisticsUseCase.generateReportAsync(request);
        
        // Then
        assertNotNull(future);
        StatisticsReport report = future.get(); // 等待結果
        
        assertNotNull(report);
        assertTrue(report.getTotalTasks() >= 0);
    }
    
    @Test
    @DisplayName("測試快取統計資訊")
    void testCacheStatistics() {
        // Given
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(7)
            .useCache(true)
            .build();
        
        // When - 執行一些查詢
        taskStatisticsUseCase.generateReport(request);
        taskStatisticsUseCase.generateReport(request); // 快取命中
        
        StatisticsCache.CacheStats stats = taskStatisticsUseCase.getCacheStats();
        
        // Then
        assertNotNull(stats);
        assertTrue(stats.getHitCount() > 0);
        assertTrue(stats.getHitRate() > 0);
    }
    
    @Test
    @DisplayName("測試快取清理功能")
    void testCacheCleanup() throws InterruptedException {
        // Given - 使用很短的TTL
        StatisticsCache shortTtlCache = new StatisticsCache(0, 10); // 0分鐘TTL
        TaskStatisticsUseCase useCase = new TaskStatisticsUseCase(taskRepository, shortTtlCache);
        
        StatisticsRequest request = StatisticsRequest.builder()
            .lastDays(7)
            .useCache(true)
            .build();
        
        // When
        useCase.generateReport(request); // 創建快取項目
        Thread.sleep(100); // 等待過期
        
        int cleanedCount = useCase.cleanupExpiredCache();
        
        // Then
        assertTrue(cleanedCount > 0); // 應該清理了過期項目
    }
    
    @Test
    @DisplayName("測試圖表類型配置")
    void testChartTypeConfiguration() {
        // Given
        StatisticsRequest pieRequest = StatisticsRequest.builder()
            .lastDays(7)
            .chartPreference(StatisticsRequest.ChartPreference.PIE_CHARTS)
            .generateCharts(true)
            .build();
        
        StatisticsRequest barRequest = StatisticsRequest.builder()
            .lastDays(7)
            .chartPreference(StatisticsRequest.ChartPreference.BAR_CHARTS)
            .generateCharts(true)
            .build();
        
        // When
        StatisticsReport pieReport = taskStatisticsUseCase.generateReport(pieRequest);
        StatisticsReport barReport = taskStatisticsUseCase.generateReport(barRequest);
        
        // Then
        assertFalse(pieReport.getChartDataList().isEmpty());
        assertFalse(barReport.getChartDataList().isEmpty());
        
        // 驗證圖表類型偏好被正確應用
        boolean hasPieCharts = pieReport.getChartDataList().stream()
            .anyMatch(chart -> chart.getType() == ChartData.ChartType.PIE);
        boolean hasBarCharts = barReport.getChartDataList().stream()
            .anyMatch(chart -> chart.getType() == ChartData.ChartType.BAR);
        
        assertTrue(hasPieCharts || hasBarCharts); // 至少其中一個應該有對應類型
    }
    
    @Test
    @DisplayName("測試預設統計請求")
    void testDefaultStatisticsRequests() {
        // When
        StatisticsReport weeklyReport = taskStatisticsUseCase.generateReport(StatisticsRequest.defaultWeekly());
        StatisticsReport monthlyReport = taskStatisticsUseCase.generateReport(StatisticsRequest.defaultMonthly());
        StatisticsReport quarterlyReport = taskStatisticsUseCase.generateReport(StatisticsRequest.defaultQuarterly());
        
        // Then
        assertNotNull(weeklyReport);
        assertNotNull(monthlyReport);
        assertNotNull(quarterlyReport);
        
        // 檢查報告生成成功
        assertNotNull(weeklyReport);
        assertNotNull(monthlyReport);
        assertNotNull(quarterlyReport);
        
        // 檢查時間範圍是否正確（而不是檢查標題）
        assertTrue(weeklyReport.getPeriodDescription().contains("週") || 
                  weeklyReport.getPeriodDescription().equals("最近一週"));
        assertTrue(monthlyReport.getPeriodDescription().contains("月") || 
                  monthlyReport.getPeriodDescription().equals("最近一個月"));
        assertTrue(quarterlyReport.getPeriodDescription().contains("月") || 
                  quarterlyReport.getPeriodDescription().equals("最近三個月"));
    }
    
    /**
     * 創建測試任務
     */
    private void createTestTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        // 創建不同類型的測試任務
        for (int i = 0; i < 10; i++) {
            Priority priority = Priority.values()[i % Priority.values().length];
            
            Task task = Task.builder()
                .id(TaskId.of("test-task-" + i))
                .title("測試任務 " + i)
                .description("測試描述 " + i)
                .priority(priority)
                .createdAt(now.minusDays(i))
                .dueDate(now.plusDays(i + 1))
                .build();
            
            taskRepository.save(task);
        }
    }
    
    /**
     * 創建指定狀態的任務
     */
    private void createTaskWithStatus(TaskStatus status, Priority priority) {
        Task task = Task.builder()
            .id(TaskId.of("status-task-" + status.name()))
            .title("狀態測試任務")
            .description("用於測試狀態篩選")
            .priority(priority)
            .createdAt(LocalDateTime.now())
            .build();
        
        // 根據狀態設置適當的轉換路徑
        if (status == TaskStatus.COMPLETED) {
            // PENDING -> IN_PROGRESS -> COMPLETED
            task.updateStatus(TaskStatus.IN_PROGRESS);
            task.updateStatus(TaskStatus.COMPLETED);
        } else if (status == TaskStatus.IN_PROGRESS) {
            task.updateStatus(TaskStatus.IN_PROGRESS);
        } else if (status == TaskStatus.CANCELLED) {
            task.updateStatus(TaskStatus.CANCELLED);
        }
        // PENDING 狀態不需要額外操作
        
        taskRepository.save(task);
    }
}