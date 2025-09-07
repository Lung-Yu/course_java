# Map 介面與實作

## 學習目標

- 理解 Map 介面的鍵值對映射概念
- 掌握 HashMap、TreeMap、LinkedHashMap 的區別
- 學會選擇適合的 Map 實作類
- 熟練使用 Map 的各種操作方法
- 理解鍵的唯一性和 equals/hashCode 的重要性

## 1. Map 介面概述

Map 介面表示鍵值對的映射關係，每個鍵最多只能映射到一個值。Map 不是 Collection 的子介面，它是一個獨立的介面族系。

### Map 介面特性

- **鍵值對映射**：將鍵映射到值的物件
- **鍵的唯一性**：每個鍵最多只能出現一次
- **值可重複**：不同的鍵可以映射到相同的值
- **無索引存取**：不支援基於索引的操作

### 主要實作類比較

| 實作類 | 底層結構 | 排序 | 性能 | 特點 |
|--------|----------|------|------|------|
| HashMap | 雜湊表 | 無序 | O(1) | 最常用，性能最佳 |
| LinkedHashMap | 雜湊表+鏈表 | 插入/存取順序 | O(1) | 保持順序 |
| TreeMap | 紅黑樹 | 鍵排序 | O(log n) | 自動排序 |
| Hashtable | 雜湊表 | 無序 | O(1) | 線程安全，已過時 |

## 2. HashMap 詳解

HashMap 是最常用的 Map 實作，基於雜湊表實現，提供最佳的性能。

### 核心特性

- **底層結構**：雜湊表 (數組 + 鏈表 + 紅黑樹)
- **排序**：無序 (與插入順序無關)
- **NULL 值**：允許一個 null 鍵和多個 null 值
- **非線程安全**：需要外部同步

### 基本操作

```java
import java.util.*;

public class HashMapExample {
    public static void main(String[] args) {
        // 創建 HashMap
        Map<String, Integer> studentScores = new HashMap<>();
        
        // 添加鍵值對
        studentScores.put("張三", 85);
        studentScores.put("李四", 92);
        studentScores.put("王五", 78);
        studentScores.put("趙六", 88);
        
        System.out.println("學生成績: " + studentScores);
        
        // 獲取值
        Integer score = studentScores.get("張三");
        System.out.println("張三的成績: " + score);
        
        // 檢查是否包含鍵或值
        boolean hasStudent = studentScores.containsKey("李四");
        boolean hasScore = studentScores.containsValue(90);
        System.out.println("是否有李四: " + hasStudent);
        System.out.println("是否有90分: " + hasScore);
        
        // 更新值
        studentScores.put("張三", 90); // 覆蓋原值
        System.out.println("更新張三成績後: " + studentScores);
        
        // 安全獲取值
        Integer defaultScore = studentScores.getOrDefault("陳七", 0);
        System.out.println("陳七的成績 (預設0): " + defaultScore);
        
        // 條件性添加
        Integer oldValue = studentScores.putIfAbsent("陳七", 95);
        System.out.println("putIfAbsent 返回值: " + oldValue);
        System.out.println("添加陳七後: " + studentScores);
        
        // 移除鍵值對
        Integer removedScore = studentScores.remove("王五");
        System.out.println("移除的成績: " + removedScore);
        System.out.println("移除王五後: " + studentScores);
        
        // 大小和是否為空
        System.out.println("學生數量: " + studentScores.size());
        System.out.println("是否為空: " + studentScores.isEmpty());
    }
}
```

### HashMap 內部機制

