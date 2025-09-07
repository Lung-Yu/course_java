# 例外處理機制 (Exception Handling)

## 學習目標
- 理解Java例外處理的核心概念
- 掌握try-catch-finally語法結構
- 學會設計和使用自訂例外類別
- 了解checked與unchecked例外的區別
- 掌握例外處理的最佳實務

## 核心概念

### 1. Java例外處理架構
```
Throwable
├── Error (系統錯誤，不應被捕獲)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
└── Exception (應用程式例外)
    ├── RuntimeException (Unchecked Exceptions)
    │   ├── NullPointerException
    │   ├── IllegalArgumentException
    │   ├── IndexOutOfBoundsException
    │   └── NumberFormatException
    └── Checked Exceptions
        ├── IOException
        ├── SQLException
        ├── ClassNotFoundException
        └── 自訂例外類別
```

### 2. Checked vs Unchecked 例外

#### Checked Exceptions (編譯時例外)
- 必須在編譯時處理
- 必須用try-catch捕獲或用throws聲明
- 例如：IOException, SQLException

#### Unchecked Exceptions (執行時例外)
- 不強制在編譯時處理
- 繼承自RuntimeException
- 例如：NullPointerException, IllegalArgumentException

### 3. 例外處理語法

#### 基本語法
```java
try {
    // 可能拋出例外的程式碼
} catch (SpecificException e) {
    // 處理特定例外
} catch (Exception e) {
    // 處理其他例外
} finally {
    // 無論是否發生例外都會執行
}
```

#### Try-with-resources (Java 7+)
```java
try (FileReader file = new FileReader("data.txt")) {
    // 使用資源
} catch (IOException e) {
    // 處理例外
}
// 資源會自動關閉
```

### 4. 例外處理最佳實務

1. **具體捕獲**：捕獲最具體的例外類型
2. **早期失敗**：儘早檢測和報告錯誤
3. **資源管理**：確保資源正確關閉
4. **日誌記錄**：記錄例外資訊以便除錯
5. **不要忽略例外**：至少要記錄例外
6. **自訂例外**：為業務邏輯建立有意義的例外

## 實務應用

### 1. 基本例外處理
```java
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.err.println("除零錯誤: " + e.getMessage());
}
```

### 2. 多重例外捕獲
```java
try {
    // 可能拋出多種例外的程式碼
} catch (IOException | SQLException e) {
    // Java 7+ 語法
    handleException(e);
}
```

### 3. 自訂例外類別
```java
public class InvalidUserException extends Exception {
    public InvalidUserException(String message) {
        super(message);
    }
    
    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## 實務練習

### 練習1：檔案處理系統
建立健壯的檔案處理系統，妥善處理各種檔案操作例外。

### 練習2：用戶驗證系統
設計用戶驗證系統，使用自訂例外處理各種驗證失敗情況。

### 練習3：資料庫操作層
實作資料存取層，處理資料庫連線和操作例外。

## 編譯與執行
```bash
javac ExceptionHandlingDemo.java
java ExceptionHandlingDemo
```