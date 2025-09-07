# 字串處理與基礎API (String Processing and Basic APIs)

## 📖 學習目標

通過本章學習，你將掌握：
- String 類別的特性和內部機制
- 字串的創建、比較和操作方法
- StringBuilder 和 StringBuffer 的使用
- Scanner 類別進行輸入處理
- 格式化輸出的各種方法
- 正則表達式的基本應用
- 字串處理的效能考量

---

## 🔍 String 類別基礎

### String 的特性

Java 中的 String 具有以下重要特性：

- **不可變性（Immutable）**：String 物件一旦創建就無法修改
- **字串常量池（String Pool）**：JVM 維護一個字串常量池來優化記憶體
- **物件性質**：String 是參考型別，不是基本型別
- **UTF-16 編碼**：內部使用 UTF-16 編碼存儲字符

### String 的創建方式

```java
// 1. 字串字面量（推薦）
String str1 = "Hello World";

// 2. 使用 new 關鍵字
String str2 = new String("Hello World");

// 3. 字符陣列構造
char[] chars = {'H', 'e', 'l', 'l', 'o'};
String str3 = new String(chars);

// 4. 字節陣列構造
byte[] bytes = {72, 101, 108, 108, 111};
String str4 = new String(bytes);

// 5. StringBuilder 轉換
StringBuilder sb = new StringBuilder("Hello");
String str5 = sb.toString();
```

### 字串常量池機制

```java
// 字串字面量會放入常量池
String s1 = "Hello";
String s2 = "Hello";
System.out.println(s1 == s2);  // true，指向同一個物件

// new String() 會在堆記憶體創建新物件
String s3 = new String("Hello");
System.out.println(s1 == s3);  // false，不同物件

// intern() 方法可以將字串放入常量池
String s4 = s3.intern();
System.out.println(s1 == s4);  // true，指向常量池中的物件
```

---

## 🔧 String 基本操作

### 字串比較

```java
/**
 * 字串比較方法
 */
String str1 = "Hello";
String str2 = "hello";
String str3 = "Hello";

// 1. equals() - 比較內容
System.out.println(str1.equals(str3));        // true
System.out.println(str1.equals(str2));        // false

// 2. equalsIgnoreCase() - 忽略大小寫比較
System.out.println(str1.equalsIgnoreCase(str2)); // true

// 3. compareTo() - 字典序比較
System.out.println(str1.compareTo(str2));     // 負數
System.out.println(str1.compareTo(str3));     // 0

// 4. compareToIgnoreCase() - 忽略大小寫的字典序比較
System.out.println(str1.compareToIgnoreCase(str2)); // 0

// 5. == 比較（比較參考，不推薦用於內容比較）
System.out.println(str1 == str3);             // 可能為 true（字串池）
```

### 字串搜尋和檢查

```java
String text = "Java Programming Language";

// 查找字符或子字串
System.out.println(text.indexOf('a'));           // 1
System.out.println(text.indexOf("Programming")); // 5
System.out.println(text.lastIndexOf('a'));       // 23

// 檢查前綴和後綴
System.out.println(text.startsWith("Java"));     // true
System.out.println(text.endsWith("Language"));   // true

// 檢查是否包含
System.out.println(text.contains("Program"));    // true

// 檢查是否為空
System.out.println("".isEmpty());                // true
System.out.println("  ".isEmpty());              // false
System.out.println("  ".isBlank());              // true (Java 11+)
```

### 字串提取和分割

```java
String sentence = "Java is powerful and versatile";

// 提取子字串
System.out.println(sentence.substring(5));       // "is powerful and versatile"
System.out.println(sentence.substring(5, 7));    // "is"

// 分割字串
String[] words = sentence.split(" ");
System.out.println(Arrays.toString(words));      // [Java, is, powerful, and, versatile]

// 使用正則表達式分割
String data = "apple,banana;orange:grape";
String[] fruits = data.split("[,;:]");
System.out.println(Arrays.toString(fruits));     // [apple, banana, orange, grape]

// 限制分割數量
String[] limited = sentence.split(" ", 3);
System.out.println(Arrays.toString(limited));    // [Java, is, powerful and versatile]
```

### 字串修改操作