```java
import java.util.*;

public class HashMapInternals {
    public static void main(String[] args) {
        // 演示雜湊衝突和解決
        demonstrateHashCollision();
        
        // 演示容量和負載因子
        demonstrateCapacityAndLoadFactor();
        
        // 演示 null 鍵值的處理
        demonstrateNullHandling();
    }
    
    public static void demonstrateHashCollision() {
        System.out.println("=== 雜湊衝突演示 ===\n");
        
        Map<String, String> map = new HashMap<>();
        
        // 這些字串可能產生雜湊衝突
        map.put("Aa", "Value1");
        map.put("BB", "Value2");  // "Aa".hashCode() == "BB".hashCode()
        
        System.out.println("Aa 的 hashCode: " + "Aa".hashCode());
        System.out.println("BB 的 hashCode: " + "BB".hashCode());
        System.out.println("雜湊碼相同但仍能正確存儲: " + map);
        
        // HashMap 使用 equals() 方法來區分具有相同雜湊碼的鍵
        System.out.println("Aa equals BB: " + "Aa".equals("BB"));
        System.out.println();
    }
    
    public static void demonstrateCapacityAndLoadFactor() {
        System.out.println("=== 容量和負載因子 ===\n");
        
        // 預設容量 16，負載因子 0.75
        Map<Integer, String> defaultMap = new HashMap<>();
        
        // 指定初始容量
        Map<Integer, String> customMap = new HashMap<>(32);
        
        // 指定初始容量和負載因子
        Map<Integer, String> customMap2 = new HashMap<>(32, 0.8f);
        
        System.out.println("建議：如果知道大致的元素數量，指定合適的初始容量可以提升性能");
        
        // 測試擴容
        Map<Integer, Integer> testMap = new HashMap<>(4); // 小容量便於觀察擴容
        
        for (int i = 0; i < 10; i++) {
            testMap.put(i, i * 10);
            System.out.println("添加元素 " + i + "，當前大小: " + testMap.size());
        }
        
        System.out.println();
    }
    
    public static void demonstrateNullHandling() {
        System.out.println("=== NULL 處理 ===\n");
        
        Map<String, String> map = new HashMap<>();
        
        // 添加 null 鍵
        map.put(null, "null鍵的值");
        
        // 添加 null 值
        map.put("key1", null);
        map.put("key2", null);  // 多個 null 值是允許的
        
        map.put("key3", "正常值");
        
        System.out.println("包含 null 的 Map: " + map);
        
        // 獲取 null 鍵的值
        String nullKeyValue = map.get(null);
        System.out.println("null 鍵的值: " + nullKeyValue);
        
        // 檢查是否包含 null 鍵
        boolean hasNullKey = map.containsKey(null);
        boolean hasNullValue = map.containsValue(null);
        System.out.println("包含 null 鍵: " + hasNullKey);
        System.out.println("包含 null 值: " + hasNullValue);
        
        System.out.println();
    }
}
```

## 3. TreeMap 詳解

TreeMap 基於紅黑樹實現，鍵會自動排序。

### 核心特性

- **底層結構**：紅黑樹 (自平衡二元搜尋樹)
- **排序**：鍵的自然順序或自定義 Comparator
- **NULL 值**：不允許 null 鍵，允許 null 值
- **導航方法**：提供範圍查詢功能

### 基本操作

