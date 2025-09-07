# 泛型方法 (Generic Methods)

## 📖 學習目標

通過本節學習，你將掌握：
- 如何定義和使用泛型方法
- 靜態泛型方法與實例泛型方法
- 有界類型參數在方法中的應用
- 泛型方法的推斷機制
- 實用的泛型工具方法設計

---

## 🎯 什麼是泛型方法？

泛型方法是在方法層級定義類型參數的方法，允許方法接受不同類型的參數，同時保持類型安全。

### 基本語法

```java
// 靜態泛型方法
public static <T> void methodName(T parameter) {
    // 方法實現
}

// 實例泛型方法
public <T> T methodName(T parameter) {
    // 方法實現
    return parameter;
}

// 多個類型參數
public static <T, U, R> R combine(T first, U second) {
    // 方法實現
}
```

### 🔍 關鍵要點

1. **類型參數位置**：在方法修飾符之後，回傳類型之前
2. **獨立於類別**：即使在非泛型類別中也可以定義泛型方法
3. **類型推斷**：編譯器可以自動推斷類型參數
4. **局部作用域**：類型參數只在該方法內有效

---

## 🔧 靜態泛型方法

### 基本範例

```java
/**
 * 交換陣列中兩個元素的位置
 * @param <T> 元素類型
 * @param array 目標陣列
 * @param i 第一個索引
 * @param j 第二個索引
 */
public static <T> void swap(T[] array, int i, int j) {
    if (array == null) {
        throw new IllegalArgumentException("陣列不能為 null");
    }
    if (i < 0 || i >= array.length || j < 0 || j >= array.length) {
        throw new IndexOutOfBoundsException("索引超出範圍");
    }
    
    T temp = array[i];
    array[i] = array[j];
    array[j] = temp;
}

/**
 * 在陣列中搜尋元素
 * @param <T> 元素類型
 * @param array 搜尋陣列
 * @param target 目標元素
 * @return 元素索引，找不到返回 -1
 */
public static <T> int indexOf(T[] array, T target) {
    if (array == null) {
        return -1;
    }
    
    for (int i = 0; i < array.length; i++) {
        if (Objects.equals(array[i], target)) {
            return i;
        }
    }
    return -1;
}
```

### 使用範例

```java
// 字串陣列
String[] words = {"Java", "Python", "Go", "JavaScript"};
GenericMethods.swap(words, 0, 3);  // 類型推斷為 String
int index = GenericMethods.indexOf(words, "Python");

// 整數陣列
Integer[] numbers = {1, 2, 3, 4, 5};
GenericMethods.swap(numbers, 1, 4);  // 類型推斷為 Integer
int numIndex = GenericMethods.indexOf(numbers, 3);
```

---

## 🏗️ 實例泛型方法

### 容器中的泛型方法

```java
public class GenericContainer<T> {
    private List<T> items;
    
    public GenericContainer() {
        this.items = new ArrayList<>();
    }
    
    /**
     * 轉換容器內容為其他類型
     * @param <R> 目標類型
     * @param mapper 轉換函數
     * @return 新的容器
     */
    public <R> GenericContainer<R> map(Function<T, R> mapper) {
        GenericContainer<R> result = new GenericContainer<>();
        for (T item : items) {
            result.add(mapper.apply(item));
        }
        return result;
    }
    
    /**
     * 過濾容器元素
     * @param predicate 過濾條件
     * @return 新的容器
     */
    public GenericContainer<T> filter(Predicate<T> predicate) {
        GenericContainer<T> result = new GenericContainer<>();
        for (T item : items) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    /**
     * 查找第一個滿足條件的元素
     * @param predicate 查找條件
     * @return Optional 包裝的結果
     */
    public Optional<T> findFirst(Predicate<T> predicate) {
        for (T item : items) {
            if (predicate.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
    
    public void add(T item) {
        items.add(item);
    }
    
    public List<T> getItems() {
        return new ArrayList<>(items);
    }
}
```

---

## 🎯 有界類型參數

### 數字操作方法

```java
/**
 * 計算數字列表的總和
 * @param <T> 必須是 Number 的子類型
 * @param numbers 數字列表
 * @return 總和
 */
public static <T extends Number> double sum(List<T> numbers) {
    double total = 0.0;
    for (T number : numbers) {
        total += number.doubleValue();
    }
    return total;
}

/**
 * 計算平均值
 * @param <T> 必須是 Number 的子類型
 * @param numbers 數字列表
 * @return 平均值
 */
public static <T extends Number> double average(List<T> numbers) {
    if (numbers.isEmpty()) {
        return 0.0;
    }
    return sum(numbers) / numbers.size();
}

/**
 * 找出最大值
 * @param <T> 必須實現 Comparable 介面
 * @param items 比較列表
 * @return 最大值
 */
public static <T extends Comparable<T>> T max(List<T> items) {
    if (items == null || items.isEmpty()) {
        throw new IllegalArgumentException("列表不能為空");
    }
    
    T max = items.get(0);
    for (int i = 1; i < items.size(); i++) {
        T current = items.get(i);
        if (current != null && current.compareTo(max) > 0) {
            max = current;
        }
    }
    return max;
}
```

