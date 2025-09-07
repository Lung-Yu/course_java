# Map 練習題

## 學習目標

通過這些練習，你將會掌握：
- HashMap、TreeMap、LinkedHashMap 的使用和選擇
- Map 的鍵值對操作和遍歷方式
- Map 在演算法和資料處理中的應用
- 自定義鍵類別的實現要求

---

## 🟢 基礎練習 (Easy)

### 練習 1.1：Map 基本操作和遍歷

**題目描述：**
創建一個程式，演示 Map 的基本操作和不同的遍歷方式：
1. 創建不同類型的 Map (HashMap, TreeMap, LinkedHashMap)
2. 添加、查詢、修改、刪除鍵值對
3. 演示不同遍歷方式的特性
4. 比較不同 Map 實現的順序特性

**要求：**
- 測試所有基本 Map 操作
- 演示 entrySet()、keySet()、values() 的使用
- 觀察不同 Map 實現的排序特性

**預期功能：**
```java
Map<String, Integer> map = new HashMap<>();
map.put("apple", 5);
map.put("banana", 3);
map.put("orange", 8);

// 不同遍歷方式
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " = " + entry.getValue());
}

for (String key : map.keySet()) {
    System.out.println(key + " = " + map.get(key));
}

map.values().forEach(System.out::println);
```

<details>
<summary>💡 思路提示</summary>

```java
// 創建不同類型的 Map
Map<String, Integer> hashMap = new HashMap<>();
Map<String, Integer> treeMap = new TreeMap<>();
Map<String, Integer> linkedHashMap = new LinkedHashMap<>();

// 測試插入順序保持
String[] keys = {"banana", "apple", "orange"};
for (String key : keys) {
    hashMap.put(key, key.length());
    treeMap.put(key, key.length());
    linkedHashMap.put(key, key.length());
}
```
</details>

---

### 練習 1.2：字符計數器

**題目描述：**
實現一個字符計數器，統計字串中每個字符的出現次數。

**方法簽名：**
```java
public static Map<Character, Integer> countCharacters(String text)
public static void printCharacterCount(Map<Character, Integer> charCount)
public static char getMostFrequentChar(Map<Character, Integer> charCount)
```

**測試文本：**
```
"Hello World! How are you today?"
```

**預期輸出：**
```
字符計數結果：
  : 4
! : 1
H : 1
W : 1
a : 3
d : 3
...

最頻繁字符: 'o' (出現 4 次)
```

<details>
<summary>💡 思路提示</summary>

```java
public static Map<Character, Integer> countCharacters(String text) {
    Map<Character, Integer> charCount = new HashMap<>();
    for (char c : text.toCharArray()) {
        charCount.put(c, charCount.getOrDefault(c, 0) + 1);
    }
    return charCount;
}
```
</details>

---

### 練習 1.3：學生成績管理

**題目描述：**
創建一個學生成績管理系統，使用 Map 存儲學生資訊和成績。

**要求：**
- 使用學號作為鍵，學生物件作為值
- 支援添加、查詢、修改學生資訊
- 計算平均成績、最高分、最低分

**類別設計：**
```java
class Student {
    private String id;
    private String name;
    private Map<String, Double> grades;  // 科目 -> 成績
    
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.grades = new HashMap<>();
    }
    
    public void addGrade(String subject, double grade) {
        grades.put(subject, grade);
    }
    
    public double getAverageGrade() {
        // 計算平均成績
    }
    
    public String getBestSubject() {
        // 找出最高分科目
    }
}

class GradeManager {
    private Map<String, Student> students;
    
    public void addStudent(Student student) {
        // 添加學生
    }
    
    public Student getStudent(String id) {
        // 查詢學生
    }
    
    public Map<String, Double> getClassAverage() {
        // 計算各科目班級平均分
    }
}
```

<details>
<summary>🔧 技術提示</summary>

```java
public double getAverageGrade() {
    if (grades.isEmpty()) return 0.0;
    return grades.values().stream()
                 .mapToDouble(Double::doubleValue)
                 .average()
                 .orElse(0.0);
}
```
</details>

---

## 🟡 中等練習 (Medium)

### 練習 2.1：實現 LRU 快取

**題目描述：**
使用 LinkedHashMap 實現一個 LRU (Least Recently Used) 快取。