```java
String original = "  Java Programming  ";

// 大小寫轉換
System.out.println(original.toLowerCase());      // "  java programming  "
System.out.println(original.toUpperCase());      // "  JAVA PROGRAMMING  "

// 去除空白
System.out.println(original.trim());             // "Java Programming"
System.out.println(original.strip());            // "Java Programming" (Java 11+)

// 替換
System.out.println(original.replace("Java", "Python"));     // "  Python Programming  "
System.out.println(original.replaceAll("\\s+", "_"));       // "_Java_Programming_"
System.out.println(original.replaceFirst("a", "A"));        // "  JAva Programming  "

// 字符提取
System.out.println(original.charAt(2));          // 'J'
char[] chars = original.toCharArray();
System.out.println(Arrays.toString(chars));
```

---

## 🔨 StringBuilder 和 StringBuffer

### 為什麼需要 StringBuilder？

由於 String 的不可變性，頻繁的字串連接會創建大量臨時物件：

```java
// 效率低下的做法
String result = "";
for (int i = 0; i < 1000; i++) {
    result += "a";  // 每次都創建新的 String 物件
}

// 高效的做法
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append("a");  // 在內部可變緩衝區中操作
}
String result = sb.toString();
```

### StringBuilder 基本操作

```java
/**
 * StringBuilder 使用示例
 */
public class StringBuilderExample {
    public static void demonstrateStringBuilder() {
        // 創建 StringBuilder
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder("Initial content");
        StringBuilder sb3 = new StringBuilder(50);  // 指定初始容量
        
        // 追加內容
        sb.append("Hello");
        sb.append(" ");
        sb.append("World");
        sb.append('!');
        sb.append(42);
        sb.append(3.14);
        
        System.out.println(sb.toString());  // "Hello World!423.14"
        
        // 插入內容
        sb.insert(5, " Beautiful");
        System.out.println(sb.toString());  // "Hello Beautiful World!423.14"
        
        // 刪除內容
        sb.delete(5, 15);  // 刪除 " Beautiful"
        System.out.println(sb.toString());  // "Hello World!423.14"
        
        sb.deleteCharAt(sb.length() - 1);  // 刪除最後一個字符
        System.out.println(sb.toString());  // "Hello World!423.1"
        
        // 替換內容
        sb.replace(6, 11, "Java");  // 將 "World" 替換為 "Java"
        System.out.println(sb.toString());  // "Hello Java!423.1"
        
        // 反轉字串
        sb.reverse();
        System.out.println(sb.toString());  // "1.324!avaJ olleH"
        
        // 設置字符
        sb.setCharAt(0, '2');
        System.out.println(sb.toString());  // "2.324!avaJ olleH"
        
        // 長度和容量
        System.out.println("長度: " + sb.length());
        System.out.println("容量: " + sb.capacity());
        
        // 重置 StringBuilder
        sb.setLength(0);  // 清空內容但保留容量
        System.out.println("清空後長度: " + sb.length());
    }
}
```

### StringBuffer vs StringBuilder

| 特性 | StringBuilder | StringBuffer |
|------|---------------|--------------|
| 執行緒安全 | 否 | 是 |
| 效能 | 更快 | 較慢（同步開銷） |
| 使用場景 | 單執行緒環境 | 多執行緒環境 |
| 引入版本 | Java 5 | Java 1.0 |

```java
// 單執行緒環境推薦使用 StringBuilder
StringBuilder sb = new StringBuilder();

// 多執行緒環境使用 StringBuffer
StringBuffer buffer = new StringBuffer();
```

---

## 📥 Scanner 輸入處理

### Scanner 基本使用

```java
import java.util.Scanner;

/**
 * Scanner 輸入處理示例
 */
public class ScannerExample {
    public static void demonstrateBasicInput() {
        // 創建 Scanner 物件（記得關閉）
        try (Scanner scanner = new Scanner(System.in)) {
            
            System.out.print("請輸入您的姓名: ");
            String name = scanner.nextLine();
            
            System.out.print("請輸入您的年齡: ");
            int age = scanner.nextInt();
            
            System.out.print("請輸入您的薪水: ");
            double salary = scanner.nextDouble();
            
            System.out.print("您是學生嗎? (true/false): ");
            boolean isStudent = scanner.nextBoolean();
            
            System.out.println("\n=== 輸入摘要 ===");
            System.out.println("姓名: " + name);
            System.out.println("年齡: " + age);
            System.out.println("薪水: " + salary);
            System.out.println("學生: " + isStudent);
        }
    }
}
```

