package com.tygrus.task_list.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tygrus.task_list.application.dto.CreateTaskRequest;
import com.tygrus.task_list.application.exception.InvalidFileFormatException;
import com.tygrus.task_list.domain.model.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * JSON 檔案解析器
 * 
 * 解析 JSON 格式的任務檔案
 * 預期格式: [{"title": "...", "description": "...", "priority": "...", "dueDate": "..."}]
 */
public class JsonFileParser implements FileParser {
    
    private static final Logger logger = LogManager.getLogger(JsonFileParser.class);
    private static final String[] SUPPORTED_EXTENSIONS = {"json"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final ObjectMapper objectMapper;
    
    public JsonFileParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public Stream<CreateTaskRequest> parse(InputStream inputStream, String fileName) {
        if (!supports(fileName)) {
            throw new InvalidFileFormatException(fileName, "JSON", getFileExtension(fileName));
        }
        
        try {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            List<CreateTaskRequest> requests = new ArrayList<>();
            
            if (rootNode.isArray()) {
                // JSON 陣列格式
                for (JsonNode taskNode : rootNode) {
                    CreateTaskRequest request = parseJsonNode(taskNode);
                    if (request != null) {
                        requests.add(request);
                    }
                }
            } else {
                // 單一 JSON 物件
                CreateTaskRequest request = parseJsonNode(rootNode);
                if (request != null) {
                    requests.add(request);
                }
            }
            
            return requests.stream();
            
        } catch (IOException e) {
            throw new InvalidFileFormatException("Failed to parse JSON file: " + fileName, e);
        }
    }
    
    @Override
    public boolean supports(String fileName) {
        if (fileName == null) return false;
        String extension = getFileExtension(fileName).toLowerCase();
        for (String supportedExt : SUPPORTED_EXTENSIONS) {
            if (supportedExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String[] getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS.clone();
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }
    
    private CreateTaskRequest parseJsonNode(JsonNode taskNode) {
        try {
            String title = getTextValue(taskNode, "title");
            String description = getTextValue(taskNode, "description");
            Priority priority = parsePriority(getTextValue(taskNode, "priority"));
            LocalDateTime dueDate = parseDateTime(getTextValue(taskNode, "dueDate"));
            
            if (title == null || title.trim().isEmpty()) {
                return null; // 跳過沒有標題的任務
            }
            
            return CreateTaskRequest.builder()
                .title(title.trim())
                .description(description != null && !description.trim().isEmpty() ? description.trim() : null)
                .priority(priority)
                .dueDate(dueDate)
                .build();
                
        } catch (Exception e) {
            // 記錄解析錯誤但不中斷整個處理流程
            logger.error("Failed to parse JSON node: {}, Error: {}", taskNode, e.getMessage());
            return null;
        }
    }
    
    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && fieldNode.isTextual() ? fieldNode.asText() : null;
    }
    
    private Priority parsePriority(String priorityStr) {
        if (priorityStr == null || priorityStr.isEmpty()) {
            return Priority.MEDIUM;
        }
        
        try {
            return Priority.valueOf(priorityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Priority.MEDIUM; // 預設為 MEDIUM
        }
    }
    
    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null; // 無法解析的日期設為 null
        }
    }
}