**要求：**
- 固定容量，超過容量時移除最久未使用的項目
- get 和 put 操作都算作使用
- 時間複雜度：O(1)

**類別設計：**
```java
public class LRUCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> cache;
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                // 實現移除邏輯
            }
        };
    }
    
    public V get(K key) {
        // 獲取值
    }
    
    public void put(K key, V value) {
        // 存入值
    }
    
    public int size() {
        return cache.size();
    }
}
```

**測試案例：**
```java
LRUCache<String, Integer> cache = new LRUCache<>(3);
cache.put("a", 1);
cache.put("b", 2);
cache.put("c", 3);
cache.get("a");  // 使用 a
cache.put("d", 4);  // 應該移除 b
System.out.println(cache.get("b"));  // null，已被移除
```

<details>
<summary>🔧 技術提示</summary>

```java
@Override
protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > capacity;
}

public V get(K key) {
    return cache.get(key);  // LinkedHashMap 會自動調整順序
}
```
</details>

---

### 練習 2.2：群組異位詞 (Group Anagrams)

**題目描述：**
給定一個字串陣列，將異位詞分組。異位詞是由相同字母重新排列形成的單詞。

**方法簽名：**
```java
public static List<List<String>> groupAnagrams(String[] strs)
```

**測試案例：**
```java
String[] input = {"eat", "tea", "tan", "ate", "nat", "bat"};
List<List<String>> result = groupAnagrams(input);
// 結果: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
```

**進階要求：**
- 如何處理大小寫不敏感的情況？
- 如何優化演算法的時間和空間複雜度？

<details>
<summary>💡 思路提示</summary>

```java
public static List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> map = new HashMap<>();
    
    for (String str : strs) {
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        String key = new String(chars);
        
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
    }
    
    return new ArrayList<>(map.values());
}
```
</details>

---

### 練習 2.3：實現稀疏矩陣

**題目描述：**
使用 Map 實現一個稀疏矩陣（大部分元素為 0 的矩陣），只存儲非零元素。

**要求：**
- 支援矩陣的基本操作：獲取、設置元素
- 實現矩陣加法和乘法
- 記憶體效率優化

**類別設計：**
```java
public class SparseMatrix {
    private final int rows;
    private final int cols;
    private final Map<String, Double> matrix;  // "row,col" -> value
    
    public SparseMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = new HashMap<>();
    }
    
    public void set(int row, int col, double value) {
        if (value == 0) {
            matrix.remove(getKey(row, col));
        } else {
            matrix.put(getKey(row, col), value);
        }
    }
    
    public double get(int row, int col) {
        return matrix.getOrDefault(getKey(row, col), 0.0);
    }
    
    public SparseMatrix add(SparseMatrix other) {
        // 實現矩陣加法
    }
    
    public SparseMatrix multiply(SparseMatrix other) {
        // 實現矩陣乘法
    }
    
    private String getKey(int row, int col) {
        return row + "," + col;
    }
}
```

<details>
<summary>🔧 技術提示</summary>

```java
public SparseMatrix add(SparseMatrix other) {
    if (this.rows != other.rows || this.cols != other.cols) {
        throw new IllegalArgumentException("Matrix dimensions must match");
    }
    
    SparseMatrix result = new SparseMatrix(rows, cols);
    
    // 添加第一個矩陣的元素
    for (Map.Entry<String, Double> entry : this.matrix.entrySet()) {
        result.matrix.put(entry.getKey(), entry.getValue());
    }
    
    // 添加第二個矩陣的元素
    for (Map.Entry<String, Double> entry : other.matrix.entrySet()) {
        String key = entry.getKey();
        double sum = result.matrix.getOrDefault(key, 0.0) + entry.getValue();
        if (sum == 0) {
            result.matrix.remove(key);
        } else {
            result.matrix.put(key, sum);
        }
    }
    
    return result;
}
```
</details>

---

### 練習 2.4：實現計數排序

**題目描述：**
使用 Map 實現計數排序演算法，適用於範圍有限的整數排序。

**方法簽名：**
```java
public static int[] countingSort(int[] nums)
public static int[] countingSortWithMap(int[] nums)  // 使用 Map 實現
```

**要求：**
- 使用 TreeMap 保證鍵的有序性
- 處理負數的情況
- 分析時間和空間複雜度

