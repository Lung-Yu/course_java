import java.util.*;
import java.util.function.*;
import java.io.Serializable;

/**
 * 泛型方法示例
 * 
 * 本類別展示 Java 泛型方法的定義和使用，包括：
 * 1. 靜態泛型方法
 * 2. 實例泛型方法
 * 3. 有界類型參數
 * 4. 類型推斷
 * 5. 實用工具方法
 * 
 * 編譯：javac GenericMethodsDemo.java
 * 執行：java GenericMethodsDemo
 * 
 * @author Java Course
 * @version 1.0
 */
public class GenericMethodsDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 泛型方法示例 ===\n");
        
        // 1. 靜態泛型方法示例
        demonstrateStaticGenericMethods();
        
        // 2. 實例泛型方法示例
        demonstrateInstanceGenericMethods();
        
        // 3. 有界類型參數示例
        demonstrateBoundedGenericMethods();
        
        // 4. 類型推斷示例
        demonstrateTypeInference();
        
        // 5. 實用工具方法示例
        demonstrateUtilityMethods();
    }
    
    /**
     * 示例 1：靜態泛型方法
     */
    private static void demonstrateStaticGenericMethods() {
        System.out.println("1. 靜態泛型方法示例");
        System.out.println("======================");
        
        // 陣列交換
        String[] fruits = {"蘋果", "香蕉", "橘子", "葡萄"};
        System.out.println("交換前: " + Arrays.toString(fruits));
        ArrayUtils.swap(fruits, 0, 3);
        System.out.println("交換後: " + Arrays.toString(fruits));
        
        Integer[] numbers = {1, 2, 3, 4, 5};
        System.out.println("數字陣列交換前: " + Arrays.toString(numbers));
        ArrayUtils.swap(numbers, 1, 4);
        System.out.println("數字陣列交換後: " + Arrays.toString(numbers));
        
        // 陣列搜尋
        int fruitIndex = ArrayUtils.indexOf(fruits, "香蕉");
        int numberIndex = ArrayUtils.indexOf(numbers, 5);
        System.out.println("香蕉的索引: " + fruitIndex);
        System.out.println("數字 5 的索引: " + numberIndex);
        
        // 陣列操作
        String[] words = {"Java", "Python", "Go", "JavaScript"};
        System.out.println("原始陣列: " + Arrays.toString(words));
        
        boolean containsJava = ArrayUtils.contains(words, "Java");
        boolean containsCpp = ArrayUtils.contains(words, "C++");
        System.out.println("包含 Java: " + containsJava);
        System.out.println("包含 C++: " + containsCpp);
        
        ArrayUtils.reverse(words);
        System.out.println("反轉後: " + Arrays.toString(words));
        
        // 創建陣列
        String[] createdArray = ArrayUtils.createArray("A", "B", "C", "D");
        System.out.println("創建的陣列: " + Arrays.toString(createdArray));
        
        List<String> wordList = ArrayUtils.arrayToList(words);
        System.out.println("陣列轉列表: " + wordList);
        
        System.out.println();
    }
    
    /**
     * 示例 2：實例泛型方法
     */
    private static void demonstrateInstanceGenericMethods() {
        System.out.println("2. 實例泛型方法示例");
        System.out.println("======================");
        
        // 字串容器
        GenericContainer<String> stringContainer = new GenericContainer<>();
        stringContainer.add("Java");
        stringContainer.add("Python");
        stringContainer.add("JavaScript");
        stringContainer.add("Go");
        
        System.out.println("原始字串容器: " + stringContainer.getItems());
        
        // 轉換為長度
        GenericContainer<Integer> lengthContainer = stringContainer.map(String::length);
        System.out.println("字串長度容器: " + lengthContainer.getItems());
        
        // 轉換為大寫
        GenericContainer<String> upperContainer = stringContainer.map(String::toUpperCase);
        System.out.println("大寫字串容器: " + upperContainer.getItems());
        
        // 過濾操作
        GenericContainer<String> filteredContainer = stringContainer.filter(s -> s.length() > 3);
        System.out.println("長度大於 3 的字串: " + filteredContainer.getItems());
        
        GenericContainer<String> javaContainer = stringContainer.filter(s -> s.contains("Java"));
        System.out.println("包含 Java 的字串: " + javaContainer.getItems());
        
        // 查找操作
        Optional<String> firstLong = stringContainer.findFirst(s -> s.length() > 5);
        System.out.println("第一個長度大於 5 的字串: " + firstLong.orElse("未找到"));
        
        Optional<String> firstPython = stringContainer.findFirst(s -> s.equals("Python"));
        System.out.println("查找 Python: " + firstPython.orElse("未找到"));
        
        // 數字容器示例
        GenericContainer<Integer> numberContainer = new GenericContainer<>();
        numberContainer.add(1);
        numberContainer.add(4);
        numberContainer.add(9);
        numberContainer.add(16);
        numberContainer.add(25);
        
        System.out.println("數字容器: " + numberContainer.getItems());
        
        // 轉換為平方根
        GenericContainer<Double> sqrtContainer = numberContainer.map(n -> Math.sqrt(n.doubleValue()));
        System.out.println("平方根容器: " + sqrtContainer.getItems());
        
        // 過濾偶數
        GenericContainer<Integer> evenContainer = numberContainer.filter(n -> n % 2 == 0);
        System.out.println("偶數容器: " + evenContainer.getItems());
        
        System.out.println();
    }
    
    /**
     * 示例 3：有界類型參數的泛型方法
     */
    private static void demonstrateBoundedGenericMethods() {
        System.out.println("3. 有界類型參數示例");
        System.out.println("======================");
        
        // 數字操作
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3, 4.4, 5.5);
        List<Float> floats = Arrays.asList(1.0f, 2.0f, 3.0f, 4.0f);
        
        System.out.println("整數列表: " + integers);
        System.out.println("整數總和: " + NumberUtils.sum(integers));
        System.out.println("整數平均值: " + NumberUtils.average(integers));
        System.out.println("整數最大值: " + NumberUtils.max(integers));
        System.out.println("整數最小值: " + NumberUtils.min(integers));
        
        System.out.println("\n浮點數列表: " + doubles);
        System.out.println("浮點數總和: " + NumberUtils.sum(doubles));
        System.out.println("浮點數平均值: " + NumberUtils.average(doubles));
        
        System.out.println("\nFloat 列表: " + floats);
        System.out.println("Float 總和: " + NumberUtils.sum(floats));
        
        // 比較操作
        List<String> words = Arrays.asList("Java", "Python", "Go", "JavaScript", "C++");
        System.out.println("\n字串列表: " + words);
        System.out.println("最大字串: " + ComparableUtils.max(words));
        System.out.println("最小字串: " + ComparableUtils.min(words));
        
        List<String> sortedWords = ComparableUtils.sortCopy(words);
        System.out.println("排序後: " + sortedWords);
        
        // 日期比較
        List<java.util.Date> dates = Arrays.asList(
            new Date(System.currentTimeMillis() - 86400000),  // 昨天
            new Date(),  // 今天
            new Date(System.currentTimeMillis() + 86400000)   // 明天
        );
        
        System.out.println("\n最早日期: " + ComparableUtils.min(dates));
        System.out.println("最晚日期: " + ComparableUtils.max(dates));
        
        // 多重界限示例
        List<String> serializableStrings = Arrays.asList("Hello", "World", "Java");
        List<String> processed = BoundedUtils.sortAndProcess(serializableStrings);
        System.out.println("處理後的可序列化字串: " + processed);
        
        System.out.println();
    }
    
    /**
     * 示例 4：類型推斷
     */
    private static void demonstrateTypeInference() {
        System.out.println("4. 類型推斷示例");
        System.out.println("==================");
        
        // 自動類型推斷
        List<String> stringList = TypeInferenceUtils.createList("a", "b", "c", "d");
        System.out.println("推斷為 String 列表: " + stringList);
        
        List<Integer> intList = TypeInferenceUtils.createList(1, 2, 3, 4, 5);
        System.out.println("推斷為 Integer 列表: " + intList);
        
        List<Double> doubleList = TypeInferenceUtils.createList(1.1, 2.2, 3.3);
        System.out.println("推斷為 Double 列表: " + doubleList);
        
        // 合併列表
        List<String> list1 = Arrays.asList("x", "y");
        List<String> list2 = Arrays.asList("z", "w");
        List<String> merged = TypeInferenceUtils.mergeLists(list1, list2);
        System.out.println("合併列表: " + merged);
        
        // 鍵值對創建
        Map<String, Integer> map1 = TypeInferenceUtils.createMap("one", 1, "two", 2);
        System.out.println("創建的 Map: " + map1);
        
        // 明確指定類型參數（有時必要）
        List<Number> numberList = TypeInferenceUtils.<Number>createList(1, 2.0, 3L, 4.0f);
        System.out.println("明確指定 Number 列表: " + numberList);
        
        // 配對操作
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        List<Integer> ages = Arrays.asList(25, 30, 35);
        List<Pair<String, Integer>> pairs = TypeInferenceUtils.zip(names, ages);
        System.out.println("配對結果: " + pairs);
        
        System.out.println();
    }
    
    /**
     * 示例 5：實用工具方法
     */
    private static void demonstrateUtilityMethods() {
        System.out.println("5. 實用工具方法示例");
        System.out.println("======================");
        
        // 集合工具
        List<String> fruits = Arrays.asList("蘋果", "香蕉", "橘子", "葡萄", "蘋果");
        System.out.println("水果列表: " + fruits);
        
        Optional<String> thirdFruit = CollectionUtils.safeGet(fruits, 2);
        Optional<String> tenthFruit = CollectionUtils.safeGet(fruits, 10);
        System.out.println("第三個水果: " + thirdFruit.orElse("無"));
        System.out.println("第十個水果: " + tenthFruit.orElse("無"));
        
        // 集合交集
        Set<String> set1 = new HashSet<>(Arrays.asList("Java", "Python", "C++", "Go"));
        Set<String> set2 = new HashSet<>(Arrays.asList("Python", "JavaScript", "Go", "Rust"));
        
        System.out.println("集合 1: " + set1);
        System.out.println("集合 2: " + set2);
        System.out.println("是否有交集: " + CollectionUtils.hasIntersection(set1, set2));
        
        Set<String> intersection = CollectionUtils.intersection(set1, set2);
        System.out.println("交集: " + intersection);
        
        Set<String> union = CollectionUtils.union(set1, set2);
        System.out.println("聯集: " + union);
        
        // 批次轉換
        List<String> words = Arrays.asList("Java", "Python", "Go");
        List<Integer> lengths = CollectionUtils.mapList(words, String::length);
        System.out.println("字串長度: " + lengths);
        
        List<String> upperWords = CollectionUtils.mapList(words, String::toUpperCase);
        System.out.println("大寫字串: " + upperWords);
        
        // 過濾操作
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> evenNumbers = CollectionUtils.filterList(numbers, n -> n % 2 == 0);
        List<Integer> largeNumbers = CollectionUtils.filterList(numbers, n -> n > 5);
        
        System.out.println("原始數字: " + numbers);
        System.out.println("偶數: " + evenNumbers);
        System.out.println("大於 5 的數字: " + largeNumbers);
        
        // 分組操作
        Map<Boolean, List<Integer>> groupedNumbers = CollectionUtils.partition(numbers, n -> n % 2 == 0);
        System.out.println("偶數分組: " + groupedNumbers.get(true));
        System.out.println("奇數分組: " + groupedNumbers.get(false));
        
        // 安全操作
        String nullString = null;
        String nonNullString = UtilityMethods.requireNonNull("Hello", "字串不能為 null");
        System.out.println("非空字串: " + nonNullString);
        
        try {
            UtilityMethods.requireNonNull(nullString, "這個字串是 null");
        } catch (IllegalArgumentException e) {
            System.out.println("捕獲異常: " + e.getMessage());
        }
        
        // 安全轉型
        Object obj1 = "Hello World";
        Object obj2 = 123;
        
        Optional<String> str1 = UtilityMethods.safeCast(obj1, String.class);
        Optional<String> str2 = UtilityMethods.safeCast(obj2, String.class);
        
        System.out.println("obj1 轉字串: " + str1.orElse("轉換失敗"));
        System.out.println("obj2 轉字串: " + str2.orElse("轉換失敗"));
        
        System.out.println();
    }
}

