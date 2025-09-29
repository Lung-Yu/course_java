package com.tygrus.task_list.application.dto;

import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 統計查詢請求 DTO
 * 
 * 用於配置統計分析的查詢條件和維度
 */
public class StatisticsRequest {
    
    /**
     * 統計維度枚舉
     */
    public enum Dimension {
        STATUS("status", "按狀態統計"),
        PRIORITY("priority", "按優先級統計"),
        TIME_DAILY("time_daily", "按日統計"),
        TIME_WEEKLY("time_weekly", "按週統計"),
        TIME_MONTHLY("time_monthly", "按月統計"),
        TIME_QUARTERLY("time_quarterly", "按季統計"),
        COMPLETION_TIME("completion_time", "完成時間分析"),
        OVERDUE_ANALYSIS("overdue_analysis", "逾期分析");
        
        private final String code;
        private final String displayName;
        
        Dimension(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }
        
        public String getCode() { return code; }
        public String getDisplayName() { return displayName; }
    }
    
    /**
     * 圖表類型偏好
     */
    public enum ChartPreference {
        AUTO("auto", "自動選擇"),
        PIE_CHARTS("pie", "餅圖優先"),
        BAR_CHARTS("bar", "柱狀圖優先"),
        LINE_CHARTS("line", "折線圖優先"),
        MIXED("mixed", "混合圖表");
        
        private final String code;
        private final String displayName;
        
        ChartPreference(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }
        
        public String getCode() { return code; }
        public String getDisplayName() { return displayName; }
    }
    
    // 時間範圍
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    
    // 篩選條件
    private final Set<TaskStatus> statusFilter;
    private final Set<Priority> priorityFilter;
    private final boolean includeDeleted;
    
    // 統計維度
    private final Set<Dimension> dimensions;
    
    // 圖表偏好
    private final ChartPreference chartPreference;
    private final boolean generateCharts;
    
    // 效能設定
    private final boolean useCache;
    private final int maxResults;
    private final boolean enableMemoryOptimization;
    
    // 客製化設定
    private final String reportTitle;
    private final String description;
    
    private StatisticsRequest(Builder builder) {
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.statusFilter = Set.copyOf(builder.statusFilter);
        this.priorityFilter = Set.copyOf(builder.priorityFilter);
        this.includeDeleted = builder.includeDeleted;
        this.dimensions = Set.copyOf(builder.dimensions);
        this.chartPreference = builder.chartPreference;
        this.generateCharts = builder.generateCharts;
        this.useCache = builder.useCache;
        this.maxResults = builder.maxResults;
        this.enableMemoryOptimization = builder.enableMemoryOptimization;
        this.reportTitle = builder.reportTitle;
        this.description = builder.description;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Set<TaskStatus> statusFilter = Set.of();
        private Set<Priority> priorityFilter = Set.of();
        private boolean includeDeleted = false;
        private Set<Dimension> dimensions = Set.of(Dimension.STATUS, Dimension.PRIORITY);
        private ChartPreference chartPreference = ChartPreference.AUTO;
        private boolean generateCharts = true;
        private boolean useCache = true;
        private int maxResults = 10000;
        private boolean enableMemoryOptimization = true;
        private String reportTitle;
        private String description;
        
        public Builder timeRange(LocalDateTime start, LocalDateTime end) {
            this.startDate = start;
            this.endDate = end;
            return this;
        }
        
        public Builder lastDays(int days) {
            this.endDate = LocalDateTime.now();
            this.startDate = this.endDate.minusDays(days);
            return this;
        }
        
        public Builder lastWeeks(int weeks) {
            this.endDate = LocalDateTime.now();
            this.startDate = this.endDate.minusWeeks(weeks);
            return this;
        }
        
        public Builder lastMonths(int months) {
            this.endDate = LocalDateTime.now();
            this.startDate = this.endDate.minusMonths(months);
            return this;
        }
        
        public Builder statusFilter(Set<TaskStatus> statusFilter) {
            this.statusFilter = statusFilter;
            return this;
        }
        
        public Builder priorityFilter(Set<Priority> priorityFilter) {
            this.priorityFilter = priorityFilter;
            return this;
        }
        
        public Builder includeDeleted(boolean includeDeleted) {
            this.includeDeleted = includeDeleted;
            return this;
        }
        
        public Builder dimensions(Set<Dimension> dimensions) {
            this.dimensions = dimensions;
            return this;
        }
        
        public Builder addDimension(Dimension dimension) {
            java.util.Set<Dimension> newDimensions = new java.util.HashSet<>(this.dimensions);
            newDimensions.add(dimension);
            this.dimensions = newDimensions;
            return this;
        }
        
        public Builder chartPreference(ChartPreference chartPreference) {
            this.chartPreference = chartPreference;
            return this;
        }
        
        public Builder generateCharts(boolean generateCharts) {
            this.generateCharts = generateCharts;
            return this;
        }
        
        public Builder useCache(boolean useCache) {
            this.useCache = useCache;
            return this;
        }
        
        public Builder maxResults(int maxResults) {
            this.maxResults = maxResults;
            return this;
        }
        
        public Builder enableMemoryOptimization(boolean enableMemoryOptimization) {
            this.enableMemoryOptimization = enableMemoryOptimization;
            return this;
        }
        
        public Builder reportTitle(String reportTitle) {
            this.reportTitle = reportTitle;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public StatisticsRequest build() {
            // 預設時間範圍為最近30天
            if (startDate == null || endDate == null) {
                endDate = LocalDateTime.now();
                startDate = endDate.minusDays(30);
            }
            
            // 驗證時間範圍
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date must be before end date");
            }
            
            // 預設報告標題
            if (reportTitle == null) {
                reportTitle = "任務統計報告";
            }
            
            return new StatisticsRequest(this);
        }
    }
    