```java
import java.util.*;

public class TreeMapExample {
    public static void main(String[] args) {
        // 創建 TreeMap (自然排序)
        TreeMap<String, Integer> cityPopulation = new TreeMap<>();
        
        // 添加數據 (會按鍵排序)
        cityPopulation.put("台北", 2600000);
        cityPopulation.put("高雄", 2750000);
        cityPopulation.put("台中", 2800000);
        cityPopulation.put("桃園", 2250000);
        cityPopulation.put("台南", 1880000);
        
        System.out.println("城市人口 (按城市名排序): " + cityPopulation);
        
        // TreeMap 特有的導航方法
        System.out.println("\n=== 導航方法 ===");
        System.out.println("第一個城市: " + cityPopulation.firstKey());
        System.out.println("最後一個城市: " + cityPopulation.lastKey());
        
        String targetCity = "台中";
        System.out.println("小於 " + targetCity + " 的最大鍵: " + cityPopulation.lowerKey(targetCity));
        System.out.println("大於 " + targetCity + " 的最小鍵: " + cityPopulation.higherKey(targetCity));
        System.out.println("小於等於 " + targetCity + " 的最大鍵: " + cityPopulation.floorKey(targetCity));
        System.out.println("大於等於 " + targetCity + " 的最小鍵: " + cityPopulation.ceilingKey(targetCity));
        
        // 子映射操作
        System.out.println("\n=== 子映射 ===");
        SortedMap<String, Integer> headMap = cityPopulation.headMap("台南");
        SortedMap<String, Integer> tailMap = cityPopulation.tailMap("台南");
        SortedMap<String, Integer> subMap = cityPopulation.subMap("台中", "高雄");
        
        System.out.println("字母順序小於 '台南' 的城市: " + headMap);
        System.out.println("字母順序大於等於 '台南' 的城市: " + tailMap);
        System.out.println("字母順序在 '台中' 和 '高雄' 之間的城市: " + subMap);
        
        // 移除第一個和最後一個
        System.out.println("\n=== 移除操作 ===");
        Map.Entry<String, Integer> firstEntry = cityPopulation.pollFirstEntry();
        Map.Entry<String, Integer> lastEntry = cityPopulation.pollLastEntry();
        
        System.out.println("移除的第一個條目: " + firstEntry);
        System.out.println("移除的最後一個條目: " + lastEntry);
        System.out.println("移除後的 Map: " + cityPopulation);
    }
}
```

### 自定義排序

```java
import java.util.*;

class Employee {
    private String name;
    private double salary;
    private int age;
    
    public Employee(String name, double salary, int age) {
        this.name = name;
        this.salary = salary;
        this.age = age;
    }
    
    // Getters
    public String getName() { return name; }
    public double getSalary() { return salary; }
    public int getAge() { return age; }
    
    @Override
    public String toString() {
        return String.format("Employee{name='%s', salary=%.0f, age=%d}", name, salary, age);
    }
}

public class TreeMapWithComparator {
    public static void main(String[] args) {
        // 按薪水降序排列
        TreeMap<Employee, String> employeesByHighSalary = new TreeMap<>(
            (e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary())
        );
        
        employeesByHighSalary.put(new Employee("張三", 50000, 28), "資深工程師");
        employeesByHighSalary.put(new Employee("李四", 60000, 32), "專案經理");
        employeesByHighSalary.put(new Employee("王五", 45000, 25), "初級工程師");
        employeesByHighSalary.put(new Employee("趙六", 75000, 35), "技術主管");
        
        System.out.println("按薪水降序排列:");
        employeesByHighSalary.forEach((emp, position) -> 
            System.out.println("  " + emp + " => " + position));
        
        // 按年齡升序排列
        TreeMap<Employee, String> employeesByAge = new TreeMap<>(
            Comparator.comparing(Employee::getAge)
        );
        
        employeesByAge.putAll(employeesByHighSalary);
        System.out.println("\n按年齡升序排列:");
        employeesByAge.forEach((emp, position) -> 
            System.out.println("  " + emp + " => " + position));
        
        // 複合排序：先按薪水，再按年齡
        TreeMap<Employee, String> employeesByMultipleCriteria = new TreeMap<>(
            Comparator.comparing(Employee::getSalary)
                     .thenComparing(Employee::getAge)
        );
        
        employeesByMultipleCriteria.putAll(employeesByHighSalary);
        System.out.println("\n按薪水和年齡排序:");
        employeesByMultipleCriteria.forEach((emp, position) -> 
            System.out.println("  " + emp + " => " + position));
    }
}
```

## 4. LinkedHashMap 詳解

LinkedHashMap 結合了 HashMap 的性能和有序性。

### 核心特性

- **底層結構**：雜湊表 + 雙向鏈表
- **排序**：插入順序或存取順序
- **性能**：略慢於 HashMap，但比 TreeMap 快
- **LRU 支援**：可實現 LRU 緩存

