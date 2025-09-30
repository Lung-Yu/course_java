package com.tygrus.task_list.application.usecase;

import com.tygrus.task_list.application.dto.ExportFormat;
import com.tygrus.task_list.application.dto.ExportResult;
import com.tygrus.task_list.application.dto.ExportTasksRequest;
import com.tygrus.task_list.application.service.TaskExporter;
import com.tygrus.task_list.domain.model.Priority;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.model.TaskStatus;
import com.tygrus.task_list.domain.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ExportTasksUseCase 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExportTasksUseCase 測試")
class ExportTasksUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskExporter csvExporter;

    @Mock
    private TaskExporter jsonExporter;

    private ExportTasksUseCase exportTasksUseCase;
    private List<Task> sampleTasks;

    @BeforeEach
    void setUp() {
        List<TaskExporter> exporters = Arrays.asList(csvExporter, jsonExporter);
        exportTasksUseCase = new ExportTasksUseCase(taskRepository, exporters);

        // 準備測試資料
        sampleTasks = createSampleTasks();
        
        // 設定 mock 行為 - 使用 lenient() 避免 UnnecessaryStubbingException
        lenient().when(csvExporter.supports(any(ExportFormat.class))).thenReturn(false);
        lenient().when(csvExporter.supports(ExportFormat.CSV)).thenReturn(true);
        lenient().when(csvExporter.getMimeType()).thenReturn("text/csv");
        
        lenient().when(jsonExporter.supports(any(ExportFormat.class))).thenReturn(false);
        lenient().when(jsonExporter.supports(ExportFormat.JSON)).thenReturn(true);
        lenient().when(jsonExporter.getMimeType()).thenReturn("application/json");
    }

    private List<Task> createSampleTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        Task task1 = Task.builder()
            .id(TaskId.of("task-1"))
            .title("Task 1")
            .description("Description 1")
            .priority(Priority.HIGH)
            .dueDate(now.plusDays(1))
            .createdAt(now)
            .build();
        
        Task task2 = Task.builder()
            .id(TaskId.of("task-2"))
            .title("Task 2")
            .description("Description 2")
            .priority(Priority.MEDIUM)
            .dueDate(now.plusDays(2))
            .createdAt(now)
            .build();
        
        return Arrays.asList(task1, task2);
    }

    @Nested
    @DisplayName("基本匯出功能測試")
    class BasicExportTest {

        @Test
        @DisplayName("應該能夠成功匯出 CSV 格式")
        void shouldExportCsvSuccessfully() throws Exception {
            // Given
            ExportTasksRequest request = new ExportTasksRequest(ExportFormat.CSV, "test.csv");
            byte[] expectedContent = "test,csv,content".getBytes();
            
            when(taskRepository.findAll()).thenReturn(sampleTasks);
            when(csvExporter.export(anyList(), anyString())).thenReturn(expectedContent);

            // When
            ExportResult result = exportTasksUseCase.exportTasks(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getFileName()).isEqualTo("test.csv");
            assertThat(result.getFileContent()).isEqualTo(expectedContent);
            assertThat(result.getMimeType()).isEqualTo("text/csv");
            assertThat(result.getExportedCount()).isEqualTo(2);
            
            verify(taskRepository).findAll();
            verify(csvExporter).export(sampleTasks, "test.csv");
        }

        @Test
        @DisplayName("應該能夠成功匯出 JSON 格式")
        void shouldExportJsonSuccessfully() throws Exception {
            // Given
            ExportTasksRequest request = new ExportTasksRequest(ExportFormat.JSON, "test.json");
            byte[] expectedContent = "{\"tasks\":[]}".getBytes();
            
            when(taskRepository.findAll()).thenReturn(sampleTasks);
            when(jsonExporter.export(anyList(), anyString())).thenReturn(expectedContent);

            // When
            ExportResult result = exportTasksUseCase.exportTasks(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getFileName()).isEqualTo("test.json");
            assertThat(result.getFileContent()).isEqualTo(expectedContent);
            assertThat(result.getMimeType()).isEqualTo("application/json");
            assertThat(result.getExportedCount()).isEqualTo(2);
            
            verify(taskRepository).findAll();
            verify(jsonExporter).export(sampleTasks, "test.json");
        }

        @Test
        @DisplayName("當沒有找到支援的匯出器時應該回傳錯誤")
        void shouldReturnErrorWhenNoExporterFound() {
            // Given
            ExportTasksRequest request = new ExportTasksRequest(ExportFormat.EXCEL, "test.xlsx");
            
            // When
            ExportResult result = exportTasksUseCase.exportTasks(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getErrorMessage()).contains("No exporter found for format");
        }
    }

    @Nested
    @DisplayName("檔案名稱處理測試")
    class FileNameHandlingTest {

        @Test
        @DisplayName("當未指定檔案名稱時應該產生預設名稱")
        void shouldGenerateDefaultFileNameWhenNotSpecified() throws Exception {
            // Given
            ExportTasksRequest request = new ExportTasksRequest();
            request.setFormat(ExportFormat.CSV);
            byte[] expectedContent = "test,content".getBytes();
            
            when(taskRepository.findAll()).thenReturn(sampleTasks);
            when(csvExporter.export(anyList(), anyString())).thenReturn(expectedContent);

            // When
            ExportResult result = exportTasksUseCase.exportTasks(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getFileName()).startsWith("tasks_export_");
            assertThat(result.getFileName()).endsWith(".csv");
        }

        @Test
        @DisplayName("應該自動添加正確的副檔名")
        void shouldAddCorrectFileExtension() throws Exception {
            // Given
            ExportTasksRequest request = new ExportTasksRequest(ExportFormat.CSV, "test");
            byte[] expectedContent = "test,content".getBytes();
            
            when(taskRepository.findAll()).thenReturn(sampleTasks);
            when(csvExporter.export(anyList(), anyString())).thenReturn(expectedContent);

            // When
            ExportResult result = exportTasksUseCase.exportTasks(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getFileName()).isEqualTo("test.csv");
        }
    }

    @Nested
    @DisplayName("篩選條件測試")
    class FilteringTest {

        @Test
        @DisplayName("應該能夠根據狀態篩選任務")
        void shouldFilterTasksByStatus() throws Exception {
            // Given
            ExportTasksRequest request = new ExportTasksRequest(ExportFormat.CSV, "test.csv");
            request.setStatusFilter(Arrays.asList(TaskStatus.TODO, TaskStatus.IN_PROGRESS));
            byte[] expectedContent = "filtered,content".getBytes();
            
            when(taskRepository.findAll()).thenReturn(sampleTasks);
            when(csvExporter.export(anyList(), anyString())).thenReturn(expectedContent);

            // When
            ExportResult result = exportTasksUseCase.exportTasks(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            verify(csvExporter).export(anyList(), eq("test.csv"));
        }

        @Test
        @DisplayName("應該能夠根據標題篩選任務")
        void shouldFilterTasksByTitle() throws Exception {
            // Given
            ExportTasksRequest request = new ExportTasksRequest(ExportFormat.CSV, "test.csv");
            request.setTitleFilter("Task 1");
            byte[] expectedContent = "filtered,content".getBytes();
            
            when(taskRepository.findAll()).thenReturn(sampleTasks);
            when(csvExporter.export(anyList(), anyString())).thenReturn(expectedContent);

            // When
            ExportResult result = exportTasksUseCase.exportTasks(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            verify(csvExporter).export(anyList(), eq("test.csv"));
        }
    }

    @Nested
    @DisplayName("錯誤處理測試")
    class ErrorHandlingTest {

        @Test
        @DisplayName("當請求為 null 時應該拋出例外")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> exportTasksUseCase.exportTasks(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("當匯出器拋出例外時應該回傳錯誤結果")
        void shouldReturnErrorWhenExporterThrowsException() throws Exception {
            // Given
            ExportTasksRequest request = new ExportTasksRequest(ExportFormat.CSV, "test.csv");
            
            when(taskRepository.findAll()).thenReturn(sampleTasks);
            when(csvExporter.export(anyList(), anyString())).thenThrow(new RuntimeException("Export failed"));

            // When
            ExportResult result = exportTasksUseCase.exportTasks(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getErrorMessage()).contains("Failed to export tasks");
        }

        @Test
        @DisplayName("當超過最大記錄數限制時應該拋出例外")
        void shouldThrowExceptionWhenExceedsMaxRecords() {
            // Given
            ExportTasksRequest request = new ExportTasksRequest(ExportFormat.CSV, "test.csv");
            request.setMaxRecords(100000); // 超過限制

            // When & Then
            ExportResult result = exportTasksUseCase.exportTasks(request);
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getErrorMessage()).contains("Max records cannot exceed");
        }
    }

    @Nested
    @DisplayName("支援格式查詢測試")
    class SupportedFormatsTest {

        @Test
        @DisplayName("應該回傳所有支援的匯出格式")
        void shouldReturnAllSupportedFormats() {
            // When
            List<ExportFormat> supportedFormats = exportTasksUseCase.getSupportedFormats();

            // Then
            assertThat(supportedFormats).isNotNull();
            assertThat(supportedFormats).hasSize(2);
            assertThat(supportedFormats).contains(ExportFormat.CSV, ExportFormat.JSON);
        }
    }
}