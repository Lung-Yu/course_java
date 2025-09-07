import java.util.*;

/**
 * 泛型基礎使用示例
 * 
 * 本類別展示 Java 泛型的基本使用方法，包括：
 * 1. 泛型集合的使用
 * 2. 類型安全的優勢
 * 3. 基本的泛型語法
 * 4. 實用的泛型工具
 * 
 * 編譯：javac GenericsBasicsUsage.java
 * 執行：java GenericsBasicsUsage
 * 
 * @author Java Course
 * @version 1.0
 */
public class GenericsBasicsUsage {
    
    public static void main(String[] args) {
        System.out.println("=== 泛型基礎使用示例 ===\n");
        
        // 1. 展示沒有泛型的問題
        demonstrateProblemsWithoutGenerics();
        
        // 2. 展示泛型的解決方案
        demonstrateGenericsSolution();
        
        // 3. 基本泛型集合使用
        demonstrateGenericCollections();
        
        // 4. 類型安全的優勢
        demonstrateTypeSafety();
        
        // 5. 實用的泛型工具
        demonstrateGenericUtilities();
    }
    
    /**
     * 示例 1：展示沒有泛型的問題
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void demonstrateProblemsWithoutGenerics() {
        System.out.println("1. 沒有泛型的問題");
        System.out.println("==================");
        
        // 使用原始類型（Raw Type）
        List rawList = new ArrayList();
        rawList.add("字串");
        rawList.add(123);
        rawList.add(3.14);
        rawList.add(true);
        
        System.out.println("原始列表內容: " + rawList);
        
        // 需要強制轉換，容易出錯
        try {
            String first = (String) rawList.get(0);  // OK
            System.out.println("第一個元素（字串）: " + first);
            
            String second = (String) rawList.get(1); // 運行時異常！
            System.out.println("第二個元素: " + second);
        } catch (ClassCastException e) {
            System.err.println("❌ 類型轉換錯誤: " + e.getMessage());
        }
        
        // 編譯器無法提供類型安全保證
        System.out.println("⚠️  原始類型無法提供編譯時類型檢查");
        System.out.println();
    }
    
    /**
     * 示例 2：展示泛型的解決方案
     */
    private static void demonstrateGenericsSolution() {
        System.out.println("2. 泛型的解決方案");
        System.out.println("==================");
        
        // 使用泛型集合
        List<String> stringList = new ArrayList<>();
        stringList.add("Java");
        stringList.add("Python");
        stringList.add("JavaScript");
        // stringList.add(123);  // 編譯錯誤！
        
        System.out.println("字串列表: " + stringList);
        
        // 不需要強制轉換
        String firstLang = stringList.get(0);  // 類型安全
        System.out.println("第一個程式語言: " + firstLang);
        
        // 編譯時類型檢查
        System.out.println("✅ 泛型提供編譯時類型安全");
        
        // 不同類型的泛型集合
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Boolean> flags = Arrays.asList(true, false, true);
        
        System.out.println("數字列表: " + numbers);
        System.out.println("布林列表: " + flags);
        System.out.println();
    }
    
    /**
     * 示例 3：基本泛型集合使用
     */
    private static void demonstrateGenericCollections() {
        System.out.println("3. 基本泛型集合使用");
        System.out.println("======================");
        
        // List<T>
        System.out.println("--- List<T> 範例 ---");
        List<String> fruits = new ArrayList<>();
        fruits.add("蘋果");
        fruits.add("香蕉");
        fruits.add("橘子");
        System.out.println("水果列表: " + fruits);
        System.out.println("第一個水果: " + fruits.get(0));
        
        // Set<T>
        System.out.println("\n--- Set<T> 範例 ---");
        Set<Integer> uniqueNumbers = new HashSet<>();
        uniqueNumbers.add(1);
        uniqueNumbers.add(2);
        uniqueNumbers.add(2);  // 重複，不會被添加
        uniqueNumbers.add(3);
        System.out.println("唯一數字集合: " + uniqueNumbers);
        
        // Map<K, V>
        System.out.println("\n--- Map<K, V> 範例 ---");
        Map<String, Integer> studentAges = new HashMap<>();
        studentAges.put("Alice", 20);
        studentAges.put("Bob", 22);
        studentAges.put("Charlie", 21);
        System.out.println("學生年齡對應表: " + studentAges);
        System.out.println("Alice 的年齡: " + studentAges.get("Alice"));
        
        // Queue<T>
        System.out.println("\n--- Queue<T> 範例 ---");
        Queue<String> taskQueue = new LinkedList<>();
        taskQueue.offer("任務 1");
        taskQueue.offer("任務 2");
        taskQueue.offer("任務 3");
        System.out.println("任務佇列: " + taskQueue);
        System.out.println("執行任務: " + taskQueue.poll());
        System.out.println("剩餘任務: " + taskQueue);
        
        System.out.println();
    }
    