```java
import java.util.*;

public class LinkedHashMapExample {
    public static void main(String[] args) {
        // 演示插入順序
        demonstrateInsertionOrder();
        
        // 演示存取順序
        demonstrateAccessOrder();
        
        // 實現 LRU 緩存
        demonstrateLRUCache();
    }
    
    public static void demonstrateInsertionOrder() {
        System.out.println("=== 插入順序示範 ===\n");
        
        // 預設保持插入順序
        Map<String, Integer> insertionOrderMap = new LinkedHashMap<>();
        
        insertionOrderMap.put("第三個", 3);
        insertionOrderMap.put("第一個", 1);
        insertionOrderMap.put("第二個", 2);
        insertionOrderMap.put("第四個", 4);
        
        System.out.println("LinkedHashMap (插入順序): " + insertionOrderMap);
        
        // 對比 HashMap
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("第三個", 3);
        hashMap.put("第一個", 1);
        hashMap.put("第二個", 2);
        hashMap.put("第四個", 4);
        
        System.out.println("HashMap (無序): " + hashMap);
        System.out.println();
    }
    
    public static void demonstrateAccessOrder() {
        System.out.println("=== 存取順序示範 ===\n");
        
        // 設定為存取順序模式
        Map<String, Integer> accessOrderMap = new LinkedHashMap<>(16, 0.75f, true);
        
        accessOrderMap.put("A", 1);
        accessOrderMap.put("B", 2);
        accessOrderMap.put("C", 3);
        accessOrderMap.put("D", 4);
        
        System.out.println("初始順序: " + accessOrderMap);
        
        // 存取一些元素
        accessOrderMap.get("B");
        accessOrderMap.get("A");
        
        System.out.println("存取 B, A 後: " + accessOrderMap);
        
        // 再次存取
        accessOrderMap.get("C");
        
        System.out.println("存取 C 後: " + accessOrderMap);
        System.out.println();
    }
    
    public static void demonstrateLRUCache() {
        System.out.println("=== LRU 緩存實現 ===\n");
        
        // 實現簡單的 LRU 緩存
        class LRUCache<K, V> extends LinkedHashMap<K, V> {
            private final int maxCapacity;
            
            public LRUCache(int maxCapacity) {
                super(16, 0.75f, true); // 啟用存取順序
                this.maxCapacity = maxCapacity;
            }
            
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxCapacity;
            }
        }
        
        LRUCache<String, String> cache = new LRUCache<>(3);
        
        // 添加元素
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        System.out.println("初始緩存: " + cache);
        
        // 存取 key1，使其變成最新的
        cache.get("key1");
        System.out.println("存取 key1 後: " + cache);
        
        // 添加新元素，應該移除最舊的 key2
        cache.put("key4", "value4");
        System.out.println("添加 key4 後: " + cache);
        
        // 再添加元素
        cache.put("key5", "value5");
        System.out.println("添加 key5 後: " + cache);
    }
}
```

## 5. Map 的遍歷方式

```java
import java.util.*;

public class MapTraversalMethods {
    public static void main(String[] args) {
        Map<String, Integer> fruitPrices = new HashMap<>();
        fruitPrices.put("蘋果", 30);
        fruitPrices.put("香蕉", 20);
        fruitPrices.put("橘子", 25);
        fruitPrices.put("葡萄", 40);
        
        System.out.println("原始 Map: " + fruitPrices + "\n");
        
        // 方法一：遍歷鍵集合
        System.out.println("1. 遍歷鍵集合:");
        for (String fruit : fruitPrices.keySet()) {
            Integer price = fruitPrices.get(fruit);
            System.out.println("  " + fruit + ": $" + price);
        }
        
        // 方法二：遍歷值集合
        System.out.println("\n2. 遍歷值集合:");
        for (Integer price : fruitPrices.values()) {
            System.out.println("  價格: $" + price);
        }
        
        // 方法三：遍歷鍵值對
        System.out.println("\n3. 遍歷鍵值對 (推薦):");
        for (Map.Entry<String, Integer> entry : fruitPrices.entrySet()) {
            System.out.println("  " + entry.getKey() + ": $" + entry.getValue());
        }
        
        // 方法四：使用 Iterator
        System.out.println("\n4. 使用 Iterator:");
        Iterator<Map.Entry<String, Integer>> iterator = fruitPrices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            System.out.println("  " + entry.getKey() + ": $" + entry.getValue());
        }
        
        // 方法五：forEach 方法 (Java 8+)
        System.out.println("\n5. forEach 方法:");
        fruitPrices.forEach((fruit, price) -> 
            System.out.println("  " + fruit + ": $" + price));
        
        // 方法六：Stream API (Java 8+)
        System.out.println("\n6. Stream API:");
        fruitPrices.entrySet().stream()
            .sorted(Map.Entry.comparingByValue()) // 按價格排序
            .forEach(entry -> 
                System.out.println("  " + entry.getKey() + ": $" + entry.getValue()));
    }
}
```

