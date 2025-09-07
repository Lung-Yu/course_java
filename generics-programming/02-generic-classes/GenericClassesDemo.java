import java.util.*;
import java.io.Serializable;

/**
 * 泛型類別示例
 * 
 * 本類別展示 Java 泛型類別的設計和使用，包括：
 * 1. 單一類型參數的泛型類別
 * 2. 多類型參數的泛型類別
 * 3. 泛型類別的繼承
 * 4. 有界類型參數
 * 5. 實用的泛型類別設計
 * 
 * 編譯：javac GenericClassesDemo.java
 * 執行：java GenericClassesDemo
 * 
 * @author Java Course
 * @version 1.0
 */
public class GenericClassesDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 泛型類別示例 ===\n");
        
        // 1. 單一類型參數示例
        demonstrateSingleTypeParameter();
        
        // 2. 多類型參數示例
        demonstrateMultipleTypeParameters();
        
        // 3. 泛型類別繼承示例
        demonstrateGenericInheritance();
        
        // 4. 有界類型參數示例
        demonstrateBoundedTypeParameters();
        
        // 5. 實用泛型類別示例
        demonstratePracticalGenericClasses();
    }
    
    /**
     * 示例 1：單一類型參數的泛型類別
     */
    private static void demonstrateSingleTypeParameter() {
        System.out.println("1. 單一類型參數示例");
        System.out.println("======================");
        
        // 基本泛型容器
        GenericBox<String> stringBox = new GenericBox<>();
        stringBox.set("Hello Generics!");
        System.out.println("字串容器: " + stringBox);
        System.out.println("內容: " + stringBox.get());
        System.out.println("是否為空: " + stringBox.isEmpty());
        
        GenericBox<Integer> intBox = new GenericBox<>(42);
        System.out.println("整數容器: " + intBox);
        System.out.println("值: " + intBox.get());
        
        // 複雜類型
        GenericBox<List<String>> listBox = new GenericBox<>();
        listBox.set(Arrays.asList("Java", "Python", "JavaScript"));
        System.out.println("列表容器: " + listBox);
        
        // 自定義類型
        Person person = new Person("Alice", 30);
        GenericBox<Person> personBox = new GenericBox<>(person);
        System.out.println("人物容器: " + personBox);
        
        System.out.println();
    }
    
    /**
     * 示例 2：多類型參數的泛型類別
     */
    private static void demonstrateMultipleTypeParameters() {
        System.out.println("2. 多類型參數示例");
        System.out.println("====================");
        
        // 鍵值對
        GenericPair<String, Integer> nameAge = new GenericPair<>("Alice", 25);
        System.out.println("姓名-年齡: " + nameAge);
        System.out.println("姓名: " + nameAge.getKey());
        System.out.println("年齡: " + nameAge.getValue());
        
        GenericPair<Integer, String> idName = new GenericPair<>(1001, "Bob");
        System.out.println("ID-姓名: " + idName);
        
        // 三元組
        Triple<String, Integer, Double> studentInfo = 
            new Triple<>("Charlie", 22, 95.5);
        System.out.println("學生資訊: " + studentInfo);
        System.out.println("姓名: " + studentInfo.getFirst());
        System.out.println("年齡: " + studentInfo.getSecond());
        System.out.println("分數: " + studentInfo.getThird());
        
        // 座標點
        GenericPair<Double, Double> coordinate = new GenericPair<>(3.14, 2.71);
        System.out.println("座標: " + coordinate);
        
        // 不同類型組合
        GenericPair<List<String>, Map<String, Integer>> complexPair = 
            new GenericPair<>(
                Arrays.asList("a", "b", "c"),
                Map.of("x", 1, "y", 2)
            );
        System.out.println("複雜鍵值對: " + complexPair);
        
        System.out.println();
    }
    
    /**
     * 示例 3：泛型類別繼承
     */
    private static void demonstrateGenericInheritance() {
        System.out.println("3. 泛型類別繼承示例");
        System.out.println("========================");
        
        // 子類別保持泛型
        ColoredBox<String> coloredStringBox = new ColoredBox<>("Hello", "Red");
        System.out.println("彩色字串容器: " + coloredStringBox);
        System.out.println("顏色: " + coloredStringBox.getColor());
        
        ColoredBox<Integer> coloredIntBox = new ColoredBox<>(42, "Blue");
        System.out.println("彩色整數容器: " + coloredIntBox);
        
        // 子類別指定具體類型
        StringBox stringBox = new StringBox("Hello World");
        System.out.println("字串專用容器: " + stringBox);
        System.out.println("長度: " + stringBox.getLength());
        System.out.println("大寫: " + stringBox.toUpperCase());
        
        // 子類別添加新類型參數
        ExtendedBox<String, Integer> extendedBox = 
            new ExtendedBox<>("Content", 123);
        System.out.println("擴展容器: " + extendedBox);
        System.out.println("內容: " + extendedBox.get());
        System.out.println("元資料: " + extendedBox.getMetadata());
        
        System.out.println();
    }
    
    /**
     * 示例 4：有界類型參數
     */
    private static void demonstrateBoundedTypeParameters() {
        System.out.println("4. 有界類型參數示例");
        System.out.println("======================");
        
        // 數字容器
        NumberBox<Integer> intNumberBox = new NumberBox<>(42);
        System.out.println("整數容器: " + intNumberBox.getNumber());
        System.out.println("雙精度值: " + intNumberBox.getDoubleValue());
        System.out.println("是否為零: " + intNumberBox.isZero());
        System.out.println("是否為正數: " + intNumberBox.isPositive());
        
        NumberBox<Double> doubleNumberBox = new NumberBox<>(3.14159);
        System.out.println("浮點數容器: " + doubleNumberBox.getNumber());
        System.out.println("整數值: " + doubleNumberBox.getIntValue());
        System.out.println("是否為正數: " + doubleNumberBox.isPositive());
        
        NumberBox<Float> floatNumberBox = new NumberBox<>(0.0f);
        System.out.println("Float 容器: " + floatNumberBox.getNumber());
        System.out.println("是否為零: " + floatNumberBox.isZero());
        
        // 可排序容器（多重界限）
        SortableBox<String> sortableStringBox = new SortableBox<>("Hello");
        SortableBox<String> anotherSortableBox = new SortableBox<>("World");
        
        System.out.println("可排序容器 1: " + sortableStringBox.getItem());
        System.out.println("可排序容器 2: " + anotherSortableBox.getItem());
        System.out.println("比較結果: " + sortableStringBox.compareTo(anotherSortableBox));
        
        SortableBox<Integer> sortableIntBox = new SortableBox<>(100);
        SortableBox<Integer> anotherIntBox = new SortableBox<>(200);
        System.out.println("整數比較結果: " + sortableIntBox.compareTo(anotherIntBox));
        
        System.out.println();
    }
    
    /**
     * 示例 5：實用的泛型類別
     */
    private static void demonstratePracticalGenericClasses() {
        System.out.println("5. 實用泛型類別示例");
        System.out.println("======================");
        
        // 泛型堆疊
        System.out.println("--- 泛型堆疊 ---");
        GenericStack<String> stringStack = new GenericStack<>();
        stringStack.push("First");
        stringStack.push("Second");
        stringStack.push("Third");
        
        System.out.println("堆疊大小: " + stringStack.size());
        System.out.println("頂部元素: " + stringStack.peek());
        
        while (!stringStack.isEmpty()) {
            System.out.println("彈出: " + stringStack.pop());
        }
        
        // 數字堆疊
        GenericStack<Integer> numberStack = new GenericStack<>();
        for (int i = 1; i <= 5; i++) {
            numberStack.push(i);
        }
        
        System.out.println("數字堆疊內容:");
        while (!numberStack.isEmpty()) {
            System.out.print(numberStack.pop() + " ");
        }
        System.out.println();
        
        // 泛型快取
        System.out.println("\n--- 泛型快取 ---");
        GenericCache<String, String> stringCache = new GenericCache<>(3);
        stringCache.put("user1", "Alice");
        stringCache.put("user2", "Bob");
        stringCache.put("user3", "Charlie");
        
        System.out.println("快取大小: " + stringCache.size());
        System.out.println("user1: " + stringCache.get("user1"));
        System.out.println("user2: " + stringCache.get("user2"));
        
        // 添加第四個元素，會移除最舊的
        stringCache.put("user4", "David");
        System.out.println("添加 user4 後，user1: " + stringCache.get("user1"));
        System.out.println("user4: " + stringCache.get("user4"));
        
        // 分數快取
        GenericCache<Integer, Double> scoreCache = new GenericCache<>(5);
        scoreCache.put(1001, 95.5);
        scoreCache.put(1002, 87.3);
        scoreCache.put(1003, 92.1);
        
        System.out.println("學生 1001 分數: " + scoreCache.get(1001));
        System.out.println("學生 1002 分數: " + scoreCache.get(1002));
        
        // 結果容器
        System.out.println("\n--- 結果容器 ---");
        Result<String> successResult = Result.success("操作成功");
        Result<String> errorResult = Result.error("檔案不存在");
        
        System.out.println("成功結果: " + successResult);
        System.out.println("是否成功: " + successResult.isSuccess());
        System.out.println("值: " + successResult.getValue());
        
        System.out.println("錯誤結果: " + errorResult);
        System.out.println("是否成功: " + errorResult.isSuccess());
        System.out.println("錯誤訊息: " + errorResult.getError());
        
        Result<Integer> numberResult = Result.success(42);
        System.out.println("數字結果: " + numberResult.getValue());
        
        System.out.println();
    }
}

