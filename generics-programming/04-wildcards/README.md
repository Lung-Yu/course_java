# 通配符 (Wildcards)

## 📖 學習目標

通過本節學習，你將掌握：
- 通配符的概念和作用
- 無界通配符 (`?`)
- 上界通配符 (`? extends T`)
- 下界通配符 (`? super T`)
- PECS 原則 (Producer Extends, Consumer Super)
- 通配符的實際應用場景

---

## 🎯 什麼是通配符？

通配符是泛型中的特殊語法，用於表示未知的類型。它提供了更大的靈活性，允許方法接受不同但相關的泛型類型。

### 🔍 為什麼需要通配符？

```java
// 沒有通配符的問題
void printList(List<Object> list) {
    // 只能接受 List<Object>，不能接受 List<String>
}

// 使用通配符的解決方案
void printList(List<?> list) {
    // 可以接受任何類型的 List
}
```

---

## 🌟 三種通配符類型

| 通配符 | 語法 | 說明 | 使用場景 |
|--------|------|------|----------|
| **無界通配符** | `?` | 表示任何類型 | 只讀取，不關心具體類型 |
| **上界通配符** | `? extends T` | T 或 T 的子類型 | 讀取數據（Producer） |
| **下界通配符** | `? super T` | T 或 T 的父類型 | 寫入數據（Consumer） |

---

## ❓ 無界通配符 (Unbounded Wildcards)

### 基本語法

```java
List<?> unknownList;
Set<?> unknownSet;
Map<?, ?> unknownMap;
```

### 使用場景

```java
/**
 * 列印任何類型的列表
 * @param list 任意類型的列表
 */
public static void printAnyList(List<?> list) {
    for (Object item : list) {  // 只能視為 Object
        System.out.println(item);
    }
    // list.add("hello");  // 編譯錯誤！無法添加元素
}

/**
 * 獲取集合大小
 * @param collection 任意類型的集合
 * @return 集合大小
 */
public static int getSize(Collection<?> collection) {
    return collection.size();  // 可以調用不依賴類型的方法
}

/**
 * 檢查集合是否為空
 * @param collection 任意類型的集合
 * @return 如果為空返回 true
 */
public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
}
```

### ⚠️ 限制

```java
List<?> list = new ArrayList<String>();
// list.add("hello");     // 編譯錯誤！
// list.add(new Object());// 編譯錯誤！
list.add(null);           // 只能添加 null

Object item = list.get(0); // 只能取出 Object 類型
```

---

## ⬆️ 上界通配符 (Upper Bounded Wildcards)

### 基本語法

```java
List<? extends Number> numbers;
List<? extends Animal> animals;
```

### 使用場景 - 讀取數據

```java
/**
 * 計算數字列表總和
 * @param numbers Number 或其子類型的列表
 * @return 總和
 */
public static double sumNumbers(List<? extends Number> numbers) {
    double sum = 0.0;
    for (Number number : numbers) {  // 可以視為 Number
        sum += number.doubleValue();
    }
    return sum;
}

/**
 * 查找最大的 Comparable 物件
 * @param <T> 類型參數
 * @param items Comparable 的子類型列表
 * @return 最大值
 */
public static <T extends Comparable<T>> T findMax(List<? extends T> items) {
    if (items.isEmpty()) {
        return null;
    }
    
    T max = items.get(0);
    for (T item : items) {
        if (item.compareTo(max) > 0) {
            max = item;
        }
    }
    return max;
}
```

### 使用範例

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3);
List<Float> floats = Arrays.asList(1.0f, 2.0f, 3.0f);

// 都可以傳給接受 List<? extends Number> 的方法
double intSum = sumNumbers(integers);    // ✅ Integer extends Number
double doubleSum = sumNumbers(doubles);  // ✅ Double extends Number
double floatSum = sumNumbers(floats);    // ✅ Float extends Number
```

### ⚠️ 限制

```java
List<? extends Number> numbers = new ArrayList<Integer>();
// numbers.add(42);        // 編譯錯誤！無法確定具體類型
// numbers.add(3.14);      // 編譯錯誤！
numbers.add(null);         // 只能添加 null

