# 類別設計與建構

## 學習目標
- 掌握Java類別的基本結構
- 理解建構子的作用和使用方式
- 學會設計合理的類別架構
- 掌握方法重載概念

## 核心概念

### 1. 類別的基本結構
```java
public class ClassName {
    // 屬性 (Fields)
    private dataType fieldName;
    
    // 建構子 (Constructors)
    public ClassName() {
        // 預設建構子
    }
    
    public ClassName(parameters) {
        // 帶參數建構子
    }
    
    // 方法 (Methods)
    public returnType methodName(parameters) {
        // 方法實作
    }
}
```

### 2. 建構子設計原則
- 每個類別至少要有一個建構子
- 建構子負責初始化物件狀態
- 可以有多個建構子（建構子重載）
- 使用this()呼叫其他建構子

### 3. 存取修飾符
- `public`: 公開存取
- `private`: 僅類別內部存取
- `protected`: 套件內和子類別存取
- 預設（無修飾符）: 套件內存取

### 4. 方法重載 (Method Overloading)
- 同一個類別中有多個同名方法
- 參數列表必須不同（型別、數量、順序）
- 回傳型別可以相同或不同

## 設計最佳實務

### 1. 屬性封裝
```java
private String name;  // 私有屬性

public String getName() {    // Getter
    return name;
}

public void setName(String name) {  // Setter
    if (name != null && !name.trim().isEmpty()) {
        this.name = name;
    }
}
```

### 2. 建構子鏈接
```java
public Student() {
    this("未知", 0);  // 呼叫另一個建構子
}

public Student(String name, int score) {
    this.name = name;
    this.score = score;
}
```

### 3. 驗證邏輯
```java
public void setScore(int score) {
    if (score >= 0 && score <= 100) {
        this.score = score;
    } else {
        throw new IllegalArgumentException("分數必須在0-100之間");
    }
}
```

## 實務練習

### 練習1：學生管理系統
設計Student類別，包含完整的屬性管理和驗證。

### 練習2：銀行帳戶系統
設計BankAccount類別，實現安全的金額操作。

### 練習3：圖書館藏書系統
設計Book類別，展示完整的類別設計原則。

## 編譯與執行
```bash
javac Student.java
java Student
```