// ==================== 泛型類別定義 ====================

/**
 * 基本泛型容器
 * @param <T> 儲存的資料類型
 */
class GenericBox<T> {
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

/**
 * 泛型鍵值對
 * @param <K> 鍵的類型
 * @param <V> 值的類型
 */
class GenericPair<K, V> {
    private K key;
    private V value;
    
    public GenericPair(K key, V value) {
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
        
        GenericPair<?, ?> pair = (GenericPair<?, ?>) obj;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}

/**
 * 三元組容器
 * @param <T> 第一個值的類型
 * @param <U> 第二個值的類型
 * @param <V> 第三個值的類型
 */
class Triple<T, U, V> {
    private final T first;
    private final U second;
    private final V third;
    
    public Triple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    
    public T getFirst() {
        return first;
    }
    
    public U getSecond() {
        return second;
    }
    
    public V getThird() {
        return third;
    }
    
    @Override
    public String toString() {
        return String.format("Triple{%s, %s, %s}", first, second, third);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) obj;
        return Objects.equals(first, triple.first) &&
               Objects.equals(second, triple.second) &&
               Objects.equals(third, triple.third);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}

// ==================== 繼承範例 ====================

/**
 * 彩色容器（保持泛型）
 * @param <T> 內容類型
 */
class ColoredBox<T> extends GenericBox<T> {
    private String color;
    