Number num = numbers.get(0); // 可以讀取為 Number
```

---

## ⬇️ 下界通配符 (Lower Bounded Wildcards)

### 基本語法

```java
List<? super Integer> numbers;
List<? super String> strings;
```

### 使用場景 - 寫入數據

```java
/**
 * 向列表添加整數
 * @param numbers Integer 或其父類型的列表
 */
public static void addIntegers(List<? super Integer> numbers) {
    numbers.add(1);      // ✅ 可以添加 Integer
    numbers.add(2);      // ✅ 可以添加 Integer
    numbers.add(3);      // ✅ 可以添加 Integer
    // numbers.add(3.14); // 編譯錯誤！不能添加 Double
}

/**
 * 將元素添加到集合中
 * @param <T> 元素類型
 * @param collection T 或其父類型的集合
 * @param items 要添加的元素
 */
public static <T> void addAll(Collection<? super T> collection, T... items) {
    for (T item : items) {
        collection.add(item);
    }
}
```

### 使用範例

```java
List<Number> numberList = new ArrayList<>();
List<Object> objectList = new ArrayList<>();

// 都可以接受 Integer
addIntegers(numberList);  // ✅ Number 是 Integer 的父類
addIntegers(objectList);  // ✅ Object 是 Integer 的父類

// List<Integer> intList = new ArrayList<>();
// addIntegers(intList);  // 編譯錯誤！Integer 不是 Integer 的父類
```

### ⚠️ 限制

```java
List<? super Integer> numbers = new ArrayList<Number>();
numbers.add(42);           // ✅ 可以添加 Integer
numbers.add(new Integer(5)); // ✅ 可以添加 Integer

Object obj = numbers.get(0); // 只能取出 Object
// Integer num = numbers.get(0); // 編譯錯誤！
```

---

## 🎯 PECS 原則

### Producer Extends, Consumer Super

這是使用通配符的黃金法則：

- **Producer Extends** (`? extends T`)：當你需要**讀取**資料時使用
- **Consumer Super** (`? super T`)：當你需要**寫入**資料時使用

### 完美的 PECS 示例

```java
/**
 * 複製列表元素
 * @param <T> 元素類型
 * @param source 來源列表（Producer - 生產數據）
 * @param destination 目標列表（Consumer - 消費數據）
 */
public static <T> void copy(List<? extends T> source, List<? super T> destination) {
    for (T item : source) {        // 從 source 讀取（Producer）
        destination.add(item);     // 向 destination 寫入（Consumer）
    }
}
```

### 實際應用

```java
// 設置不同的列表
List<Integer> integers = Arrays.asList(1, 2, 3);
List<Number> numbers = new ArrayList<>();
List<Object> objects = new ArrayList<>();

// PECS 使得以下調用都合法
copy(integers, numbers);  // Integer -> Number
copy(integers, objects);  // Integer -> Object
copy(numbers, objects);   // Number -> Object

System.out.println("Numbers: " + numbers);
System.out.println("Objects: " + objects);
```

---

## 🔄 通配符比較

### 範例：不同通配符的行為

```java
public class WildcardComparison {
    
    // 無界通配符 - 只能讀取為 Object
    public static void processUnbounded(List<?> list) {
        for (Object item : list) {
            System.out.println(item);
        }
        // list.add(anything);  // 編譯錯誤！
    }
    
    // 上界通配符 - 可以讀取為 Number
    public static double processUpperBounded(List<? extends Number> numbers) {
        double sum = 0;
        for (Number num : numbers) {
            sum += num.doubleValue();
        }
        // numbers.add(42);  // 編譯錯誤！
        return sum;
    }
    
    // 下界通配符 - 可以寫入 Integer
    public static void processLowerBounded(List<? super Integer> numbers) {
        numbers.add(42);
        numbers.add(100);
        // Integer num = numbers.get(0);  // 編譯錯誤！
        Object obj = numbers.get(0);     // 只能取出 Object
    }
}
```

---

## 🛠️ 實用通配符工具

### 集合工具方法

```java
public class WildcardUtils {
    
