# List 介面與實作

## 學習目標

- 理解 List 介面的特性和設計理念
- 掌握 ArrayList 和 LinkedList 的區別和使用場景
- 學會選擇適合的 List 實作類
- 熟練使用 List 的常用操作方法
- 了解不同實作類的性能特點

## 1. List 介面概述

List 是 Java 集合框架中的核心介面之一，它表示一個有序的集合，允許重複元素。List 擴展了 Collection 介面，提供了基於索引的操作方法。

### List 介面特性

- **有序性**：維護元素的插入順序
- **允許重複**：可以包含相同的元素
- **基於索引**：可透過索引存取和操作元素
- **動態大小**：可以動態調整大小

### 主要實作類

| 實作類 | 底層結構 | 特點 | 適用場景 |
|--------|----------|------|----------|
| ArrayList | 動態陣列 | 隨機存取快速 | 讀多寫少 |
| LinkedList | 雙向鏈表 | 插入刪除快速 | 寫多讀少 |
| Vector | 動態陣列 | 線程安全 | 多線程環境 |

## 2. ArrayList 詳解

ArrayList 是最常用的 List 實作，底層使用動態陣列實現。

### 核心特性

- **底層結構**：Object[] 陣列
- **初始容量**：10 (預設)
- **擴容機制**：1.5 倍擴容
- **非線程安全**：單線程使用

### 基本操作

```java
import java.util.*;

public class ArrayListExample {
    public static void main(String[] args) {
        // 創建 ArrayList
        List<String> fruits = new ArrayList<>();
        
        // 添加元素
        fruits.add("蘋果");
        fruits.add("香蕉");
        fruits.add("橘子");
        fruits.add(1, "草莓"); // 在指定位置插入
        
        System.out.println("水果列表: " + fruits);
        
        // 存取元素
        String firstFruit = fruits.get(0);
        System.out.println("第一個水果: " + firstFruit);
        
        // 修改元素
        fruits.set(2, "葡萄");
        System.out.println("修改後: " + fruits);
        
        // 檢查是否包含
        boolean hasApple = fruits.contains("蘋果");
        System.out.println("是否包含蘋果: " + hasApple);
        
        // 獲取索引
        int index = fruits.indexOf("香蕉");
        System.out.println("香蕉的索引: " + index);
        
        // 移除元素
        fruits.remove("草莓");           // 按值移除
        fruits.remove(1);               // 按索引移除
        System.out.println("移除後: " + fruits);
        
        // 大小和是否為空
        System.out.println("列表大小: " + fruits.size());
        System.out.println("是否為空: " + fruits.isEmpty());
    }
}
```

### ArrayList 容量管理

```java
import java.util.*;

public class ArrayListCapacity {
    public static void main(String[] args) {
        // 預設容量的 ArrayList
        List<Integer> defaultList = new ArrayList<>();
        
        // 指定初始容量的 ArrayList
        List<Integer> capacityList = new ArrayList<>(100);
        
        // 從其他集合創建 ArrayList
        List<Integer> sourceList = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> copyList = new ArrayList<>(sourceList);
        
        System.out.println("從集合複製: " + copyList);
        
        // 效能測試：預設容量 vs 指定容量
        testPerformance();
    }
    
    public static void testPerformance() {
        final int SIZE = 100000;
        
        // 測試預設容量
        long startTime = System.currentTimeMillis();
        List<Integer> defaultList = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            defaultList.add(i);
        }
        long defaultTime = System.currentTimeMillis() - startTime;
        
        // 測試指定容量
        startTime = System.currentTimeMillis();
        List<Integer> capacityList = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            capacityList.add(i);
        }
        long capacityTime = System.currentTimeMillis() - startTime;
        
        System.out.println("添加 " + SIZE + " 個元素:");
        System.out.println("預設容量耗時: " + defaultTime + "ms");
        System.out.println("指定容量耗時: " + capacityTime + "ms");
        System.out.println("性能提升: " + (defaultTime - capacityTime) + "ms");
    }
}
```

## 3. LinkedList 詳解

LinkedList 使用雙向鏈表實現，同時實作了 List 和 Deque 介面。

### 核心特性

- **底層結構**：雙向鏈表
- **記憶體分配**：非連續記憶體
- **非線程安全**：單線程使用
- **實作多個介面**：List、Deque、Queue

### 基本操作