### Scanner 進階功能

```java
/**
 * Scanner 進階使用
 */
public class AdvancedScannerExample {
    
    public static void demonstrateAdvancedFeatures() {
        String input = "John,25,Engineer,50000.0\nAlice,30,Manager,75000.0";
        
        try (Scanner scanner = new Scanner(input)) {
            // 設置分隔符
            scanner.useDelimiter(",|\\n");
            
            while (scanner.hasNext()) {
                String name = scanner.next();
                int age = scanner.nextInt();
                String job = scanner.next();
                double salary = scanner.nextDouble();
                
                System.out.printf("員工: %s, 年齡: %d, 職位: %s, 薪水: %.2f%n",
                                name, age, job, salary);
            }
        }
    }
    
    public static void demonstrateValidation() {
        try (Scanner scanner = new Scanner(System.in)) {
            
            // 驗證整數輸入
            System.out.print("請輸入一個整數 (1-100): ");
            while (!scanner.hasNextInt()) {
                System.out.print("無效輸入！請輸入一個整數: ");
                scanner.next(); // 消費無效輸入
            }
            
            int number = scanner.nextInt();
            while (number < 1 || number > 100) {
                System.out.print("數字必須在 1-100 之間，請重新輸入: ");
                while (!scanner.hasNextInt()) {
                    System.out.print("無效輸入！請輸入一個整數: ");
                    scanner.next();
                }
                number = scanner.nextInt();
            }
            
            System.out.println("您輸入的數字是: " + number);
            
            // 驗證郵箱格式
            scanner.nextLine(); // 消費換行符
            System.out.print("請輸入您的郵箱: ");
            String email = scanner.nextLine();
            
            while (!isValidEmail(email)) {
                System.out.print("郵箱格式不正確，請重新輸入: ");
                email = scanner.nextLine();
            }
            
            System.out.println("郵箱驗證成功: " + email);
        }
    }
    
    private static boolean isValidEmail(String email) {
        return email != null && 
               email.contains("@") && 
               email.contains(".") &&
               email.indexOf('@') < email.lastIndexOf('.');
    }
}
```

### 從文件讀取數據

```java
import java.io.File;
import java.io.FileNotFoundException;

/**
 * 從文件讀取數據
 */
public class FileScanner {
    
    public static void readFromFile(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            
            int lineNumber = 1;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                System.out.println("第 " + lineNumber + " 行: " + line);
                lineNumber++;
            }
            
        } catch (FileNotFoundException e) {
            System.err.println("文件未找到: " + filename);
        }
    }
    
    public static void parseCSVData(String csvData) {
        try (Scanner scanner = new Scanner(csvData)) {
            
            // 讀取表頭
            if (scanner.hasNextLine()) {
                String header = scanner.nextLine();
                System.out.println("表頭: " + header);
            }
            
            // 讀取數據行
            int rowNumber = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");
                
                System.out.printf("第 %d 行數據: %s%n", rowNumber, 
                                Arrays.toString(fields));
                rowNumber++;
            }
        }
    }
}
```

---

## 📊 格式化輸出

### printf 格式化

