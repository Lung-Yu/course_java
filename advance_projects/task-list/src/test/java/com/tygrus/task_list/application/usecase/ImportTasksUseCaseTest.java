package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.CreateTaskRequest;
import com.tygrus.task_list.application.dto.ImportResult;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.exception.FileSizeExceededException;
import com.tygrus.task_list.application.exception.InvalidFileFormatException;
import com.tygrus.task_list.application.service.CsvFileParser;
import com.tygrus.task_list.application.service.FileParser;
import com.tygrus.task_list.application.service.JsonFileParser;
import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ImportTasksUseCase 的 TDD 測試
 */
@ExtendWith(MockitoExtension.class)
class ImportTasksUseCaseTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    private ImportTasksUseCase useCase;
    private List<FileParser> fileParsers;
    
    @BeforeEach
    void setUp() {
        fileParsers = Arrays.asList(new CsvFileParser(), new JsonFileParser());
        useCase = new ImportTasksUseCase(taskRepository, fileParsers);
    }
    
    @Test
    void constructor_WithNullRepository_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> new ImportTasksUseCase(null, fileParsers))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("TaskRepository cannot be null");
    }
    
    @Test
    void constructor_WithNullParsers_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> new ImportTasksUseCase(taskRepository, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("FileParsers cannot be null");
    }
    
    @Test
    void constructor_WithEmptyParsers_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> new ImportTasksUseCase(taskRepository, Arrays.asList()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("At least one FileParser must be provided");
    }
    
    @Test
    void execute_WithNullFileContent_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> useCase.execute(null, "test.csv"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("File content cannot be null");
    }
    
    @Test
    void execute_WithNullFileName_ShouldThrowException() {
        // Given
        byte[] content = "title,description,priority,dueDate\n".getBytes();
        
        // When & Then
        assertThatThrownBy(() -> useCase.execute(content, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("File name cannot be null or empty");
    }
    
    @Test
    void execute_WithEmptyFileName_ShouldThrowException() {
        // Given
        byte[] content = "title,description,priority,dueDate\n".getBytes();
        
        // When & Then
        assertThatThrownBy(() -> useCase.execute(content, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("File name cannot be null or empty");
    }
    
    @Test
    void execute_WithUnsupportedFileFormat_ShouldThrowInvalidFileFormatException() {
        // Given
        byte[] content = "some content".getBytes();
        String fileName = "test.txt";
        
        // When & Then
        assertThatThrownBy(() -> useCase.execute(content, fileName))
            .isInstanceOf(InvalidFileFormatException.class)
            .hasMessageContaining("Unsupported file format")
            .hasMessageContaining("test.txt")
            .hasMessageContaining("csv, json");
    }
    
    @Test
    void execute_WithFileSizeExceeded_ShouldThrowFileSizeExceededException() {
        // Given
        byte[] content = new byte[11 * 1024 * 1024]; // 11MB
        String fileName = "test.csv";
        
        // When & Then
        assertThatThrownBy(() -> useCase.execute(content, fileName))
            .isInstanceOf(FileSizeExceededException.class)
            .hasMessageContaining("test.csv")
            .hasMessageContaining("size exceeded limit");
    }
    
    @Test
    void execute_WithValidCsvFile_ShouldReturnSuccessResult() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "Task 1,Description 1,HIGH,2024-12-31 23:59:59\n" +
                           "Task 2,Description 2,MEDIUM,\n";
        byte[] content = csvContent.getBytes();
        String fileName = "test.csv";
        
        Task savedTask1 = createMockTask("Task 1", "Description 1", Priority.HIGH);
        Task savedTask2 = createMockTask("Task 2", "Description 2", Priority.MEDIUM);
        
        when(taskRepository.save(any(Task.class)))
            .thenReturn(savedTask1)
            .thenReturn(savedTask2);
        
        // When
        ImportResult result = useCase.execute(content, fileName);
        
        // Then
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isEqualTo(0);
        assertThat(result.getSuccessfulTasks()).hasSize(2);
        assertThat(result.getErrorMessages()).isEmpty();
        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getSuccessRate()).isEqualTo(1.0);
        
        verify(taskRepository, times(2)).save(any(Task.class));
    }
    
    @Test
    void execute_WithValidJsonFile_ShouldReturnSuccessResult() {
        // Given
        String jsonContent = "[" +
                           "{\"title\":\"Task 1\",\"description\":\"Description 1\",\"priority\":\"HIGH\",\"dueDate\":\"2024-12-31 23:59:59\"}," +
                           "{\"title\":\"Task 2\",\"description\":\"Description 2\",\"priority\":\"MEDIUM\"}" +
                           "]";
        byte[] content = jsonContent.getBytes();
        String fileName = "test.json";
        
        Task savedTask1 = createMockTask("Task 1", "Description 1", Priority.HIGH);
        Task savedTask2 = createMockTask("Task 2", "Description 2", Priority.MEDIUM);
        
        when(taskRepository.save(any(Task.class)))
            .thenReturn(savedTask1)
            .thenReturn(savedTask2);
        
        // When
        ImportResult result = useCase.execute(content, fileName);
        
        // Then
        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isEqualTo(0);
        assertThat(result.getSuccessfulTasks()).hasSize(2);
        assertThat(result.getErrorMessages()).isEmpty();
        assertThat(result.hasErrors()).isFalse();
        
        verify(taskRepository, times(2)).save(any(Task.class));
    }
    
    @Test
    void execute_WithPartiallyValidCsv_ShouldReturnMixedResult() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "Task 1,Description 1,HIGH,2024-12-31 23:59:59\n" +
                           ",Invalid Task,,\n" + // 空標題
                           "Task 3,Description 3,MEDIUM,\n";
        byte[] content = csvContent.getBytes();
        String fileName = "test.csv";
        
        Task savedTask1 = createMockTask("Task 1", "Description 1", Priority.HIGH);
        Task savedTask3 = createMockTask("Task 3", "Description 3", Priority.MEDIUM);
        
        when(taskRepository.save(any(Task.class)))
            .thenReturn(savedTask1)
            .thenReturn(savedTask3);
        
        // When
        ImportResult result = useCase.execute(content, fileName);
        
        // Then
        assertThat(result.getTotalCount()).isEqualTo(2); // 只有有效的任務被計算
        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isEqualTo(0);
        assertThat(result.getSuccessfulTasks()).hasSize(2);
        
        verify(taskRepository, times(2)).save(any(Task.class));
    }
    
    @Test
    void execute_WithRepositoryException_ShouldReturnFailureResult() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "Task 1,Description 1,HIGH,2024-12-31 23:59:59\n";
        byte[] content = csvContent.getBytes();
        String fileName = "test.csv";
        
        when(taskRepository.save(any(Task.class)))
            .thenThrow(new RuntimeException("Database error"));
        
        // When
        ImportResult result = useCase.execute(content, fileName);
        
        // Then
        assertThat(result.getTotalCount()).isEqualTo(1);
        assertThat(result.getSuccessCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(1);
        assertThat(result.getErrorMessages()).hasSize(1);
        assertThat(result.getErrorMessages().get(0)).contains("Database error");
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getSuccessRate()).isEqualTo(0.0);
        
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    
    @Test
    void execute_WithEmptyFile_ShouldReturnEmptyResult() {
        // Given
        String csvContent = "title,description,priority,dueDate\n"; // 只有標題行
        byte[] content = csvContent.getBytes();
        String fileName = "test.csv";
        
        // When
        ImportResult result = useCase.execute(content, fileName);
        
        // Then
        assertThat(result.getTotalCount()).isEqualTo(0);
        assertThat(result.getSuccessCount()).isEqualTo(0);
        assertThat(result.getFailureCount()).isEqualTo(0);
        assertThat(result.getSuccessfulTasks()).isEmpty();
        assertThat(result.getErrorMessages()).isEmpty();
        assertThat(result.hasErrors()).isFalse();
        
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    private Task createMockTask(String title, String description, Priority priority) {
        TaskId taskId = TaskId.generate();
        return Task.builder()
            .id(taskId)
            .title(title)
            .description(description)
            .priority(priority)
            .createdAt(LocalDateTime.now())
            .build();
    }
}