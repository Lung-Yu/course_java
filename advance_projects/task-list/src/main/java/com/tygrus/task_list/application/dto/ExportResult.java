package com.tygrus.task_list.application.dto;

import java.time.LocalDateTime;

/**
 * 任務匯出結果 DTO
 * 
 * 封裝匯出操作的結果資訊
 */
public class ExportResult {
    
    private final boolean success;
    private final String fileName;
    private final byte[] fileContent;
    private final String mimeType;
    private final int exportedCount;
    private final LocalDateTime exportTime;
    private final long fileSizeBytes;
    private final String errorMessage;
    
    // 成功結果的建構函數
    public ExportResult(String fileName, byte[] fileContent, String mimeType, 
                       int exportedCount, LocalDateTime exportTime) {
        this.success = true;
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.mimeType = mimeType;
        this.exportedCount = exportedCount;
        this.exportTime = exportTime;
        this.fileSizeBytes = fileContent != null ? fileContent.length : 0;
        this.errorMessage = null;
    }
    
    // 失敗結果的建構函數
    public ExportResult(String errorMessage) {
        this.success = false;
        this.fileName = null;
        this.fileContent = null;
        this.mimeType = null;
        this.exportedCount = 0;
        this.exportTime = LocalDateTime.now();
        this.fileSizeBytes = 0;
        this.errorMessage = errorMessage;
    }
    
    // Getter 方法
    public boolean isSuccess() {
        return success;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public byte[] getFileContent() {
        return fileContent;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public int getExportedCount() {
        return exportedCount;
    }
    
    public LocalDateTime getExportTime() {
        return exportTime;
    }
    
    public long getFileSizeBytes() {
        return fileSizeBytes;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * 格式化檔案大小為可讀格式
     * 
     * @return 格式化的檔案大小 (例如: "1.2 KB", "3.4 MB")
     */
    public String getFormattedFileSize() {
        if (fileSizeBytes < 1024) {
            return fileSizeBytes + " B";
        } else if (fileSizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", fileSizeBytes / 1024.0);
        } else {
            return String.format("%.1f MB", fileSizeBytes / (1024.0 * 1024.0));
        }
    }
    
    @Override
    public String toString() {
        if (success) {
            return "ExportResult{" +
                    "success=true" +
                    ", fileName='" + fileName + '\'' +
                    ", exportedCount=" + exportedCount +
                    ", fileSize=" + getFormattedFileSize() +
                    ", exportTime=" + exportTime +
                    '}';
        } else {
            return "ExportResult{" +
                    "success=false" +
                    ", errorMessage='" + errorMessage + '\'' +
                    ", exportTime=" + exportTime +
                    '}';
        }
    }
}