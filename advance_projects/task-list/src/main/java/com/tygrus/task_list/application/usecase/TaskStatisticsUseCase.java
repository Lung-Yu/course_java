package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.ChartData;
import com.tygrus.task_list.application.dto.StatisticsReport;
import com.tygrus.task_list.application.dto.StatisticsRequest;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.repository.TaskRepository;
import com.tygrus.task_list.infrastructure.cache.StatisticsCache;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UC-010: 任務統計分析 UseCase
 * 
 * 提供進階的任務統計分析功能，包含：
 * - Stream API 複雜統計計算
 * - 多維度分析（狀態、優先級、時間）
 * - 多種圖表格式支援
 * - 記憶體優化的大資料處理
 * - 可配置的統計維度
 * - 快取機制提升效能
 */
public class TaskStatisticsUseCase {
    
    private final TaskRepository taskRepository;
    private final StatisticsCache statisticsCache;
    
    // 日期格式化器
    private static final DateTimeFormatter DAILY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter WEEKLY_FORMAT = DateTimeFormatter.ofPattern("yyyy-'W'ww");
    private static final DateTimeFormatter MONTHLY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter QUARTERLY_FORMAT = DateTimeFormatter.ofPattern("yyyy-'Q'Q");
    
    public TaskStatisticsUseCase(TaskRepository taskRepository, StatisticsCache statisticsCache) {
        this.taskRepository = taskRepository;
        this.statisticsCache = statisticsCache;
    }
    
    /**
     * 生成統計報告
     */
    public StatisticsReport generateReport(StatisticsRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 檢查快取
        if (request.isUseCache()) {
            Optional<StatisticsReport> cached = statisticsCache.get(request.getCacheKey());
            if (cached.isPresent()) {
                return enhanceReportWithCacheInfo(cached.get(), startTime, true);
            }
        }
        
        // 獲取任務資料
        List<TaskDTO> tasks = fetchTasksInDateRange(request);
        
        // 使用記憶體優化處理大資料集
        Stream<TaskDTO> taskStream = request.isEnableMemoryOptimization() && tasks.size() > 1000
            ? tasks.parallelStream()
            : tasks.stream();
        
        // 建立統計報告
        StatisticsReport report = buildStatisticsReport(request, taskStream, tasks.size());
        
        // 快取結果
        if (request.isUseCache()) {
            statisticsCache.put(request.getCacheKey(), report);
        }
        
        return enhanceReportWithCacheInfo(report, startTime, false);
    }
    
    /**
     * 非同步生成統計報告
     */
    public CompletableFuture<StatisticsReport> generateReportAsync(StatisticsRequest request) {
        return CompletableFuture.supplyAsync(() -> generateReport(request));
    }
    
    /**
     * 獲取時間範圍內的任務
     */
    private List<TaskDTO> fetchTasksInDateRange(StatisticsRequest request) {
        List<Task> allTasks = taskRepository.findAll();
        System.out.println("DEBUG: Found " + allTasks.size() + " tasks from repository");
        
        List<TaskDTO> result = allTasks.stream()
            .filter(task -> {
                boolean notDeleted = !task.isDeleted() || request.isIncludeDeleted();
                System.out.println("DEBUG: Task " + task.getId() + " - deleted filter: " + notDeleted);
                return notDeleted;
            })
            .map(task -> {
                TaskDTO dto = TaskDTO.fromTask(task);
                System.out.println("DEBUG: Mapped task " + task.getId() + " to DTO, createdAt: " + dto.getCreatedAt());
                return dto;
            })
            .filter(task -> {
                boolean inRange = isTaskInDateRange(task, request);
                System.out.println("DEBUG: Task " + task.getId() + " - date range filter: " + inRange + 
                    " (created: " + task.getCreatedAt() + ", range: " + request.getStartDate() + " to " + request.getEndDate() + ")");
                return inRange;
            })
            .filter(task -> {
                boolean statusMatch = matchesStatusFilter(task, request);
                System.out.println("DEBUG: Task " + task.getId() + " - status filter: " + statusMatch);
                return statusMatch;
            })
            .filter(task -> {
                boolean priorityMatch = matchesPriorityFilter(task, request);
                System.out.println("DEBUG: Task " + task.getId() + " - priority filter: " + priorityMatch);
                return priorityMatch;
            })
            .limit(request.getMaxResults())
            .collect(Collectors.toList());
        
        System.out.println("DEBUG: Final filtered result: " + result.size() + " tasks");
        return result;
    }
    
