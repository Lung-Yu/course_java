# Set 練習題

## 學習目標

通過這些練習，你將會掌握：
- HashSet、TreeSet、LinkedHashSet 的使用和選擇
- Set 的去重特性和集合運算
- 自定義物件的 equals 和 hashCode 實現
- Set 在演算法中的應用

---

## 🟢 基礎練習 (Easy)

### 練習 1.1：Set 基本操作和去重

**題目描述：**
創建一個程式，演示 Set 的基本操作和自動去重功能：
1. 創建一個 HashSet 存儲整數
2. 添加一些重複的數字
3. 演示 Set 的自動去重
4. 測試包含、移除、大小等操作
5. 比較不同 Set 實現的順序特性

**要求：**
- 同時測試 HashSet、TreeSet、LinkedHashSet
- 觀察插入順序的保持情況

**預期輸出：**
```
原始數據: [1, 2, 3, 2, 4, 1, 5, 3]

HashSet: [1, 2, 3, 4, 5] (無序)
TreeSet: [1, 2, 3, 4, 5] (排序)
LinkedHashSet: [1, 2, 3, 4, 5] (插入順序)

包含 3: true
移除 3 後: [1, 2, 4, 5]
大小: 4
```

<details>
<summary>💡 思路提示</summary>

```java
Set<Integer> hashSet = new HashSet<>();
Set<Integer> treeSet = new TreeSet<>();
Set<Integer> linkedHashSet = new LinkedHashSet<>();

int[] numbers = {1, 2, 3, 2, 4, 1, 5, 3};
for (int num : numbers) {
    hashSet.add(num);
    treeSet.add(num);
    linkedHashSet.add(num);
}
```
</details>

---

### 練習 1.2：字串去重和統計

**題目描述：**
給定一個包含重複單詞的文本，使用 Set 進行去重並統計：
1. 提取所有唯一的單詞
2. 統計唯一單詞的數量
3. 按字母順序排列單詞
4. 找出最長和最短的單詞

**測試文本：**
```
"The quick brown fox jumps over the lazy dog. The dog was sleeping under the tree."
```

**方法簽名：**
```java
public static Set<String> extractUniqueWords(String text)
public static String findLongestWord(Set<String> words)
public static String findShortestWord(Set<String> words)
```

<details>
<summary>💡 思路提示</summary>

1. 使用 `text.toLowerCase().split("\\W+")` 分割單詞
2. 使用 TreeSet 自動排序
3. 遍歷 Set 找出最長/最短單詞
</details>

---

### 練習 1.3：集合運算實現

**題目描述：**
實現基本的集合運算方法：聯集、交集、差集、對稱差集。

**方法簽名：**
```java
public static <T> Set<T> union(Set<T> set1, Set<T> set2)
public static <T> Set<T> intersection(Set<T> set1, Set<T> set2)
public static <T> Set<T> difference(Set<T> set1, Set<T> set2)
public static <T> Set<T> symmetricDifference(Set<T> set1, Set<T> set2)
```

**測試案例：**
```java
Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
Set<Integer> set2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));

System.out.println("聯集: " + union(set1, set2));           // [1, 2, 3, 4, 5, 6, 7, 8]
System.out.println("交集: " + intersection(set1, set2));    // [4, 5]
System.out.println("差集: " + difference(set1, set2));      // [1, 2, 3]
System.out.println("對稱差集: " + symmetricDifference(set1, set2)); // [1, 2, 3, 6, 7, 8]
```

<details>
<summary>🔧 技術提示</summary>

```java
public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
    Set<T> result = new HashSet<>(set1);
    result.addAll(set2);
    return result;
}
```
</details>

---

## 🟡 中等練習 (Medium)

### 練習 2.1：自定義物件的 Set 操作

**題目描述：**
創建一個 `Person` 類別，正確實現 `equals()` 和 `hashCode()` 方法，並測試在 Set 中的行為。

**Person 類別要求：**
- 包含 name、age、email 屬性
- 兩個 Person 物件相等當且僅當 email 相同
- 正確實現 hashCode 方法

**測試要求：**
1. 創建具有相同 email 但其他屬性不同的 Person 物件
2. 添加到 Set 中驗證去重效果
3. 測試在不同 Set 實現中的行為

**類別框架：**
```java
class Person {
    private String name;
    private int age;
    private String email;
    
    // 建構函數
    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    @Override
    public boolean equals(Object obj) {
        // 實現 equals 方法
    }
    
    @Override
    public int hashCode() {
        // 實現 hashCode 方法
    }
    
    @Override
    public String toString() {
        return String.format("Person{name='%s', age=%d, email='%s'}", name, age, email);
    }
}
```

