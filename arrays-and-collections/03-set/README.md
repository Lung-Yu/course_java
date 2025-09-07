# Set 介面與實作

## 學習目標

- 理解 Set 介面的去重特性
- 掌握 HashSet、TreeSet、LinkedHashSet 的區別
- 學會選擇適合的 Set 實作類
- 理解 equals() 和 hashCode() 的重要性
- 熟練使用 Set 進行集合運算

## 1. Set 介面概述

Set 是 Java 集合框架中表示不包含重複元素的集合介面。它模擬了數學中的集合概念，提供了基本的集合運算功能。

### Set 介面特性

- **無重複元素**：不允許包含重複的元素
- **無索引存取**：不支援基於索引的操作
- **數學集合**：支援交集、聯集、差集等運算
- **NULL 元素**：大多數實作允許一個 null 元素

### 主要實作類比較

| 實作類 | 底層結構 | 排序 | 性能 | 特點 |
|--------|----------|------|------|------|
| HashSet | 雜湊表 | 無序 | O(1) | 最快的查找 |
| LinkedHashSet | 雜湊表+鏈表 | 插入順序 | O(1) | 保持插入順序 |
| TreeSet | 紅黑樹 | 自然/自定義 | O(log n) | 自動排序 |

## 2. HashSet 詳解

HashSet 是最常用的 Set 實作，基於雜湊表實現，提供最佳的性能。

### 核心特性

- **底層結構**：雜湊表 (HashMap)
- **排序**：無序 (與插入順序無關)
- **NULL 值**：允許一個 null 元素
- **非線程安全**：需要外部同步

### 基本操作

```java
import java.util.*;

public class HashSetExample {
    public static void main(String[] args) {
        // 創建 HashSet
        Set<String> fruits = new HashSet<>();
        
        // 添加元素
        fruits.add("蘋果");
        fruits.add("香蕉");
        fruits.add("橘子");
        fruits.add("蘋果"); // 重複元素，不會被添加
        
        System.out.println("水果集合: " + fruits);
        System.out.println("集合大小: " + fruits.size());
        
        // 檢查是否包含
        boolean hasApple = fruits.contains("蘋果");
        System.out.println("是否包含蘋果: " + hasApple);
        
        // 移除元素
        boolean removed = fruits.remove("香蕉");
        System.out.println("移除香蕉: " + removed);
        System.out.println("移除後: " + fruits);
        
        // 遍歷集合
        System.out.println("遍歷集合:");
        for (String fruit : fruits) {
            System.out.println("  " + fruit);
        }
    }
}
```

### equals() 和 hashCode() 的重要性

```java
import java.util.*;

class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // 正確實作 equals()
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }
    
    // 正確實作 hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
}

public class HashSetWithCustomObjects {
    public static void main(String[] args) {
        Set<Person> people = new HashSet<>();
        
        // 添加 Person 物件
        people.add(new Person("張三", 25));
        people.add(new Person("李四", 30));
        people.add(new Person("張三", 25)); // 應該被認為是重複的
        
        System.out.println("人員集合大小: " + people.size());
        System.out.println("人員集合: " + people);
        
        // 檢查是否包含特定人員
        Person target = new Person("張三", 25);
        boolean contains = people.contains(target);
        System.out.println("是否包含 " + target + ": " + contains);
    }
}
```

## 3. TreeSet 詳解

TreeSet 基於紅黑樹實現，元素會自動排序。

### 核心特性

- **底層結構**：紅黑樹 (平衡二元搜尋樹)
- **排序**：自然順序或自定義 Comparator
- **NULL 值**：不允許 null 元素
- **導航方法**：提供範圍查詢功能

### 基本操作

