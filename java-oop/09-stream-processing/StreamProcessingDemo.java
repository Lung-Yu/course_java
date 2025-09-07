/**
 * Java Stream API 與 Lambda 表達式示範
 * 展示現代Java函數式程式設計的核心概念
 */

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.time.LocalDate;
import java.time.Month;

/**
 * 學生類別 - 用於示範Stream操作
 */
class Student {
    private String name;
    private int age;
    private String major;
    private double score;
    private LocalDate enrollmentDate;
    
    public Student(String name, int age, String major, double score, LocalDate enrollmentDate) {
        this.name = name;
        this.age = age;
        this.major = major;
        this.score = score;
        this.enrollmentDate = enrollmentDate;
    }
    
    // Getter方法
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMajor() { return major; }
    public double getScore() { return score; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    
    public boolean isPass() { return score >= 60; }
    public String getGrade() {
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }
    
    @Override
    public String toString() {
        return String.format("%s(%d歲, %s, %.1f分)", name, age, major, score);
    }
}

/**
 * 產品類別 - 用於示範商業資料分析
 */
class Product {
    private String name;
    private String category;
    private double price;
    private int quantity;
    private LocalDate saleDate;
    
    public Product(String name, String category, double price, int quantity, LocalDate saleDate) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.saleDate = saleDate;
    }
    
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public LocalDate getSaleDate() { return saleDate; }
    public double getTotalSales() { return price * quantity; }
    
    @Override
    public String toString() {
        return String.format("%s[%s] $%.2f x %d = $%.2f", 
                           name, category, price, quantity, getTotalSales());
    }
}

/**
 * 日誌條目類別 - 用於示範日誌分析
 */
class LogEntry {
    private LocalDate date;
    private String level;
    private String message;
    private String source;
    
    public LogEntry(LocalDate date, String level, String message, String source) {
        this.date = date;
        this.level = level;
        this.message = message;
        this.source = source;
    }
    
    public LocalDate getDate() { return date; }
    public String getLevel() { return level; }
    public String getMessage() { return message; }
    public String getSource() { return source; }
    
    public boolean isError() { return "ERROR".equals(level); }
    public boolean isWarning() { return "WARNING".equals(level); }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s (%s)", date, level, message, source);
    }
}

/**
 * Stream API 示範類別
 */
public class StreamProcessingDemo {
    
    /**
     * 建立測試學生資料
     */
    private static List<Student> createStudentData() {
        return Arrays.asList(
            new Student("張三", 20, "資訊工程", 85.5, LocalDate.of(2022, 9, 1)),
            new Student("李四", 21, "電機工程", 92.0, LocalDate.of(2021, 9, 1)),
            new Student("王五", 19, "機械工程", 78.5, LocalDate.of(2023, 9, 1)),
            new Student("趙六", 22, "資訊工程", 88.0, LocalDate.of(2020, 9, 1)),
            new Student("錢七", 20, "化學工程", 95.5, LocalDate.of(2022, 9, 1)),
            new Student("孫八", 21, "電機工程", 67.0, LocalDate.of(2021, 9, 1)),
            new Student("周九", 19, "機械工程", 73.5, LocalDate.of(2023, 9, 1)),
            new Student("吳十", 23, "資訊工程", 91.0, LocalDate.of(2019, 9, 1)),
            new Student("鄭十一", 20, "化學工程", 82.5, LocalDate.of(2022, 9, 1)),
            new Student("王十二", 21, "電機工程", 76.0, LocalDate.of(2021, 9, 1))
        );
    }
    
    /**
     * 建立測試產品資料
     */
    private static List<Product> createProductData() {
        return Arrays.asList(
            new Product("iPhone 15", "電子產品", 30000, 5, LocalDate.of(2024, 1, 15)),
            new Product("MacBook Pro", "電子產品", 60000, 2, LocalDate.of(2024, 1, 20)),
            new Product("Nike 運動鞋", "服飾", 3500, 10, LocalDate.of(2024, 1, 18)),
            new Product("Adidas T恤", "服飾", 1200, 15, LocalDate.of(2024, 1, 22)),
            new Product("咖啡豆", "食品", 450, 20, LocalDate.of(2024, 1, 25)),
            new Product("有機茶葉", "食品", 800, 8, LocalDate.of(2024, 1, 28)),
            new Product("iPad Air", "電子產品", 20000, 3, LocalDate.of(2024, 2, 1)),
            new Product("Levi's 牛仔褲", "服飾", 2800, 7, LocalDate.of(2024, 2, 3)),
            new Product("巧克力", "食品", 250, 30, LocalDate.of(2024, 2, 5)),
            new Product("AirPods", "電子產品", 6000, 12, LocalDate.of(2024, 2, 8))
        );
    }
    
