package com.tygrus.task_list;

import com.tygrus.task_list.application.dto.ImportResult;
import com.tygrus.task_list.application.dto.TaskDTO;
import com.tygrus.task_list.application.service.CsvFileParser;
import com.tygrus.task_list.application.service.FileParser;
import com.tygrus.task_list.application.service.JsonFileParser;
import com.tygrus.task_list.application.usecase.ImportTasksUseCase;
import com.tygrus.task_list.domain.model.Task;
import com.tygrus.task_list.domain.model.TaskId;
import com.tygrus.task_list.domain.repository.TaskRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UC-005 ImportTasksUseCase 功能示範
 * 
 * 此程式展示如何使用 ImportTasksUseCase 進行批次任務匯入
 * 包含 CSV 和 JSON 格式的檔案處理示範
 */
public class ImportTasksDemo {
    
    /**
     * 簡單的內存任務儲存庫實作，用於示範
     */
    private static class InMemoryTaskRepository implements TaskRepository {
        private final ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();
        private final AtomicLong sequenceGenerator = new AtomicLong(1);
        
        @Override
        public Task save(Task task) {
            // 模擬保存操作
            tasks.put(task.getId().getValue(), task);
            return task;
        }
        
        @Override
        public Optional<Task> findById(TaskId taskId) {
            return Optional.ofNullable(tasks.get(taskId.getValue()));
        }
        
        @Override
        public boolean existsById(TaskId taskId) {
            return tasks.containsKey(taskId.getValue());
        }
        
        @Override
        public void deleteById(TaskId taskId) {
            tasks.remove(taskId.getValue());
        }
        
        @Override
        public List<Task> findAll() {
            return List.copyOf(tasks.values());
        }
        
        public void clear() {
            tasks.clear();
        }
        
        public int size() {
            return tasks.size();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== UC-005 ImportTasksUseCase 功能示範 ===\n");
        
        // 初始化系統組件
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        List<FileParser> parsers = Arrays.asList(new CsvFileParser(), new JsonFileParser());
        ImportTasksUseCase importUseCase = new ImportTasksUseCase(repository, parsers);
        
        // 示範 CSV 檔案匯入
        demonstrateCsvImport(importUseCase, repository);
        
        // 示範 JSON 檔案匯入
        demonstrateJsonImport(importUseCase, repository);
        
        // 示範異常處理
        demonstrateErrorHandling(importUseCase);
        
        // 顯示最終統計
        showFinalStatistics(repository);
        
        System.out.println("\n=== 示範完成 ===");
    }
    
    private static void demonstrateCsvImport(ImportTasksUseCase useCase, InMemoryTaskRepository repository) {
        System.out.println("📄 CSV 檔案匯入示範");
        System.out.println("─".repeat(50));
        
        try {
            Path csvPath = Paths.get("sample_data/tasks.csv");
            if (Files.exists(csvPath)) {
                byte[] csvContent = Files.readAllBytes(csvPath);
                
                System.out.println("檔案大小: " + csvContent.length + " bytes");
                System.out.println("執行匯入...");
                
                ImportResult result = useCase.execute(csvContent, "tasks.csv");
                
                printImportResult("CSV 匯入結果", result);
                
            } else {
                System.out.println("❌ 找不到範例 CSV 檔案: " + csvPath);
                demonstrateInlineExample("CSV", createSampleCsvContent(), useCase);
            }
        } catch (IOException e) {
            System.out.println("❌ 讀取 CSV 檔案時發生錯誤: " + e.getMessage());
            demonstrateInlineExample("CSV", createSampleCsvContent(), useCase);
        }
        
        System.out.println();
    }
    
    private static void demonstrateJsonImport(ImportTasksUseCase useCase, InMemoryTaskRepository repository) {
        System.out.println("📋 JSON 檔案匯入示範");
        System.out.println("─".repeat(50));
        
        try {
            Path jsonPath = Paths.get("sample_data/tasks.json");
            if (Files.exists(jsonPath)) {
                byte[] jsonContent = Files.readAllBytes(jsonPath);
                
                System.out.println("檔案大小: " + jsonContent.length + " bytes");
                System.out.println("執行匯入...");
                
                ImportResult result = useCase.execute(jsonContent, "tasks.json");
                
                printImportResult("JSON 匯入結果", result);
                
            } else {
                System.out.println("❌ 找不到範例 JSON 檔案: " + jsonPath);
                demonstrateInlineExample("JSON", createSampleJsonContent(), useCase);
            }
        } catch (IOException e) {
            System.out.println("❌ 讀取 JSON 檔案時發生錯誤: " + e.getMessage());
            demonstrateInlineExample("JSON", createSampleJsonContent(), useCase);
        }
        
        System.out.println();
    }
    