```java
import java.util.*;

public class TreeSetExample {
    public static void main(String[] args) {
        // 創建 TreeSet (自然排序)
        TreeSet<Integer> numbers = new TreeSet<>();
        
        // 添加元素 (會自動排序)
        numbers.add(5);
        numbers.add(2);
        numbers.add(8);
        numbers.add(1);
        numbers.add(9);
        numbers.add(5); // 重複元素，不會被添加
        
        System.out.println("數字集合 (自動排序): " + numbers);
        
        // TreeSet 特有的導航方法
        System.out.println("第一個元素: " + numbers.first());
        System.out.println("最後一個元素: " + numbers.last());
        System.out.println("小於 5 的最大元素: " + numbers.lower(5));
        System.out.println("大於等於 5 的最小元素: " + numbers.ceiling(5));
        System.out.println("大於 5 的最小元素: " + numbers.higher(5));
        System.out.println("小於等於 5 的最大元素: " + numbers.floor(5));
        
        // 子集合操作
        SortedSet<Integer> headSet = numbers.headSet(5);
        SortedSet<Integer> tailSet = numbers.tailSet(5);
        SortedSet<Integer> subSet = numbers.subSet(2, 8);
        
        System.out.println("小於 5 的元素: " + headSet);
        System.out.println("大於等於 5 的元素: " + tailSet);
        System.out.println("範圍 [2, 8) 的元素: " + subSet);
    }
}
```

### 自定義排序

```java
import java.util.*;

class Student {
    private String name;
    private double score;
    
    public Student(String name, double score) {
        this.name = name;
        this.score = score;
    }
    
    public String getName() { return name; }
    public double getScore() { return score; }
    
    @Override
    public String toString() {
        return String.format("Student{name='%s', score=%.1f}", name, score);
    }
}

public class TreeSetWithComparator {
    public static void main(String[] args) {
        // 按分數降序排列
        TreeSet<Student> studentsByScore = new TreeSet<>(
            (s1, s2) -> Double.compare(s2.getScore(), s1.getScore())
        );
        
        studentsByScore.add(new Student("張三", 85.5));
        studentsByScore.add(new Student("李四", 92.0));
        studentsByScore.add(new Student("王五", 78.5));
        studentsByScore.add(new Student("趙六", 88.0));
        
        System.out.println("按分數降序排列:");
        for (Student student : studentsByScore) {
            System.out.println("  " + student);
        }
        
        // 按姓名字母順序排列
        TreeSet<Student> studentsByName = new TreeSet<>(
            Comparator.comparing(Student::getName)
        );
        
        studentsByName.addAll(studentsByScore);
        System.out.println("\n按姓名排序:");
        for (Student student : studentsByName) {
            System.out.println("  " + student);
        }
        
        // 複合排序：先按分數，再按姓名
        TreeSet<Student> studentsByScoreAndName = new TreeSet<>(
            Comparator.comparing(Student::getScore)
                     .thenComparing(Student::getName)
        );
        
        studentsByScoreAndName.addAll(studentsByScore);
        System.out.println("\n按分數和姓名排序:");
        for (Student student : studentsByScoreAndName) {
            System.out.println("  " + student);
        }
    }
}
```

## 4. LinkedHashSet 詳解

LinkedHashSet 結合了 HashSet 的快速存取和有序性。

### 核心特性

- **底層結構**：雜湊表 + 雙向鏈表
- **排序**：維持插入順序
- **性能**：略慢於 HashSet，但比 TreeSet 快
- **記憶體**：比 HashSet 使用更多記憶體

```java
import java.util.*;

public class LinkedHashSetExample {
    public static void main(String[] args) {
        // 比較不同 Set 實作的順序
        demonstrateInsertionOrder();
        
        // LinkedHashSet 的實際應用
        demonstratePracticalUse();
    }
    
    public static void demonstrateInsertionOrder() {
        System.out.println("=== 不同 Set 實作的順序比較 ===\n");
        
        String[] fruits = {"香蕉", "蘋果", "橘子", "葡萄", "草莓"};
        
        // HashSet - 無序
        Set<String> hashSet = new HashSet<>();
        Collections.addAll(hashSet, fruits);
        System.out.println("HashSet (無序): " + hashSet);
        
        // LinkedHashSet - 插入順序
        Set<String> linkedHashSet = new LinkedHashSet<>();
        Collections.addAll(linkedHashSet, fruits);
        System.out.println("LinkedHashSet (插入順序): " + linkedHashSet);
        
        // TreeSet - 自然排序
        Set<String> treeSet = new TreeSet<>();
        Collections.addAll(treeSet, fruits);
        System.out.println("TreeSet (字母順序): " + treeSet);
    }
    
    public static void demonstratePracticalUse() {
        System.out.println("\n=== LinkedHashSet 實際應用 ===\n");
        
        // 去重同時保持順序
        List<String> userInput = Arrays.asList(
            "Java", "Python", "Java", "C++", "Python", "JavaScript", "Java"
        );
        
        System.out.println("原始輸入: " + userInput);
        
        // 使用 LinkedHashSet 去重並保持順序
        Set<String> uniqueLanguages = new LinkedHashSet<>(userInput);
        System.out.println("去重後保持順序: " + uniqueLanguages);
        
        // 轉回 List
        List<String> result = new ArrayList<>(uniqueLanguages);
        System.out.println("轉回 List: " + result);
    }
}
```