```java
import java.util.*;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<String> cities = new LinkedList<>();
        
        // 添加元素
        cities.add("台北");
        cities.add("台中");
        cities.add("高雄");
        
        // LinkedList 特有的首尾操作
        cities.addFirst("基隆");    // 在開頭添加
        cities.addLast("屏東");     // 在結尾添加
        
        System.out.println("城市列表: " + cities);
        
        // 存取首尾元素
        String first = cities.getFirst();
        String last = cities.getLast();
        System.out.println("第一個城市: " + first);
        System.out.println("最後一個城市: " + last);
        
        // 移除首尾元素
        String removedFirst = cities.removeFirst();
        String removedLast = cities.removeLast();
        System.out.println("移除的首個城市: " + removedFirst);
        System.out.println("移除的最後城市: " + removedLast);
        System.out.println("移除後: " + cities);
        
        // 作為 Queue 使用
        cities.offer("花蓮");       // 入隊
        cities.offer("台東");
        System.out.println("入隊後: " + cities);
        
        String polled = cities.poll(); // 出隊
        System.out.println("出隊元素: " + polled);
        System.out.println("出隊後: " + cities);
        
        // 作為 Stack 使用
        cities.push("宜蘭");        // 入棧
        cities.push("新竹");
        System.out.println("入棧後: " + cities);
        
        String popped = cities.pop(); // 出棧
        System.out.println("出棧元素: " + popped);
        System.out.println("出棧後: " + cities);
    }
}
```

## 4. ArrayList vs LinkedList 性能比較

### 時間複雜度對比

| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| 添加到末尾 | O(1)* | O(1) |
| 添加到指定位置 | O(n) | O(n) |
| 移除末尾元素 | O(1) | O(1) |
| 移除指定位置 | O(n) | O(n) |
| 隨機存取 | O(1) | O(n) |
| 順序遍歷 | O(n) | O(n) |

*平均情況，擴容時為 O(n)

### 性能測試

```java
import java.util.*;

public class ListPerformanceComparison {
    private static final int SIZE = 100000;
    
    public static void main(String[] args) {
        System.out.println("=== List 性能比較測試 ===\n");
        
        testRandomAccess();
        testSequentialAccess();
        testInsertionAtBeginning();
        testInsertionAtEnd();
        testRemovalFromBeginning();
    }
    
    // 測試隨機存取性能
    public static void testRandomAccess() {
        System.out.println("1. 隨機存取測試 (10000次):");
        
        List<Integer> arrayList = createArrayList();
        List<Integer> linkedList = createLinkedList();
        Random random = new Random();
        
        // ArrayList 隨機存取
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            int index = random.nextInt(arrayList.size());
            arrayList.get(index);
        }
        long arrayListTime = System.nanoTime() - startTime;
        
        // LinkedList 隨機存取
        startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            int index = random.nextInt(linkedList.size());
            linkedList.get(index);
        }
        long linkedListTime = System.nanoTime() - startTime;
        
        System.out.printf("ArrayList: %.2f ms\n", arrayListTime / 1_000_000.0);
        System.out.printf("LinkedList: %.2f ms\n", linkedListTime / 1_000_000.0);
        System.out.printf("ArrayList 快 %.2f 倍\n\n", 
            (double) linkedListTime / arrayListTime);
    }
    
    // 測試順序存取性能
    public static void testSequentialAccess() {
        System.out.println("2. 順序存取測試:");
        
        List<Integer> arrayList = createArrayList();
        List<Integer> linkedList = createLinkedList();
        
        // ArrayList 順序存取
        long startTime = System.nanoTime();
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i);
        }
        long arrayListTime = System.nanoTime() - startTime;
        
        // LinkedList 順序存取
        startTime = System.nanoTime();
        for (int i = 0; i < linkedList.size(); i++) {
            linkedList.get(i);
        }
        long linkedListTime = System.nanoTime() - startTime;
        
        System.out.printf("ArrayList: %.2f ms\n", arrayListTime / 1_000_000.0);
        System.out.printf("LinkedList: %.2f ms\n", linkedListTime / 1_000_000.0);
        
        // 使用迭代器的順序存取
        startTime = System.nanoTime();
        for (Integer value : arrayList) {
            // 只是存取，不做任何操作
        }
        long arrayListIteratorTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (Integer value : linkedList) {
            // 只是存取，不做任何操作
        }
        long linkedListIteratorTime = System.nanoTime() - startTime;
        
        System.out.printf("ArrayList (迭代器): %.2f ms\n", arrayListIteratorTime / 1_000_000.0);
        System.out.printf("LinkedList (迭代器): %.2f ms\n\n", linkedListIteratorTime / 1_000_000.0);
    }
    
    // 測試開頭插入性能
    public static void testInsertionAtBeginning() {
        System.out.println("3. 開頭插入測試 (10000次):");
        
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        
        // ArrayList 開頭插入
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            arrayList.add(0, i);
        }
        long arrayListTime = System.nanoTime() - startTime;
        
        // LinkedList 開頭插入
        startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            linkedList.add(0, i);
        }
        long linkedListTime = System.nanoTime() - startTime;
        
        System.out.printf("ArrayList: %.2f ms\n", arrayListTime / 1_000_000.0);
        System.out.printf("LinkedList: %.2f ms\n", linkedListTime / 1_000_000.0);
        System.out.printf("LinkedList 快 %.2f 倍\n\n", 
            (double) arrayListTime / linkedListTime);
    }
    
    // 測試末尾添加性能
    public static void testInsertionAtEnd() {
        System.out.println("4. 末尾添加測試:");
        
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        
        // ArrayList 末尾添加
        long startTime = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            arrayList.add(i);
        }
        long arrayListTime = System.nanoTime() - startTime;
        
        // LinkedList 末尾添加
        startTime = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            linkedList.add(i);
        }
        long linkedListTime = System.nanoTime() - startTime;
        
        System.out.printf("ArrayList: %.2f ms\n", arrayListTime / 1_000_000.0);
        System.out.printf("LinkedList: %.2f ms\n\n", linkedListTime / 1_000_000.0);
    }
    
    // 測試開頭移除性能
    public static void testRemovalFromBeginning() {
        System.out.println("5. 開頭移除測試:");
        
        List<Integer> arrayList = createArrayList();
        List<Integer> linkedList = createLinkedList();
        
        // ArrayList 開頭移除
        long startTime = System.nanoTime();
        while (!arrayList.isEmpty()) {
            arrayList.remove(0);
        }
        long arrayListTime = System.nanoTime() - startTime;
        
        // LinkedList 開頭移除
        startTime = System.nanoTime();
        while (!linkedList.isEmpty()) {
            linkedList.remove(0);
        }
        long linkedListTime = System.nanoTime() - startTime;
        
        System.out.printf("ArrayList: %.2f ms\n", arrayListTime / 1_000_000.0);
        System.out.printf("LinkedList: %.2f ms\n", linkedListTime / 1_000_000.0);
        System.out.printf("LinkedList 快 %.2f 倍\n\n", 
            (double) arrayListTime / linkedListTime);
    }
    
    private static List<Integer> createArrayList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            list.add(i);
        }
        return list;
    }
    
    private static List<Integer> createLinkedList() {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < SIZE; i++) {
            list.add(i);
        }
        return list;
    }
}
```