## 6. Map 實際應用範例

### 詞頻統計系統

```java
import java.util.*;
import java.util.stream.Collectors;

public class WordFrequencyCounter {
    
    public static void main(String[] args) {
        String text = "Java is a programming language. Java is powerful. " +
                     "Programming with Java is fun. Java developers love Java.";
        
        System.out.println("原始文字: " + text + "\n");
        
        // 統計詞頻
        Map<String, Integer> wordFreq = countWordFrequency(text);
        displayWordFrequency(wordFreq);
        
        // 找出最常出現的詞
        findMostFrequentWords(wordFreq, 3);
        
        // 按字母順序顯示
        displaySortedByWord(wordFreq);
        
        // 按頻率降序顯示
        displaySortedByFrequency(wordFreq);
    }
    
    public static Map<String, Integer> countWordFrequency(String text) {
        Map<String, Integer> wordCount = new HashMap<>();
        
        // 分割並清理單詞
        String[] words = text.toLowerCase()
                            .replaceAll("[^a-zA-Z\\s]", "") // 移除標點符號
                            .split("\\s+");
        
        // 統計每個單詞的出現次數
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        
        return wordCount;
    }
    
    public static void displayWordFrequency(Map<String, Integer> wordFreq) {
        System.out.println("=== 詞頻統計 ===");
        wordFreq.forEach((word, freq) -> 
            System.out.println(word + ": " + freq + " 次"));
        System.out.println("總共 " + wordFreq.size() + " 個不同的單詞\n");
    }
    
    public static void findMostFrequentWords(Map<String, Integer> wordFreq, int topN) {
        System.out.println("=== 最常出現的 " + topN + " 個單詞 ===");
        
        wordFreq.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(topN)
            .forEach(entry -> 
                System.out.println(entry.getKey() + ": " + entry.getValue() + " 次"));
        System.out.println();
    }
    
    public static void displaySortedByWord(Map<String, Integer> wordFreq) {
        System.out.println("=== 按字母順序排列 ===");
        
        TreeMap<String, Integer> sortedByWord = new TreeMap<>(wordFreq);
        sortedByWord.forEach((word, freq) -> 
            System.out.println(word + ": " + freq + " 次"));
        System.out.println();
    }
    
    public static void displaySortedByFrequency(Map<String, Integer> wordFreq) {
        System.out.println("=== 按頻率降序排列 ===");
        
        LinkedHashMap<String, Integer> sortedByFreq = wordFreq.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toLinkedHashMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
        
        sortedByFreq.forEach((word, freq) -> 
            System.out.println(word + ": " + freq + " 次"));
        System.out.println();
    }
}
```

### 學生成績管理系統

