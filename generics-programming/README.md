# 泛型程式設計 (Generics Programming)

## 📖 學習目標

通過本章學習，你將掌握：
- 泛型的基本概念和語法
- 泛型類別、介面、方法的定義和使用
- 通配符（Wildcards）的應用
- 類型擦除（Type Erasure）的原理
- 泛型的最佳實踐和限制

---

## 🔍 泛型基礎概念

### 什麼是泛型？

泛型（Generics）是 Java 5 引入的重要特性，允許在編譯時進行類型檢查，提供：

- **類型安全**：在編譯時檢查類型錯誤
- **消除強制轉換**：避免手動類型轉換
- **程式碼重用**：一套程式碼適用多種類型
- **效能提升**：避免運行時的類型檢查

### 為什麼需要泛型？

**沒有泛型的問題：**
```java
// Java 5 之前的寫法
List list = new ArrayList();
list.add("Hello");
list.add(123);  // 可以添加任何類型

// 需要強制轉換，可能出現 ClassCastException
String str = (String) list.get(0);  // OK
String str2 = (String) list.get(1); // 運行時異常！
```

**使用泛型的解決方案：**
```java
// Java 5 之後的寫法
List<String> list = new ArrayList<String>();
list.add("Hello");
// list.add(123);  // 編譯錯誤！類型不匹配

// 不需要強制轉換
String str = list.get(0);  // 編譯時保證類型安全
```

---

## 🎯 泛型語法基礎

### 基本語法

```java
// 泛型類別
class Container<T> {
    private T item;
    
    public void set(T item) {
        this.item = item;
    }
    
    public T get() {
        return item;
    }
}

// 泛型介面
interface Comparable<T> {
    int compareTo(T other);
}

// 泛型方法
public static <T> void swap(T[] array, int i, int j) {
    T temp = array[i];
    array[i] = array[j];
    array[j] = temp;
}
```

### 類型參數命名約定

| 符號 | 含義 | 範例 |
|------|------|------|
| T | Type（類型） | `List<T>` |
| E | Element（元素） | `Set<E>` |
| K | Key（鍵） | `Map<K, V>` |
| V | Value（值） | `Map<K, V>` |
| N | Number（數字） | `Comparable<N>` |
| S, U, V | 2nd, 3rd, 4th types | `Function<T, R>` |

---

## 📦 泛型類別

### 基本泛型類別

```java
/**
 * 基本的泛型容器類別
 * @param <T> 存儲的元素類型
 */
public class GenericBox<T> {
    private T content;
    
    public GenericBox() {
    }
    
    public GenericBox(T content) {
        this.content = content;
    }
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
    
    public boolean isEmpty() {
        return content == null;
    }
    
    @Override
    public String toString() {
        return "GenericBox{content=" + content + "}";
    }
}
```

### 多類型參數的泛型類別

```java
/**
 * 鍵值對容器
 * @param <K> 鍵的類型
 * @param <V> 值的類型
 */
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() {
        return key;
    }
    
    public V getValue() {
        return value;
    }
    
    public void setKey(K key) {
        this.key = key;
    }
    
    public void setValue(V value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format("Pair{key=%s, value=%s}", key, value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
```

### 泛型類別繼承

```java
/**
 * 泛型類別繼承示例
 */
// 1. 子類別保持泛型
class ColoredBox<T> extends GenericBox<T> {
    private String color;
    
    public ColoredBox(T content, String color) {
        super(content);
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
    
    @Override
    public String toString() {
        return String.format("ColoredBox{content=%s, color=%s}", get(), color);
    }
}

// 2. 子類別指定具體類型
class StringBox extends GenericBox<String> {
    public StringBox(String content) {
        super(content);
    }
    
    public int getLength() {
        String content = get();
        return content != null ? content.length() : 0;
    }
}

// 3. 子類別添加新的類型參數
class TripleBox<T, U> extends GenericBox<T> {
    private U secondContent;
    
    public TripleBox(T firstContent, U secondContent) {
        super(firstContent);
        this.secondContent = secondContent;
    }
    
    public U getSecondContent() {
        return secondContent;
    }
}
```

---

## 🔧 泛型方法

### 靜態泛型方法