    /**
     * 建立測試日誌資料
     */
    private static List<LogEntry> createLogData() {
        return Arrays.asList(
            new LogEntry(LocalDate.of(2024, 1, 1), "INFO", "系統啟動", "MainApp"),
            new LogEntry(LocalDate.of(2024, 1, 1), "DEBUG", "載入配置檔案", "ConfigManager"),
            new LogEntry(LocalDate.of(2024, 1, 1), "WARNING", "記憶體使用率高", "MemoryMonitor"),
            new LogEntry(LocalDate.of(2024, 1, 2), "ERROR", "資料庫連線失敗", "DatabaseManager"),
            new LogEntry(LocalDate.of(2024, 1, 2), "INFO", "使用者登入", "AuthService"),
            new LogEntry(LocalDate.of(2024, 1, 2), "ERROR", "檔案讀取失敗", "FileService"),
            new LogEntry(LocalDate.of(2024, 1, 3), "WARNING", "API回應時間過長", "APIGateway"),
            new LogEntry(LocalDate.of(2024, 1, 3), "INFO", "定期備份完成", "BackupService"),
            new LogEntry(LocalDate.of(2024, 1, 3), "DEBUG", "快取清理", "CacheManager"),
            new LogEntry(LocalDate.of(2024, 1, 4), "ERROR", "外部服務無回應", "ExternalService")
        );
    }
    
    /**
     * 基本Stream操作示範
     */
    private static void demonstrateBasicOperations() {
        System.out.println("=== 基本Stream操作示範 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 篩選偶數
        System.out.println("原始數字: " + numbers);
        List<Integer> evenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("偶數: " + evenNumbers);
        
        // 轉換（平方）
        List<Integer> squares = numbers.stream()
                .map(n -> n * n)
                .collect(Collectors.toList());
        System.out.println("平方: " + squares);
        
        // 篩選和轉換的組合
        List<Integer> evenSquares = numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .collect(Collectors.toList());
        System.out.println("偶數的平方: " + evenSquares);
        
        // 統計操作
        OptionalDouble average = numbers.stream()
                .mapToInt(Integer::intValue)
                .average();
        System.out.println("平均值: " + average.orElse(0.0));
        
        int sum = numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("總和: " + sum);
        
        System.out.println();
    }
    
    /**
     * 學生資料分析示範
     */
    private static void demonstrateStudentAnalysis() {
        System.out.println("=== 學生資料分析示範 ===");
        
        List<Student> students = createStudentData();
        
        // 顯示所有學生
        System.out.println("所有學生:");
        students.forEach(System.out::println);
        
        // 篩選及格學生
        System.out.println("\n及格學生:");
        students.stream()
                .filter(Student::isPass)
                .forEach(System.out::println);
        
        // 按科系分組統計
        System.out.println("\n各科系學生數量:");
        Map<String, Long> majorCount = students.stream()
                .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
        majorCount.forEach((major, count) -> 
            System.out.println(major + ": " + count + "人"));
        
        // 計算各科系平均分數
        System.out.println("\n各科系平均分數:");
        Map<String, Double> majorAverage = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getMajor,
                    Collectors.averagingDouble(Student::getScore)
                ));
        majorAverage.forEach((major, avg) -> 
            System.out.println(major + ": " + String.format("%.2f", avg) + "分"));
        
        // 找出分數最高的學生
        Optional<Student> topStudent = students.stream()
                .max(Comparator.comparingDouble(Student::getScore));
        topStudent.ifPresent(s -> 
            System.out.println("\n分數最高的學生: " + s));
        
        // 按分數排序（前5名）
        System.out.println("\n前5名學生:");
        students.stream()
                .sorted(Comparator.comparingDouble(Student::getScore).reversed())
                .limit(5)
                .forEach(System.out::println);
        
