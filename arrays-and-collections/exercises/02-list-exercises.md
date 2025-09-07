# List 練習題

## 學習目標

通過這些練習，你將會掌握：
- ArrayList 和 LinkedList 的使用和選擇
- List 介面的常用方法
- Iterator 和 for-each 迴圈的使用
- List 相關演算法的實現

---

## 🟢 基礎練習 (Easy)

### 練習 1.1：List 基本操作

**題目描述：**
創建一個程式，演示 ArrayList 的基本操作：
1. 創建一個存儲學生姓名的 ArrayList
2. 添加 5 個學生姓名
3. 印出所有學生姓名
4. 在指定位置插入一個新學生
5. 移除一個學生
6. 檢查是否包含某個學生
7. 獲取 List 的大小

**要求：**
- 使用不同的添加和移除方法
- 提供清楚的輸出格式

**預期輸出：**
```
初始學生列表: [Alice, Bob, Charlie, Diana, Eve]
插入 Frank 到索引 2: [Alice, Bob, Frank, Charlie, Diana, Eve]
移除 Charlie: [Alice, Bob, Frank, Diana, Eve]
是否包含 Alice: true
是否包含 Charlie: false
學生總數: 5
```

<details>
<summary>💡 思路提示</summary>

1. 使用 `ArrayList<String>` 存儲學生姓名
2. 使用 `add()`, `add(index, element)`, `remove()`, `contains()`, `size()` 方法
</details>

---

### 練習 1.2：List 遍歷方法比較

**題目描述：**
創建一個包含整數的 List，使用以下三種方法遍歷並計算總和：
1. 傳統 for 迴圈（使用索引）
2. 增強 for 迴圈（for-each）
3. Iterator

**要求：**
- 測量每種方法的執行時間
- 比較不同遍歷方式的效能

**方法簽名：**
```java
public static long sumWithTraditionalFor(List<Integer> list)
public static long sumWithEnhancedFor(List<Integer> list)
public static long sumWithIterator(List<Integer> list)
```

<details>
<summary>💡 思路提示</summary>

使用 `System.nanoTime()` 測量執行時間：
```java
long start = System.nanoTime();
// 執行操作
long end = System.nanoTime();
long duration = end - start;
```
</details>

---

### 練習 1.3：ArrayList vs LinkedList 效能比較

**題目描述：**
實現一個程式，比較 ArrayList 和 LinkedList 在不同操作上的效能差異：
1. 順序添加元素（在尾部）
2. 隨機位置插入元素
3. 隨機訪問元素
4. 順序刪除元素（從頭部）

**要求：**
- 測試 10,000 個元素
- 記錄每種操作的時間
- 分析結果並解釋原因

<details>
<summary>💡 思路提示</summary>

預期結果：
- ArrayList：尾部添加快，隨機訪問快
- LinkedList：頭部/中間插入快，順序訪問相對較慢
</details>

---

## 🟡 中等練習 (Medium)

### 練習 2.1：自定義排序

**題目描述：**
創建一個 `Student` 類別，包含姓名、年齡和分數。實現以下排序功能：
1. 按年齡升序排序
2. 按分數降序排序
3. 按姓名字母順序排序
4. 複合排序：先按分數降序，分數相同則按年齡升序

**Student 類別：**
```java
class Student {
    private String name;
    private int age;
    private double score;
    
    // 建構函數、getter、setter、toString
}
```

**要求：**
- 使用 `Collections.sort()` 和 `Comparator`
- 提供多種排序選項

<details>
<summary>🔧 技術提示</summary>

```java
// 使用 Comparator
Collections.sort(students, Comparator.comparing(Student::getAge));

// 複合排序
Collections.sort(students, 
    Comparator.comparing(Student::getScore).reversed()
              .thenComparing(Student::getAge));
```
</details>

---

### 練習 2.2：List 的集合運算

**題目描述：**
實現以下方法，對兩個 List 進行集合運算：

**方法簽名：**
```java
public static <T> List<T> union(List<T> list1, List<T> list2)        // 聯集
public static <T> List<T> intersection(List<T> list1, List<T> list2) // 交集
public static <T> List<T> difference(List<T> list1, List<T> list2)   // 差集
```

**測試案例：**
```java
List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> list2 = Arrays.asList(4, 5, 6, 7, 8);

System.out.println(union(list1, list2));        // [1, 2, 3, 4, 5, 6, 7, 8]
System.out.println(intersection(list1, list2)); // [4, 5]
System.out.println(difference(list1, list2));   // [1, 2, 3]
```

**要求：**
- 結果不包含重複元素
- 保持元素的相對順序

<details>
<summary>💡 思路提示</summary>

可以使用 Set 來去重，或者使用 `contains()` 方法檢查元素是否存在。
</details>

---

### 練習 2.3：實現 LRU 快取

**題目描述：**
使用 List 實現一個簡單的 LRU（Least Recently Used）快取。

**要求：**
- 固定容量，當達到容量上限時移除最久未使用的元素
- 支援 `get(key)` 和 `put(key, value)` 操作
- `get` 操作會更新元素的使用時間

**類別設計：**
```java
class LRUCache<K, V> {
    private final int capacity;
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
    }
    
    public V get(K key) {
        // 實現獲取邏輯
    }
    
    public void put(K key, V value) {
        // 實現放入邏輯
    }
}
```