    /**
     * 檢查任務是否在指定時間範圍內
     */
    private boolean isTaskInDateRange(TaskDTO task, StatisticsRequest request) {
        LocalDateTime taskDate = task.getCreatedAt();
        return !taskDate.isBefore(request.getStartDate()) && !taskDate.isAfter(request.getEndDate());
    }
    
    /**
     * 檢查任務是否符合狀態篩選
     */
    private boolean matchesStatusFilter(TaskDTO task, StatisticsRequest request) {
        return request.getStatusFilter().isEmpty() || request.getStatusFilter().contains(task.getStatus());
    }
    
    /**
     * 檢查任務是否符合優先級篩選
     */
    private boolean matchesPriorityFilter(TaskDTO task, StatisticsRequest request) {
        return request.getPriorityFilter().isEmpty() || request.getPriorityFilter().contains(task.getPriority());
    }
    
    /**
     * 建立統計報告
     */
    private StatisticsReport buildStatisticsReport(StatisticsRequest request, Stream<TaskDTO> taskStream, int totalCount) {
        // 收集基本統計資料（使用函數式程式設計）
        List<TaskDTO> taskList = taskStream.collect(Collectors.toList());
        
        var basicStats = calculateBasicStatistics(taskList);
        var dimensionStats = calculateDimensionStatistics(taskList, request.getDimensions());
        var chartDataList = generateChartData(dimensionStats, request.getChartPreference());
        
        return StatisticsReport.builder()
            .period(request.getStartDate(), request.getEndDate(), generatePeriodDescription(request))
            .totalTasks(totalCount)
            .completedTasks(basicStats.getOrDefault("completed", 0L))
            .pendingTasks(basicStats.getOrDefault("pending", 0L))
            .inProgressTasks(basicStats.getOrDefault("inProgress", 0L))
            .cancelledTasks(basicStats.getOrDefault("cancelled", 0L))
            .completionRate(calculateCompletionRate(basicStats))
            .tasksByStatus(dimensionStats.getOrDefault("status", Map.of()))
            .tasksByPriority(dimensionStats.getOrDefault("priority", Map.of()))
            .tasksByTimeGroup(dimensionStats.getOrDefault("time", Map.of()))
            .avgCompletionDays(calculateAverageCompletionDays(taskList))
            .overdueTasks(calculateOverdueTasks(taskList))
            .chartDataList(chartDataList)
            .build();
    }
    
    /**
     * 計算基本統計數據（使用 Stream API）
     */
    private Map<String, Long> calculateBasicStatistics(List<TaskDTO> tasks) {
        return tasks.stream()
            .collect(Collectors.groupingBy(
                task -> task.getStatus().name().toLowerCase(),
                Collectors.counting()
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                entry -> mapStatusKey(entry.getKey()),
                Map.Entry::getValue,
                (v1, v2) -> v1 + v2 // 合併重複鍵
            ));
    }
    
    /**
     * 映射狀態鍵
     */
    private String mapStatusKey(String statusKey) {
        return switch (statusKey) {
            case "todo", "pending" -> "pending";
            case "in_progress" -> "inProgress";
            case "completed" -> "completed";
            case "cancelled" -> "cancelled";
            default -> statusKey;
        };
    }
    
    /**
     * 計算多維度統計（函數式程式設計展示）
     */
    private Map<String, Map<String, Long>> calculateDimensionStatistics(
            List<TaskDTO> tasks, 
            Set<StatisticsRequest.Dimension> dimensions) {
        
        Map<String, Map<String, Long>> results = new HashMap<>();
        
        // 按狀態統計
        if (dimensions.contains(StatisticsRequest.Dimension.STATUS)) {
            results.put("status", groupByStatus(tasks));
        }
        
        // 按優先級統計
        if (dimensions.contains(StatisticsRequest.Dimension.PRIORITY)) {
            results.put("priority", groupByPriority(tasks));
        }
        
        // 時間維度統計
        Set<StatisticsRequest.Dimension> timeDimensions = Set.of(
            StatisticsRequest.Dimension.TIME_DAILY,
            StatisticsRequest.Dimension.TIME_WEEKLY,
            StatisticsRequest.Dimension.TIME_MONTHLY,
            StatisticsRequest.Dimension.TIME_QUARTERLY
        );
        
        Optional<StatisticsRequest.Dimension> timeDimension = dimensions.stream()
            .filter(timeDimensions::contains)
            .findFirst();
        
        if (timeDimension.isPresent()) {
            results.put("time", groupByTime(tasks, timeDimension.get()));
        }
        
        return results;
    }
    