    /**
     * 建立快速預設設定
     */
    public static StatisticsRequest defaultWeekly() {
        return builder()
            .lastWeeks(1)
            .reportTitle("週報統計")
            .build();
    }
    
    public static StatisticsRequest defaultMonthly() {
        return builder()
            .lastMonths(1)
            .reportTitle("月報統計")
            .build();
    }
    
    public static StatisticsRequest defaultQuarterly() {
        return builder()
            .lastMonths(3)
            .reportTitle("季報統計")
            .build();
    }
    
    // Getters
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Set<TaskStatus> getStatusFilter() { return statusFilter; }
    public Set<Priority> getPriorityFilter() { return priorityFilter; }
    public boolean isIncludeDeleted() { return includeDeleted; }
    public Set<Dimension> getDimensions() { return dimensions; }
    public ChartPreference getChartPreference() { return chartPreference; }
    public boolean isGenerateCharts() { return generateCharts; }
    public boolean isUseCache() { return useCache; }
    public int getMaxResults() { return maxResults; }
    public boolean isEnableMemoryOptimization() { return enableMemoryOptimization; }
    public String getReportTitle() { return reportTitle; }
    public String getDescription() { return description; }
    
    /**
     * 生成快取鍵
     */
    public String getCacheKey() {
        return String.format("statistics_%s_%s_%s_%s_%s",
            startDate.toLocalDate(),
            endDate.toLocalDate(),
            statusFilter.hashCode(),
            priorityFilter.hashCode(),
            dimensions.hashCode()
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StatisticsRequest that = (StatisticsRequest) obj;
        return Objects.equals(startDate, that.startDate) &&
               Objects.equals(endDate, that.endDate) &&
               Objects.equals(statusFilter, that.statusFilter) &&
               Objects.equals(priorityFilter, that.priorityFilter) &&
               Objects.equals(dimensions, that.dimensions);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, statusFilter, priorityFilter, dimensions);
    }
    
    @Override
    public String toString() {
        return String.format(
            "StatisticsRequest{period=%s to %s, dimensions=%s, useCache=%s}",
            startDate.toLocalDate(), endDate.toLocalDate(), dimensions, useCache
        );
    }
}