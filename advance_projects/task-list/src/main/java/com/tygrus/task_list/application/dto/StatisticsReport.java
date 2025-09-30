package com.tygrus.task_list.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 統計報告 DTO
 * 
 * 用於封裝任務統計分析結果，支援多維度統計資料
 */
public class StatisticsReport {
    
    private final LocalDateTime generatedAt;
    private final LocalDateTime periodStart;
    private final LocalDateTime periodEnd;
    private final String periodDescription;
    
    // 基本統計
    private final long totalTasks;
    private final long completedTasks;
    private final long pendingTasks;
    private final long inProgressTasks;
    private final long cancelledTasks;
    private final double completionRate;
    
    // 按狀態分組統計
    private final Map<String, Long> tasksByStatus;
    
    // 按優先級分組統計
    private final Map<String, Long> tasksByPriority;
    
    // 時間分析
    private final Map<String, Long> tasksByTimeGroup;
    private final double avgCompletionDays;
    private final long overdueTasks;
    
    // 圖表資料
    private final List<ChartData> chartDataList;
    
    // 效能統計
    private final long processingTimeMs;
    private final boolean fromCache;
    
    private StatisticsReport(Builder builder) {
        this.generatedAt = builder.generatedAt;
        this.periodStart = builder.periodStart;
        this.periodEnd = builder.periodEnd;
        this.periodDescription = builder.periodDescription;
        this.totalTasks = builder.totalTasks;
        this.completedTasks = builder.completedTasks;
        this.pendingTasks = builder.pendingTasks;
        this.inProgressTasks = builder.inProgressTasks;
        this.cancelledTasks = builder.cancelledTasks;
        this.completionRate = builder.completionRate;
        this.tasksByStatus = Map.copyOf(builder.tasksByStatus);
        this.tasksByPriority = Map.copyOf(builder.tasksByPriority);
        this.tasksByTimeGroup = Map.copyOf(builder.tasksByTimeGroup);
        this.avgCompletionDays = builder.avgCompletionDays;
        this.overdueTasks = builder.overdueTasks;
        this.chartDataList = List.copyOf(builder.chartDataList);
        this.processingTimeMs = builder.processingTimeMs;
        this.fromCache = builder.fromCache;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private LocalDateTime generatedAt = LocalDateTime.now();
        private LocalDateTime periodStart;
        private LocalDateTime periodEnd;
        private String periodDescription;
        private long totalTasks;
        private long completedTasks;
        private long pendingTasks;
        private long inProgressTasks;
        private long cancelledTasks;
        private double completionRate;
        private Map<String, Long> tasksByStatus = Map.of();
        private Map<String, Long> tasksByPriority = Map.of();
        private Map<String, Long> tasksByTimeGroup = Map.of();
        private double avgCompletionDays;
        private long overdueTasks;
        private List<ChartData> chartDataList = List.of();
        private long processingTimeMs;
        private boolean fromCache;
        
        public Builder generatedAt(LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }
        
        public Builder period(LocalDateTime start, LocalDateTime end, String description) {
            this.periodStart = start;
            this.periodEnd = end;
            this.periodDescription = description;
            return this;
        }
        
        public Builder totalTasks(long totalTasks) {
            this.totalTasks = totalTasks;
            return this;
        }
        
        public Builder completedTasks(long completedTasks) {
            this.completedTasks = completedTasks;
            return this;
        }
        
        public Builder pendingTasks(long pendingTasks) {
            this.pendingTasks = pendingTasks;
            return this;
        }
        
        public Builder inProgressTasks(long inProgressTasks) {
            this.inProgressTasks = inProgressTasks;
            return this;
        }
        
        public Builder cancelledTasks(long cancelledTasks) {
            this.cancelledTasks = cancelledTasks;
            return this;
        }
        
        public Builder completionRate(double completionRate) {
            this.completionRate = completionRate;
            return this;
        }
        
        public Builder tasksByStatus(Map<String, Long> tasksByStatus) {
            this.tasksByStatus = tasksByStatus;
            return this;
        }
        
        public Builder tasksByPriority(Map<String, Long> tasksByPriority) {
            this.tasksByPriority = tasksByPriority;
            return this;
        }
        
        public Builder tasksByTimeGroup(Map<String, Long> tasksByTimeGroup) {
            this.tasksByTimeGroup = tasksByTimeGroup;
            return this;
        }
        
        public Builder avgCompletionDays(double avgCompletionDays) {
            this.avgCompletionDays = avgCompletionDays;
            return this;
        }
        
        public Builder overdueTasks(long overdueTasks) {
            this.overdueTasks = overdueTasks;
            return this;
        }
        
        public Builder chartDataList(List<ChartData> chartDataList) {
            this.chartDataList = chartDataList;
            return this;
        }
        
        public Builder processingTime(long processingTimeMs) {
            this.processingTimeMs = processingTimeMs;
            return this;
        }
        
        public Builder fromCache(boolean fromCache) {
            this.fromCache = fromCache;
            return this;
        }
        
        public StatisticsReport build() {
            return new StatisticsReport(this);
        }
    }
    
    // Getters
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public LocalDateTime getPeriodStart() { return periodStart; }
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public String getPeriodDescription() { return periodDescription; }
    public long getTotalTasks() { return totalTasks; }
    public long getCompletedTasks() { return completedTasks; }
    public long getPendingTasks() { return pendingTasks; }
    public long getInProgressTasks() { return inProgressTasks; }
    public long getCancelledTasks() { return cancelledTasks; }
    public double getCompletionRate() { return completionRate; }
    public Map<String, Long> getTasksByStatus() { return tasksByStatus; }
    public Map<String, Long> getTasksByPriority() { return tasksByPriority; }
    public Map<String, Long> getTasksByTimeGroup() { return tasksByTimeGroup; }
    public double getAvgCompletionDays() { return avgCompletionDays; }
    public long getOverdueTasks() { return overdueTasks; }
    public List<ChartData> getChartDataList() { return chartDataList; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    public boolean isFromCache() { return fromCache; }
    
    // 便捷方法用於前端統計展示
    public double getInProgressRate() {
        return totalTasks > 0 ? (double) inProgressTasks / totalTasks * 100 : 0;
    }
    
    public double getPendingRate() {
        return totalTasks > 0 ? (double) pendingTasks / totalTasks * 100 : 0;
    }
    
    public long getHighPriorityTasks() {
        return tasksByPriority.getOrDefault("HIGH", 0L);
    }
    
    public long getMediumPriorityTasks() {
        return tasksByPriority.getOrDefault("MEDIUM", 0L);
    }
    
    public long getLowPriorityTasks() {
        return tasksByPriority.getOrDefault("LOW", 0L);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StatisticsReport that = (StatisticsReport) obj;
        return Objects.equals(generatedAt, that.generatedAt) &&
               Objects.equals(periodStart, that.periodStart) &&
               Objects.equals(periodEnd, that.periodEnd);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(generatedAt, periodStart, periodEnd);
    }
    
    @Override
    public String toString() {
        return String.format(
            "StatisticsReport{period='%s', totalTasks=%d, completionRate=%.2f%%, fromCache=%s}",
            periodDescription, totalTasks, completionRate * 100, fromCache
        );
    }
}