// ==================== 陣列工具類別 ====================

/**
 * 陣列操作工具類別
 */
class ArrayUtils {
    
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
    
    /**
     * 檢查陣列是否包含元素
     * @param <T> 元素類型
     * @param array 搜尋陣列
     * @param target 目標元素
     * @return 如果包含返回 true
     */
    public static <T> boolean contains(T[] array, T target) {
        return indexOf(array, target) != -1;
    }
    
    /**
     * 反轉陣列
     * @param <T> 元素類型
     * @param array 目標陣列
     */
    public static <T> void reverse(T[] array) {
        if (array == null) {
            return;
        }
        
        int left = 0;
        int right = array.length - 1;
        
        while (left < right) {
            swap(array, left, right);
            left++;
            right--;
        }
    }
    
    /**
     * 使用可變參數創建陣列
     * @param <T> 元素類型
     * @param elements 元素
     * @return 創建的陣列
     */
    @SafeVarargs
    public static <T> T[] createArray(T... elements) {
        return elements;
    }
    
    /**
     * 陣列轉列表
     * @param <T> 元素類型
     * @param array 源陣列
     * @return 轉換後的列表
     */
    public static <T> List<T> arrayToList(T[] array) {
        if (array == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(array));
    }
}

// ==================== 泛型容器類別 ====================