    /**
     * 按狀態分組統計（Stream API 示範）
     */
    private Map<String, Long> groupByStatus(List<TaskDTO> tasks) {
        return tasks.stream()
            .collect(Collectors.groupingBy(
                task -> task.getStatus().getDisplayName(),
                LinkedHashMap::new, // 保持順序
                Collectors.counting()
            ));
    }
    
    /**
     * 按優先級分組統計（函數式程式設計示範）
     */
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
    
    /**
     * 按時間分組統計（複雜 Stream 操作示範）
     */
    private Map<String, Long> groupByTime(List<TaskDTO> tasks, StatisticsRequest.Dimension timeDimension) {
        Function<TaskDTO, String> groupingFunction = createTimeGroupingFunction(timeDimension);
        
        return tasks.stream()
            .collect(Collectors.groupingBy(
                groupingFunction,
                LinkedHashMap::new, // 保持時間順序
                Collectors.counting()
            ));
    }
    
    /**
     * 創建時間分組函數（高階函數示範）
     */
    private Function<TaskDTO, String> createTimeGroupingFunction(StatisticsRequest.Dimension timeDimension) {
        return switch (timeDimension) {
            case TIME_DAILY -> task -> task.getCreatedAt().format(DAILY_FORMAT);
            case TIME_WEEKLY -> task -> {
                LocalDateTime date = task.getCreatedAt();
                // 計算週數（從週一開始）
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
    
    /**
     * 計算完成率
     */
    private double calculateCompletionRate(Map<String, Long> basicStats) {
        long total = basicStats.values().stream().mapToLong(Long::longValue).sum();
        long completed = basicStats.getOrDefault("completed", 0L);
        return total > 0 ? (double) completed / total : 0.0;
    }
    
    /**
     * 計算平均完成天數（Stream API 進階用法）
     */
    private double calculateAverageCompletionDays(List<TaskDTO> tasks) {
        return tasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .filter(task -> task.getUpdatedAt() != null)
            .mapToDouble(task -> ChronoUnit.DAYS.between(task.getCreatedAt(), task.getUpdatedAt()))
            .average()
            .orElse(0.0);
    }
    
    /**
     * 計算逾期任務數量
     */
    private long calculateOverdueTasks(List<TaskDTO> tasks) {
        LocalDateTime now = LocalDateTime.now();
        
        return tasks.stream()
            .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
            .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
            .filter(task -> task.getDueDate() != null)
            .filter(task -> task.getDueDate().isBefore(now))
            .count();
    }
    
    /**
     * 生成圖表資料
     */
    private List<ChartData> generateChartData(
            Map<String, Map<String, Long>> dimensionStats,
            StatisticsRequest.ChartPreference chartPreference) {
        
        List<ChartData> chartDataList = new ArrayList<>();
        
        // 狀態分佈餅圖
        if (dimensionStats.containsKey("status")) {
            ChartData statusChart = createStatusChart(dimensionStats.get("status"), chartPreference);
            chartDataList.add(statusChart);
        }
        
        // 優先級分佈圖表
        if (dimensionStats.containsKey("priority")) {
            ChartData priorityChart = createPriorityChart(dimensionStats.get("priority"), chartPreference);
            chartDataList.add(priorityChart);
        }
        
        // 時間趨勢圖表
        if (dimensionStats.containsKey("time")) {
            ChartData timeChart = createTimeChart(dimensionStats.get("time"));
            chartDataList.add(timeChart);
        }
        
        return chartDataList;
    }
    
    /**
     * 創建狀態分佈圖表
     */
    private ChartData createStatusChart(Map<String, Long> statusData, StatisticsRequest.ChartPreference preference) {
        List<ChartData.DataPoint> dataPoints = statusData.entrySet().stream()
            .map(entry -> new ChartData.DataPoint(entry.getKey(), entry.getValue().doubleValue()))
            .collect(Collectors.toList());
        
        ChartData.ChartType chartType = determineChartType(preference, "status");
        
        return ChartData.builder()
            .id("status-distribution")
            .title("任務狀態分佈")
            .subtitle("各狀態任務數量統計")
            .type(chartType)
            .dataPoints(dataPoints)
            .build();
    }
    
    /**
     * 創建優先級分佈圖表
     */
    private ChartData createPriorityChart(Map<String, Long> priorityData, StatisticsRequest.ChartPreference preference) {
        List<ChartData.DataPoint> dataPoints = priorityData.entrySet().stream()
            .map(entry -> new ChartData.DataPoint(entry.getKey(), entry.getValue().doubleValue()))
            .collect(Collectors.toList());
        
        ChartData.ChartType chartType = determineChartType(preference, "priority");
        
        return ChartData.builder()
            .id("priority-distribution")
            .title("任務優先級分佈")
            .subtitle("各優先級任務數量統計")
            .type(chartType)
            .dataPoints(dataPoints)
            .yAxisLabel("任務數量")
            .build();
    }
    
    /**
     * 創建時間趨勢圖表
     */
    private ChartData createTimeChart(Map<String, Long> timeData) {
        List<ChartData.DataPoint> dataPoints = timeData.entrySet().stream()
            .map(entry -> new ChartData.DataPoint(entry.getKey(), entry.getValue().doubleValue()))
            .collect(Collectors.toList());
        
        return ChartData.builder()
            .id("time-trend")
            .title("任務創建時間趨勢")
            .subtitle("任務數量隨時間變化")
            .type(ChartData.ChartType.LINE)
            .dataPoints(dataPoints)
            .xAxisLabel("時間")
            .yAxisLabel("任務數量")
            .build();
    }
    
    /**
     * 根據偏好決定圖表類型
     */
    private ChartData.ChartType determineChartType(StatisticsRequest.ChartPreference preference, String dataType) {
        return switch (preference) {
            case PIE_CHARTS -> ChartData.ChartType.PIE;
            case BAR_CHARTS -> ChartData.ChartType.BAR;
            case LINE_CHARTS -> ChartData.ChartType.LINE;
            case MIXED -> "status".equals(dataType) ? ChartData.ChartType.PIE : ChartData.ChartType.BAR;
            case AUTO -> "priority".equals(dataType) ? ChartData.ChartType.BAR : ChartData.ChartType.PIE;
        };
    }
    
    /**
     * 生成時間段描述
     */
    private String generatePeriodDescription(StatisticsRequest request) {
        long daysBetween = ChronoUnit.DAYS.between(
            request.getStartDate().toLocalDate(), 
            request.getEndDate().toLocalDate()
        );
        
        if (daysBetween <= 7) {
            return "最近一週";
        } else if (daysBetween <= 31) {
            return "最近一個月";
        } else if (daysBetween <= 93) {
            return "最近三個月";
        } else {
            return String.format("%s 至 %s", 
                request.getStartDate().toLocalDate().format(DAILY_FORMAT),
                request.getEndDate().toLocalDate().format(DAILY_FORMAT)
            );
        }
    }
    
    /**
     * 增強報告資訊（加入快取和效能資料）
     */
    private StatisticsReport enhanceReportWithCacheInfo(StatisticsReport original, long startTime, boolean fromCache) {
        long processingTime = System.currentTimeMillis() - startTime;
        
        return StatisticsReport.builder()
            .generatedAt(original.getGeneratedAt())
            .period(original.getPeriodStart(), original.getPeriodEnd(), original.getPeriodDescription())
            .totalTasks(original.getTotalTasks())
            .completedTasks(original.getCompletedTasks())
            .pendingTasks(original.getPendingTasks())
            .inProgressTasks(original.getInProgressTasks())
            .cancelledTasks(original.getCancelledTasks())
            .completionRate(original.getCompletionRate())
            .tasksByStatus(original.getTasksByStatus())
            .tasksByPriority(original.getTasksByPriority())
            .tasksByTimeGroup(original.getTasksByTimeGroup())
            .avgCompletionDays(original.getAvgCompletionDays())
            .overdueTasks(original.getOverdueTasks())
            .chartDataList(original.getChartDataList())
            .processingTime(processingTime)
            .fromCache(fromCache)
            .build();
    }
    
    /**
     * 獲取快取統計資訊
     */
    public StatisticsCache.CacheStats getCacheStats() {
        return statisticsCache.getStats();
    }
    
    /**
     * 清理過期快取
     */
    public int cleanupExpiredCache() {
        return statisticsCache.cleanupExpired();
    }
}