package com.tygrus.task_list.application.service.impl;

import com.tygrus.task_list.application.dto.ExportFormat;
import com.tygrus.task_list.application.service.TaskExporter;
import com.tygrus.task_list.domain.model.Task;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CSV 格式任務匯出器
 * 
 * 實作將任務匯出為 CSV 格式的功能
 */
@Component
public class CsvTaskExporter implements TaskExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public boolean supports(ExportFormat format) {
        return ExportFormat.CSV.equals(format);
    }
    
    @Override
    public byte[] export(List<Task> tasks, String fileName) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {
            
            // 寫入 CSV 標題行
            writer.println("ID,Title,Description,Status,Priority,Due Date,Created At,Updated At");
            
            // 寫入任務資料
            for (Task task : tasks) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    escapeCsvField(task.getId().getValue()),
                    escapeCsvField(task.getTitle()),
                    escapeCsvField(task.getDescription()),
                    task.getStatus().name(),
                    task.getPriority() != null ? task.getPriority().name() : "",
                    task.getDueDate() != null ? task.getDueDate().format(DATE_FORMATTER) : "",
                    task.getCreatedAt().format(DATE_FORMATTER),
                    task.getUpdatedAt().format(DATE_FORMATTER)
                );
            }
            
            writer.flush();
            return baos.toByteArray();
        }
    }
    
    @Override
    public String getMimeType() {
        return ExportFormat.CSV.getMimeType();
    }
    
    /**
     * 轉義 CSV 欄位中的特殊字元
     * 
     * @param field 原始欄位值
     * @return 轉義後的欄位值
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // 將雙引號轉義為兩個雙引號
        return field.replace("\"", "\"\"");
    }
}