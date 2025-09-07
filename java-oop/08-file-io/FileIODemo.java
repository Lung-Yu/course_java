/**
 * 檔案讀寫與I/O操作示範
 * 展示Java中各種檔案處理方式和最佳實務
 */

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日誌管理器示範類別
 * 展示檔案寫入、追加、緩衝等概念
 */
class LogManager {
    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR
    }
    
    private String logFileName;
    private LogLevel currentLogLevel;
    private DateTimeFormatter dateFormatter;
    
    public LogManager(String logFileName) {
        this.logFileName = logFileName;
        this.currentLogLevel = LogLevel.INFO;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // 確保日誌目錄存在
        createLogDirectoryIfNotExists();
    }
    
    private void createLogDirectoryIfNotExists() {
        try {
            Path logPath = Paths.get(logFileName).getParent();
            if (logPath != null && !Files.exists(logPath)) {
                Files.createDirectories(logPath);
                System.out.println("建立日誌目錄: " + logPath);
            }
        } catch (IOException e) {
            System.err.println("無法建立日誌目錄: " + e.getMessage());
        }
    }
    
    public void setLogLevel(LogLevel level) {
        this.currentLogLevel = level;
        logInternal(LogLevel.INFO, "日誌等級已設定為: " + level);
    }
    
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }
    
    public void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }
    
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }
    
    public void error(String message, Exception e) {
        log(LogLevel.ERROR, message + " - 例外: " + e.getMessage());
    }
    
    private void log(LogLevel level, String message) {
        if (level.ordinal() >= currentLogLevel.ordinal()) {
            logInternal(level, message);
        }
    }
    
    private void logInternal(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String logEntry = String.format("[%s] %s: %s", timestamp, level, message);
        
        // 同時輸出到控制台和檔案
        System.out.println(logEntry);
        writeToFile(logEntry);
    }
    
    private void writeToFile(String logEntry) {
        // 使用try-with-resources確保資源正確關閉
        try (FileWriter fw = new FileWriter(logFileName, true);  // true表示追加模式
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            bw.write(logEntry);
            bw.newLine();
            bw.flush();  // 確保資料立即寫入
            
        } catch (IOException e) {
            System.err.println("寫入日誌檔案失敗: " + e.getMessage());
        }
    }
    
    /**
     * 讀取日誌檔案內容
     */
    public List<String> readLogFile() {
        List<String> logs = new ArrayList<>();
        
        try (FileReader fr = new FileReader(logFileName);
             BufferedReader br = new BufferedReader(fr)) {
            
            String line;
            while ((line = br.readLine()) != null) {
                logs.add(line);
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("日誌檔案不存在: " + logFileName);
        } catch (IOException e) {
            System.err.println("讀取日誌檔案失敗: " + e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * 清空日誌檔案
     */
    public void clearLog() {
        try (FileWriter fw = new FileWriter(logFileName, false)) {
            // 以覆寫模式開啟檔案，什麼都不寫就是清空
            System.out.println("日誌檔案已清空");
        } catch (IOException e) {
            System.err.println("清空日誌檔案失敗: " + e.getMessage());
        }
    }
}

/**
 * 配置檔案管理器
 * 示範Properties檔案的讀寫
 */
class ConfigFileManager {
    private String configFileName;
    private Properties properties;
    
    public ConfigFileManager(String configFileName) {
        this.configFileName = configFileName;
        this.properties = new Properties();
        loadConfig();
    }
    
    /**
     * 載入配置檔案
     */
    public void loadConfig() {
        try (FileInputStream fis = new FileInputStream(configFileName);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            
            properties.load(bis);
            System.out.println("配置檔案載入成功: " + configFileName);
            
        } catch (FileNotFoundException e) {
            System.out.println("配置檔案不存在，將建立預設配置: " + configFileName);
            createDefaultConfig();
        } catch (IOException e) {
            System.err.println("載入配置檔案失敗: " + e.getMessage());
            createDefaultConfig();
        }
    }
    
    /**
     * 建立預設配置
     */
    private void createDefaultConfig() {
        properties.setProperty("database.host", "localhost");
        properties.setProperty("database.port", "3306");
        properties.setProperty("database.name", "myapp");
        properties.setProperty("cache.enabled", "true");
        properties.setProperty("cache.size", "1000");
        properties.setProperty("log.level", "INFO");
        
        saveConfig();
    }
    
    /**
     * 儲存配置檔案
     */
    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(configFileName);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            properties.store(bos, "應用程式配置檔案 - " + LocalDateTime.now());
            System.out.println("配置檔案儲存成功");
            
        } catch (IOException e) {
            System.err.println("儲存配置檔案失敗: " + e.getMessage());
        }
    }
    
    /**
     * 取得配置值
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * 設定配置值
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        System.out.println("配置已更新: " + key + " = " + value);
    }
    
    /**
     * 顯示所有配置
     */
    public void displayAllProperties() {
        System.out.println("=== 當前配置 ===");
        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + " = " + properties.getProperty(key));
        }
        System.out.println("================");
    }
}