## 5. Set 的集合運算

```java
import java.util.*;

public class SetOperations {
    public static void main(String[] args) {
        Set<Integer> setA = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> setB = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));
        
        System.out.println("集合 A: " + setA);
        System.out.println("集合 B: " + setB);
        
        // 聯集 (Union)
        Set<Integer> union = new HashSet<>(setA);
        union.addAll(setB);
        System.out.println("聯集 A ∪ B: " + union);
        
        // 交集 (Intersection)
        Set<Integer> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        System.out.println("交集 A ∩ B: " + intersection);
        
        // 差集 (Difference)
        Set<Integer> differenceAB = new HashSet<>(setA);
        differenceAB.removeAll(setB);
        System.out.println("差集 A - B: " + differenceAB);
        
        Set<Integer> differenceBA = new HashSet<>(setB);
        differenceBA.removeAll(setA);
        System.out.println("差集 B - A: " + differenceBA);
        
        // 對稱差集 (Symmetric Difference)
        Set<Integer> symmetricDiff = new HashSet<>(union);
        symmetricDiff.removeAll(intersection);
        System.out.println("對稱差集 (A ∪ B) - (A ∩ B): " + symmetricDiff);
        
        // 檢查包含關係
        Set<Integer> subset = new HashSet<>(Arrays.asList(1, 2, 3));
        boolean isSubset = setA.containsAll(subset);
        System.out.println("子集 {1,2,3} ⊆ A: " + isSubset);
        
        // 檢查是否不相交
        Set<Integer> setC = new HashSet<>(Arrays.asList(9, 10, 11));
        boolean disjoint = Collections.disjoint(setA, setC);
        System.out.println("A 和 {9,10,11} 不相交: " + disjoint);
    }
}
```

## 6. Set 性能比較

