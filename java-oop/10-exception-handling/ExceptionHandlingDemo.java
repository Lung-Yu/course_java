/**
 * Java 例外處理機制示範
 * 展示例外處理的核心概念、最佳實務和實際應用
 */

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ================================
// 自訂例外類別
// ================================

/**
 * 用戶相關例外
 */
class UserException extends Exception {
    public UserException(String message) {
        super(message);
    }
    
    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * 無效用戶例外
 */
class InvalidUserException extends UserException {
    public InvalidUserException(String message) {
        super("無效用戶: " + message);
    }
}

/**
 * 用戶未找到例外
 */
class UserNotFoundException extends UserException {
    public UserNotFoundException(String userId) {
        super("找不到用戶: " + userId);
    }
}

/**
 * 餘額不足例外
 */
class InsufficientBalanceException extends RuntimeException {
    private double currentBalance;
    private double requestedAmount;
    
    public InsufficientBalanceException(double currentBalance, double requestedAmount) {
        super(String.format("餘額不足: 當前餘額 $%.2f, 請求金額 $%.2f", 
                          currentBalance, requestedAmount));
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }
    
    public double getCurrentBalance() { return currentBalance; }
    public double getRequestedAmount() { return requestedAmount; }
}

/**
 * 業務邏輯例外
 */
class BusinessLogicException extends Exception {
    private String errorCode;
    
    public BusinessLogicException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() { return errorCode; }
}

// ================================
// 示範類別
// ================================

/**
 * 用戶類別
 */
class User {
    private String userId;
    private String name;
    private String email;
    private double balance;
    
    public User(String userId, String name, String email, double balance) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.balance = balance;
    }
    
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public double getBalance() { return balance; }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', balance=%.2f}", 
                           userId, name, email, balance);
    }
}

/**
 * 用戶管理器 - 示範各種例外處理情況
 */
class UserManager {
    private Map<String, User> users;
    private Set<String> validDomains;
    
    public UserManager() {
        users = new HashMap<>();
        validDomains = Set.of("gmail.com", "yahoo.com", "company.com");
        
        // 初始化一些測試用戶
        initializeTestUsers();
    }
    
    private void initializeTestUsers() {
        try {
            addUser("user001", "張三", "zhang@gmail.com", 1000.0);
            addUser("user002", "李四", "li@yahoo.com", 500.0);
            addUser("user003", "王五", "wang@company.com", 2000.0);
        } catch (InvalidUserException e) {
            System.err.println("初始化用戶時發生錯誤: " + e.getMessage());
        }
    }
    
    /**
     * 新增用戶 - 示範檢查型例外
     */
    public void addUser(String userId, String name, String email, double balance) 
            throws InvalidUserException {
        
        // 驗證用戶ID
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUserException("用戶ID不能為空");
        }
        
        if (users.containsKey(userId)) {
            throw new InvalidUserException("用戶ID已存在: " + userId);
        }
        
        // 驗證姓名
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserException("姓名不能為空");
        }
        
        // 驗證email
        validateEmail(email);
        
        // 驗證餘額
        if (balance < 0) {
            throw new InvalidUserException("初始餘額不能為負數");
        }
        
        User user = new User(userId, name, email, balance);
        users.put(userId, user);
        System.out.println("用戶新增成功: " + user);
    }
    
    /**
     * 驗證email格式
     */
    private void validateEmail(String email) throws InvalidUserException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserException("Email不能為空");
        }
        
        if (!email.contains("@")) {
            throw new InvalidUserException("Email格式不正確: " + email);
        }
        
        String domain = email.substring(email.indexOf("@") + 1);
        if (!validDomains.contains(domain)) {
            throw new InvalidUserException("不支援的Email域名: " + domain);
        }
    }
    
    /**
     * 查找用戶 - 示範檢查型例外
     */
    public User findUser(String userId) throws UserNotFoundException {
        if (userId == null) {
            throw new IllegalArgumentException("用戶ID不能為null");
        }
        
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        
        return user;
    }
    
    /**
     * 轉帳操作 - 示範多種例外處理
     */
    public void transfer(String fromUserId, String toUserId, double amount) 
            throws UserNotFoundException, BusinessLogicException {
        
        // 驗證金額
        if (amount <= 0) {
            throw new IllegalArgumentException("轉帳金額必須大於0");
        }
        
        // 查找用戶（可能拋出UserNotFoundException）
        User fromUser = findUser(fromUserId);
        User toUser = findUser(toUserId);
        
        // 檢查餘額（運行時例外）
        if (fromUser.getBalance() < amount) {
            throw new InsufficientBalanceException(fromUser.getBalance(), amount);
        }
        
        // 業務邏輯驗證
        if (fromUserId.equals(toUserId)) {
            throw new BusinessLogicException("SAME_USER", "不能轉帳給自己");
        }
        
        if (amount > 10000) {
            throw new BusinessLogicException("AMOUNT_LIMIT", "單次轉帳金額不能超過$10,000");
        }
        
        // 執行轉帳
        fromUser.setBalance(fromUser.getBalance() - amount);
        toUser.setBalance(toUser.getBalance() + amount);
        
        System.out.printf("轉帳成功: %s -> %s, 金額: $%.2f%n", 
                         fromUser.getName(), toUser.getName(), amount);
    }
    
    /**
     * 取得所有用戶
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}

/**
 * 檔案處理器 - 示範資源管理和例外處理
 */
