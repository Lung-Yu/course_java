package com.tygrus.task_list.application.exception;

/**
 * 無效檔案格式異常
 * 
 * 當上傳的檔案格式不符合預期時拋出
 */
public class InvalidFileFormatException extends RuntimeException {
    
    private final String fileName;
    private final String expectedFormat;
    private final String actualFormat;
    
    public InvalidFileFormatException(String message) {
        super(message);
        this.fileName = null;
        this.expectedFormat = null;
        this.actualFormat = null;
    }
    
    public InvalidFileFormatException(String fileName, String expectedFormat, String actualFormat) {
        super(String.format("Invalid file format for '%s'. Expected: %s, Actual: %s", 
            fileName, expectedFormat, actualFormat));
        this.fileName = fileName;
        this.expectedFormat = expectedFormat;
        this.actualFormat = actualFormat;
    }
    
    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
        this.expectedFormat = null;
        this.actualFormat = null;
    }
    
    public InvalidFileFormatException(String fileName, String expectedFormat, String actualFormat, Throwable cause) {
        super(String.format("Invalid file format for '%s'. Expected: %s, Actual: %s", 
            fileName, expectedFormat, actualFormat), cause);
        this.fileName = fileName;
        this.expectedFormat = expectedFormat;
        this.actualFormat = actualFormat;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getExpectedFormat() {
        return expectedFormat;
    }
    
    public String getActualFormat() {
        return actualFormat;
    }
}