        // 按等第分組
        System.out.println("\n按等第分組:");
        Map<String, List<Student>> gradeGroups = students.stream()
                .collect(Collectors.groupingBy(Student::getGrade));
        gradeGroups.forEach((grade, studentList) -> {
            System.out.println(grade + "等 (" + studentList.size() + "人): " + 
                studentList.stream().map(Student::getName).collect(Collectors.joining(", ")));
        });
        
        // 統計資訊
        DoubleSummaryStatistics stats = students.stream()
                .mapToDouble(Student::getScore)
                .summaryStatistics();
        System.out.println("\n分數統計:");
        System.out.println("  總人數: " + stats.getCount());
        System.out.println("  平均分: " + String.format("%.2f", stats.getAverage()));
        System.out.println("  最高分: " + stats.getMax());
        System.out.println("  最低分: " + stats.getMin());
        System.out.println("  總分: " + stats.getSum());
        
        System.out.println();
    }
    
    /**
     * 商品銷售分析示範
     */
    private static void demonstrateProductAnalysis() {
        System.out.println("=== 商品銷售分析示範 ===");
        
        List<Product> products = createProductData();
        
        // 總銷售額
        double totalSales = products.stream()
                .mapToDouble(Product::getTotalSales)
                .sum();
        System.out.println("總銷售額: $" + String.format("%.2f", totalSales));
        
        // 各類別銷售額
        System.out.println("\n各類別銷售額:");
        Map<String, Double> categorySales = products.stream()
                .collect(Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.summingDouble(Product::getTotalSales)
                ));
        categorySales.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> 
                    System.out.println(entry.getKey() + ": $" + 
                                     String.format("%.2f", entry.getValue())));
        
        // 銷售額前3名商品
        System.out.println("\n銷售額前3名商品:");
        products.stream()
                .sorted(Comparator.comparingDouble(Product::getTotalSales).reversed())
                .limit(3)
                .forEach(p -> System.out.println(p + " (銷售額: $" + 
                                               String.format("%.2f", p.getTotalSales()) + ")"));
        
        // 各類別中銷售額最高的商品
        System.out.println("\n各類別中銷售額最高的商品:");
        Map<String, Optional<Product>> topProductByCategory = products.stream()
                .collect(Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.maxBy(Comparator.comparingDouble(Product::getTotalSales))
                ));
        topProductByCategory.forEach((category, optProduct) -> 
            optProduct.ifPresent(p -> 
                System.out.println(category + ": " + p.getName() + 
                                 " ($" + String.format("%.2f", p.getTotalSales()) + ")")));
        
        // 價格區間分析
        System.out.println("\n商品價格區間分析:");
        Map<String, Long> priceRanges = products.stream()
                .collect(Collectors.groupingBy(p -> {
                    double price = p.getPrice();
                    if (price < 1000) return "低價 (<$1000)";
                    else if (price < 10000) return "中價 ($1000-$10000)";
                    else return "高價 (>$10000)";
                }, Collectors.counting()));
        priceRanges.forEach((range, count) -> 
            System.out.println(range + ": " + count + "項商品"));
        
        System.out.println();
    }
    
    /**
     * 日誌分析示範
     */
    private static void demonstrateLogAnalysis() {
        System.out.println("=== 日誌分析示範 ===");
        
        List<LogEntry> logs = createLogData();
        
        // 各等級日誌數量
        System.out.println("各等級日誌數量:");
        Map<String, Long> levelCount = logs.stream()
                .collect(Collectors.groupingBy(LogEntry::getLevel, Collectors.counting()));
        levelCount.forEach((level, count) -> 
            System.out.println(level + ": " + count + "條"));
        
        // 錯誤日誌
        System.out.println("\n錯誤日誌:");
        logs.stream()
                .filter(LogEntry::isError)
                .forEach(System.out::println);
        
        // 各來源系統的日誌數量
        System.out.println("\n各來源系統的日誌數量:");
        Map<String, Long> sourceCount = logs.stream()
                .collect(Collectors.groupingBy(LogEntry::getSource, Collectors.counting()));
        sourceCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> 
                    System.out.println(entry.getKey() + ": " + entry.getValue() + "條"));
        
        // 按日期分組日誌
        System.out.println("\n按日期分組日誌數量:");
        Map<LocalDate, Long> dateCount = logs.stream()
                .collect(Collectors.groupingBy(LogEntry::getDate, Collectors.counting()));
        dateCount.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> 
                    System.out.println(entry.getKey() + ": " + entry.getValue() + "條"));
        
        // 問題日誌（ERROR和WARNING）
        System.out.println("\n問題日誌數量:");
        long problemLogCount = logs.stream()
                .filter(log -> log.isError() || log.isWarning())
                .count();
        System.out.println("總問題日誌: " + problemLogCount + "條");
        
        System.out.println();
    }
    
    /**
     * 進階Stream操作示範
     */
    private static void demonstrateAdvancedOperations() {
        System.out.println("=== 進階Stream操作示範 ===");
        
        // flatMap示範
        List<List<String>> nestedList = Arrays.asList(
                Arrays.asList("A", "B", "C"),
                Arrays.asList("D", "E"),
                Arrays.asList("F", "G", "H", "I")
        );
        
        System.out.println("原始嵌套列表: " + nestedList);
        List<String> flatList = nestedList.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("扁平化後: " + flatList);
        
        // reduce操作示範
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        int sum = numbers.stream()
                .reduce(0, Integer::sum);
        System.out.println("\n數字總和 (reduce): " + sum);
        
        Optional<Integer> product = numbers.stream()
                .reduce((a, b) -> a * b);
        System.out.println("數字乘積: " + product.orElse(0));
        
        // 自訂Collector
        String joinedNumbers = numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" + ", "(", ") = " + sum));
        System.out.println("組合字串: " + joinedNumbers);
        
        // 分區（partitioning）
        List<Student> students = createStudentData();
        Map<Boolean, List<Student>> passPartition = students.stream()
                .collect(Collectors.partitioningBy(Student::isPass));
        
        System.out.println("\n學生及格分區:");
        System.out.println("及格學生數: " + passPartition.get(true).size());
        System.out.println("不及格學生數: " + passPartition.get(false).size());
        
        // 多層分組
        Map<String, Map<String, List<Student>>> multiGroup = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getMajor,
                    Collectors.groupingBy(Student::getGrade)
                ));
        
        System.out.println("\n多層分組（科系 -> 等第）:");
        multiGroup.forEach((major, gradeMap) -> {
            System.out.println(major + ":");
            gradeMap.forEach((grade, studentList) -> 
                System.out.println("  " + grade + "等: " + studentList.size() + "人"));
        });
        
        System.out.println();
    }
    
    /**
     * 並行處理示範
     */
    private static void demonstrateParallelProcessing() {
        System.out.println("=== 並行處理示範 ===");
        
        // 建立大量資料進行性能測試
        List<Integer> largeList = IntStream.rangeClosed(1, 1000000)
                .boxed()
                .collect(Collectors.toList());
        
        // 序列處理
        long startTime = System.currentTimeMillis();
        long sequentialSum = largeList.stream()
                .mapToLong(n -> n * n)
                .sum();
        long sequentialTime = System.currentTimeMillis() - startTime;
        
        // 並行處理
        startTime = System.currentTimeMillis();
        long parallelSum = largeList.parallelStream()
                .mapToLong(n -> n * n)
                .sum();
        long parallelTime = System.currentTimeMillis() - startTime;
        
        System.out.println("處理100萬個數字的平方和:");
        System.out.println("序列處理結果: " + sequentialSum + " (耗時: " + sequentialTime + "ms)");
        System.out.println("並行處理結果: " + parallelSum + " (耗時: " + parallelTime + "ms)");
        System.out.println("性能提升: " + String.format("%.2f", (double) sequentialTime / parallelTime) + "倍");
        
        // 注意事項：並行處理的執行緒安全
        System.out.println("\n並行處理注意事項:");
        System.out.println("1. 適合CPU密集型任務");
        System.out.println("2. 資料量要足夠大才有效果");
        System.out.println("3. 注意執行緒安全問題");
        System.out.println("4. 避免在並行流中使用有狀態的操作");
        
        System.out.println();
    }
    
    public static void main(String[] args) {
        System.out.println("=== Java Stream API 與 Lambda 表達式示範 ===\n");
        
        demonstrateBasicOperations();
        demonstrateStudentAnalysis();
        demonstrateProductAnalysis();
        demonstrateLogAnalysis();
        demonstrateAdvancedOperations();
        demonstrateParallelProcessing();
        
        System.out.println("=== Stream處理示範完成 ===");
    }
}