```java
/**
 * printf 格式化輸出示例
 */
public class PrintfExample {
    
    public static void demonstratePrintfFormatting() {
        String name = "Alice";
        int age = 25;
        double salary = 50000.789;
        boolean married = true;
        
        // 基本格式化
        System.out.printf("姓名: %s%n", name);
        System.out.printf("年齡: %d%n", age);
        System.out.printf("薪水: %.2f%n", salary);
        System.out.printf("已婚: %b%n", married);
        
        // 寬度和對齊
        System.out.printf("%-10s | %5d | %10.2f%n", name, age, salary);
        System.out.printf("%-10s | %5d | %10.2f%n", "Bob", 30, 75000.0);
        
        // 數字格式化
        int number = 12345;
        System.out.printf("十進制: %d%n", number);
        System.out.printf("八進制: %o%n", number);
        System.out.printf("十六進制: %x%n", number);
        System.out.printf("十六進制(大寫): %X%n", number);
        
        // 浮點數格式化
        double pi = Math.PI;
        System.out.printf("PI: %f%n", pi);           // 3.141593
        System.out.printf("PI: %.2f%n", pi);         // 3.14
        System.out.printf("PI: %e%n", pi);           // 3.141593e+00
        System.out.printf("PI: %g%n", pi);           // 3.14159
        
        // 填充零
        System.out.printf("編號: %05d%n", 42);       // 00042
        System.out.printf("價格: %08.2f%n", 123.4);  // 00123.40
        
        // 正負號
        System.out.printf("正數: %+d%n", 42);        // +42
        System.out.printf("負數: %+d%n", -42);       // -42
        
        // 千分位分隔符
        System.out.printf("大數: %,d%n", 1234567);   // 1,234,567
    }
}
```

### String.format() 方法

```java
/**
 * String.format() 使用示例
 */
public class StringFormatExample {
    
    public static void demonstrateStringFormat() {
        // 基本格式化
        String formatted = String.format("Hello, %s! You are %d years old.", 
                                        "Alice", 25);
        System.out.println(formatted);
        
        // 創建表格
        String header = String.format("%-10s | %-5s | %-10s", "Name", "Age", "Salary");
        String separator = String.format("%-10s-+-%-5s-+-%-10s", 
                                        "----------", "-----", "----------");
        String row1 = String.format("%-10s | %-5d | %-10.2f", "Alice", 25, 50000.0);
        String row2 = String.format("%-10s | %-5d | %-10.2f", "Bob", 30, 75000.0);
        
        System.out.println(header);
        System.out.println(separator);
        System.out.println(row1);
        System.out.println(row2);
        
        // 格式化日期時間
        Date now = new Date();
        String dateFormatted = String.format("現在時間: %tY-%tm-%td %tH:%tM:%tS", 
                                            now, now, now, now, now, now);
        System.out.println(dateFormatted);
        
        // 更簡潔的日期格式化
        String dateSimple = String.format("現在時間: %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", now);
        System.out.println(dateSimple);
    }
}
```

### DecimalFormat 數字格式化

```java
import java.text.DecimalFormat;

/**
 * DecimalFormat 數字格式化
 */
public class DecimalFormatExample {
    
    public static void demonstrateDecimalFormat() {
        double[] numbers = {123.456, 1234.5, 0.123, 1234567.89};
        
        // 不同的格式模式
        String[] patterns = {
            "#.##",           // 最多兩位小數
            "0.00",           // 固定兩位小數
            "#,##0.00",       // 千分位分隔符
            "¥#,##0.00",      // 貨幣格式
            "#.##%",          // 百分比格式
            "0.00E0"          // 科學記數法
        };
        
        for (String pattern : patterns) {
            DecimalFormat df = new DecimalFormat(pattern);
            System.out.println("格式: " + pattern);
            
            for (double number : numbers) {
                System.out.printf("  %10.3f -> %s%n", number, df.format(number));
            }
            System.out.println();
        }
    }
}
```

---

## 🔍 正則表達式基礎

### Pattern 和 Matcher