    /**
     * 檢查兩個集合是否相等
     * @param list1 第一個列表
     * @param list2 第二個列表
     * @return 如果相等返回 true
     */
    public static boolean listsEqual(List<?> list1, List<?> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        
        for (int i = 0; i < list1.size(); i++) {
            Object item1 = list1.get(i);
            Object item2 = list2.get(i);
            if (!Objects.equals(item1, item2)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 合併多個集合
     * @param <T> 元素類型
     * @param target 目標集合
     * @param sources 來源集合
     */
    @SafeVarargs
    public static <T> void mergeCollections(Collection<? super T> target, 
                                          Collection<? extends T>... sources) {
        for (Collection<? extends T> source : sources) {
            for (T item : source) {
                target.add(item);
            }
        }
    }
    
    /**
     * 過濾並複製元素
     * @param <T> 元素類型
     * @param source 來源集合
     * @param target 目標集合
     * @param predicate 過濾條件
     */
    public static <T> void filterAndCopy(Collection<? extends T> source,
                                       Collection<? super T> target,
                                       Predicate<? super T> predicate) {
        for (T item : source) {
            if (predicate.test(item)) {
                target.add(item);
            }
        }
    }
    
    /**
     * 統計滿足條件的元素數量
     * @param <T> 元素類型
     * @param collection 檢查的集合
     * @param predicate 統計條件
     * @return 滿足條件的元素數量
     */
    public static <T> long count(Collection<? extends T> collection,
                               Predicate<? super T> predicate) {
        long count = 0;
        for (T item : collection) {
            if (predicate.test(item)) {
                count++;
            }
        }
        return count;
    }
}
```

### 函數式工具方法

```java
public class FunctionalWildcards {
    
    /**
     * 轉換並收集結果
     * @param <T> 源類型
     * @param <R> 目標類型
     * @param source 來源集合
     * @param mapper 轉換函數
     * @param target 目標集合
     */
    public static <T, R> void mapAndCollect(Collection<? extends T> source,
                                          Function<? super T, ? extends R> mapper,
                                          Collection<? super R> target) {
        for (T item : source) {
            R result = mapper.apply(item);
            target.add(result);
        }
    }
    
    /**
     * 歸約操作
     * @param <T> 元素類型
     * @param <R> 結果類型
     * @param collection 集合
     * @param identity 初始值
     * @param accumulator 累加器
     * @return 歸約結果
     */
    public static <T, R> R reduce(Collection<? extends T> collection,
                                R identity,
                                BiFunction<? super R, ? super T, ? extends R> accumulator) {
        R result = identity;
        for (T item : collection) {
            result = accumulator.apply(result, item);
        }
        return result;
    }
}
```

---

## 💡 最佳實踐

### 1. 選擇合適的通配符

```java
// ✅ 好的實踐
public static double sum(List<? extends Number> numbers) {
    // 只需要讀取，使用 extends
}

public static void addNumbers(List<? super Integer> numbers) {
    // 需要寫入，使用 super
}

public static int getSize(Collection<?> collection) {
    // 不關心類型，使用無界通配符
}
```

### 2. 避免過度複雜化

```java
// ❌ 過度複雜
public static void complexMethod(List<? extends Map<? super String, ? extends Number>> list) {
    // 很難理解和使用
}

// ✅ 簡潔明了
public static void simpleMethod(List<? extends Map<String, Number>> list) {
    // 更容易理解
}
```

### 3. 文檔說明

```java
/**
 * 計算數字的總和
 * @param numbers 包含 Number 或其子類型的列表
 *                支援 Integer, Double, Float 等
 * @return 所有數字的總和
 */
public static double sum(List<? extends Number> numbers) {
    // 實現
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
java WildcardsDemo
```

---

## 🎯 重點回顧

1. **無界通配符 (`?`)**：適用於不關心具體類型的場景
2. **上界通配符 (`? extends T`)**：適用於讀取數據的場景
3. **下界通配符 (`? super T`)**：適用於寫入數據的場景
4. **PECS 原則**：Producer Extends, Consumer Super
5. **增加 API 的靈活性**：接受更廣泛的參數類型

---

## 📖 相關文件

- [泛型基礎 ←](../01-generics-basics/)
- [泛型類別 ←](../02-generic-classes/)
- [泛型方法 ←](../03-generic-methods/)
- [練習題 →](../exercises/)

---

**記住：掌握通配符是靈活使用泛型的關鍵，PECS 原則是你的最佳指南！** 🎯