class FileProcessor {
    
    /**
     * 安全讀取檔案 - 使用try-with-resources
     */
    public static String safeReadFile(String fileName) {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            System.out.println("檔案讀取成功: " + fileName);
            
        } catch (FileNotFoundException e) {
            System.err.println("檔案不存在: " + fileName);
            return null;
        } catch (IOException e) {
            System.err.println("讀取檔案時發生錯誤: " + e.getMessage());
            return null;
        }
        
        return content.toString();
    }
    
    /**
     * 安全寫入檔案
     */
    public static boolean safeWriteFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            writer.flush();
            System.out.println("檔案寫入成功: " + fileName);
            return true;
            
        } catch (IOException e) {
            System.err.println("寫入檔案時發生錯誤: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 處理多個檔案操作
     */
    public static void processMultipleFiles(String[] fileNames) {
        List<String> successFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        
        for (String fileName : fileNames) {
            try {
                String content = safeReadFile(fileName);
                if (content != null) {
                    // 處理檔案內容
                    processFileContent(content);
                    successFiles.add(fileName);
                }
            } catch (Exception e) {
                System.err.println("處理檔案失敗 " + fileName + ": " + e.getMessage());
                failedFiles.add(fileName);
            }
        }
        
        System.out.println("\n檔案處理結果:");
        System.out.println("成功: " + successFiles.size() + " 個檔案");
        System.out.println("失敗: " + failedFiles.size() + " 個檔案");
    }
    
    private static void processFileContent(String content) throws Exception {
        if (content == null || content.trim().isEmpty()) {
            throw new Exception("檔案內容為空");
        }
        
        // 模擬處理邏輯
        System.out.println("處理檔案內容，長度: " + content.length());
    }
}

/**
 * 計算機類別 - 示範運行時例外處理
 */
class Calculator {
    
    /**
     * 安全除法運算
     */
    public static double safeDivide(double a, double b) {
        try {
            if (b == 0) {
                throw new ArithmeticException("除數不能為零");
            }
            return a / b;
        } catch (ArithmeticException e) {
            System.err.println("計算錯誤: " + e.getMessage());
            return Double.NaN;  // 返回NaN表示無效結果
        }
    }
    
    /**
     * 安全開根號運算
     */
    public static double safeSqrt(double number) {
        if (number < 0) {
            throw new IllegalArgumentException("不能對負數開根號: " + number);
        }
        return Math.sqrt(number);
    }
    
    /**
     * 安全數字解析
     */
    public static Optional<Integer> safeParseInt(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            System.err.println("無法解析為整數: " + str);
            return Optional.empty();
        }
    }
    
    /**
     * 批次處理數字字串
     */
    public static void processBatchNumbers(String[] numberStrings) {
        List<Integer> validNumbers = new ArrayList<>();
        List<String> invalidNumbers = new ArrayList<>();
        
        for (String numStr : numberStrings) {
            Optional<Integer> number = safeParseInt(numStr);
            if (number.isPresent()) {
                validNumbers.add(number.get());
            } else {
                invalidNumbers.add(numStr);
            }
        }
        
        System.out.println("有效數字: " + validNumbers);
        System.out.println("無效數字: " + invalidNumbers);
        
        if (!validNumbers.isEmpty()) {
            int sum = validNumbers.stream().mapToInt(Integer::intValue).sum();
            double average = validNumbers.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            System.out.println("總和: " + sum + ", 平均: " + String.format("%.2f", average));
        }
    }
}