    private static void demonstrateInlineExample(String format, String content, ImportTasksUseCase useCase) {
        System.out.println("使用內建範例 " + format + " 資料:");
        System.out.println(content);
        System.out.println("\n執行匯入...");
        
        ImportResult result = useCase.execute(content.getBytes(), "sample." + format.toLowerCase());
        printImportResult(format + " 匯入結果", result);
    }
    
    private static void demonstrateErrorHandling(ImportTasksUseCase useCase) {
        System.out.println("⚠️ 異常處理示範");
        System.out.println("─".repeat(50));
        
        // 測試不支援的檔案格式
        try {
            useCase.execute("invalid content".getBytes(), "test.txt");
        } catch (Exception e) {
            System.out.println("✅ 不支援格式處理正確: " + e.getClass().getSimpleName());
            System.out.println("   訊息: " + e.getMessage());
        }
        
        // 測試檔案大小超限（模擬大檔案）
        try {
            byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
            useCase.execute(largeContent, "large.csv");
        } catch (Exception e) {
            System.out.println("✅ 檔案大小檢查正確: " + e.getClass().getSimpleName());
            System.out.println("   訊息: " + e.getMessage());
        }
        
        // 測試空檔案名
        try {
            useCase.execute("title,description\n".getBytes(), "");
        } catch (Exception e) {
            System.out.println("✅ 空檔案名檢查正確: " + e.getClass().getSimpleName());
            System.out.println("   訊息: " + e.getMessage());
        }
        
        // 測試 null 內容
        try {
            useCase.execute(null, "test.csv");
        } catch (Exception e) {
            System.out.println("✅ null 內容檢查正確: " + e.getClass().getSimpleName());
            System.out.println("   訊息: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    private static void showFinalStatistics(InMemoryTaskRepository repository) {
        System.out.println("📊 最終統計資訊");
        System.out.println("─".repeat(50));
        System.out.println("總任務數: " + repository.size());
        
        if (repository.size() > 0) {
            System.out.println("\n匯入的任務清單:");
            repository.findAll().forEach(task -> {
                System.out.printf("• ID: %s | 標題: %s | 優先級: %s%n", 
                    task.getId().getValue().substring(0, 8) + "...",
                    task.getTitle(),
                    task.getPriority());
            });
        }
    }
    
    private static void printImportResult(String title, ImportResult result) {
        System.out.println(title + ":");
        System.out.printf("  📈 總計: %d | ✅ 成功: %d | ❌ 失敗: %d%n", 
            result.getTotalCount(), result.getSuccessCount(), result.getFailureCount());
        System.out.printf("  📊 成功率: %.2f%%%n", result.getSuccessRate() * 100);
        
        if (result.hasErrors()) {
            System.out.println("  🚨 錯誤訊息:");
            result.getErrorMessages().forEach(error -> System.out.println("     • " + error));
        }
        
        if (!result.getSuccessfulTasks().isEmpty()) {
            System.out.println("  ✅ 成功匯入的任務:");
            result.getSuccessfulTasks().stream()
                .limit(3) // 只顯示前3個
                .forEach(task -> System.out.printf("     • %s (%s)%n", 
                    task.getTitle(), task.getPriority()));
            
            if (result.getSuccessfulTasks().size() > 3) {
                System.out.println("     • ... 還有 " + (result.getSuccessfulTasks().size() - 3) + " 個任務");
            }
        }
    }
    
    private static String createSampleCsvContent() {
        return "title,description,priority,dueDate\n" +
               "示範任務1,這是第一個示範任務,HIGH,2025-12-31 23:59:59\n" +
               "示範任務2,這是第二個示範任務,MEDIUM,\n" +
               "示範任務3,這是第三個示範任務,LOW,2025-06-15 10:00:00\n";
    }
    
    private static String createSampleJsonContent() {
        return "[\n" +
               "  {\"title\":\"JSON 示範任務1\",\"description\":\"JSON 格式的第一個任務\",\"priority\":\"HIGH\",\"dueDate\":\"2025-12-31 23:59:59\"},\n" +
               "  {\"title\":\"JSON 示範任務2\",\"description\":\"JSON 格式的第二個任務\",\"priority\":\"MEDIUM\"},\n" +
               "  {\"title\":\"JSON 示範任務3\",\"description\":\"JSON 格式的第三個任務\",\"priority\":\"LOW\",\"dueDate\":\"2025-06-15 10:00:00\"}\n" +
               "]";
    }
}