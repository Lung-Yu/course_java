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
 * CsvFileParser 的單元測試
 */
class CsvFileParserTest {
    
    private CsvFileParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new CsvFileParser();
    }
    
    @Test
    void supports_WithCsvFile_ShouldReturnTrue() {
        // When & Then
        assertThat(parser.supports("test.csv")).isTrue();
        assertThat(parser.supports("data.CSV")).isTrue();
        assertThat(parser.supports("tasks.csv")).isTrue();
    }
    
    @Test
    void supports_WithNonCsvFile_ShouldReturnFalse() {
        // When & Then
        assertThat(parser.supports("test.txt")).isFalse();
        assertThat(parser.supports("data.json")).isFalse();
        assertThat(parser.supports("tasks.xlsx")).isFalse();
        assertThat(parser.supports("noextension")).isFalse();
    }
    
    @Test
    void supports_WithNullFileName_ShouldReturnFalse() {
        // When & Then
        assertThat(parser.supports(null)).isFalse();
    }
    
    @Test
    void getSupportedExtensions_ShouldReturnCsv() {
        // When
        String[] extensions = parser.getSupportedExtensions();
        
        // Then
        assertThat(extensions).containsExactly("csv");
    }
    
    @Test
    void parse_WithValidCsvContent_ShouldReturnCorrectTasks() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "Task 1,Description 1,HIGH,2024-12-31 23:59:59\n" +
                           "Task 2,Description 2,MEDIUM,\n" +
                           "Task 3,,LOW,2024-01-01 00:00:00\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.csv")
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
    void parse_WithQuotedFields_ShouldHandleCorrectly() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "\"Task with, comma\",\"Description with \"\"quotes\"\"\",MEDIUM,\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.csv")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(1);
        
        CreateTaskRequest task = requests.get(0);
        assertThat(task.getTitle()).isEqualTo("Task with, comma");
        assertThat(task.getDescription()).isEqualTo("Description with \"quotes\"");
        assertThat(task.getPriority()).isEqualTo(Priority.MEDIUM);
    }
    
    @Test
    void parse_WithEmptyLines_ShouldSkipEmptyLines() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "Task 1,Description 1,HIGH,\n" +
                           "\n" +
                           "   \n" +
                           "Task 2,Description 2,MEDIUM,\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.csv")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getTitle()).isEqualTo("Task 1");
        assertThat(requests.get(1).getTitle()).isEqualTo("Task 2");
    }
    
    @Test
    void parse_WithInvalidPriority_ShouldUseMediumAsDefault() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "Task 1,Description 1,INVALID_PRIORITY,\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.csv")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getPriority()).isEqualTo(Priority.MEDIUM);
    }
    
    @Test
    void parse_WithInvalidDate_ShouldSetDateToNull() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "Task 1,Description 1,HIGH,invalid-date\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.csv")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDueDate()).isNull();
    }
    
    @Test
    void parse_WithEmptyTitle_ShouldFilterOut() {
        // Given
        String csvContent = "title,description,priority,dueDate\n" +
                           "Task 1,Description 1,HIGH,\n" +
                           ",Description 2,MEDIUM,\n" +
                           "   ,Description 3,LOW,\n" +
                           "Task 4,Description 4,HIGH,\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.csv")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getTitle()).isEqualTo("Task 1");
        assertThat(requests.get(1).getTitle()).isEqualTo("Task 4");
    }
    
    @Test
    void parse_WithUnsupportedFileName_ShouldThrowException() {
        // Given
        String csvContent = "title,description,priority,dueDate\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        
        // When & Then
        assertThatThrownBy(() -> parser.parse(inputStream, "test.txt"))
            .isInstanceOf(InvalidFileFormatException.class)
            .hasMessageContaining("test.txt")
            .hasMessageContaining("CSV");
    }
    
    @Test
    void parse_WithOnlyHeader_ShouldReturnEmptyStream() {
        // Given
        String csvContent = "title,description,priority,dueDate\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        
        // When
        List<CreateTaskRequest> requests = parser.parse(inputStream, "test.csv")
            .collect(Collectors.toList());
        
        // Then
        assertThat(requests).isEmpty();
    }
}