```java
public class GenericMethods {
    
    /**
     * 交換陣列中兩個元素的位置
     * @param <T> 陣列元素類型
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
     * 在陣列中尋找指定元素
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
    
    /**
     * 將陣列轉換為 List
     * @param <T> 元素類型
     * @param array 源陣列
     * @return 轉換後的 List
     */
    public static <T> List<T> arrayToList(T[] array) {
        if (array == null) {
            return new ArrayList<>();
        }
        
        List<T> list = new ArrayList<>();
        for (T item : array) {
            list.add(item);
        }
        return list;
    }
    
    /**
     * 比較兩個 Comparable 物件
     * @param <T> 必須實現 Comparable 介面
     * @param a 第一個物件
     * @param b 第二個物件
     * @return 較大的物件
     */
    public static <T extends Comparable<T>> T max(T a, T b) {
        if (a == null && b == null) {
            return null;
        }
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        
        return a.compareTo(b) > 0 ? a : b;
    }
}
```

### 實例泛型方法

```java
public class GenericContainer<T> {
    private List<T> items;
    
    public GenericContainer() {
        this.items = new ArrayList<>();
    }
    
    /**
     * 添加元素
     */
    public void add(T item) {
        items.add(item);
    }
    
    /**
     * 泛型方法：將容器內容轉換為其他類型
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
     * 泛型方法：過濾元素
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
     * 泛型方法：查找第一個滿足條件的元素
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
    
    public List<T> getItems() {
        return new ArrayList<>(items);
    }
    
    public int size() {
        return items.size();
    }
    
    @Override
    public String toString() {
        return "GenericContainer{items=" + items + "}";
    }
}
```

---

## 🎭 有界類型參數

### 上界通配符（Upper Bounded）

```java
/**
 * 有界類型參數示例
 */
public class BoundedGenerics {
    
    /**
     * 計算數字列表的總和
     * T 必須是 Number 或其子類別
     */
    public static <T extends Number> double sum(List<T> numbers) {
        double total = 0.0;
        for (T number : numbers) {
            total += number.doubleValue();
        }
        return total;
    }
    
    /**
     * 找出 Comparable 列表中的最大值
     * T 必須實現 Comparable 介面
     */
    public static <T extends Comparable<T>> T findMax(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        
        T max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            T current = list.get(i);
            if (current != null && current.compareTo(max) > 0) {
                max = current;
            }
        }
        return max;
    }
    
    /**
     * 多重界限示例
     * T 必須既實現 Comparable 又實現 Serializable
     */
    public static <T extends Comparable<T> & Serializable> void process(T item) {
        // 可以使用 Comparable 的方法
        System.out.println("Item: " + item);
        
        // 也可以進行序列化操作
        // ... 序列化邏輯
    }
}
```

### 通配符類型

```java
/**
 * 通配符使用示例
 */
public class WildcardExamples {
    
    /**
     * 無界通配符：可以讀取任何類型的 List
     */
    public static void printList(List<?> list) {
        for (Object item : list) {
            System.out.println(item);
        }
    }
    
    /**
     * 上界通配符：可以讀取 Number 及其子類別的 List
     */
    public static double calculateSum(List<? extends Number> numbers) {
        double sum = 0.0;
        for (Number number : numbers) {
            sum += number.doubleValue();
        }
        return sum;
    }
    
    /**
     * 下界通配符：可以寫入 Integer 及其父類別的 List
     */
    public static void addNumbers(List<? super Integer> numbers) {
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        // numbers.add(3.14); // 編譯錯誤！不能添加 Double
    }
    
    /**
     * 複製列表內容
     * 展示 PECS 原則：Producer Extends, Consumer Super
     */
    public static <T> void copy(List<? extends T> source, List<? super T> destination) {
        for (T item : source) {
            destination.add(item);
        }
    }
    
    /**
     * 比較兩個列表是否相等
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
}
```

---

## 🏗️ 泛型介面

### 基本泛型介面

```java
/**
 * 泛型比較器介面
 */
@FunctionalInterface
public interface GenericComparator<T> {
    int compare(T o1, T o2);
    
    /**
     * 反轉比較器
     */
    default GenericComparator<T> reversed() {
        return (o1, o2) -> compare(o2, o1);
    }
    
    /**
     * 組合比較器
     */
    default GenericComparator<T> thenComparing(GenericComparator<T> other) {
        return (o1, o2) -> {
            int result = compare(o1, o2);
            return result != 0 ? result : other.compare(o1, o2);
        };
    }
}

/**
 * 泛型工廠介面
 */
@FunctionalInterface
public interface Factory<T> {
    T create();
    
    /**
     * 帶參數的工廠方法
     */
    static <T> Factory<T> from(Supplier<T> supplier) {
        return supplier::get;
    }
}

/**
 * 泛型轉換器介面
 */
@FunctionalInterface
public interface Transformer<T, R> {
    R transform(T input);
    
    /**
     * 組合轉換器
     */
    default <V> Transformer<T, V> andThen(Transformer<R, V> after) {
        return input -> after.transform(transform(input));
    }
    
    /**
     * 預先轉換
     */
    default <V> Transformer<V, R> compose(Transformer<V, T> before) {
        return input -> transform(before.transform(input));
    }
}
```