/**
 * 支援泛型方法的容器
 * @param <T> 元素類型
 */
class GenericContainer<T> {
    private List<T> items;
    
    public GenericContainer() {
        this.items = new ArrayList<>();
    }
    
    public void add(T item) {
        items.add(item);
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
    
    /**
     * 歸約操作
     * @param <R> 結果類型
     * @param identity 初始值
     * @param accumulator 累加器
     * @return 歸約結果
     */
    public <R> R reduce(R identity, BiFunction<R, T, R> accumulator) {
        R result = identity;
        for (T item : items) {
            result = accumulator.apply(result, item);
        }
        return result;
    }
    
    public List<T> getItems() {
        return new ArrayList<>(items);
    }
    
    public int size() {
        return items.size();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
}

// ==================== 數字工具類別 ====================

/**
 * 數字操作工具（有界類型參數）
 */
class NumberUtils {
    
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
     * @param <T> 必須是 Number 的子類型並實現 Comparable
     * @param numbers 數字列表
     * @return 最大值
     */
    public static <T extends Number & Comparable<T>> T max(List<T> numbers) {
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("列表不能為空");
        }
        
        T max = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            T current = numbers.get(i);
            if (current != null && current.compareTo(max) > 0) {
                max = current;
            }
        }
        return max;
    }
    