    public ColoredBox(T content, String color) {
        super(content);
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return String.format("ColoredBox{content=%s, color=%s}", get(), color);
    }
}

/**
 * 字串專用容器（指定具體類型）
 */
class StringBox extends GenericBox<String> {
    public StringBox(String content) {
        super(content);
    }
    
    public int getLength() {
        String content = get();
        return content != null ? content.length() : 0;
    }
    
    public String toUpperCase() {
        String content = get();
        return content != null ? content.toUpperCase() : null;
    }
    
    public String toLowerCase() {
        String content = get();
        return content != null ? content.toLowerCase() : null;
    }
    
    public boolean contains(String substring) {
        String content = get();
        return content != null && content.contains(substring);
    }
}

/**
 * 擴展容器（添加新類型參數）
 * @param <T> 主要內容類型
 * @param <U> 額外資料類型
 */
class ExtendedBox<T, U> extends GenericBox<T> {
    private U metadata;
    
    public ExtendedBox(T content, U metadata) {
        super(content);
        this.metadata = metadata;
    }
    
    public U getMetadata() {
        return metadata;
    }
    
    public void setMetadata(U metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return String.format("ExtendedBox{content=%s, metadata=%s}", get(), metadata);
    }
}

// ==================== 有界類型參數範例 ====================

/**
 * 數字容器（上界限制）
 * @param <T> 必須是 Number 或其子類型
 */
class NumberBox<T extends Number> {
    private T number;
    