**測試案例：**
```java
int[] nums = {4, 2, 2, 8, 3, 3, 1};
int[] sorted = countingSortWithMap(nums);
// 結果: [1, 2, 2, 3, 3, 4, 8]
```

<details>
<summary>💡 思路提示</summary>

```java
public static int[] countingSortWithMap(int[] nums) {
    Map<Integer, Integer> countMap = new TreeMap<>();
    
    // 計數
    for (int num : nums) {
        countMap.put(num, countMap.getOrDefault(num, 0) + 1);
    }
    
    // 重建陣列
    int[] result = new int[nums.length];
    int index = 0;
    for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
        int value = entry.getKey();
        int count = entry.getValue();
        for (int i = 0; i < count; i++) {
            result[index++] = value;
        }
    }
    
    return result;
}
```
</details>

---

## 🔴 困難練習 (Hard)

### 練習 3.1：實現一致性雜湊 (Consistent Hashing)

**題目描述：**
實現一致性雜湊演算法，用於分散式系統中的負載均衡。

**要求：**
- 支援添加和移除節點
- 最小化重新分佈的資料量
- 實現虛擬節點以改善負載均衡

**類別設計：**
```java
public class ConsistentHashing<T> {
    private final TreeMap<Integer, String> ring;  // 雜湊環
    private final Map<String, T> nodes;           // 實際節點
    private final int virtualNodes;               // 虛擬節點數量
    
    public ConsistentHashing(int virtualNodes) {
        this.ring = new TreeMap<>();
        this.nodes = new HashMap<>();
        this.virtualNodes = virtualNodes;
    }
    
    public void addNode(String nodeId, T node) {
        // 添加節點和其虛擬節點
    }
    
    public void removeNode(String nodeId) {
        // 移除節點和其虛擬節點
    }
    
    public T getNode(String key) {
        // 根據鍵找到對應的節點
    }
    
    private int hash(String key) {
        // 雜湊函數
    }
}
```

**測試場景：**
```java
ConsistentHashing<String> ch = new ConsistentHashing<>(100);
ch.addNode("node1", "Server1");
ch.addNode("node2", "Server2");
ch.addNode("node3", "Server3");

String server = ch.getNode("user123");  // 找到負責的伺服器
```

<details>
<summary>💡 思路提示</summary>

```java
public void addNode(String nodeId, T node) {
    nodes.put(nodeId, node);
    for (int i = 0; i < virtualNodes; i++) {
        String virtualNodeId = nodeId + "#" + i;
        int hash = hash(virtualNodeId);
        ring.put(hash, nodeId);
    }
}

public T getNode(String key) {
    if (ring.isEmpty()) return null;
    
    int hash = hash(key);
    Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
    if (entry == null) {
        entry = ring.firstEntry();  // 環形結構
    }
    
    return nodes.get(entry.getValue());
}
```
</details>

---

### 練習 3.2：實現多層級快取

**題目描述：**
實現一個多層級快取系統，模擬 CPU 快取層次結構。

**要求：**
- L1、L2、L3 多層快取
- 不同層級有不同的容量和訪問時間
- 實現快取一致性協議
- 支援寫回和寫穿策略

**類別設計：**
```java
public class MultiLevelCache<K, V> {
    private final List<CacheLevel<K, V>> levels;
    private final CacheCoherence<K, V> coherence;
    
    static class CacheLevel<K, V> {
        private final int capacity;
        private final int accessTime;
        private final Map<K, CacheEntry<V>> cache;
        
        // 快取層級實現
    }
    
    static class CacheEntry<V> {
        private V value;
        private boolean dirty;
        private long timestamp;
        
        // 快取項目實現
    }
    
    public V get(K key) {
        // 多層級查找
    }
    
    public void put(K key, V value) {
        // 多層級寫入
    }
}
```

---

### 練習 3.3：實現分散式 Map

**題目描述：**
實現一個分散式 Map，支援多節點資料分片和複製。

**要求：**
- 資料分片策略
- 複製和一致性保證
- 故障檢測和恢復
- 動態擴縮容

**核心介面：**
```java
public interface DistributedMap<K, V> {
    V put(K key, V value);
    V get(K key);
    V remove(K key);
    
    void addNode(String nodeId);
    void removeNode(String nodeId);
    
    Map<String, Object> getClusterStatus();
}
```

---

## 🧠 進階挑戰