    /**
     * 找出最小值
     * @param <T> 必須是 Number 的子類型並實現 Comparable
     * @param numbers 數字列表
     * @return 最小值
     */
    public static <T extends Number & Comparable<T>> T min(List<T> numbers) {
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("列表不能為空");
        }
        
        T min = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            T current = numbers.get(i);
            if (current != null && current.compareTo(min) < 0) {
                min = current;
            }
        }
        return min;
    }
}

// ==================== 可比較物件工具類別 ====================

/**
 * Comparable 物件操作工具
 */
class ComparableUtils {
    
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
    
    /**
     * 找出最小值
     * @param <T> 必須實現 Comparable 介面
     * @param items 比較列表
     * @return 最小值
     */
    public static <T extends Comparable<T>> T min(List<T> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("列表不能為空");
        }
        
        T min = items.get(0);
        for (int i = 1; i < items.size(); i++) {
            T current = items.get(i);
            if (current != null && current.compareTo(min) < 0) {
                min = current;
            }
        }
        return min;
    }
    
    /**
     * 排序並回傳新列表
     * @param <T> 必須實現 Comparable 介面
     * @param items 原始列表
     * @return 排序後的新列表
     */
    public static <T extends Comparable<T>> List<T> sortCopy(List<T> items) {
        List<T> copy = new ArrayList<>(items);
        Collections.sort(copy);
        return copy;
    }
    
    /**
     * 檢查列表是否已排序
     * @param <T> 必須實現 Comparable 介面
     * @param items 檢查列表
     * @return 如果已排序返回 true
     */
    public static <T extends Comparable<T>> boolean isSorted(List<T> items) {
        for (int i = 1; i < items.size(); i++) {
            if (items.get(i - 1).compareTo(items.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }
}

// ==================== 多重界限工具類別 ====================

/**
 * 多重界限示例
 */
class BoundedUtils {
    
    /**
     * 處理既可比較又可序列化的物件
     * @param <T> 必須既實現 Comparable 又實現 Serializable
     * @param items 物件列表
     * @return 處理後的列表
     */
    public static <T extends Comparable<T> & Serializable> List<T> sortAndProcess(List<T> items) {
        List<T> sorted = new ArrayList<>(items);
        Collections.sort(sorted);
        
        // 可以進行序列化操作（這裡僅作示例）
        System.out.println("物件已排序且可序列化，大小: " + sorted.size());
        
        return sorted;
    }
    
    /**
     * 比較並檢查是否可序列化
     * @param <T> 多重界限類型
     * @param a 第一個物件
     * @param b 第二個物件
     * @return 比較結果
     */
    public static <T extends Comparable<T> & Serializable> int compareSerializable(T a, T b) {
        // 可以使用 Comparable 的方法
        int result = a.compareTo(b);
        
        // 也可以確認可序列化
        System.out.println("比較的物件都是可序列化的");
        
        return result;
    }
}

// ==================== 類型推斷工具類別 ====================

/**
 * 類型推斷示例
 */
class TypeInferenceUtils {
    
    /**
     * 創建列表的工廠方法
     * @param <T> 元素類型（自動推斷）
     * @param elements 元素
     * @return 創建的列表
     */
    @SafeVarargs
    public static <T> List<T> createList(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }
    
    /**
     * 合併兩個列表
     * @param <T> 元素類型
     * @param list1 第一個列表
     * @param list2 第二個列表
     * @return 合併後的列表
     */
    public static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        List<T> merged = new ArrayList<>(list1);
        merged.addAll(list2);
        return merged;
    }
    
    /**
     * 創建 Map 的工廠方法
     * @param <K> 鍵類型
     * @param <V> 值類型
     * @param key1 第一個鍵
     * @param value1 第一個值
     * @param key2 第二個鍵
     * @param value2 第二個值
     * @return 創建的 Map
     */
    public static <K, V> Map<K, V> createMap(K key1, V value1, K key2, V value2) {
        Map<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }
    
    /**
     * 配對兩個列表
     * @param <T> 第一個列表的類型
     * @param <U> 第二個列表的類型
     * @param list1 第一個列表
     * @param list2 第二個列表
     * @return 配對結果
     */
    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        List<Pair<T, U>> result = new ArrayList<>();
        int minSize = Math.min(list1.size(), list2.size());
        
        for (int i = 0; i < minSize; i++) {
            result.add(new Pair<>(list1.get(i), list2.get(i)));
        }
        
        return result;
    }
}

