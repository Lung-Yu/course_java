package com.tygrus.task_list.application.service;

import com.tygrus.task_list.application.dto.CreateTaskRequest;
import com.tygrus.task_list.application.exception.InvalidFileFormatException;
import com.tygrus.task_list.domain.model.Priority;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CSV 檔案解析器
 * 
 * 解析 CSV 格式的任務檔案
 * 預期格式: title,description,priority,dueDate
 */
public class CsvFileParser implements FileParser {
    
    private static final String[] SUPPORTED_EXTENSIONS = {"csv"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String CSV_DELIMITER = ",";
    
    @Override
    public Stream<CreateTaskRequest> parse(InputStream inputStream, String fileName) {
        if (!supports(fileName)) {
            throw new InvalidFileFormatException(fileName, "CSV", getFileExtension(fileName));
        }
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            // 讀取所有行並關閉 reader，然後返回處理過的 Stream
            List<String> lines = reader.lines()
                .skip(1) // 跳過標題行
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());
            reader.close();
            
            return lines.stream()
                .map(line -> parseCsvLine(line, fileName))
                .filter(request -> request != null); // 過濾掉解析失敗的行
        } catch (IOException e) {
            throw new InvalidFileFormatException("Failed to read CSV file: " + fileName, e);
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
    
    private CreateTaskRequest parseCsvLine(String line, String fileName) {
        try {
            String[] fields = parseCSVLine(line);
            
            if (fields.length < 1) {
                return null; // 跳過空行
            }
            
            String title = fields.length > 0 ? fields[0].trim() : "";
            String description = fields.length > 1 ? fields[1].trim() : null;
            Priority priority = fields.length > 2 ? parsePriority(fields[2].trim()) : Priority.MEDIUM;
            LocalDateTime dueDate = fields.length > 3 ? parseDateTime(fields[3].trim()) : null;
            
            if (title.isEmpty()) {
                return null; // 跳過沒有標題的行
            }
            
            return CreateTaskRequest.builder()
                .title(title)
                .description(description.isEmpty() ? null : description)
                .priority(priority)
                .dueDate(dueDate)
                .build();
                
        } catch (Exception e) {
            // 記錄解析錯誤但不中斷整個處理流程
            System.err.println("Failed to parse CSV line: " + line + ", Error: " + e.getMessage());
            return null;
        }
    }
    
    private String[] parseCSVLine(String line) {
        // 簡單的 CSV 解析，處理引號內的逗號
        String[] fields = new String[4];
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int fieldIndex = 0;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 處理雙引號轉義 ""
                    current.append('"');
                    i++; // 跳過下一個引號
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                if (fieldIndex < fields.length) {
                    fields[fieldIndex] = current.toString();
                    current.setLength(0);
                    fieldIndex++;
                }
            } else {
                current.append(c);
            }
        }
        
        // 處理最後一個欄位
        if (fieldIndex < fields.length) {
            fields[fieldIndex] = current.toString();
        }
        
        // 填充空字串給未填的欄位
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null) {
                fields[i] = "";
            }
        }
        
        return fields;
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