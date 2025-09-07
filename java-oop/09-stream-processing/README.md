# 串流處理 (Stream Processing)

## 學習目標
- 掌握Java 8 Stream API的核心概念
- 理解函數式程式設計範式
- 熟練使用Lambda表達式
- 學會使用Stream進行資料處理和轉換

## 核心概念

### 1. Stream API 簡介
Stream API是Java 8引入的重要特性，提供了聲明式的資料處理方式：
- **聲明式**：專注於「做什麼」而非「怎麼做」
- **函數式**：使用函數作為參數，支援鏈式操作
- **並行化**：輕鬆實現並行處理

### 2. Stream的特性
- **不是資料結構**：Stream不儲存資料，而是對資料來源的抽象
- **惰性求值**：中間操作是惰性的，只有遇到終端操作才會執行
- **可消費性**：Stream只能使用一次，使用後即被消費

### 3. Stream操作分類

#### 中間操作 (Intermediate Operations)
- `filter()` - 篩選
- `map()` - 轉換
- `flatMap()` - 扁平化映射
- `distinct()` - 去重
- `sorted()` - 排序
- `limit()` - 限制數量
- `skip()` - 跳過元素

#### 終端操作 (Terminal Operations)
- `forEach()` - 遍歷
- `collect()` - 收集
- `reduce()` - 歸約
- `count()` - 計數
- `anyMatch()`, `allMatch()`, `noneMatch()` - 匹配
- `findFirst()`, `findAny()` - 查找

### 4. Lambda表達式語法
```java
// 無參數
() -> System.out.println("Hello")

// 單一參數
x -> x * 2

// 多個參數
(x, y) -> x + y

// 複雜邏輯（需要大括號）
x -> {
    if (x > 0) return x * 2;
    else return 0;
}
```

## 實務應用

### 1. 基本篩選和轉換
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// 篩選長度大於3的名字並轉為大寫
List<String> result = names.stream()
    .filter(name -> name.length() > 3)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### 2. 複雜的資料處理
```java
// 分組統計
Map<String, Long> groupCount = students.stream()
    .collect(Collectors.groupingBy(
        Student::getMajor,
        Collectors.counting()
    ));
```

### 3. 並行處理
```java
// 使用parallelStream()進行並行處理
long count = data.parallelStream()
    .filter(item -> item.isValid())
    .mapToInt(Item::getValue)
    .sum();
```

## 實務練習

### 練習1：學生成績分析
使用Stream API分析學生成績資料，包括平均分、及格率等統計。

### 練習2：商品銷售分析
處理銷售資料，計算各類別商品的銷售總額和排名。

### 練習3：日誌分析系統
使用Stream處理大量日誌資料，提取有用資訊。

## 最佳實務
1. 優先使用Stream API而非傳統迴圈
2. 避免在Stream中使用有副作用的操作
3. 合理使用並行Stream，注意執行緒安全
4. 使用方法引用提高程式碼可讀性

## 編譯與執行
```bash
javac StreamProcessingDemo.java
java StreamProcessingDemo
```