```java
import java.util.*;

class StudentGrade {
    private String studentName;
    private Map<String, Double> subjectScores;
    
    public StudentGrade(String studentName) {
        this.studentName = studentName;
        this.subjectScores = new HashMap<>();
    }
    
    public void addScore(String subject, double score) {
        subjectScores.put(subject, score);
    }
    
    public double getAverageScore() {
        if (subjectScores.isEmpty()) return 0.0;
        
        double total = subjectScores.values().stream()
                                  .mapToDouble(Double::doubleValue)
                                  .sum();
        return total / subjectScores.size();
    }
    
    public String getStudentName() { return studentName; }
    public Map<String, Double> getSubjectScores() { return new HashMap<>(subjectScores); }
    
    @Override
    public String toString() {
        return String.format("StudentGrade{name='%s', scores=%s, avg=%.1f}", 
                           studentName, subjectScores, getAverageScore());
    }
}

public class StudentGradeManager {
    private Map<String, StudentGrade> students;
    private Map<String, List<Double>> subjectScores;
    
    public StudentGradeManager() {
        this.students = new HashMap<>();
        this.subjectScores = new HashMap<>();
    }
    
    public void addStudent(String studentName) {
        students.put(studentName, new StudentGrade(studentName));
        System.out.println("已添加學生: " + studentName);
    }
    
    public void addScore(String studentName, String subject, double score) {
        StudentGrade student = students.get(studentName);
        if (student == null) {
            System.out.println("學生 " + studentName + " 不存在，自動添加");
            addStudent(studentName);
            student = students.get(studentName);
        }
        
        student.addScore(subject, score);
        
        // 更新科目分數列表
        subjectScores.computeIfAbsent(subject, k -> new ArrayList<>()).add(score);
        
        System.out.println("已添加成績: " + studentName + " " + subject + " " + score);
    }
    
    public void displayAllStudents() {
        System.out.println("\n=== 所有學生成績 ===");
        if (students.isEmpty()) {
            System.out.println("沒有學生記錄");
            return;
        }
        
        students.values().forEach(System.out::println);
    }
    
    public void displaySubjectStatistics() {
        System.out.println("\n=== 各科目統計 ===");
        
        for (Map.Entry<String, List<Double>> entry : subjectScores.entrySet()) {
            String subject = entry.getKey();
            List<Double> scores = entry.getValue();
            
            double average = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            
            System.out.printf("%s: 平均%.1f, 最高%.1f, 最低%.1f (共%d人)\n", 
                            subject, average, max, min, scores.size());
        }
    }
    
    public void displayTopStudents(int topN) {
        System.out.println("\n=== 前 " + topN + " 名學生 ===");
        
        students.values().stream()
            .sorted((s1, s2) -> Double.compare(s2.getAverageScore(), s1.getAverageScore()))
            .limit(topN)
            .forEach(student -> 
                System.out.printf("%s: 平均 %.1f 分\n", 
                                student.getStudentName(), student.getAverageScore()));
    }
    
    public void displayStudentRanking() {
        System.out.println("\n=== 學生排名 ===");
        
        // 使用 TreeMap 按平均分數排序
        TreeMap<Double, List<String>> rankingMap = new TreeMap<>(Collections.reverseOrder());
        
        for (StudentGrade student : students.values()) {
            double avgScore = student.getAverageScore();
            rankingMap.computeIfAbsent(avgScore, k -> new ArrayList<>())
                     .add(student.getStudentName());
        }
        
        int rank = 1;
        for (Map.Entry<Double, List<String>> entry : rankingMap.entrySet()) {
            double score = entry.getKey();
            List<String> studentsWithSameScore = entry.getValue();
            
            for (String studentName : studentsWithSameScore) {
                System.out.printf("第 %d 名: %s (%.1f 分)\n", rank, studentName, score);
            }
            rank += studentsWithSameScore.size();
        }
    }
    
    public static void main(String[] args) {
        StudentGradeManager manager = new StudentGradeManager();
        
        // 添加學生和成績
        manager.addScore("張三", "數學", 85);
        manager.addScore("張三", "英文", 92);
        manager.addScore("張三", "物理", 78);
        
        manager.addScore("李四", "數學", 90);
        manager.addScore("李四", "英文", 88);
        manager.addScore("李四", "物理", 85);
        
        manager.addScore("王五", "數學", 78);
        manager.addScore("王五", "英文", 85);
        manager.addScore("王五", "物理", 90);
        
        manager.addScore("趙六", "數學", 95);
        manager.addScore("趙六", "英文", 90);
        manager.addScore("趙六", "物理", 88);
        
        // 顯示各種統計信息
        manager.displayAllStudents();
        manager.displaySubjectStatistics();
        manager.displayTopStudents(3);
        manager.displayStudentRanking();
    }
}
```

