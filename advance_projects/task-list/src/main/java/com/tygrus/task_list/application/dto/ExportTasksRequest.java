package com.tygrus.task_list.application.dto;

import com.tygrus.task_list.domain.model.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任務匯出請求 DTO
 * 
 * 封裝匯出任務的篩選條件和參數
 */
public class ExportTasksRequest {
    
    private ExportFormat format = ExportFormat.CSV;
    private String fileName;
    private List<TaskStatus> statusFilter;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
    private String titleFilter;
    private boolean includeDeleted = false;
    private int maxRecords = 10000; // 預設最大匯出 10,000 筆
    
    // 建構函數
    public ExportTasksRequest() {}
    
    public ExportTasksRequest(ExportFormat format, String fileName) {
        this.format = format;
        this.fileName = fileName;
    }
    
    // Getter 和 Setter
    public ExportFormat getFormat() {
        return format;
    }
    
    public void setFormat(ExportFormat format) {
        this.format = format;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public List<TaskStatus> getStatusFilter() {
        return statusFilter;
    }
    
    public void setStatusFilter(List<TaskStatus> statusFilter) {
        this.statusFilter = statusFilter;
    }
    
    public LocalDateTime getCreatedFrom() {
        return createdFrom;
    }
    
    public void setCreatedFrom(LocalDateTime createdFrom) {
        this.createdFrom = createdFrom;
    }
    
    public LocalDateTime getCreatedTo() {
        return createdTo;
    }
    
    public void setCreatedTo(LocalDateTime createdTo) {
        this.createdTo = createdTo;
    }
    
    public LocalDateTime getDueDateFrom() {
        return dueDateFrom;
    }
    
    public void setDueDateFrom(LocalDateTime dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
    }
    
    public LocalDateTime getDueDateTo() {
        return dueDateTo;
    }
    
    public void setDueDateTo(LocalDateTime dueDateTo) {
        this.dueDateTo = dueDateTo;
    }
    
    public String getTitleFilter() {
        return titleFilter;
    }
    
    public void setTitleFilter(String titleFilter) {
        this.titleFilter = titleFilter;
    }
    
    public boolean isIncludeDeleted() {
        return includeDeleted;
    }
    
    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }
    
    public int getMaxRecords() {
        return maxRecords;
    }
    
    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }
    
    @Override
    public String toString() {
        return "ExportTasksRequest{" +
                "format=" + format +
                ", fileName='" + fileName + '\'' +
                ", statusFilter=" + statusFilter +
                ", createdFrom=" + createdFrom +
                ", createdTo=" + createdTo +
                ", dueDateFrom=" + dueDateFrom +
                ", dueDateTo=" + dueDateTo +
                ", titleFilter='" + titleFilter + '\'' +
                ", includeDeleted=" + includeDeleted +
                ", maxRecords=" + maxRecords +
                '}';
    }
}