/**
 * 檔案工具類別
 * 示範各種檔案操作方法
 */
class FileUtils {
    
    /**
     * 複製檔案（使用位元組流）
     */
    public static void copyFile(String sourceFile, String targetFile) {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             FileOutputStream fos = new FileOutputStream(targetFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            byte[] buffer = new byte[8192];  // 8KB緩衝區
            int bytesRead;
            long totalBytes = 0;
            
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            
            System.out.println("檔案複製完成: " + sourceFile + " -> " + targetFile);
            System.out.println("複製位元組數: " + totalBytes);
            
        } catch (IOException e) {
            System.err.println("檔案複製失敗: " + e.getMessage());
        }
    }
    
    /**
     * 讀取整個檔案為字串
     */
    public static String readFileAsString(String fileName) {
        StringBuilder content = new StringBuilder();
        
        try (FileReader fr = new FileReader(fileName);
             BufferedReader br = new BufferedReader(fr)) {
            
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            
        } catch (IOException e) {
            System.err.println("讀取檔案失敗: " + e.getMessage());
            return null;
        }
        
        return content.toString();
    }
    
    /**
     * 將字串寫入檔案
     */
    public static void writeStringToFile(String fileName, String content) {
        try (FileWriter fw = new FileWriter(fileName);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            bw.write(content);
            System.out.println("內容已寫入檔案: " + fileName);
            
        } catch (IOException e) {
            System.err.println("寫入檔案失敗: " + e.getMessage());
        }
    }
    
    /**
     * 使用NIO.2的現代檔案操作
     */
    public static void modernFileOperations(String fileName) {
        try {
            Path path = Paths.get(fileName);
            
            // 檢查檔案是否存在
            if (Files.exists(path)) {
                System.out.println("檔案存在: " + fileName);
                
                // 取得檔案資訊
                long size = Files.size(path);
                System.out.println("檔案大小: " + size + " 位元組");
                
                // 讀取所有行
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                System.out.println("檔案行數: " + lines.size());
                
                // 顯示前3行
                System.out.println("前3行內容:");
                lines.stream().limit(3).forEach(line -> System.out.println("  " + line));
                
            } else {
                System.out.println("檔案不存在: " + fileName);
                
                // 建立檔案並寫入內容
                List<String> sampleContent = Arrays.asList(
                    "這是使用NIO.2建立的檔案",
                    "第二行內容",
                    "第三行內容"
                );
                Files.write(path, sampleContent, StandardCharsets.UTF_8);
                System.out.println("檔案已建立: " + fileName);
            }
            
        } catch (IOException e) {
            System.err.println("檔案操作失敗: " + e.getMessage());
        }
    }
    
    /**
     * 取得檔案資訊
     */
    public static void getFileInfo(String fileName) {
        File file = new File(fileName);
        
        if (file.exists()) {
            System.out.println("=== 檔案資訊 ===");
            System.out.println("檔案名稱: " + file.getName());
            System.out.println("絕對路徑: " + file.getAbsolutePath());
            System.out.println("檔案大小: " + file.length() + " 位元組");
            System.out.println("是否為目錄: " + file.isDirectory());
            System.out.println("是否可讀: " + file.canRead());
            System.out.println("是否可寫: " + file.canWrite());
            System.out.println("最後修改時間: " + new Date(file.lastModified()));
            System.out.println("================");
        } else {
            System.out.println("檔案不存在: " + fileName);
        }
    }
}

/**
 * CSV檔案處理器
 * 示範結構化資料的檔案處理
 */
class CSVProcessor {
    
    /**
     * 讀取CSV檔案
     */
    public static List<String[]> readCSV(String fileName) {
        List<String[]> records = new ArrayList<>();
        
        try (FileReader fr = new FileReader(fileName);
             BufferedReader br = new BufferedReader(fr)) {
            
            String line;
            while ((line = br.readLine()) != null) {
                // 簡單的CSV解析（不處理複雜情況如引號內的逗號）
                String[] fields = line.split(",");
                // 移除每個欄位的前後空格
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }
                records.add(fields);
            }
            
            System.out.println("CSV檔案讀取完成，記錄數: " + records.size());
            
        } catch (IOException e) {
            System.err.println("讀取CSV檔案失敗: " + e.getMessage());
        }
        