## 7. Map 性能比較

```java
import java.util.*;

public class MapPerformanceComparison {
    private static final int SIZE = 100000;
    
    public static void main(String[] args) {
        System.out.println("=== Map 性能比較測試 ===\n");
        
        testPutPerformance();
        testGetPerformance();
        testRemovePerformance();
        testMemoryUsage();
    }
    
    public static void testPutPerformance() {
        System.out.println("1. 插入性能測試 (10萬個元素):");
        
        // HashMap
        long startTime = System.currentTimeMillis();
        Map<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            hashMap.put(i, "Value" + i);
        }
        long hashMapTime = System.currentTimeMillis() - startTime;
        
        // LinkedHashMap
        startTime = System.currentTimeMillis();
        Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < SIZE; i++) {
            linkedHashMap.put(i, "Value" + i);
        }
        long linkedHashMapTime = System.currentTimeMillis() - startTime;
        
        // TreeMap
        startTime = System.currentTimeMillis();
        Map<Integer, String> treeMap = new TreeMap<>();
        for (int i = 0; i < SIZE; i++) {
            treeMap.put(i, "Value" + i);
        }
        long treeMapTime = System.currentTimeMillis() - startTime;
        
        System.out.println("HashMap: " + hashMapTime + "ms");
        System.out.println("LinkedHashMap: " + linkedHashMapTime + "ms");
        System.out.println("TreeMap: " + treeMapTime + "ms");
        System.out.println();
    }
    
    public static void testGetPerformance() {
        System.out.println("2. 查找性能測試 (1萬次隨機查找):");
        
        // 準備測試數據
        Map<Integer, String> hashMap = new HashMap<>();
        Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
        Map<Integer, String> treeMap = new TreeMap<>();
        
        for (int i = 0; i < SIZE; i++) {
            String value = "Value" + i;
            hashMap.put(i, value);
            linkedHashMap.put(i, value);
            treeMap.put(i, value);
        }
        
        Random random = new Random();
        int[] searchKeys = new int[10000];
        for (int i = 0; i < searchKeys.length; i++) {
            searchKeys[i] = random.nextInt(SIZE);
        }
        
        // HashMap 查找測試
        long startTime = System.nanoTime();
        for (int key : searchKeys) {
            hashMap.get(key);
        }
        long hashMapTime = System.nanoTime() - startTime;
        
        // LinkedHashMap 查找測試
        startTime = System.nanoTime();
        for (int key : searchKeys) {
            linkedHashMap.get(key);
        }
        long linkedHashMapTime = System.nanoTime() - startTime;
        
        // TreeMap 查找測試
        startTime = System.nanoTime();
        for (int key : searchKeys) {
            treeMap.get(key);
        }
        long treeMapTime = System.nanoTime() - startTime;
        
        System.out.printf("HashMap: %.2f ms\n", hashMapTime / 1_000_000.0);
        System.out.printf("LinkedHashMap: %.2f ms\n", linkedHashMapTime / 1_000_000.0);
        System.out.printf("TreeMap: %.2f ms\n", treeMapTime / 1_000_000.0);
        System.out.println();
    }
    
    public static void testRemovePerformance() {
        System.out.println("3. 移除性能測試 (移除一半元素):");
        
        // 準備測試數據
        Map<Integer, String> hashMap = new HashMap<>();
        Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
        Map<Integer, String> treeMap = new TreeMap<>();
        
        for (int i = 0; i < SIZE; i++) {
            String value = "Value" + i;
            hashMap.put(i, value);
            linkedHashMap.put(i, value);
            treeMap.put(i, value);
        }
        
        // HashMap 移除測試
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE / 2; i++) {
            hashMap.remove(i);
        }
        long hashMapTime = System.currentTimeMillis() - startTime;
        
        // LinkedHashMap 移除測試
        startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE / 2; i++) {
            linkedHashMap.remove(i);
        }
        long linkedHashMapTime = System.currentTimeMillis() - startTime;
        
        // TreeMap 移除測試
        startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE / 2; i++) {
            treeMap.remove(i);
        }
        long treeMapTime = System.currentTimeMillis() - startTime;
        
        System.out.println("HashMap: " + hashMapTime + "ms");
        System.out.println("LinkedHashMap: " + linkedHashMapTime + "ms");
        System.out.println("TreeMap: " + treeMapTime + "ms");
        System.out.println();
    }
    
    public static void testMemoryUsage() {
        System.out.println("4. 記憶體使用情況:");
        
        Runtime runtime = Runtime.getRuntime();
        
        // 測試 HashMap
        runtime.gc();
        long beforeHashMap = runtime.totalMemory() - runtime.freeMemory();
        Map<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < SIZE; i++) {
            hashMap.put(i, "Value" + i);
        }
        long afterHashMap = runtime.totalMemory() - runtime.freeMemory();
        
        // 測試 LinkedHashMap
        hashMap = null;
        runtime.gc();
        long beforeLinkedHashMap = runtime.totalMemory() - runtime.freeMemory();
        Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < SIZE; i++) {
            linkedHashMap.put(i, "Value" + i);
        }
        long afterLinkedHashMap = runtime.totalMemory() - runtime.freeMemory();
        
        // 測試 TreeMap
        linkedHashMap = null;
        runtime.gc();
        long beforeTreeMap = runtime.totalMemory() - runtime.freeMemory();
        Map<Integer, String> treeMap = new TreeMap<>();
        for (int i = 0; i < SIZE; i++) {
            treeMap.put(i, "Value" + i);
        }
        long afterTreeMap = runtime.totalMemory() - runtime.freeMemory();
        
        System.out.printf("HashMap 記憶體使用: %.2f MB\n", 
                         (afterHashMap - beforeHashMap) / 1024.0 / 1024.0);
        System.out.printf("LinkedHashMap 記憶體使用: %.2f MB\n", 
                         (afterLinkedHashMap - beforeLinkedHashMap) / 1024.0 / 1024.0);
        System.out.printf("TreeMap 記憶體使用: %.2f MB\n", 
                         (afterTreeMap - beforeTreeMap) / 1024.0 / 1024.0);
        System.out.println();
    }
}
```