<details>
<summary>🔧 技術提示</summary>

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Person person = (Person) obj;
    return Objects.equals(email, person.email);
}

@Override
public int hashCode() {
    return Objects.hash(email);
}
```
</details>

---

### 練習 2.2：找出兩個陣列的共同元素

**題目描述：**
給定兩個整數陣列，找出它們的共同元素（intersection），每個元素在結果中只出現一次。

**方法簽名：**
```java
public static int[] intersection(int[] nums1, int[] nums2)
```

**測試案例：**
```java
int[] nums1 = {1, 2, 2, 1};
int[] nums2 = {2, 2};
int[] result = intersection(nums1, nums2);
// 結果: [2]

int[] nums3 = {4, 9, 5};
int[] nums4 = {9, 4, 9, 8, 4};
int[] result2 = intersection(nums3, nums4);
// 結果: [4, 9] 或 [9, 4]
```

**進階要求：**
如果需要保持結果中元素的出現次數，如何修改演算法？

<details>
<summary>💡 思路提示</summary>

1. 將一個陣列轉換為 Set
2. 遍歷另一個陣列，檢查元素是否在 Set 中
3. 如果需要計算出現次數，使用 Map 而不是 Set
</details>

---

### 練習 2.3：實現布隆過濾器

**題目描述：**
使用 Set 實現一個簡單的布隆過濾器（Bloom Filter），用於快速檢測元素是否可能存在。

**要求：**
- 支援添加元素和查詢元素
- 不會有假陰性（如果元素存在，一定返回 true）
- 可能有假陽性（如果元素不存在，可能返回 true）
- 使用多個雜湊函數

**類別設計：**
```java
public class SimpleBloomFilter<T> {
    private final Set<Integer> bitSet;
    private final int size;
    private final int hashFunctionCount;
    
    public SimpleBloomFilter(int size, int hashFunctionCount) {
        this.size = size;
        this.hashFunctionCount = hashFunctionCount;
        this.bitSet = new HashSet<>();
    }
    
    public void add(T element) {
        // 實現添加邏輯
    }
    
    public boolean mightContain(T element) {
        // 實現查詢邏輯
    }
    
    private int hash(T element, int seed) {
        // 實現雜湊函數
    }
}
```

<details>
<summary>💡 思路提示</summary>

```java
public void add(T element) {
    for (int i = 0; i < hashFunctionCount; i++) {
        int hash = hash(element, i) % size;
        bitSet.add(Math.abs(hash));
    }
}

public boolean mightContain(T element) {
    for (int i = 0; i < hashFunctionCount; i++) {
        int hash = hash(element, i) % size;
        if (!bitSet.contains(Math.abs(hash))) {
            return false;
        }
    }
    return true;
}
```
</details>

---

## 🔴 困難練習 (Hard)

### 練習 3.1：實現並查集 (Union-Find)

**題目描述：**
使用 Set 的概念實現並查集資料結構，支援動態連通性查詢。

**要求實現的操作：**
```java
public class UnionFind {
    public void union(int x, int y)           // 連接兩個元素
    public boolean connected(int x, int y)    // 查詢兩個元素是否連通
    public int find(int x)                    // 找到元素的根節點
    public int getComponentCount()            // 獲取連通分量數量
    public Set<Integer> getComponent(int x)   // 獲取元素所在的連通分量
}
```

**測試案例：**
```java
UnionFind uf = new UnionFind(10);  // 0-9 十個元素
uf.union(0, 1);
uf.union(1, 2);
uf.union(3, 4);

System.out.println(uf.connected(0, 2));  // true
System.out.println(uf.connected(0, 3));  // false
System.out.println(uf.getComponentCount());  // 8 (0-1-2, 3-4, 5, 6, 7, 8, 9)
```

<details>
<summary>💡 思路提示</summary>

使用路徑壓縮和按秩合併優化：
1. 維護 parent 陣列表示父節點
2. 維護 rank 陣列表示樹的深度
3. find 操作時進行路徑壓縮
4. union 操作時按秩合併
</details>

---

### 練習 3.2：實現 LRU Set

**題目描述：**
實現一個具有 LRU（Least Recently Used）特性的 Set，當容量達到上限時，自動移除最久未訪問的元素。

**要求：**
- 固定容量
- 支援 add、contains、remove 操作
- contains 操作會更新元素的訪問時間
- 當容量滿時，自動移除最久未訪問的元素

**類別設計：**
```java
public class LRUSet<T> {
    private final int capacity;
    
    public LRUSet(int capacity) {
        this.capacity = capacity;
    }
    
    public boolean add(T element) {
        // 添加元素，可能觸發LRU淘汰
    }
    
    public boolean contains(T element) {
        // 檢查元素並更新訪問時間
    }
    
