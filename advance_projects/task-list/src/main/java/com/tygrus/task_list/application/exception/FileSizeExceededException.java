package com.tygrus.task_list.application.exception;

/**
 * 檔案大小超限異常
 * 
 * 當上傳檔案大小超過限制時拋出
 */
public class FileSizeExceededException extends RuntimeException {
    
    private final String fileName;
    private final long actualSize;
    private final long maxAllowedSize;
    
    public FileSizeExceededException(String message) {
        super(message);
        this.fileName = null;
        this.actualSize = 0;
        this.maxAllowedSize = 0;
    }
    
    public FileSizeExceededException(String fileName, long actualSize, long maxAllowedSize) {
        super(String.format("File '%s' size exceeded limit. Size: %d bytes, Max allowed: %d bytes", 
            fileName, actualSize, maxAllowedSize));
        this.fileName = fileName;
        this.actualSize = actualSize;
        this.maxAllowedSize = maxAllowedSize;
    }
    
    public FileSizeExceededException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
        this.actualSize = 0;
        this.maxAllowedSize = 0;
    }
    
    public FileSizeExceededException(String fileName, long actualSize, long maxAllowedSize, Throwable cause) {
        super(String.format("File '%s' size exceeded limit. Size: %d bytes, Max allowed: %d bytes", 
            fileName, actualSize, maxAllowedSize), cause);
        this.fileName = fileName;
        this.actualSize = actualSize;
        this.maxAllowedSize = maxAllowedSize;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public long getActualSize() {
        return actualSize;
    }
    
    public long getMaxAllowedSize() {
        return maxAllowedSize;
    }
    
    public String getFormattedSizes() {
        return String.format("Actual: %.2f KB, Max: %.2f KB", 
            actualSize / 1024.0, maxAllowedSize / 1024.0);
    }
}