```java
import java.util.*;

public class SetPerformanceComparison {
    private static final int SIZE = 100000;
    
    public static void main(String[] args) {
        System.out.println("=== Set 性能比較測試 ===\n");
        
        testAddPerformance();
        testContainsPerformance();
        testRemovePerformance();
        testIterationPerformance();
    }
    
    public static void testAddPerformance() {
        System.out.println("1. 添加性能測試 (10萬個元素):");
        
        // HashSet
        long startTime = System.currentTimeMillis();
        Set<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < SIZE; i++) {
            hashSet.add(i);
        }
        long hashSetTime = System.currentTimeMillis() - startTime;
        
        // LinkedHashSet
        startTime = System.currentTimeMillis();
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        for (int i = 0; i < SIZE; i++) {
            linkedHashSet.add(i);
        }
        long linkedHashSetTime = System.currentTimeMillis() - startTime;
        
        // TreeSet
        startTime = System.currentTimeMillis();
        Set<Integer> treeSet = new TreeSet<>();
        for (int i = 0; i < SIZE; i++) {
            treeSet.add(i);
        }
        long treeSetTime = System.currentTimeMillis() - startTime;
        
        System.out.println("HashSet: " + hashSetTime + "ms");
        System.out.println("LinkedHashSet: " + linkedHashSetTime + "ms");
        System.out.println("TreeSet: " + treeSetTime + "ms");
        System.out.println();
    }
    
    public static void testContainsPerformance() {
        System.out.println("2. 查找性能測試 (1萬次查找):");
        
        // 準備測試數據
        Set<Integer> hashSet = new HashSet<>();
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        Set<Integer> treeSet = new TreeSet<>();
        
        for (int i = 0; i < SIZE; i++) {
            hashSet.add(i);
            linkedHashSet.add(i);
            treeSet.add(i);
        }
        
        Random random = new Random();
        int[] searchValues = new int[10000];
        for (int i = 0; i < searchValues.length; i++) {
            searchValues[i] = random.nextInt(SIZE);
        }
        
        // HashSet 查找測試
        long startTime = System.nanoTime();
        for (int value : searchValues) {
            hashSet.contains(value);
        }
        long hashSetTime = System.nanoTime() - startTime;
        
        // LinkedHashSet 查找測試
        startTime = System.nanoTime();
        for (int value : searchValues) {
            linkedHashSet.contains(value);
        }
        long linkedHashSetTime = System.nanoTime() - startTime;
        
        // TreeSet 查找測試
        startTime = System.nanoTime();
        for (int value : searchValues) {
            treeSet.contains(value);
        }
        long treeSetTime = System.nanoTime() - startTime;
        
        System.out.printf("HashSet: %.2f ms\n", hashSetTime / 1_000_000.0);
        System.out.printf("LinkedHashSet: %.2f ms\n", linkedHashSetTime / 1_000_000.0);
        System.out.printf("TreeSet: %.2f ms\n", treeSetTime / 1_000_000.0);
        System.out.println();
    }
    
    public static void testRemovePerformance() {
        System.out.println("3. 移除性能測試 (移除一半元素):");
        
        // 準備測試數據
        Set<Integer> hashSet = new HashSet<>();
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        Set<Integer> treeSet = new TreeSet<>();
        
        for (int i = 0; i < SIZE; i++) {
            hashSet.add(i);
            linkedHashSet.add(i);
            treeSet.add(i);
        }
        
        // HashSet 移除測試
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE / 2; i++) {
            hashSet.remove(i);
        }
        long hashSetTime = System.currentTimeMillis() - startTime;
        
        // LinkedHashSet 移除測試
        startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE / 2; i++) {
            linkedHashSet.remove(i);
        }
        long linkedHashSetTime = System.currentTimeMillis() - startTime;
        
        // TreeSet 移除測試
        startTime = System.currentTimeMillis();
        for (int i = 0; i < SIZE / 2; i++) {
            treeSet.remove(i);
        }
        long treeSetTime = System.currentTimeMillis() - startTime;
        
        System.out.println("HashSet: " + hashSetTime + "ms");
        System.out.println("LinkedHashSet: " + linkedHashSetTime + "ms");
        System.out.println("TreeSet: " + treeSetTime + "ms");
        System.out.println();
    }
    
    public static void testIterationPerformance() {
        System.out.println("4. 遍歷性能測試:");
        
        // 準備測試數據
        Set<Integer> hashSet = new HashSet<>();
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        Set<Integer> treeSet = new TreeSet<>();
        
        for (int i = 0; i < SIZE; i++) {
            hashSet.add(i);
            linkedHashSet.add(i);
            treeSet.add(i);
        }
        
        // HashSet 遍歷測試
        long startTime = System.nanoTime();
        for (@SuppressWarnings("unused") Integer value : hashSet) {
            // 只是遍歷，不做任何操作
        }
        long hashSetTime = System.nanoTime() - startTime;
        
        // LinkedHashSet 遍歷測試
        startTime = System.nanoTime();
        for (@SuppressWarnings("unused") Integer value : linkedHashSet) {
            // 只是遍歷，不做任何操作
        }
        long linkedHashSetTime = System.nanoTime() - startTime;
        
        // TreeSet 遍歷測試
        startTime = System.nanoTime();
        for (@SuppressWarnings("unused") Integer value : treeSet) {
            // 只是遍歷，不做任何操作
        }
        long treeSetTime = System.nanoTime() - startTime;
        
        System.out.printf("HashSet: %.2f ms\n", hashSetTime / 1_000_000.0);
        System.out.printf("LinkedHashSet: %.2f ms\n", linkedHashSetTime / 1_000_000.0);
        System.out.printf("TreeSet: %.2f ms\n", treeSetTime / 1_000_000.0);
        System.out.println();
    }
}
```

## 7. 實際應用範例

### 唯一訪客統計系統