        return records;
    }
    
    /**
     * 寫入CSV檔案
     */
    public static void writeCSV(String fileName, List<String[]> records) {
        try (FileWriter fw = new FileWriter(fileName);
             BufferedWriter bw = new BufferedWriter(fw)) {
            
            for (String[] record : records) {
                bw.write(String.join(",", record));
                bw.newLine();
            }
            
            System.out.println("CSV檔案寫入完成，記錄數: " + records.size());
            
        } catch (IOException e) {
            System.err.println("寫入CSV檔案失敗: " + e.getMessage());
        }
    }
    
    /**
     * 建立範例CSV檔案
     */
    public static void createSampleCSV(String fileName) {
        List<String[]> sampleData = Arrays.asList(
            new String[]{"姓名", "年齡", "科系", "分數"},
            new String[]{"張三", "20", "資訊工程", "85"},
            new String[]{"李四", "21", "電機工程", "92"},
            new String[]{"王五", "19", "機械工程", "78"},
            new String[]{"趙六", "22", "化學工程", "88"}
        );
        
        writeCSV(fileName, sampleData);
    }
}

/**
 * 主要測試類別
 */
public class FileIODemo {
    public static void main(String[] args) {
        System.out.println("=== Java 檔案讀寫與I/O操作示範 ===\n");
        
        // 建立測試目錄
        String testDir = "demo_files";
        new File(testDir).mkdirs();
        
        // ================================
        // 1. 日誌管理器測試
        // ================================
        System.out.println("=== 1. 日誌管理器測試 ===");
        
        LogManager logger = new LogManager(testDir + "/app.log");
        logger.setLogLevel(LogManager.LogLevel.DEBUG);
        
        logger.debug("這是除錯訊息");
        logger.info("應用程式啟動");
        logger.warning("這是警告訊息");
        logger.error("這是錯誤訊息");
        
        try {
            // 模擬一個例外
            int result = 10 / 0;
        } catch (Exception e) {
            logger.error("計算錯誤", e);
        }
        
        // 讀取日誌內容
        System.out.println("\n讀取日誌檔案內容:");
        List<String> logEntries = logger.readLogFile();
        logEntries.forEach(entry -> System.out.println("  " + entry));
        
        // ================================
        // 2. 配置檔案管理器測試
        // ================================
        System.out.println("\n=== 2. 配置檔案管理器測試 ===");
        
        ConfigFileManager config = new ConfigFileManager(testDir + "/app.properties");
        config.displayAllProperties();
        
        // 修改配置
        config.setProperty("database.host", "192.168.1.100");
        config.setProperty("cache.size", "2000");
        config.saveConfig();
        
        System.out.println("\n修改後的配置:");
        config.displayAllProperties();
        
        // ================================
        // 3. 檔案工具測試
        // ================================
        System.out.println("\n=== 3. 檔案工具測試 ===");
        
        // 建立測試檔案
        String testContent = "這是測試檔案的內容\n第二行內容\n第三行內容";
        String sourceFile = testDir + "/test.txt";
        String targetFile = testDir + "/test_copy.txt";
        
        FileUtils.writeStringToFile(sourceFile, testContent);
        
        // 取得檔案資訊
        FileUtils.getFileInfo(sourceFile);
        
        // 複製檔案
        FileUtils.copyFile(sourceFile, targetFile);
        
        // 讀取檔案內容
        String readContent = FileUtils.readFileAsString(targetFile);
        System.out.println("讀取的檔案內容:");
        System.out.println(readContent);
        
        // 現代檔案操作
        FileUtils.modernFileOperations(testDir + "/modern_file.txt");
        
        // ================================
        // 4. CSV檔案處理測試
        // ================================
        System.out.println("\n=== 4. CSV檔案處理測試 ===");
        
        String csvFile = testDir + "/students.csv";
        
        // 建立範例CSV檔案
        CSVProcessor.createSampleCSV(csvFile);
        
        // 讀取CSV檔案
        List<String[]> csvData = CSVProcessor.readCSV(csvFile);
        
        System.out.println("CSV檔案內容:");
        for (String[] record : csvData) {
            System.out.println("  " + String.join(" | ", record));
        }
        
        // 新增一筆記錄並重新寫入
        csvData.add(new String[]{"錢七", "20", "生物工程", "95"});
        CSVProcessor.writeCSV(csvFile, csvData);
        
        System.out.println("\n=== 檔案I/O操作示範完成 ===");
        System.out.println("測試檔案位於: " + new File(testDir).getAbsolutePath());
    }
}