/**
 * 日誌記錄器 - 示範例外記錄
 */
class ExceptionLogger {
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void logException(Exception e) {
        logException(e, "未知操作");
    }
    
    public static void logException(Exception e, String operation) {
        System.err.println("=== 例外記錄 ===");
        System.err.println("時間: " + LocalDateTime.now().format(FORMATTER));
        System.err.println("操作: " + operation);
        System.err.println("例外類型: " + e.getClass().getSimpleName());
        System.err.println("錯誤訊息: " + e.getMessage());
        
        if (e.getCause() != null) {
            System.err.println("根本原因: " + e.getCause().getMessage());
        }
        
        System.err.println("堆疊追蹤:");
        e.printStackTrace();
        System.err.println("================\n");
    }
}

/**
 * 主要測試類別
 */
public class ExceptionHandlingDemo {
    
    /**
     * 示範基本例外處理
     */
    private static void demonstrateBasicExceptionHandling() {
        System.out.println("=== 基本例外處理示範 ===");
        
        // 1. 運行時例外處理
        try {
            int[] array = {1, 2, 3};
            System.out.println("存取陣列元素: " + array[5]);  // 會拋出IndexOutOfBoundsException
        } catch (IndexOutOfBoundsException e) {
            System.err.println("陣列索引超出範圍: " + e.getMessage());
        }
        
        // 2. 空指標例外處理
        try {
            String str = null;
            System.out.println("字串長度: " + str.length());  // 會拋出NullPointerException
        } catch (NullPointerException e) {
            System.err.println("空指標錯誤: " + e.getMessage());
        }
        
        // 3. 數字格式例外處理
        String[] testNumbers = {"123", "abc", "456", "xyz", "789"};
        System.out.println("\n處理數字字串:");
        Calculator.processBatchNumbers(testNumbers);
        
        // 4. 算術例外處理
        System.out.println("\n安全除法運算:");
        System.out.println("10 / 3 = " + Calculator.safeDivide(10, 3));
        System.out.println("10 / 0 = " + Calculator.safeDivide(10, 0));
        
        System.out.println();
    }
    
    /**
     * 示範自訂例外處理
     */
    private static void demonstrateCustomExceptions() {
        System.out.println("=== 自訂例外處理示範 ===");
        
        UserManager userManager = new UserManager();
        
        // 1. 新增用戶時的驗證例外
        System.out.println("測試用戶新增驗證:");
        
        try {
            userManager.addUser("", "空ID用戶", "test@gmail.com", 100);
        } catch (InvalidUserException e) {
            System.err.println("新增用戶失敗: " + e.getMessage());
        }
        
        try {
            userManager.addUser("user004", "測試用戶", "invalid-email", 100);
        } catch (InvalidUserException e) {
            System.err.println("新增用戶失敗: " + e.getMessage());
        }
        
        try {
            userManager.addUser("user004", "測試用戶", "test@gmail.com", 100);
            System.out.println("用戶新增成功!");
        } catch (InvalidUserException e) {
            System.err.println("新增用戶失敗: " + e.getMessage());
        }
        
        // 2. 查找用戶例外
        System.out.println("\n測試用戶查找:");
        try {
            User user = userManager.findUser("nonexistent");
            System.out.println("找到用戶: " + user);
        } catch (UserNotFoundException e) {
            System.err.println("查找用戶失敗: " + e.getMessage());
        }
        
        // 3. 轉帳操作例外
        System.out.println("\n測試轉帳操作:");
        
        // 正常轉帳
        try {
            userManager.transfer("user001", "user002", 200);
        } catch (Exception e) {
            ExceptionLogger.logException(e, "正常轉帳");
        }
        
        // 餘額不足
        try {
            userManager.transfer("user002", "user001", 2000);
        } catch (InsufficientBalanceException e) {
            System.err.println("轉帳失敗: " + e.getMessage());
            System.err.println("當前餘額: $" + e.getCurrentBalance());
            System.err.println("請求金額: $" + e.getRequestedAmount());
        } catch (Exception e) {
            ExceptionLogger.logException(e, "餘額不足測試");
        }
        
        // 業務邏輯例外
        try {
            userManager.transfer("user001", "user001", 100);  // 轉給自己
        } catch (BusinessLogicException e) {
            System.err.println("業務邏輯錯誤 [" + e.getErrorCode() + "]: " + e.getMessage());
        } catch (Exception e) {
            ExceptionLogger.logException(e, "業務邏輯測試");
        }
        
        try {
            userManager.transfer("user001", "user003", 15000);  // 超過限額
        } catch (BusinessLogicException e) {
            System.err.println("業務邏輯錯誤 [" + e.getErrorCode() + "]: " + e.getMessage());
        } catch (Exception e) {
            ExceptionLogger.logException(e, "轉帳限額測試");
        }
        
        System.out.println();
    }
    