// ==================== 集合工具類別 ====================

/**
 * 集合操作工具
 */
class CollectionUtils {
    
    /**
     * 安全地獲取列表元素
     * @param <T> 元素類型
     * @param list 目標列表
     * @param index 索引
     * @return Optional 包裝的元素
     */
    public static <T> Optional<T> safeGet(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.get(index));
    }
    
    /**
     * 檢查兩個集合是否有交集
     * @param <T> 元素類型
     * @param col1 第一個集合
     * @param col2 第二個集合
     * @return 如果有交集返回 true
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
     * @param <T> 元素類型
     * @param set1 第一個集合
     * @param set2 第二個集合
     * @return 交集
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
     * 創建集合的聯集
     * @param <T> 元素類型
     * @param set1 第一個集合
     * @param set2 第二個集合
     * @return 聯集
     */
    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>();
        if (set1 != null) {
            result.addAll(set1);
        }
        if (set2 != null) {
            result.addAll(set2);
        }
        return result;
    }
    
    /**
     * 批次轉換列表
     * @param <T> 源類型
     * @param <R> 目標類型
     * @param source 源列表
     * @param mapper 轉換函數
     * @return 轉換後的列表
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
    
    /**
     * 過濾列表
     * @param <T> 元素類型
     * @param source 源列表
     * @param predicate 過濾條件
     * @return 過濾後的列表
     */
    public static <T> List<T> filterList(List<T> source, Predicate<T> predicate) {
        if (source == null) {
            return new ArrayList<>();
        }
        
        List<T> result = new ArrayList<>();
        for (T item : source) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    /**
     * 分割列表
     * @param <T> 元素類型
     * @param source 源列表
     * @param predicate 分割條件
     * @return 分割後的 Map
     */
    public static <T> Map<Boolean, List<T>> partition(List<T> source, Predicate<T> predicate) {
        Map<Boolean, List<T>> result = new HashMap<>();
        result.put(true, new ArrayList<>());
        result.put(false, new ArrayList<>());
        
        if (source != null) {
            for (T item : source) {
                result.get(predicate.test(item)).add(item);
            }
        }
        
        return result;
    }
}