```java
import java.util.*;

public class UniqueVisitorTracker {
    private Set<String> uniqueVisitors;
    private Set<String> dailyVisitors;
    private Map<String, Integer> visitCount;
    
    public UniqueVisitorTracker() {
        this.uniqueVisitors = new HashSet<>();
        this.dailyVisitors = new LinkedHashSet<>(); // 保持訪問順序
        this.visitCount = new HashMap<>();
    }
    
    // 記錄訪客
    public void recordVisitor(String visitorId) {
        // 添加到總訪客集合
        boolean isNewVisitor = uniqueVisitors.add(visitorId);
        
        // 添加到每日訪客集合
        dailyVisitors.add(visitorId);
        
        // 更新訪問次數
        visitCount.put(visitorId, visitCount.getOrDefault(visitorId, 0) + 1);
        
        if (isNewVisitor) {
            System.out.println("新訪客: " + visitorId);
        } else {
            System.out.println("回訪客: " + visitorId + " (第 " + visitCount.get(visitorId) + " 次訪問)");
        }
    }
    
    // 獲取總唯一訪客數
    public int getTotalUniqueVisitors() {
        return uniqueVisitors.size();
    }
    
    // 獲取今日唯一訪客數
    public int getDailyUniqueVisitors() {
        return dailyVisitors.size();
    }
    
    // 檢查是否為新訪客
    public boolean isNewVisitor(String visitorId) {
        return !uniqueVisitors.contains(visitorId);
    }
    
    // 獲取訪問次數最多的訪客
    public String getMostFrequentVisitor() {
        return visitCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    
    // 重置每日統計
    public void resetDailyStats() {
        dailyVisitors.clear();
        System.out.println("每日統計已重置");
    }
    
    // 顯示統計報告
    public void showReport() {
        System.out.println("\n=== 訪客統計報告 ===");
        System.out.println("總唯一訪客數: " + getTotalUniqueVisitors());
        System.out.println("今日唯一訪客數: " + getDailyUniqueVisitors());
        
        String mostFrequent = getMostFrequentVisitor();
        if (mostFrequent != null) {
            System.out.println("最頻繁訪客: " + mostFrequent + 
                " (訪問 " + visitCount.get(mostFrequent) + " 次)");
        }
        
        System.out.println("今日訪客列表 (按訪問順序):");
        for (String visitor : dailyVisitors) {
            System.out.println("  " + visitor);
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        UniqueVisitorTracker tracker = new UniqueVisitorTracker();
        
        // 模擬訪客訪問
        System.out.println("=== 模擬網站訪問 ===");
        tracker.recordVisitor("user001");
        tracker.recordVisitor("user002");
        tracker.recordVisitor("user001"); // 重複訪問
        tracker.recordVisitor("user003");
        tracker.recordVisitor("user002"); // 重複訪問
        tracker.recordVisitor("user004");
        tracker.recordVisitor("user001"); // 再次訪問
        
        // 顯示報告
        tracker.showReport();
        
        // 檢查特定訪客
        System.out.println("user005 是新訪客嗎? " + tracker.isNewVisitor("user005"));
        System.out.println("user001 是新訪客嗎? " + tracker.isNewVisitor("user001"));
    }
}
```

## 8. 選擇建議

### 選擇 HashSet 當：
- **性能優先**：需要最快的查找、添加、刪除操作
- **不關心順序**：不需要維持任何特定順序
- **大量數據**：處理大量數據且對順序無要求

### 選擇 LinkedHashSet 當：
- **需要插入順序**：要保持元素的插入順序
- **去重且保序**：既要去重又要維持順序
- **適中性能**：可接受略低於 HashSet 的性能

### 選擇 TreeSet 當：
- **需要排序**：需要元素自動排序
- **範圍查詢**：需要範圍操作 (first, last, subSet 等)
- **NavigableSet 功能**：需要導航方法

## 重點整理

### Set 的優勢
- **自動去重**：無需手動檢查重複元素
- **數學集合運算**：支援聯集、交集、差集
- **高效查找**：O(1) 或 O(log n) 的查找性能

### 最佳實踐
1. **正確實作 equals() 和 hashCode()**：對於自定義物件很重要
2. **選擇合適的實作類**：根據需求選擇性能和功能
3. **避免修改元素**：不要修改已加入 Set 的物件的雜湊值相關欄位
4. **使用不可變物件**：作為 Set 元素時更安全

## 下一步

接下來我們將學習 Map 介面及其實作，它提供了鍵值對映射的功能，是處理關聯數據的最佳選擇。

---

**練習建議**：嘗試實作一個簡單的標籤系統，使用 Set 來管理文章的標籤，體驗 Set 在實際應用中的便利性。