    public boolean remove(T element) {
        // 移除元素
    }
    
    public int size() {
        return currentSize;
    }
}
```

<details>
<summary>🔧 技術提示</summary>

可以結合 LinkedHashMap 的 accessOrder 特性或者使用雙向鏈表 + HashMap 實現。
</details>

---

### 練習 3.3：實現分散式 Set

**題目描述：**
實現一個模擬分散式環境的 Set，支援多個節點間的資料同步。

**要求：**
- 多個節點可以獨立添加元素
- 支援節點間的資料同步
- 處理衝突解決（如時間戳）
- 最終一致性保證

**基本框架：**
```java
public class DistributedSet<T> {
    private final String nodeId;
    private final Set<TimestampedElement<T>> localSet;
    private final Map<String, DistributedSet<T>> remoteNodes;
    
    public boolean add(T element) {
        // 添加帶時間戳的元素
    }
    
    public void sync(DistributedSet<T> remoteNode) {
        // 與遠程節點同步
    }
    
    public Set<T> getElements() {
        // 獲取當前有效元素
    }
    
    static class TimestampedElement<T> {
        final T element;
        final long timestamp;
        final String nodeId;
        
        // 實現比較邏輯
    }
}
```

---

## 🧠 進階挑戰

### 挑戰 4.1：實現位元 Set (BitSet)

**題目描述：**
實現一個高效的位元 Set，用於存儲大量的小整數。

**要求：**
- 使用位運算實現
- 支援基本的 Set 操作
- 記憶體使用量與最大元素值成正比，而不是元素數量

**類別設計：**
```java
public class BitSet {
    private long[] bits;
    private int maxValue;
    
    public BitSet(int maxValue) {
        this.maxValue = maxValue;
        this.bits = new long[(maxValue + 63) / 64];
    }
    
    public void add(int value) {
        // 設置對應位為 1
    }
    
    public boolean contains(int value) {
        // 檢查對應位是否為 1
    }
    
    public void remove(int value) {
        // 設置對應位為 0
    }
    
    public BitSet union(BitSet other) {
        // 位或運算
    }
    
    public BitSet intersection(BitSet other) {
        // 位與運算
    }
}
```

---

### 挑戰 4.2：實現機率性 Set

**題目描述：**
實現一個機率性 Set，使用 Count-Min Sketch 或類似演算法，在有限記憶體下處理大量資料流。

**特性：**
- 固定記憶體使用量
- 可能有假陽性，但沒有假陰性
- 適合處理無法全部載入記憶體的大資料集

---

## 📊 實戰專案

### 專案：社交網路好友推薦系統

**專案描述：**
使用 Set 實現一個社交網路的好友推薦系統。

**功能要求：**
1. **用戶管理**
   - 添加用戶
   - 建立好友關係

2. **好友推薦演算法**
   - 共同好友推薦
   - 二度好友推薦
   - 基於興趣的推薦

3. **群組功能**
   - 創建興趣群組
   - 群組成員管理
   - 基於群組的推薦

**核心類別設計：**
```java
class User {
    private String id;
    private String name;
    private Set<String> friends;
    private Set<String> interests;
    private Set<String> groups;
}

class SocialNetwork {
    private Set<User> users;
    private Map<String, User> userIndex;
    
    public Set<User> recommendFriends(String userId) {
        // 實現好友推薦邏輯
    }
    
    public Set<User> findMutualFriends(String user1, String user2) {
        // 找共同好友
    }
    
    public Set<User> findUsersWithSimilarInterests(String userId) {
        // 基於興趣推薦
    }
}
```

**評估指標：**
- 推薦準確率
- 演算法效能
- 記憶體使用效率

---

## 📝 練習總結

完成這些練習後，你應該能夠：

1. ✅ **熟練使用 Set 介面**
   - 理解不同 Set 實現的特性
   - 正確實現 equals 和 hashCode
   - 選擇合適的 Set 實現

2. ✅ **掌握集合運算**
   - 實現聯集、交集、差集操作
   - 理解集合論在程式設計中的應用
   - 解決去重和查重問題

3. ✅ **應用進階技巧**
   - 布隆過濾器的實現
   - 並查集資料結構
   - 機率性資料結構

4. ✅ **解決實際問題**
   - 資料去重和清理
   - 社交網路分析
   - 大資料處理

## 🎯 下一步

完成 Set 練習後，可以繼續學習：
- **[Map 練習](04-map-exercises.md)** - 學習映射和索引
- **[Queue/Deque 練習](05-queue-deque-exercises.md)** - 學習佇列和棧
- **[綜合練習](06-comprehensive-exercises.md)** - 挑戰更複雜的問題

記住：**Set 是去重的利器，集合運算的基礎**！ 💪