package com.tygrus.task_list.application.service;

import com.tygrus.task_list.application.dto.CreateTaskRequest;
import com.tygrus.task_list.application.exception.InvalidFileFormatException;
import com.tygrus.task_list.domain.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * JsonFileParser 的單元測試
 */
class JsonFileParserTest {
    
    private JsonFileParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new JsonFileParser();
    }
    
    @Test
    void supports_WithJsonFile_ShouldReturnTrue() {
        // When & Then
        assertThat(parser.supports("test.json")).isTrue();
        assertThat(parser.supports("data.JSON")).isTrue();
        assertThat(parser.supports("tasks.json")).isTrue();
    }
    
    @Test
    void supports_WithNonJsonFile_ShouldReturnFalse() {
        // When & Then
        assertThat(parser.supports("test.txt")).isFalse();
        assertThat(parser.supports("data.csv")).isFalse();
        assertThat(parser.supports("tasks.xlsx")).isFalse();
        assertThat(parser.supports("noextension")).isFalse();
    }
    
    @Test
    void supports_WithNullFileName_ShouldReturnFalse() {
        // When & Then
        assertThat(parser.supports(null)).isFalse();
    }
    
    @Test
    void getSupportedExtensions_ShouldReturnJson() {
        // When
        String[] extensions = parser.getSupportedExtensions();
        
        // Then
        assertThat(extensions).containsExactly("json");
    }
    
    @Test
    void parse_WithValidJsonArray_ShouldReturnCorrectTasks() {
        // Given
        String jsonContent = "[" +
            "{\"title\":\"Task 1\",\"description\":\"Description 1\",\"priority\":\"HIGH\",\"dueDate\":\"2024-12-31 23:59:59\"}," +
            "{\"title\":\"Task 2\",\"description\":\"Description 2\",\"priority\":\"MEDIUM\"}," +
            "{\"title\":\"Task 3\",\"priority\":\"LOW\",\"dueDate\":\"2024-01-01 00:00:00\"}" +
            "]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.json")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(3);
        
        CreateTaskRequest task1 = requests.get(0);
        assertThat(task1.getTitle()).isEqualTo("Task 1");
        assertThat(task1.getDescription()).isEqualTo("Description 1");
        assertThat(task1.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task1.getDueDate()).isEqualTo(LocalDateTime.of(2024, 12, 31, 23, 59, 59));
        
        CreateTaskRequest task2 = requests.get(1);
        assertThat(task2.getTitle()).isEqualTo("Task 2");
        assertThat(task2.getDescription()).isEqualTo("Description 2");
        assertThat(task2.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(task2.getDueDate()).isNull();
        
        CreateTaskRequest task3 = requests.get(2);
        assertThat(task3.getTitle()).isEqualTo("Task 3");
        assertThat(task3.getDescription()).isNull();
        assertThat(task3.getPriority()).isEqualTo(Priority.LOW);
        assertThat(task3.getDueDate()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
    }
    
    @Test
    void parse_WithSingleJsonObject_ShouldReturnSingleTask() {
        // Given
        String jsonContent = "{\"title\":\"Single Task\",\"description\":\"Single Description\",\"priority\":\"HIGH\",\"dueDate\":\"2024-12-31 23:59:59\"}";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.json")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(1);
        
        CreateTaskRequest task = requests.get(0);
        assertThat(task.getTitle()).isEqualTo("Single Task");
        assertThat(task.getDescription()).isEqualTo("Single Description");
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.getDueDate()).isEqualTo(LocalDateTime.of(2024, 12, 31, 23, 59, 59));
    }
    
    @Test
    void parse_WithMinimalJsonFields_ShouldHandleCorrectly() {
        // Given
        String jsonContent = "[" +
            "{\"title\":\"Minimal Task 1\"}," +
            "{\"title\":\"Minimal Task 2\",\"priority\":\"HIGH\"}" +
            "]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.json")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(2);
        
        CreateTaskRequest task1 = requests.get(0);
        assertThat(task1.getTitle()).isEqualTo("Minimal Task 1");
        assertThat(task1.getDescription()).isNull();
        assertThat(task1.getPriority()).isEqualTo(Priority.MEDIUM); // 預設值
        assertThat(task1.getDueDate()).isNull();
        
        CreateTaskRequest task2 = requests.get(1);
        assertThat(task2.getTitle()).isEqualTo("Minimal Task 2");
        assertThat(task2.getPriority()).isEqualTo(Priority.HIGH);
    }
    
    @Test
    void parse_WithInvalidPriority_ShouldUseMediumAsDefault() {
        // Given
        String jsonContent = "[{\"title\":\"Task 1\",\"priority\":\"INVALID_PRIORITY\"}]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.json")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getPriority()).isEqualTo(Priority.MEDIUM);
    }
    
    @Test
    void parse_WithInvalidDate_ShouldSetDateToNull() {
        // Given
        String jsonContent = "[{\"title\":\"Task 1\",\"dueDate\":\"invalid-date\"}]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.json")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDueDate()).isNull();
    }
    
    @Test
    void parse_WithEmptyTitle_ShouldFilterOut() {
        // Given
        String jsonContent = "[" +
            "{\"title\":\"Valid Task\",\"description\":\"Description\"}," +
            "{\"title\":\"\",\"description\":\"Empty title\"}," +
            "{\"description\":\"No title field\"}," +
            "{\"title\":\"   \",\"description\":\"Whitespace title\"}" +
            "]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.json")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getTitle()).isEqualTo("Valid Task");
    }
    
    @Test
    void parse_WithEmptyArray_ShouldReturnEmptyStream() {
        // Given
        String jsonContent = "[]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.json")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).isEmpty();
    }
    
    @Test
    void parse_WithUnsupportedFileName_ShouldThrowException() {
        // Given
        String jsonContent = "[]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When & Then
        assertThatThrownBy(() -> parser.parse(inputStream, "test.txt"))
            .isInstanceOf(InvalidFileFormatException.class)
            .hasMessageContaining("test.txt")
            .hasMessageContaining("JSON");
    }
    
    @Test
    void parse_WithInvalidJson_ShouldThrowException() {
        // Given
        String jsonContent = "{invalid json content";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When & Then
        assertThatThrownBy(() -> parser.parse(inputStream, "test.json"))
            .isInstanceOf(InvalidFileFormatException.class)
            .hasMessageContaining("Failed to parse JSON file");
    }
    
    @Test
    void parse_WithEmptyDescription_ShouldSetToNull() {
        // Given
        String jsonContent = "[" +
            "{\"title\":\"Task 1\",\"description\":\"\"}," +
            "{\"title\":\"Task 2\",\"description\":\"   \"}" +
            "]";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.json")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isNull();
        assertThat(requests.get(1).getDescription()).isNull();
    }
}