    public NumberBox(T number) {
        this.number = number;
    }
    
    public T getNumber() {
        return number;
    }
    
    public void setNumber(T number) {
        this.number = number;
    }
    
    public double getDoubleValue() {
        return number.doubleValue();
    }
    
    public int getIntValue() {
        return number.intValue();
    }
    
    public long getLongValue() {
        return number.longValue();
    }
    
    public float getFloatValue() {
        return number.floatValue();
    }
    
    public boolean isZero() {
        return number.doubleValue() == 0.0;
    }
    
    public boolean isPositive() {
        return number.doubleValue() > 0.0;
    }
    
    public boolean isNegative() {
        return number.doubleValue() < 0.0;
    }
    
    @Override
    public String toString() {
        return "NumberBox{number=" + number + "}";
    }
}

/**
 * 可排序容器（多重界限）
 * @param <T> 必須既實現 Comparable 又實現 Serializable
 */
class SortableBox<T extends Comparable<T> & Serializable> {
    private T item;
    
    public SortableBox(T item) {
        this.item = item;
    }
    
    public T getItem() {
        return item;
    }
    
    public void setItem(T item) {
        this.item = item;
    }
    
    public int compareTo(SortableBox<T> other) {
        return this.item.compareTo(other.item);
    }
    
    public boolean isGreaterThan(SortableBox<T> other) {
        return this.compareTo(other) > 0;
    }
    
    public boolean isLessThan(SortableBox<T> other) {
        return this.compareTo(other) < 0;
    }
    
    @Override
    public String toString() {
        return "SortableBox{item=" + item + "}";
    }
}

// ==================== 實用泛型類別 ====================

/**
 * 泛型堆疊實現
 * @param <T> 元素類型
 */
class GenericStack<T> {
    private Node<T> top;
    private int size;
    
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
        }
    }
    
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("堆疊為空");
        }
        
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }
    
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("堆疊為空");
        }
        return top.data;
    }
    
    public boolean isEmpty() {
        return top == null;
    }
    
    public int size() {
        return size;
    }
    
    public void clear() {
        top = null;
        size = 0;
    }
}

/**
 * 泛型快取實現（LRU）
 * @param <K> 鍵類型
 * @param <V> 值類型
 */
class GenericCache<K, V> {
    private final Map<K, V> cache;
    private final int maxSize;
    
    public GenericCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > GenericCache.this.maxSize;
            }
        };
    }
    
    public void put(K key, V value) {
        cache.put(key, value);
    }
    
    public V get(K key) {
        return cache.get(key);
    }
    
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }
    
    public void remove(K key) {
        cache.remove(key);
    }
    
    public int size() {
        return cache.size();
    }
    
    public void clear() {
        cache.clear();
    }
    
    public Set<K> keySet() {
        return new HashSet<>(cache.keySet());
    }
}

/**
 * 結果容器（成功/失敗）
 * @param <T> 成功值的類型
 */
class Result<T> {
    private final boolean success;
    private final T value;
    private final String error;
    
    private Result(boolean success, T value, String error) {
        this.success = success;
        this.value = value;
        this.error = error;
    }
    
    public static <T> Result<T> success(T value) {
        return new Result<>(true, value, null);
    }
    
    public static <T> Result<T> error(String error) {
        return new Result<>(false, null, error);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public boolean isFailure() {
        return !success;
    }
    
    public T getValue() {
        if (!success) {
            throw new IllegalStateException("無法從失敗結果獲取值");
        }
        return value;
    }
    
    public String getError() {
        if (success) {
            throw new IllegalStateException("無法從成功結果獲取錯誤");
        }
        return error;
    }
    
    @Override
    public String toString() {
        if (success) {
            return "Result.success(" + value + ")";
        } else {
            return "Result.error(" + error + ")";
        }
    }
}

// ==================== 輔助類別 ====================

/**
 * 人物類別
 */
class Person {
    private final String name;
    private final int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    @Override
    public String toString() {
        return String.format("Person{name='%s', age=%d}", name, age);
    }
}