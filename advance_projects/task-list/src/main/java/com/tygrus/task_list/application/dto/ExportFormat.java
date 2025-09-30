package com.tygrus.task_list.application.dto;

/**
 * 匯出格式枚舉
 * 
 * 定義支援的匯出格式類型
 */
public enum ExportFormat {
    /**
     * CSV 格式 - 逗號分隔值文件
     */
    CSV("csv", "text/csv"),
    
    /**
     * JSON 格式 - JavaScript Object Notation
     */
    JSON("json", "application/json"),
    
    /**
     * Excel 格式 - Microsoft Excel 文件
     */
    EXCEL("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    
    private final String extension;
    private final String mimeType;
    
    ExportFormat(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    /**
     * 根據檔案名稱推斷匯出格式
     * 
     * @param fileName 檔案名稱
     * @return 匯出格式，預設為 CSV
     */
    public static ExportFormat fromFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return CSV;
        }
        
        String lowerFileName = fileName.toLowerCase();
        
        if (lowerFileName.endsWith(".json")) {
            return JSON;
        } else if (lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".xls")) {
            return EXCEL;
        } else {
            return CSV;
        }
    }
}