### 挑戰 4.1：實現 B+ 樹索引

**題目描述：**
實現一個基於 B+ 樹的索引結構，支援範圍查詢。

**要求：**
- 支援插入、查找、刪除操作
- 支援範圍查詢 (range query)
- 自動維護樹的平衡
- 葉子節點鏈接支援順序遍歷

---

### 挑戰 4.2：實現倒排索引

**題目描述：**
實現一個倒排索引，用於全文搜尋引擎。

**要求：**
- 支援文檔添加和刪除
- 實現 TF-IDF 計算
- 支援多詞查詢和片語查詢
- 實現查詢結果排序

**基本結構：**
```java
public class InvertedIndex {
    private Map<String, PostingList> index;  // 詞 -> 文檔列表
    private Map<String, Document> documents; // 文檔 ID -> 文檔
    
    static class PostingList {
        private List<Posting> postings;
        
        static class Posting {
            String docId;
            int frequency;
            List<Integer> positions;
        }
    }
}
```

---

## 📊 實戰專案

### 專案：電商推薦系統

**專案描述：**
使用 Map 實現一個電商推薦系統，包含多種推薦演算法。

**功能要求：**

1. **用戶行為追蹤**
   ```java
   class UserBehavior {
       Map<String, Double> viewHistory;    // 商品ID -> 觀看時長
       Map<String, Integer> purchaseHistory; // 商品ID -> 購買數量
       Map<String, Double> ratings;        // 商品ID -> 評分
   }
   ```

2. **商品特徵管理**
   ```java
   class Product {
       String id;
       String category;
       Map<String, String> attributes;     // 屬性名 -> 屬性值
       Map<String, Double> features;       // 特徵名 -> 特徵值
       double price;
   }
   ```

3. **推薦演算法**
   - **協同過濾**：基於用戶相似性
   - **內容過濾**：基於商品特徵
   - **混合推薦**：結合多種演算法

4. **效能指標**
   ```java
   class RecommendationMetrics {
       double precision;    // 精確率
       double recall;       // 召回率
       double coverage;     // 覆蓋率
       double diversity;    // 多樣性
   }
   ```

**核心演算法實現：**
```java
public class RecommendationEngine {
    private Map<String, UserBehavior> users;
    private Map<String, Product> products;
    
    public List<String> recommendByCollaborativeFiltering(String userId) {
        // 實現協同過濾推薦
    }
    
    public List<String> recommendByContentBased(String userId) {
        // 實現基於內容的推薦
    }
    
    public Map<String, Double> calculateUserSimilarity(String user1, String user2) {
        // 計算用戶相似度
    }
    
    public Map<String, Double> calculateProductSimilarity(String product1, String product2) {
        // 計算商品相似度
    }
}
```

---

### 專案：分散式快取系統

**專案描述：**
實現一個類似 Redis 的分散式快取系統。

**功能要求：**
1. **基本資料結構**
   - String, Hash, List, Set, ZSet
   
2. **持久化機制**
   - RDB 快照
   - AOF 日誌
   
3. **集群管理**
   - 主從複製
   - 分片策略
   - 故障轉移

4. **效能優化**
   - 記憶體池管理
   - 過期策略
   - 壓縮演算法

---

## 📝 練習總結

完成這些練習後，你應該能夠：

1. ✅ **熟練使用 Map 介面**
   - 理解不同 Map 實現的特性和適用場景
   - 正確實現自定義鍵的 equals 和 hashCode
   - 選擇合適的 Map 實現

2. ✅ **掌握進階應用**
   - LRU 快取實現
   - 一致性雜湊演算法
   - 分散式系統設計

3. ✅ **解決實際問題**
   - 資料索引和查詢優化
   - 推薦系統實現
   - 效能監控和調優

4. ✅ **系統設計能力**
   - 分散式快取架構
   - 多層級存儲設計
   - 故障恢復機制

## 🎯 下一步

完成 Map 練習後，可以繼續學習：
- **[Queue/Deque 練習](05-queue-deque-exercises.md)** - 學習佇列和雙端佇列
- **[綜合練習](06-comprehensive-exercises.md)** - 挑戰更複雜的綜合問題
- **[解答與說明](solutions/)** - 查看詳細解答和最佳實踐

記住：**Map 是資料組織的核心，索引和快取的基礎**！ 🗂️