## 5. List 的遍歷方式

```java
import java.util.*;

public class ListTraversalMethods {
    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java", "Python", "JavaScript", "C++", "Go");
        
        System.out.println("原始列表: " + languages + "\n");
        
        // 方法一：傳統 for 迴圈
        System.out.println("1. 傳統 for 迴圈:");
        for (int i = 0; i < languages.size(); i++) {
            System.out.println("  " + i + ": " + languages.get(i));
        }
        
        // 方法二：增強式 for 迴圈
        System.out.println("\n2. 增強式 for 迴圈:");
        for (String language : languages) {
            System.out.println("  " + language);
        }
        
        // 方法三：迭代器
        System.out.println("\n3. 迭代器:");
        Iterator<String> iterator = languages.iterator();
        while (iterator.hasNext()) {
            String language = iterator.next();
            System.out.println("  " + language);
        }
        
        // 方法四：ListIterator (可雙向遍歷)
        System.out.println("\n4. ListIterator 正向遍歷:");
        ListIterator<String> listIterator = languages.listIterator();
        while (listIterator.hasNext()) {
            int index = listIterator.nextIndex();
            String language = listIterator.next();
            System.out.println("  " + index + ": " + language);
        }
        
        System.out.println("\n5. ListIterator 反向遍歷:");
        while (listIterator.hasPrevious()) {
            int index = listIterator.previousIndex();
            String language = listIterator.previous();
            System.out.println("  " + index + ": " + language);
        }
        
        // 方法五：Stream API (Java 8+)
        System.out.println("\n6. Stream API:");
        languages.stream()
                .forEach(language -> System.out.println("  " + language));
        
        // 方法六：Stream API 帶索引
        System.out.println("\n7. Stream API 帶索引:");
        IntStream.range(0, languages.size())
                .forEach(i -> System.out.println("  " + i + ": " + languages.get(i)));
    }
}
```

## 6. List 實際應用範例

### 學生管理系統