    /**
     * 示例 4：類型安全的優勢
     */
    private static void demonstrateTypeSafety() {
        System.out.println("4. 類型安全的優勢");
        System.out.println("====================");
        
        // 類型安全的容器
        SafeContainer<String> stringContainer = new SafeContainer<>();
        stringContainer.set("Hello World");
        String value = stringContainer.get();  // 不需要轉換
        System.out.println("字串容器的值: " + value);
        
        SafeContainer<Integer> intContainer = new SafeContainer<>();
        intContainer.set(42);
        Integer number = intContainer.get();
        System.out.println("整數容器的值: " + number);
        
        // 泛型比較器
        System.out.println("\n--- 泛型比較器 ---");
        Comparator<String> lengthComparator = 
            (s1, s2) -> Integer.compare(s1.length(), s2.length());
        
        List<String> words = new ArrayList<>(Arrays.asList("Java", "Python", "Go", "JavaScript"));
        System.out.println("排序前: " + words);
        
        words.sort(lengthComparator);
        System.out.println("按長度排序: " + words);
        
        // 反向排序
        words.sort(lengthComparator.reversed());
        System.out.println("反向排序: " + words);
        
        // Optional 類型安全
        System.out.println("\n--- Optional 類型安全 ---");
        Optional<String> optional1 = Optional.of("存在的值");
        Optional<String> optional2 = Optional.empty();
        
        optional1.ifPresent(val -> System.out.println("值 1: " + val));
        optional2.ifPresentOrElse(
            val -> System.out.println("值 2: " + val),
            () -> System.out.println("值 2 不存在")
        );
        
        System.out.println();
    }
    
    /**
     * 示例 5：實用的泛型工具
     */
    private static void demonstrateGenericUtilities() {
        System.out.println("5. 實用的泛型工具");
        System.out.println("====================");
        
        // 集合工具測試
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        List<Integer> numbers = Arrays.asList(10, 20, 30, 40, 50);
        List<String> emptyList = new ArrayList<>();
        
        System.out.println("--- 集合工具 ---");
        System.out.println("名字列表: " + names);
        System.out.println("是否為空: " + CollectionUtils.isEmpty(names));
        System.out.println("第一個名字: " + CollectionUtils.getFirst(names).orElse("無"));
        System.out.println("最後一個名字: " + CollectionUtils.getLast(names).orElse("無"));
        
        System.out.println("\n數字列表: " + numbers);
        System.out.println("第一個數字: " + CollectionUtils.getFirst(numbers).orElse(0));
        System.out.println("最後一個數字: " + CollectionUtils.getLast(numbers).orElse(0));
        
        System.out.println("\n空列表測試:");
        System.out.println("是否為空: " + CollectionUtils.isEmpty(emptyList));
        System.out.println("第一個元素: " + CollectionUtils.getFirst(emptyList).orElse("無"));
        
        // 範圍檢查工具
        System.out.println("\n--- 範圍檢查 ---");
        NumberRange<Integer> intRange = new NumberRange<>(1, 100);
        NumberRange<Double> doubleRange = new NumberRange<>(0.0, 1.0);
        
        System.out.println("整數範圍 [1, 100]:");
        System.out.println("  50 在範圍內: " + intRange.contains(50));
        System.out.println("  150 在範圍內: " + intRange.contains(150));
        
        System.out.println("浮點數範圍 [0.0, 1.0]:");
        System.out.println("  0.5 在範圍內: " + doubleRange.contains(0.5));
        System.out.println("  1.5 在範圍內: " + doubleRange.contains(1.5));
        
        // 鍵值對
        System.out.println("\n--- 鍵值對 ---");
        Pair<String, Integer> nameAge = new Pair<>("Alice", 25);
        Pair<Integer, String> idName = new Pair<>(1001, "Bob");
        
        System.out.println("名字-年齡: " + nameAge);
        System.out.println("ID-名字: " + idName);
        
        System.out.println();
    }
}

// ==================== 輔助類別 ====================

/**
 * 類型安全的泛型容器
 * @param <T> 儲存的資料類型
 */
class SafeContainer<T> {
    private T item;
    
    public void set(T item) {
        this.item = item;
    }
    
    public T get() {
        return item;
    }
    
    public boolean hasItem() {
        return item != null;
    }
    
    @Override
    public String toString() {
        return "SafeContainer{item=" + item + "}";
    }
}

/**
 * 泛型集合工具類別
 */
class CollectionUtils {
    
    /**
     * 檢查集合是否為空
     * @param <T> 集合元素類型
     * @param collection 待檢查的集合
     * @return 如果集合為 null 或空則返回 true
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * 獲取列表的第一個元素
     * @param <T> 元素類型
     * @param list 列表
     * @return Optional 包裝的第一個元素
     */
    public static <T> Optional<T> getFirst(List<T> list) {
        if (isEmpty(list)) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }
    
    /**
     * 獲取列表的最後一個元素
     * @param <T> 元素類型
     * @param list 列表
     * @return Optional 包裝的最後一個元素
     */
    public static <T> Optional<T> getLast(List<T> list) {
        if (isEmpty(list)) {
            return Optional.empty();
        }
        return Optional.of(list.get(list.size() - 1));
    }
    
    /**
     * 獲取集合大小
     * @param <T> 元素類型
     * @param collection 集合
     * @return 集合大小，如果為 null 則返回 0
     */
    public static <T> int size(Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }
}

/**
 * 數字範圍檢查器
 * @param <T> 必須是 Number 的子類型
 */
class NumberRange<T extends Number> {
    private final T min;
    private final T max;
    
    public NumberRange(T min, T max) {
        this.min = min;
        this.max = max;
    }
    
    /**
     * 檢查數值是否在範圍內
     * @param value 待檢查的值
     * @return 如果在範圍內返回 true
     */
    public boolean contains(T value) {
        if (value == null) {
            return false;
        }
        
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        
        return val >= minVal && val <= maxVal;
    }
    
    public T getMin() {
        return min;
    }
    
    public T getMax() {
        return max;
    }
    
    @Override
    public String toString() {
        return String.format("NumberRange[%s, %s]", min, max);
    }
}

/**
 * 泛型鍵值對
 * @param <K> 鍵的類型
 * @param <V> 值的類型
 */
class Pair<K, V> {
    private final K key;
    private final V value;
    
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