### 實現泛型介面

```java
/**
 * 字串長度比較器
 */
public class StringLengthComparator implements GenericComparator<String> {
    @Override
    public int compare(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        
        return Integer.compare(s1.length(), s2.length());
    }
}

/**
 * 泛型列表工廠
 */
public class ListFactory<T> implements Factory<List<T>> {
    private final int initialCapacity;
    
    public ListFactory(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }
    
    @Override
    public List<T> create() {
        return new ArrayList<>(initialCapacity);
    }
}

/**
 * 字串轉大寫轉換器
 */
public class UpperCaseTransformer implements Transformer<String, String> {
    @Override
    public String transform(String input) {
        return input != null ? input.toUpperCase() : null;
    }
}
```

---

## ⚡ 類型擦除與限制

### 類型擦除原理

```java
/**
 * 展示類型擦除的影響
 */
public class TypeErasureDemo {
    
    /**
     * 編譯後這兩個方法的簽名相同，會產生編譯錯誤
     */
    // public void process(List<String> list) { }
    // public void process(List<Integer> list) { }  // 編譯錯誤！
    
    /**
     * 可以通過不同的方法名或參數數量來區分
     */
    public void processStrings(List<String> strings) {
        for (String str : strings) {
            System.out.println("String: " + str);
        }
    }
    
    public void processIntegers(List<Integer> integers) {
        for (Integer num : integers) {
            System.out.println("Integer: " + num);
        }
    }
    
    /**
     * 運行時無法獲取泛型類型資訊
     */
    public static void demonstrateTypeErasure() {
        List<String> stringList = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();
        
        // 運行時都是 ArrayList.class
        System.out.println("String list class: " + stringList.getClass());
        System.out.println("Integer list class: " + integerList.getClass());
        System.out.println("Are classes equal? " + 
                          (stringList.getClass() == integerList.getClass()));
    }
    
    /**
     * 無法創建泛型陣列
     */
    public static <T> void genericArrayLimitation() {
        // T[] array = new T[10];  // 編譯錯誤！
        // List<String>[] arrays = new List<String>[10];  // 編譯錯誤！
        
        // 正確的做法
        @SuppressWarnings("unchecked")
        List<String>[] arrays = new List[10];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = new ArrayList<>();
        }
    }
}
```

### 泛型的限制

```java
/**
 * 泛型使用的各種限制
 */
public class GenericLimitations<T> {
    private T item;
    
    /**
     * 不能實例化類型參數
     */
    public void cannotInstantiateTypeParameter() {
        // T instance = new T();  // 編譯錯誤！
        
        // 解決方案：使用工廠方法
        // T instance = factory.create();
    }
    
    /**
     * 不能創建類型參數的陣列
     */
    public void cannotCreateGenericArray() {
        // T[] array = new T[10];  // 編譯錯誤！
        
        // 解決方案：使用 Object 陣列並強制轉換
        @SuppressWarnings("unchecked")
        T[] array = (T[]) new Object[10];
    }
    
    /**
     * 不能使用類型參數的靜態成員
     */
    // private static T staticField;  // 編譯錯誤！
    // public static T getStaticItem() { return staticField; }  // 編譯錯誤！
    
    /**
     * 不能對類型參數使用 instanceof
     */
    public boolean checkType(Object obj) {
        // return obj instanceof T;  // 編譯錯誤！
        
        // 解決方案：傳入 Class 物件
        // return clazz.isInstance(obj);
    }
    
    /**
     * 不能捕獲類型參數異常
     */
    public void cannotCatchGenericException() {
        try {
            // 一些操作
        }
        // catch (T e) {  // 編譯錯誤！如果 T 繼承 Exception
        //     System.out.println("Caught: " + e);
        // }
        catch (Exception e) {
            System.out.println("Caught: " + e);
        }
    }
}
```

---

## 🎯 泛型最佳實踐