    /**
     * 示範檔案例外處理
     */
    private static void demonstrateFileExceptions() {
        System.out.println("=== 檔案例外處理示範 ===");
        
        // 建立測試檔案
        String testContent = "這是測試檔案內容\n第二行\n第三行";
        FileProcessor.safeWriteFile("test.txt", testContent);
        
        // 讀取存在的檔案
        String content = FileProcessor.safeReadFile("test.txt");
        if (content != null) {
            System.out.println("檔案內容長度: " + content.length());
        }
        
        // 讀取不存在的檔案
        FileProcessor.safeReadFile("nonexistent.txt");
        
        // 批次處理檔案
        String[] fileNames = {"test.txt", "nonexistent1.txt", "nonexistent2.txt"};
        FileProcessor.processMultipleFiles(fileNames);
        
        System.out.println();
    }
    
    /**
     * 示範例外鏈和詳細錯誤資訊
     */
    private static void demonstrateExceptionChaining() {
        System.out.println("=== 例外鏈示範 ===");
        
        try {
            // 模擬複雜的操作流程
            performComplexOperation();
        } catch (Exception e) {
            ExceptionLogger.logException(e, "複雜操作");
        }
        
        System.out.println();
    }
    
    private static void performComplexOperation() throws Exception {
        try {
            // 第一層操作
            riskyOperation1();
        } catch (RuntimeException e) {
            // 包裝例外並重新拋出
            throw new Exception("複雜操作在第一層失敗", e);
        }
    }
    
    private static void riskyOperation1() {
        try {
            // 第二層操作
            riskyOperation2();
        } catch (IllegalArgumentException e) {
            // 包裝例外並重新拋出
            throw new RuntimeException("第一層操作失敗", e);
        }
    }
    
    private static void riskyOperation2() {
        // 最底層的錯誤
        throw new IllegalArgumentException("最底層的參數錯誤");
    }
    
    /**
     * 示範finally區塊和資源清理
     */
    private static void demonstrateFinallyBlock() {
        System.out.println("=== Finally區塊示範 ===");
        
        FileInputStream fis = null;
        try {
            System.out.println("嘗試開啟檔案...");
            fis = new FileInputStream("nonexistent.txt");
            System.out.println("檔案開啟成功");
            
        } catch (FileNotFoundException e) {
            System.err.println("檔案不存在: " + e.getMessage());
            
        } finally {
            System.out.println("執行清理工作...");
            if (fis != null) {
                try {
                    fis.close();
                    System.out.println("檔案已關閉");
                } catch (IOException e) {
                    System.err.println("關閉檔案時發生錯誤: " + e.getMessage());
                }
            }
            System.out.println("清理工作完成");
        }
        
        System.out.println("方法執行完成\n");
    }
    
    public static void main(String[] args) {
        System.out.println("=== Java 例外處理機制示範 ===\n");
        
        demonstrateBasicExceptionHandling();
        demonstrateCustomExceptions();
        demonstrateFileExceptions();
        demonstrateExceptionChaining();
        demonstrateFinallyBlock();
        
        System.out.println("=== 例外處理示範完成 ===");
        
        // 清理測試檔案
        new java.io.File("test.txt").delete();
    }
}