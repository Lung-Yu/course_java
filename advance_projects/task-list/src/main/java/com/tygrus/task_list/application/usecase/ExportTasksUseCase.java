package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.ExportFormat;
import com.tygrus.task_list.application.dto.ExportResult;
import com.tygrus.task_list.application.dto.ExportTasksRequest;
import com.tygrus.task_list.application.service.TaskExporter;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 匯出任務 Use Case
 * 
 * 實現 UC-006: 任務匯出功能的業務邏輯
 * 支援多種格式匯出 (CSV, JSON, Excel) 和條件篩選
 */
@Service
@Transactional(readOnly = true)
public class ExportTasksUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ExportTasksUseCase.class);
    private static final int MAX_EXPORT_RECORDS = 50000; // 最大匯出記錄數
    
    private final TaskRepository taskRepository;
    private final List<TaskExporter> taskExporters;
    
    public ExportTasksUseCase(TaskRepository taskRepository, List<TaskExporter> taskExporters) {
        this.taskRepository = Objects.requireNonNull(taskRepository, "TaskRepository cannot be null");
        this.taskExporters = Objects.requireNonNull(taskExporters, "TaskExporters cannot be null");
        
        if (taskExporters.isEmpty()) {
            throw new IllegalArgumentException("At least one TaskExporter must be provided");
        }
        
        logger.info("ExportTasksUseCase initialized with {} exporters", taskExporters.size());
    }
    
    /**
     * 執行任務匯出業務邏輯
     * 
     * @param request 匯出請求
     * @return 匯出結果
     */
    public ExportResult exportTasks(ExportTasksRequest request) {
        Objects.requireNonNull(request, "Export request cannot be null");
        
        try {
            logger.info("Starting task export with request: {}", request);
            
            // 驗證請求參數
            validateRequest(request);
            
            // 查詢符合條件的任務
            List<Task> tasks = queryTasks(request);
            
            // 檢查匯出數量限制
            if (tasks.size() > request.getMaxRecords()) {
                logger.warn("Export result truncated from {} to {} records", tasks.size(), request.getMaxRecords());
                tasks = tasks.subList(0, request.getMaxRecords());
            }
            
            // 選擇適當的匯出器
            TaskExporter exporter = findExporter(request.getFormat());
            if (exporter == null) {
                String errorMsg = "No exporter found for format: " + request.getFormat();
                logger.error(errorMsg);
                return new ExportResult(errorMsg);
            }
            
            // 執行匯出
            String fileName = generateFileName(request);
            byte[] fileContent = exporter.export(tasks, fileName);
            
            ExportResult result = new ExportResult(
                fileName,
                fileContent,
                exporter.getMimeType(),
                tasks.size(),
                LocalDateTime.now()
            );
            
            logger.info("Export completed successfully: {}", result);
            return result;
            
        } catch (Exception e) {
            String errorMsg = "Failed to export tasks: " + e.getMessage();
            logger.error(errorMsg, e);
            return new ExportResult(errorMsg);
        }
    }
    
    /**
     * 驗證匯出請求參數
     * 
     * @param request 匯出請求
     */
    private void validateRequest(ExportTasksRequest request) {
        if (request.getFormat() == null) {
            throw new IllegalArgumentException("Export format cannot be null");
        }
        
        if (request.getMaxRecords() <= 0) {
            throw new IllegalArgumentException("Max records must be positive");
        }
        
        if (request.getMaxRecords() > MAX_EXPORT_RECORDS) {
            throw new IllegalArgumentException(
                "Max records cannot exceed " + MAX_EXPORT_RECORDS + ", requested: " + request.getMaxRecords()
            );
        }
        
        // 驗證日期範圍
        if (request.getCreatedFrom() != null && request.getCreatedTo() != null
                && request.getCreatedFrom().isAfter(request.getCreatedTo())) {
            throw new IllegalArgumentException("Created from date cannot be after created to date");
        }
        
        if (request.getDueDateFrom() != null && request.getDueDateTo() != null
                && request.getDueDateFrom().isAfter(request.getDueDateTo())) {
            throw new IllegalArgumentException("Due date from cannot be after due date to");
        }
    }
    
    /**
     * 根據請求條件查詢任務
     * 
     * @param request 匯出請求
     * @return 符合條件的任務清單
     */
    private List<Task> queryTasks(ExportTasksRequest request) {
        // 先取得所有任務
        List<Task> allTasks;
        if (request.isIncludeDeleted()) {
            // 包含已刪除的任務 - 需要實作專門的方法
            allTasks = taskRepository.findAll();
        } else {
            allTasks = taskRepository.findAll();
        }
        
        // 應用篩選條件
        return allTasks.stream()
            .filter(task -> matchesStatusFilter(task, request))
            .filter(task -> matchesDateFilter(task, request))
            .filter(task -> matchesTitleFilter(task, request))
            .limit(request.getMaxRecords() + 1000) // 多查一些，以便後續截斷
            .collect(Collectors.toList());
    }
    
    /**
     * 檢查任務是否符合狀態篩選條件
     */
    private boolean matchesStatusFilter(Task task, ExportTasksRequest request) {
        return request.getStatusFilter() == null || 
               request.getStatusFilter().isEmpty() || 
               request.getStatusFilter().contains(task.getStatus());
    }
    
    /**
     * 檢查任務是否符合日期篩選條件
     */
    private boolean matchesDateFilter(Task task, ExportTasksRequest request) {
        // 檢查創建日期篩選
        if (request.getCreatedFrom() != null && task.getCreatedAt().isBefore(request.getCreatedFrom())) {
            return false;
        }
        if (request.getCreatedTo() != null && task.getCreatedAt().isAfter(request.getCreatedTo())) {
            return false;
        }
        
        // 檢查到期日期篩選
        if (request.getDueDateFrom() != null) {
            if (task.getDueDate() == null || task.getDueDate().isBefore(request.getDueDateFrom())) {
                return false;
            }
        }
        if (request.getDueDateTo() != null) {
            if (task.getDueDate() == null || task.getDueDate().isAfter(request.getDueDateTo())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 檢查任務是否符合標題篩選條件
     */
    private boolean matchesTitleFilter(Task task, ExportTasksRequest request) {
        if (request.getTitleFilter() == null || request.getTitleFilter().trim().isEmpty()) {
            return true;
        }
        
        String filter = request.getTitleFilter().toLowerCase();
        return task.getTitle().toLowerCase().contains(filter);
    }
    
    /**
     * 尋找支援指定格式的匯出器
     * 
     * @param format 匯出格式
     * @return 匯出器，如果沒有找到則回傳 null
     */
    private TaskExporter findExporter(ExportFormat format) {
        return taskExporters.stream()
            .filter(exporter -> exporter.supports(format))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 產生匯出檔案名稱
     * 
     * @param request 匯出請求
     * @return 檔案名稱
     */
    private String generateFileName(ExportTasksRequest request) {
        if (request.getFileName() != null && !request.getFileName().trim().isEmpty()) {
            String fileName = request.getFileName().trim();
            // 確保檔案名稱有正確的副檔名
            String extension = "." + request.getFormat().getExtension();
            if (!fileName.toLowerCase().endsWith(extension.toLowerCase())) {
                fileName += extension;
            }
            return fileName;
        }
        
        // 產生預設檔案名稱
        String timestamp = LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
        return "tasks_export_" + timestamp + "." + request.getFormat().getExtension();
    }
    
    /**
     * 取得支援的匯出格式清單
     * 
     * @return 支援的格式清單
     */
    public List<ExportFormat> getSupportedFormats() {
        return taskExporters.stream()
            .flatMap(exporter -> 
                java.util.Arrays.stream(ExportFormat.values())
                    .filter(exporter::supports)
            )
            .distinct()
            .collect(Collectors.toList());
    }
}