## 8. 選擇建議

### 選擇 HashMap 當：
- **性能優先**：需要最快的查找、插入、刪除操作
- **不關心順序**：不需要維持任何特定順序
- **大量數據**：處理大量數據且對順序無要求
- **一般用途**：大多數情況下的預設選擇

### 選擇 LinkedHashMap 當：
- **需要插入順序**：要保持鍵值對的插入順序
- **LRU 緩存**：需要實現 LRU 緩存機制
- **有序遍歷**：需要可預測的遍歷順序

### 選擇 TreeMap 當：
- **需要排序**：需要鍵自動排序
- **範圍查詢**：需要範圍操作 (firstKey, lastKey, subMap 等)
- **NavigableMap 功能**：需要導航方法
- **有序輸出**：需要按鍵排序的輸出

## 重點整理

### Map 的優勢
- **快速查找**：O(1) 或 O(log n) 的查找性能
- **靈活的關聯**：可以建立任意的鍵值關聯
- **豐富的操作**：提供多種便利的操作方法

### 最佳實踐
1. **正確實作 equals() 和 hashCode()**：對於自定義鍵物件很重要
2. **選擇合適的實作類**：根據需求選擇性能和功能
3. **使用 entrySet() 遍歷**：比分別遍歷鍵和值更高效
4. **考慮初始容量**：HashMap 指定合適的初始容量可提升性能
5. **不可變鍵**：使用不可變物件作為鍵更安全

## 下一步

接下來我們將學習 Queue 與 Deque 介面，它們提供了佇列和雙端佇列的功能，在演算法和實際應用中非常有用。

---

**練習建議**：嘗試實作一個簡單的緩存系統，使用不同的 Map 實作來體驗它們的特性差異。