// ==================== 通用工具方法 ====================

/**
 * 通用工具方法
 */
class UtilityMethods {
    
    /**
     * 要求物件非 null
     * @param <T> 物件類型
     * @param object 檢查的物件
     * @param message 錯誤訊息
     * @return 非 null 的物件
     * @throws IllegalArgumentException 如果物件為 null
     */
    public static <T> T requireNonNull(T object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
        return object;
    }
    
    /**
     * 安全轉型
     * @param <T> 目標類型
     * @param obj 源物件
     * @param type 目標類型
     * @return Optional 包裝的轉型結果
     */
    public static <T> Optional<T> safeCast(Object obj, Class<T> type) {
        if (type.isInstance(obj)) {
            return Optional.of(type.cast(obj));
        }
        return Optional.empty();
    }
    
    /**
     * 創建預設值函數
     * @param <T> 值類型
     * @param defaultValue 預設值
     * @return 回傳預設值的函數
     */
    public static <T> Supplier<T> defaultValue(T defaultValue) {
        return () -> defaultValue;
    }
    
    /**
     * 組合兩個函數
     * @param <T> 輸入類型
     * @param <R> 中間類型
     * @param <V> 輸出類型
     * @param first 第一個函數
     * @param second 第二個函數
     * @return 組合後的函數
     */
    public static <T, R, V> Function<T, V> compose(Function<T, R> first, Function<R, V> second) {
        return input -> second.apply(first.apply(input));
    }
}

// ==================== 輔助類別 ====================

/**
 * 簡單的鍵值對
 * @param <K> 鍵類型
 * @param <V> 值類型
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
        return String.format("(%s, %s)", key, value);
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