```java
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 正則表達式使用示例
 */
public class RegexExample {
    
    public static void demonstrateBasicRegex() {
        String text = "聯絡我們: john@example.com 或 alice@test.org";
        
        // 編譯正則表達式
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = emailPattern.matcher(text);
        
        // 查找所有匹配
        System.out.println("找到的郵箱地址:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group());
            System.out.println("    位置: " + matcher.start() + "-" + matcher.end());
        }
        
        // 驗證單個字串
        String email = "user@domain.com";
        boolean isValid = emailPattern.matcher(email).matches();
        System.out.println(email + " 是否有效: " + isValid);
    }
    
    public static void demonstrateStringRegexMethods() {
        String text = "Java程式設計123很有趣456";
        
        // matches() - 整個字串匹配
        System.out.println("是否只包含字母: " + text.matches("[a-zA-Z]+"));
        
        // replaceAll() - 替換所有匹配
        String noNumbers = text.replaceAll("\\d+", "");
        System.out.println("移除數字: " + noNumbers);
        
        // replaceFirst() - 替換第一個匹配
        String replaceFirst = text.replaceFirst("\\d+", "[數字]");
        System.out.println("替換第一個數字: " + replaceFirst);
        
        // split() - 使用正則表達式分割
        String data = "apple123banana456orange789";
        String[] fruits = data.split("\\d+");
        System.out.println("分割結果: " + Arrays.toString(fruits));
    }
    
    public static void demonstrateValidation() {
        // 常用驗證模式
        Map<String, String> patterns = new HashMap<>();
        patterns.put("郵箱", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        patterns.put("手機號碼", "1[3-9]\\d{9}");
        patterns.put("身份證號", "\\d{17}[\\dXx]");
        patterns.put("郵政編碼", "\\d{6}");
        patterns.put("密碼", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$");
        
        // 測試數據
        Map<String, String[]> testData = new HashMap<>();
        testData.put("郵箱", new String[]{"user@example.com", "invalid-email", "test@test.org"});
        testData.put("手機號碼", new String[]{"13812345678", "12345678901", "15987654321"});
        testData.put("密碼", new String[]{"Password123", "password", "PASSWORD123", "Pass123!"});
        
        for (String type : patterns.keySet()) {
            System.out.println("\n=== " + type + " 驗證 ===");
            Pattern pattern = Pattern.compile(patterns.get(type));
            
            if (testData.containsKey(type)) {
                for (String test : testData.get(type)) {
                    boolean valid = pattern.matcher(test).matches();
                    System.out.printf("%-15s: %s%n", test, valid ? "有效" : "無效");
                }
            }
        }
    }
}
```

### 群組捕獲

```java
/**
 * 正則表達式群組捕獲
 */
public class RegexGroupExample {
    
    public static void demonstrateGroups() {
        String text = "生日: 1990-05-15, 電話: 010-12345678";
        
        // 日期群組捕獲
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher dateMatcher = datePattern.matcher(text);
        
        if (dateMatcher.find()) {
            System.out.println("完整日期: " + dateMatcher.group(0));  // 完整匹配
            System.out.println("年份: " + dateMatcher.group(1));      // 第一個群組
            System.out.println("月份: " + dateMatcher.group(2));      // 第二個群組
            System.out.println("日期: " + dateMatcher.group(3));      // 第三個群組
        }
        
        // 電話群組捕獲
        Pattern phonePattern = Pattern.compile("(\\d{3})-(\\d{8})");
        Matcher phoneMatcher = phonePattern.matcher(text);
        
        if (phoneMatcher.find()) {
            System.out.println("完整電話: " + phoneMatcher.group(0));
            System.out.println("區號: " + phoneMatcher.group(1));
            System.out.println("號碼: " + phoneMatcher.group(2));
        }
        
        // 命名群組 (Java 7+)
        Pattern namedPattern = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
        Matcher namedMatcher = namedPattern.matcher(text);
        
        if (namedMatcher.find()) {
            System.out.println("年份: " + namedMatcher.group("year"));
            System.out.println("月份: " + namedMatcher.group("month"));
            System.out.println("日期: " + namedMatcher.group("day"));
        }
    }
}
```

---

## ⚡ 效能考量

### 字串操作效能比較