### 1. PECS 原則

```java
/**
 * PECS 原則演示：Producer Extends, Consumer Super
 */
public class PECSDemo {
    
    /**
     * Producer - 使用 extends
     * 當你需要從結構中讀取數據時
     */
    public static double sumOfNumbers(List<? extends Number> numbers) {
        double sum = 0.0;
        for (Number number : numbers) {  // 讀取數據
            sum += number.doubleValue();
        }
        return sum;
    }
    
    /**
     * Consumer - 使用 super
     * 當你需要向結構中寫入數據時
     */
    public static void addNumbers(List<? super Integer> numbers) {
        numbers.add(1);    // 寫入數據
        numbers.add(2);
        numbers.add(3);
    }
    
    /**
     * 完美的 PECS 示例：Collections.copy
     */
    public static <T> void copyList(List<? extends T> source, List<? super T> dest) {
        for (T item : source) {     // source 是 producer
            dest.add(item);         // dest 是 consumer
        }
    }
}
```

### 2. 優先使用泛型

```java
/**
 * 泛型使用最佳實踐
 */
public class GenericBestPractices {
    
    /**
     * ✅ 好：使用泛型
     */
    public static <T> List<T> createList(T... items) {
        List<T> list = new ArrayList<>();
        for (T item : items) {
            list.add(item);
        }
        return list;
    }
    
    /**
     * ❌ 不好：使用原始類型
     */
    @SuppressWarnings("rawtypes")
    public static List createRawList(Object... items) {
        List list = new ArrayList();
        for (Object item : items) {
            list.add(item);
        }
        return list;
    }
    
    /**
     * ✅ 好：消除未檢查的警告
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list, Class<T> type) {
        T[] array = (T[]) Array.newInstance(type, list.size());
        return list.toArray(array);
    }
    
    /**
     * ✅ 好：使用有界類型參數
     */
    public static <T extends Comparable<T>> T min(T a, T b) {
        return a.compareTo(b) <= 0 ? a : b;
    }
    
    /**
     * ✅ 好：使用通配符增加靈活性
     */
    public static void printCollection(Collection<?> collection) {
        for (Object item : collection) {
            System.out.println(item);
        }
    }
}
```

### 3. 泛型設計模式

```java
/**
 * 泛型建造者模式
 */
public class GenericBuilder<T> {
    private final Class<T> type;
    private final Map<String, Object> properties;
    
    private GenericBuilder(Class<T> type) {
        this.type = type;
        this.properties = new HashMap<>();
    }
    
    public static <T> GenericBuilder<T> of(Class<T> type) {
        return new GenericBuilder<>(type);
    }
    
    public GenericBuilder<T> set(String property, Object value) {
        properties.put(property, value);
        return this;
    }
    
    public T build() {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            // 使用反射設置屬性
            // ... 實現細節
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("無法創建實例", e);
        }
    }
}

/**
 * 泛型單例模式
 */
public class GenericSingleton<T> {
    private static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        return (T) instances.computeIfAbsent(clazz, key -> {
            try {
                return key.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("無法創建單例實例", e);
            }
        });
    }
}
```

---

## 🎓 學習檢查點

完成本章學習後，你應該能夠：

### ✅ 基本能力
- [ ] 理解泛型的概念和優勢
- [ ] 能夠定義和使用泛型類別
- [ ] 能夠編寫泛型方法
- [ ] 理解類型參數的命名約定

### ✅ 進階能力
- [ ] 熟練使用有界類型參數
- [ ] 理解和應用通配符
- [ ] 掌握 PECS 原則
- [ ] 理解泛型介面的使用

### ✅ 深入理解
- [ ] 理解類型擦除的原理和影響
- [ ] 知道泛型的限制和解決方案
- [ ] 能夠遵循泛型最佳實踐
- [ ] 能夠設計泛型架構

---

## 🚀 下一步學習

掌握了泛型程式設計後，建議繼續學習：

1. **[集合框架進階](../arrays-and-collections/)** - 深入理解集合類別中的泛型應用
2. **[函數式程式設計](../functional-programming/)** - 學習 Lambda 表達式和 Stream API
3. **[反射與註解](../reflection-annotations/)** - 了解運行時類型操作
4. **[併發程式設計](../concurrency/)** - 學習泛型在多執行緒環境中的應用

**記住：泛型是現代 Java 程式設計的核心特性，掌握它對寫出高品質的程式碼至關重要！** 🎯