### 多重界限

```java
/**
 * 處理既可比較又可序列化的物件
 * @param <T> 必須既實現 Comparable 又實現 Serializable
 * @param items 物件列表
 * @return 排序後的列表
 */
public static <T extends Comparable<T> & Serializable> List<T> sortAndSerialize(List<T> items) {
    List<T> sorted = new ArrayList<>(items);
    Collections.sort(sorted);
    
    // 可以進行序列化操作（這裡僅作示例）
    System.out.println("物件已排序且可序列化");
    
    return sorted;
}
```

---

## 🔄 類型推斷

### 自動類型推斷

```java
public class TypeInference {
    
    /**
     * 創建列表的工廠方法
     */
    public static <T> List<T> createList(T... elements) {
        List<T> list = new ArrayList<>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }
    
    /**
     * 合併兩個列表
     */
    public static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        List<T> merged = new ArrayList<>(list1);
        merged.addAll(list2);
        return merged;
    }
}

// 使用範例 - 編譯器自動推斷類型
List<String> stringList = TypeInference.createList("a", "b", "c");  // T = String
List<Integer> intList = TypeInference.createList(1, 2, 3);          // T = Integer

List<String> merged = TypeInference.mergeLists(
    Arrays.asList("x", "y"), 
    Arrays.asList("z")
);  // T = String
```

### 明確指定類型

```java
// 有時需要明確指定類型參數
List<Number> numberList = TypeInference.<Number>createList(1, 2.0, 3L);

// 或者當編譯器無法推斷時
Object result = GenericMethods.<String>processItem("Hello");
```

---

## 🛠️ 實用工具方法

### 集合工具方法

```java
public class CollectionUtils {
    
    /**
     * 安全地獲取列表元素
     */
    public static <T> Optional<T> safeGet(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.get(index));
    }
    
    /**
     * 檢查兩個集合是否有交集
     */
    public static <T> boolean hasIntersection(Collection<T> col1, Collection<T> col2) {
        if (col1 == null || col2 == null) {
            return false;
        }
        
        for (T item : col1) {
            if (col2.contains(item)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 創建集合的交集
     */
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        if (set1 == null || set2 == null) {
            return new HashSet<>();
        }
        
        Set<T> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }
    
    /**
     * 批次轉換
     */
    public static <T, R> List<R> mapList(List<T> source, Function<T, R> mapper) {
        if (source == null) {
            return new ArrayList<>();
        }
        
        List<R> result = new ArrayList<>();
        for (T item : source) {
            result.add(mapper.apply(item));
        }
        return result;
    }
}
```

### 陣列工具方法

```java
public class ArrayUtils {
    
    /**
     * 安全創建陣列
     */
    @SafeVarargs
    public static <T> T[] createArray(T... elements) {
        return elements;
    }
    
    /**
     * 陣列轉列表
     */
    public static <T> List<T> arrayToList(T[] array) {
        if (array == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(array));
    }
    
    /**
     * 反轉陣列
     */
    public static <T> void reverse(T[] array) {
        if (array == null) {
            return;
        }
        
        int left = 0;
        int right = array.length - 1;
        
        while (left < right) {
            T temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
    }
    
    /**
     * 檢查陣列是否包含元素
     */
    public static <T> boolean contains(T[] array, T target) {
        return indexOf(array, target) != -1;
    }
}
```

---

## 💡 最佳實踐

### 1. 方法命名

```java
// ✅ 好的命名
public static <T> List<T> filterNotNull(List<T> list) { }
public static <T extends Number> T findMaxNumber(T... numbers) { }

// ❌ 不好的命名
public static <T> List<T> doSomething(List<T> list) { }
public static <T> T process(T input) { }
```

### 2. 類型參數約束

```java
// ✅ 適當使用有界類型參數
public static <T extends Comparable<T>> T min(T a, T b) {
    return a.compareTo(b) <= 0 ? a : b;
}

// ❌ 過度寬泛的類型參數
public static <T> T compare(T a, T b) {
    // 無法比較，因為 T 沒有約束
}
```

### 3. 異常處理

```java
public static <T> T requireNonNull(T object, String message) {
    if (object == null) {
        throw new IllegalArgumentException(message);
    }
    return object;
}

public static <T> Optional<T> safeCast(Object obj, Class<T> type) {
    if (type.isInstance(obj)) {
        return Optional.of(type.cast(obj));
    }
    return Optional.empty();
}
```

---

## 📚 編譯和執行

### 編譯指令
```bash
javac *.java
```

### 執行指令
```bash
java GenericMethodsDemo
```

---

## 🎯 重點回顧

1. **泛型方法提供方法級別的類型參數**
2. **類型參數位於方法修飾符和回傳類型之間**
3. **支援類型推斷，減少冗餘程式碼**
4. **有界類型參數增強方法約束**
5. **靜態和實例方法都可以是泛型的**

---

## 📖 相關文件

- [泛型基礎 ←](../01-generics-basics/)
- [泛型類別 ←](../02-generic-classes/)
- [通配符 →](../04-wildcards/)
- [練習題 →](../exercises/)

---

**記住：泛型方法是實現可重用、類型安全工具方法的關鍵技術！** 🎯