```java
/**
 * 字串操作效能測試
 */
public class StringPerformanceTest {
    
    public static void compareStringConcatenation() {
        int iterations = 10000;
        
        // 測試 String 連接
        long startTime = System.currentTimeMillis();
        String result1 = "";
        for (int i = 0; i < iterations; i++) {
            result1 += "a";
        }
        long stringTime = System.currentTimeMillis() - startTime;
        
        // 測試 StringBuilder
        startTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");
        }
        String result2 = sb.toString();
        long sbTime = System.currentTimeMillis() - startTime;
        
        // 測試 StringBuffer
        startTime = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < iterations; i++) {
            buffer.append("a");
        }
        String result3 = buffer.toString();
        long bufferTime = System.currentTimeMillis() - startTime;
        
        System.out.println("效能比較 (" + iterations + " 次操作):");
        System.out.println("String +:        " + stringTime + " ms");
        System.out.println("StringBuilder:   " + sbTime + " ms");
        System.out.println("StringBuffer:    " + bufferTime + " ms");
        
        System.out.println("\n結果長度確認:");
        System.out.println("String: " + result1.length());
        System.out.println("StringBuilder: " + result2.length());
        System.out.println("StringBuffer: " + result3.length());
    }
    
    public static void demonstrateBestPractices() {
        System.out.println("=== 字串處理最佳實踐 ===");
        
        // 1. 使用字串字面量而不是 new String()
        String good = "Hello World";           // 推薦
        String bad = new String("Hello World"); // 不推薦
        
        // 2. 使用 StringBuilder 進行多次字串操作
        StringBuilder sb = new StringBuilder();
        sb.append("Hello").append(" ").append("World");
        
        // 3. 預先分配足夠的容量
        StringBuilder sbWithCapacity = new StringBuilder(100);
        
        // 4. 使用 String.join() 連接集合
        List<String> words = Arrays.asList("Java", "is", "awesome");
        String joined = String.join(" ", words);
        System.out.println("使用 join(): " + joined);
        
        // 5. 使用 intern() 節省記憶體（適用於大量重複字串）
        String s1 = new String("repeated").intern();
        String s2 = new String("repeated").intern();
        System.out.println("intern() 後相同參考: " + (s1 == s2));
        
        // 6. 避免不必要的字串操作
        String text = "Hello World";
        if (text.startsWith("Hello")) {  // 不需要 substring 再比較
            System.out.println("以 Hello 開頭");
        }
    }
}
```

### 記憶體使用最佳化

```java
/**
 * 字串記憶體使用最佳化
 */
public class StringMemoryOptimization {
    
    public static void demonstrateStringPool() {
        // 字串常量池的使用
        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = "He" + "llo";      // 編譯時常量
        String s4 = new String("Hello").intern();
        
        System.out.println("字串常量池測試:");
        System.out.println("s1 == s2: " + (s1 == s2));  // true
        System.out.println("s1 == s3: " + (s1 == s3));  // true
        System.out.println("s1 == s4: " + (s1 == s4));  // true
        
        // 動態字串不會自動進入常量池
        String prefix = "He";
        String s5 = prefix + "llo";    // 運行時連接
        System.out.println("s1 == s5: " + (s1 == s5));  // false
        System.out.println("s1 == s5.intern(): " + (s1 == s5.intern()));  // true
    }
    
    public static void demonstrateStringBuilderCapacity() {
        // StringBuilder 容量管理
        StringBuilder sb1 = new StringBuilder();        // 默認容量 16
        StringBuilder sb2 = new StringBuilder(100);     // 指定容量 100
        
        System.out.println("默認容量: " + sb1.capacity());
        System.out.println("指定容量: " + sb2.capacity());
        
        // 容量不足時會自動擴展
        for (int i = 0; i < 20; i++) {
            sb1.append("a");
            if (i == 15 || i == 16 || i == 17) {
                System.out.println("添加第 " + (i+1) + " 個字符後容量: " + sb1.capacity());
            }
        }
    }
}
```

---

## 🎯 實際應用場景

### 文本處理工具