<details>
<summary>💡 思路提示</summary>

使用一個 List 存儲快取項目，最近使用的放在最前面，最久未使用的在最後面。
</details>

---

## 🔴 困難練習 (Hard)

### 練習 3.1：實現可持久化 List

**題目描述：**
實現一個不可變的 List，每次修改操作都返回一個新的 List 實例，但盡量共享內部結構以節省記憶體。

**要求實現的方法：**
```java
public class PersistentList<T> {
    public PersistentList<T> add(T element)
    public PersistentList<T> set(int index, T element)
    public PersistentList<T> remove(int index)
    public T get(int index)
    public int size()
}
```

**要求：**
- 所有操作都是不可變的
- 盡量共享內部結構
- 支援高效的隨機訪問

<details>
<summary>💡 思路提示</summary>

可以使用樹狀結構（如 Trie）或者 Copy-on-Write 策略實現結構共享。
</details>

---

### 練習 3.2：多線程安全的 List

**題目描述：**
實現一個線程安全的 List，支援多個線程同時讀寫。

**要求：**
- 支援基本的 List 操作
- 保證線程安全
- 盡量優化讀操作的效能（讀寫鎖）

**基本框架：**
```java
public class ThreadSafeList<T> {
    private final List<T> list = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public void add(T element) {
        // 實現線程安全的添加
    }
    
    public T get(int index) {
        // 實現線程安全的獲取
    }
    
    // 其他方法...
}
```

<details>
<summary>🔧 技術提示</summary>

```java
// 讀操作
lock.readLock().lock();
try {
    // 讀取操作
} finally {
    lock.readLock().unlock();
}

// 寫操作
lock.writeLock().lock();
try {
    // 寫入操作
} finally {
    lock.writeLock().unlock();
}
```
</details>

---

### 練習 3.3：實現分頁 List

**題目描述：**
實現一個支援分頁的 List，可以高效地處理大量資料。

**要求：**
- 支援分頁載入資料
- 只在記憶體中保持當前頁和相鄰頁的資料
- 支援前後翻頁操作

**介面設計：**
```java
interface PagedList<T> {
    List<T> getPage(int pageNumber);
    int getTotalPages();
    int getPageSize();
    boolean hasNextPage();
    boolean hasPreviousPage();
}
```

<details>
<summary>💡 思路提示</summary>

使用 `Map<Integer, List<T>>` 快取頁面資料，實現 LRU 策略自動清理不常用的頁面。
</details>

---

## 🧠 進階挑戰

### 挑戰 4.1：實現 SubList 功能

**題目描述：**
實現一個類似 `ArrayList.subList()` 的功能，返回原 List 的一個視圖（view），對 SubList 的修改會反映到原 List 上。

**要求：**
- SubList 是原 List 的視圖，不是複製
- 支援所有基本 List 操作
- 處理並發修改檢測

---

### 挑戰 4.2：實現記憶體高效的稀疏 List

**題目描述：**
實現一個稀疏 List，只存儲非空元素，適用於大部分位置為空的場景。

**要求：**
- 支援任意索引訪問
- 記憶體使用量與非空元素數量成正比
- 保持 List 介面的語義

**使用場景：**
```java
SparseList<String> list = new SparseList<>();
list.set(1000000, "Hello");
list.set(2000000, "World");
// 只佔用少量記憶體，而不是 200 萬個元素的空間
```

---

## 📊 效能測試專案

### 專案：List 效能基準測試

**任務描述：**
創建一個完整的效能測試框架，比較不同 List 實現在各種場景下的效能。

**測試項目：**
1. **插入效能測試**
   - 尾部插入
   - 頭部插入
   - 隨機位置插入

2. **訪問效能測試**
   - 順序訪問
   - 隨機訪問
   - 遍歷效能

3. **刪除效能測試**
   - 尾部刪除
   - 頭部刪除
   - 隨機位置刪除

4. **記憶體使用測試**
   - 記憶體占用量
   - GC 壓力測試

**要求：**
- 測試多種 List 實現（ArrayList, LinkedList, Vector 等）
- 不同資料量的測試（1K, 10K, 100K, 1M）
- 生成效能報告和圖表
- 提供優化建議

---

## 📝 練習總結

完成這些練習後，你應該能夠：

1. ✅ **熟練使用 List 介面**
   - 掌握 ArrayList 和 LinkedList 的特性
   - 選擇合適的 List 實現
   - 使用各種遍歷方法

2. ✅ **實現進階功能**
   - 自定義排序和比較
   - 線程安全的 List 操作
   - 記憶體優化技巧

3. ✅ **理解底層實現**
   - List 的內部結構
   - 時間複雜度分析
   - 記憶體使用優化

4. ✅ **解決實際問題**
   - 快取實現
   - 分頁處理
   - 效能優化

## 🎯 下一步

完成 List 練習後，可以繼續學習：
- **[Set 練習](03-set-exercises.md)** - 學習集合和去重
- **[Map 練習](04-map-exercises.md)** - 學習映射和索引
- **[綜合練習](06-comprehensive-exercises.md)** - 挑戰更複雜的問題

記住：**理解原理，注重效能，靈活應用**！ 💪