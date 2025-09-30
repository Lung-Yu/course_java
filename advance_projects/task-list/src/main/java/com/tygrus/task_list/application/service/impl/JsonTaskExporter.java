package com.tygrus.task_list.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tygrus.task_list.application.dto.ExportFormat;
import com.tygrus.task_list.application.service.TaskExporter;
import com.tygrus.task_list.domain.model.Task;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON 格式任務匯出器
 * 
 * 實作將任務匯出為 JSON 格式的功能
 */
@Component
public class JsonTaskExporter implements TaskExporter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper objectMapper;
    
    public JsonTaskExporter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 格式化輸出
    }
    
    @Override
    public boolean supports(ExportFormat format) {
        return ExportFormat.JSON.equals(format);
    }
    
    @Override
    public byte[] export(List<Task> tasks, String fileName) throws Exception {
        List<Map<String, Object>> taskList = new ArrayList<>();
        
        for (Task task : tasks) {
            Map<String, Object> taskMap = new LinkedHashMap<>();
            taskMap.put("id", task.getId().getValue());
            taskMap.put("title", task.getTitle());
            taskMap.put("description", task.getDescription());
            taskMap.put("status", task.getStatus().name());
            taskMap.put("priority", task.getPriority() != null ? task.getPriority().name() : null);
            taskMap.put("dueDate", task.getDueDate() != null ? task.getDueDate().format(DATE_FORMATTER) : null);
            taskMap.put("createdAt", task.getCreatedAt().format(DATE_FORMATTER));
            taskMap.put("updatedAt", task.getUpdatedAt().format(DATE_FORMATTER));
            
            taskList.add(taskMap);
        }
        
        // 建立根物件
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("exportTime", java.time.LocalDateTime.now().format(DATE_FORMATTER));
        root.put("totalCount", tasks.size());
        root.put("tasks", taskList);
        
        return objectMapper.writeValueAsBytes(root);
    }
    
    @Override
    public String getMimeType() {
        return ExportFormat.JSON.getMimeType();
    }
}