```java
/**
 * 實用的文本處理工具類
 */
public class TextProcessor {
    
    /**
     * 統計文本信息
     */
    public static void analyzeText(String text) {
        if (text == null || text.isEmpty()) {
            System.out.println("文本為空");
            return;
        }
        
        System.out.println("=== 文本分析結果 ===");
        System.out.println("總字符數: " + text.length());
        System.out.println("非空字符數: " + text.replace(" ", "").length());
        
        // 統計行數
        String[] lines = text.split("\\r?\\n");
        System.out.println("行數: " + lines.length);
        
        // 統計單詞數（簡單版，以空白分隔）
        String[] words = text.trim().split("\\s+");
        System.out.println("單詞數: " + (text.trim().isEmpty() ? 0 : words.length));
        
        // 統計字符頻率
        Map<Character, Integer> charCount = new HashMap<>();
        for (char c : text.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                charCount.put(c, charCount.getOrDefault(c, 0) + 1);
            }
        }
        
        System.out.println("最常見的字符:");
        charCount.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> System.out.println("  '" + entry.getKey() + "': " + entry.getValue() + " 次"));
    }
    
    /**
     * 格式化文本
     */
    public static String formatText(String text, int lineWidth) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > lineWidth) {
                if (currentLine.length() > 0) {
                    result.append(currentLine.toString().trim()).append("\n");
                    currentLine.setLength(0);
                }
            }
            
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        
        if (currentLine.length() > 0) {
            result.append(currentLine.toString().trim());
        }
        
        return result.toString();
    }
    
    /**
     * 移除HTML標籤
     */
    public static String removeHtmlTags(String html) {
        if (html == null) {
            return null;
        }
        
        // 簡單的HTML標籤移除（不適用於複雜HTML）
        return html.replaceAll("<[^>]+>", "").trim();
    }
    
    /**
     * 駝峰命名轉換
     */
    public static String toCamelCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            } else {
                capitalizeNext = true;
            }
        }
        
        return result.toString();
    }
    
    /**
     * 生成縮寫
     */
    public static String generateAbbreviation(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder abbrev = new StringBuilder();
        String[] words = text.split("\\s+");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                abbrev.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        
        return abbrev.toString();
    }
}
```

### 配置文件解析器

```java
/**
 * 簡單的配置文件解析器
 */
public class ConfigParser {
    private Map<String, String> properties = new HashMap<>();
    
    /**
     * 從字串解析配置
     */
    public void parseConfig(String configText) {
        if (configText == null) {
            return;
        }
        
        try (Scanner scanner = new Scanner(configText)) {
            int lineNumber = 1;
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                
                // 跳過空行和註釋
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    lineNumber++;
                    continue;
                }
                
                // 解析 key=value 格式
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        
                        // 移除引號
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        
                        properties.put(key, value);
                    }
                } else {
                    System.err.println("第 " + lineNumber + " 行格式錯誤: " + line);
                }
                
                lineNumber++;
            }
        }
    }
    
    /**
     * 獲取字串配置值
     */
    public String getString(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
    
    /**
     * 獲取整數配置值
     */
    public int getInt(String key, int defaultValue) {
        String value = properties.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("配置項 " + key + " 不是有效的整數: " + value);
            }
        }
        return defaultValue;
    }
    
    /**
     * 獲取布爾配置值
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.get(key);
        if (value != null) {
            return "true".equalsIgnoreCase(value) || 
                   "yes".equalsIgnoreCase(value) ||
                   "1".equals(value);
        }
        return defaultValue;
    }
    
    /**
     * 顯示所有配置
     */
    public void printAll() {
        System.out.println("=== 配置信息 ===");
        properties.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println(entry.getKey() + " = " + entry.getValue()));
    }
}
```

---

## 🎓 學習檢查點

完成本章學習後，你應該能夠：

### ✅ 基本能力
- [ ] 理解 String 類別的不可變性和字串常量池
- [ ] 熟練使用 String 的各種操作方法
- [ ] 能夠使用 StringBuilder 進行高效的字串構建
- [ ] 掌握 Scanner 進行各種輸入處理

### ✅ 進階能力
- [ ] 熟練使用 printf 和 String.format 進行格式化輸出
- [ ] 理解和應用基本的正則表達式
- [ ] 能夠處理文本數據和配置文件
- [ ] 了解字串操作的效能考量

### ✅ 深入理解
- [ ] 能夠優化字串操作的記憶體使用
- [ ] 能夠設計文本處理工具
- [ ] 理解不同字串操作方法的適用場景
- [ ] 能夠處理複雜的文本解析需求

---

## 🚀 下一步學習

掌握了字串處理與基礎API後，建議繼續學習：

1. **[輸入輸出流](../io-streams/)** - 學習文件操作和數據流處理
2. **[異常處理](../exception-handling/)** - 掌握錯誤處理機制
3. **[集合框架進階](../arrays-and-collections/)** - 深入學習數據結構應用
4. **[日期時間API](../date-time-api/)** - 學習 Java 8+ 的新日期時間處理

**記住：字串處理是程式設計的基礎技能，熟練掌握它對日常開發工作至關重要！** 🎯