```java
import java.util.*;

class Student {
    private String name;
    private int age;
    private double score;
    
    public Student(String name, int age, double score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getScore() { return score; }
    
    @Override
    public String toString() {
        return String.format("Student{name='%s', age=%d, score=%.1f}", name, age, score);
    }
}

public class StudentManagementSystem {
    private List<Student> students;
    
    public StudentManagementSystem() {
        this.students = new ArrayList<>();
    }
    
    // 添加學生
    public void addStudent(Student student) {
        students.add(student);
        System.out.println("已添加學生: " + student);
    }
    
    // 移除學生
    public boolean removeStudent(String name) {
        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            Student student = iterator.next();
            if (student.getName().equals(name)) {
                iterator.remove();
                System.out.println("已移除學生: " + student);
                return true;
            }
        }
        System.out.println("找不到學生: " + name);
        return false;
    }
    
    // 查找學生
    public Student findStudent(String name) {
        for (Student student : students) {
            if (student.getName().equals(name)) {
                return student;
            }
        }
        return null;
    }
    
    // 顯示所有學生
    public void displayAllStudents() {
        System.out.println("\n=== 所有學生 ===");
        if (students.isEmpty()) {
            System.out.println("沒有學生記錄");
            return;
        }
        
        for (int i = 0; i < students.size(); i++) {
            System.out.println((i + 1) + ". " + students.get(i));
        }
    }
    
    // 按分數排序
    public void sortByScore() {
        students.sort((s1, s2) -> Double.compare(s2.getScore(), s1.getScore()));
        System.out.println("已按分數降序排列");
    }
    
    // 計算平均分
    public double calculateAverageScore() {
        if (students.isEmpty()) {
            return 0.0;
        }
        
        double total = 0;
        for (Student student : students) {
            total += student.getScore();
        }
        return total / students.size();
    }
    
    // 查找高分學生
    public List<Student> findHighScoreStudents(double threshold) {
        List<Student> highScoreStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.getScore() >= threshold) {
                highScoreStudents.add(student);
            }
        }
        return highScoreStudents;
    }
    
    public static void main(String[] args) {
        StudentManagementSystem sms = new StudentManagementSystem();
        
        // 添加學生
        sms.addStudent(new Student("張三", 20, 85.5));
        sms.addStudent(new Student("李四", 19, 92.0));
        sms.addStudent(new Student("王五", 21, 78.5));
        sms.addStudent(new Student("趙六", 20, 88.0));
        
        // 顯示所有學生
        sms.displayAllStudents();
        
        // 按分數排序
        sms.sortByScore();
        sms.displayAllStudents();
        
        // 計算平均分
        double average = sms.calculateAverageScore();
        System.out.println("\n班級平均分: " + String.format("%.2f", average));
        
        // 查找高分學生
        List<Student> highScoreStudents = sms.findHighScoreStudents(85.0);
        System.out.println("\n高分學生 (≥85分):");
        for (Student student : highScoreStudents) {
            System.out.println("  " + student);
        }
        
        // 查找特定學生
        Student found = sms.findStudent("李四");
        if (found != null) {
            System.out.println("\n找到學生: " + found);
        }
        
        // 移除學生
        sms.removeStudent("王五");
        sms.displayAllStudents();
    }
}
```

## 7. 選擇建議

### 什麼時候使用 ArrayList

- **頻繁隨機存取**：需要經常使用 get(index) 操作
- **讀多寫少**：主要進行查詢操作，很少插入/刪除
- **記憶體敏感**：需要較少的記憶體開銷
- **簡單場景**：大多數情況下的預設選擇

### 什麼時候使用 LinkedList

- **頻繁插入/刪除**：經常在列表開頭或中間插入/刪除元素
- **不需要隨機存取**：很少使用 get(index) 操作
- **實作 Queue/Deque**：需要佇列或雙端佇列功能
- **大小變化頻繁**：列表大小經常變化

## 重點整理

### ArrayList 優勢
- O(1) 隨機存取時間
- 記憶體使用效率高
- 快速的順序遍歷
- 緩存友好的記憶體布局

### LinkedList 優勢
- O(1) 首尾插入/刪除
- 不需要預分配空間
- 實作多個介面 (List, Deque, Queue)
- 不會有擴容成本

### 最佳實踐
1. **大多數情況選擇 ArrayList**
2. **頻繁插入/刪除時考慮 LinkedList**
3. **指定初始容量以提升性能**
4. **使用增強式 for 迴圈或 Stream 進行遍歷**
5. **避免在大型 LinkedList 上使用 get(index)**

## 下一步

接下來我們將學習 Set 介面及其實作，它提供了去重集合的功能，是處理唯一元素集合的最佳選擇。

---

**練習建議**：嘗試實作自己的簡單 List 類，這將幫助您更深入理解 List 的內部工作原理。