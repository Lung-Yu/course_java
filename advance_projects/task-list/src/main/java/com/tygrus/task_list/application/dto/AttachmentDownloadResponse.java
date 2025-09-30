package com.tygrus.task_list.application.dto;

/**
 * 附件下載回應 DTO
 * Attachment download response data transfer object
 */
public class AttachmentDownloadResponse {

    private final String filename;
    private final String contentType;
    private final long fileSize;
    private final byte[] fileData;
    private final boolean success;
    private final String errorMessage;

    // 成功的回應建構子
    public AttachmentDownloadResponse(String filename, String contentType, 
                                    long fileSize, byte[] fileData) {
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileData = fileData;
        this.success = true;
        this.errorMessage = null;
    }

    // 失敗的回應建構子
    public AttachmentDownloadResponse(String errorMessage) {
        this.filename = null;
        this.contentType = null;
        this.fileSize = 0;
        this.fileData = null;
        this.success = false;
        this.errorMessage = errorMessage;
    }

    /**
     * 創建成功的下載回應
     */
    public static AttachmentDownloadResponse success(String filename, String contentType, 
                                                   long fileSize, byte[] fileData) {
        return new AttachmentDownloadResponse(filename, contentType, fileSize, fileData);
    }

    /**
     * 創建失敗的下載回應
     */
    public static AttachmentDownloadResponse error(String errorMessage) {
        return new AttachmentDownloadResponse(errorMessage);
    }

    // Getters
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public long getFileSize() { return fileSize; }
    public byte[] getFileData() { return fileData; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }

    /**
     * 獲取格式化的檔案大小
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    @Override
    public String toString() {
        if (success) {
            return "AttachmentDownloadResponse{" +
                    "filename='" + filename + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", fileSize=" + fileSize +
                    ", formattedSize='" + getFormattedFileSize() + '\'' +
                    ", success=" + success +
                    '}';
        } else {
            return "AttachmentDownloadResponse{" +
                    "success=" + success +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}