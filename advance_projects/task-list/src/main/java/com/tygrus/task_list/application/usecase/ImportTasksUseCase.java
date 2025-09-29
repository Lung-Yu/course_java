package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.CreateTaskRequest;
import com.tygrus.task_list.application.dto.ImportResult;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.exception.FileSizeExceededException;
import com.tygrus.task_list.application.exception.InvalidFileFormatException;
import com.tygrus.task_list.application.service.FileParser;
import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.repository.TaskRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * 匯入任務Use Case
 * 
 * 實現UC-005: 批次匯入任務的業務邏輯
 * 支援 CSV 和 JSON 格式檔案匯入
 */
public class ImportTasksUseCase {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int BATCH_SIZE = 100; // 批次處理大小
    
    private final TaskRepository taskRepository;
    private final List<FileParser> fileParsers;
    
    public ImportTasksUseCase(TaskRepository taskRepository, List<FileParser> fileParsers) {
        this.taskRepository = Objects.requireNonNull(taskRepository, "TaskRepository cannot be null");
        this.fileParsers = Objects.requireNonNull(fileParsers, "FileParsers cannot be null");
        
        if (fileParsers.isEmpty()) {
            throw new IllegalArgumentException("At least one FileParser must be provided");
        }
    }
    
    /**
     * 執行批次匯入任務業務邏輯
     * 
     * @param fileContent 檔案內容 (byte array)
     * @param fileName 檔案名稱
     * @return 匯入結果
     */
    public ImportResult execute(byte[] fileContent, String fileName) {
        // 驗證輸入
        validateInput(fileContent, fileName);
        
        // 檔案大小檢查
        validateFileSize(fileContent, fileName);
        
        // 找到適合的解析器
        FileParser parser = findSuitableParser(fileName);
        
        // 解析檔案並批次處理
        try (InputStream inputStream = new ByteArrayInputStream(fileContent)) {
            return processTasksInBatches(parser, inputStream, fileName);
        } catch (IOException e) {
            throw new InvalidFileFormatException("Failed to process file: " + fileName, e);
        }
    }
    
    private void validateInput(byte[] fileContent, String fileName) {
        if (fileContent == null) {
            throw new IllegalArgumentException("File content cannot be null");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
    }
    
    private void validateFileSize(byte[] fileContent, String fileName) {
        if (fileContent.length > MAX_FILE_SIZE) {
            throw new FileSizeExceededException(fileName, fileContent.length, MAX_FILE_SIZE);
        }
    }
    
    private FileParser findSuitableParser(String fileName) {
        return fileParsers.stream()
            .filter(parser -> parser.supports(fileName))
            .findFirst()
            .orElseThrow(() -> {
                String supportedFormats = String.join(", ", getSupportedFormats());
                return new InvalidFileFormatException(
                    String.format("Unsupported file format for '%s'. Supported formats: %s", 
                        fileName, supportedFormats));
            });
    }
    
    private String[] getSupportedFormats() {
        return fileParsers.stream()
            .flatMap(parser -> Stream.of(parser.getSupportedExtensions()))
            .distinct()
            .toArray(String[]::new);
    }
    
    private ImportResult processTasksInBatches(FileParser parser, InputStream inputStream, String fileName) {
        ImportResult.Builder resultBuilder = ImportResult.builder();
        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        List<String> errorMessages = new ArrayList<>();
        List<TaskDTO> successfulTasks = new ArrayList<>();
        
        try (Stream<CreateTaskRequest> requestStream = parser.parse(inputStream, fileName)) {
            // 使用 Stream API 進行批次處理
            requestStream
                .peek(request -> totalCount.incrementAndGet())
                .map(request -> {
                    try {
                        // 建立並儲存任務
                        Task task = createTaskFromRequest(request);
                        Task savedTask = taskRepository.save(task);
                        TaskDTO taskDTO = TaskDTO.fromTask(savedTask);
                        
                        successCount.incrementAndGet();
                        return ProcessResult.success(taskDTO);
                    } catch (Exception e) {
                        return ProcessResult.failure("Line " + totalCount.get() + ": " + e.getMessage());
                    }
                })
                .forEach(result -> {
                    if (result.isSuccess()) {
                        successfulTasks.add(result.getTaskDTO());
                    } else {
                        errorMessages.add(result.getErrorMessage());
                    }
                });
                
        } catch (Exception e) {
            errorMessages.add("Failed to parse file: " + e.getMessage());
        }
        
        return resultBuilder
            .totalCount(totalCount.get())
            .successCount(successCount.get())
            .failureCount(totalCount.get() - successCount.get())
            .errorMessages(errorMessages)
            .successfulTasks(successfulTasks)
            .build();
    }
    
    private Task createTaskFromRequest(CreateTaskRequest request) {
        // 驗證請求
        validateTaskRequest(request);
        
        return Task.builder()
            .id(TaskId.generate())
            .title(request.getTitle())
            .description(request.getDescription())
            .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
            .dueDate(request.getDueDate())
            .createdAt(LocalDateTime.now())
            .build();
    }
    
    private void validateTaskRequest(CreateTaskRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateTaskRequest cannot be null");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        if (request.getTitle().length() > 255) {
            throw new IllegalArgumentException("Task title cannot exceed 255 characters");
        }
        if (request.getDescription() != null && request.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Task description cannot exceed 1000 characters");
        }
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
    }
    
    /**
     * 內部類別：處理結果
     */
    private static class ProcessResult {
        private final boolean success;
        private final TaskDTO taskDTO;
        private final String errorMessage;
        
        private ProcessResult(boolean success, TaskDTO taskDTO, String errorMessage) {
            this.success = success;
            this.taskDTO = taskDTO;
            this.errorMessage = errorMessage;
        }
        
        public static ProcessResult success(TaskDTO taskDTO) {
            return new ProcessResult(true, taskDTO, null);
        }
        
        public static ProcessResult failure(String errorMessage) {
            return new ProcessResult(false, null, errorMessage);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public TaskDTO getTaskDTO() {
            return taskDTO;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}