import java.util.*;
import java.util.function.*;

/**
 * 泛型基礎示例
 * 
 * 本類別展示 Java 泛型的基本概念和語法，包括：
 * 1. 泛型類別的定義和使用
 * 2. 泛型方法的編寫
 * 3. 有界類型參數
 * 4. 通配符的應用
 * 5. 泛型的實際應用場景
 * 
 * @author Java Course
 * @version 1.0
 */
public class GenericsBasicsDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Java 泛型基礎示例 ===\n");
        
        // 1. 基本泛型容器示例
        demonstrateGenericContainer();
        
        // 2. 泛型方法示例
        demonstrateGenericMethods();
        
        // 3. 有界類型參數示例
        demonstrateBoundedTypeParameters();
        
        // 4. 通配符示例
        demonstrateWildcards();
        
        // 5. 泛型集合操作
        demonstrateGenericCollections();
        
        // 6. 實際應用場景
        demonstratePracticalUseCases();
    }
    
    /**
     * 示例 1：基本泛型容器
     */
    private static void demonstrateGenericContainer() {
        System.out.println("1. 基本泛型容器示例");
        System.out.println("========================");
        
        // 創建不同類型的泛型容器
        SimpleBox<String> stringBox = new SimpleBox<>("Hello, Generics!");
        SimpleBox<Integer> intBox = new SimpleBox<>(42);
        SimpleBox<Double> doubleBox = new SimpleBox<>(3.14159);
        
        System.out.println("字串容器: " + stringBox);
        System.out.println("整數容器: " + intBox);
        System.out.println("浮點數容器: " + doubleBox);
        
        // 展示類型安全
        String str = stringBox.getValue();  // 不需要強制轉換
        Integer num = intBox.getValue();    // 編譯時保證類型安全
        
        System.out.println("取出的字串: " + str);
        System.out.println("取出的整數: " + num);
        
        // 鍵值對容器示例
        KeyValuePair<String, Integer> pair = new KeyValuePair<>("年齡", 25);
        System.out.println("鍵值對: " + pair);
        
        KeyValuePair<Integer, String> reversePair = new KeyValuePair<>(1, "第一名");
        System.out.println("反向鍵值對: " + reversePair);
        
        System.out.println();
    }
    
    /**
     * 示例 2：泛型方法
     */
    private static void demonstrateGenericMethods() {
        System.out.println("2. 泛型方法示例");
        System.out.println("==================");
        
        // 陣列交換元素
        String[] fruits = {"蘋果", "香蕉", "橘子", "葡萄"};
        System.out.println("交換前: " + Arrays.toString(fruits));
        swap(fruits, 0, 2);
        System.out.println("交換後: " + Arrays.toString(fruits));
        
        Integer[] numbers = {1, 2, 3, 4, 5};
        System.out.println("數字陣列交換前: " + Arrays.toString(numbers));
        swap(numbers, 1, 4);
        System.out.println("數字陣列交換後: " + Arrays.toString(numbers));
        
        // 搜尋元素
        int index = findIndex(fruits, "香蕉");
        System.out.println("香蕉的索引: " + index);
        
        int numIndex = findIndex(numbers, 5);
        System.out.println("數字 5 的索引: " + numIndex);
        
        // 比較元素
        System.out.println("比較 'apple' 和 'banana': " + compare("apple", "banana"));
        System.out.println("比較 10 和 5: " + compare(10, 5));
        System.out.println("比較 3.14 和 2.71: " + compare(3.14, 2.71));
        
        // 創建陣列
        String[] strArray = createArray("Hello", "World", "Java", "Generics");
        System.out.println("創建的字串陣列: " + Arrays.toString(strArray));
        
        Integer[] intArray = createArray(1, 2, 3, 4, 5);
        System.out.println("創建的整數陣列: " + Arrays.toString(intArray));
        
        System.out.println();
    }
    
    /**
     * 示例 3：有界類型參數
     */
    private static void demonstrateBoundedTypeParameters() {
        System.out.println("3. 有界類型參數示例");
        System.out.println("======================");
        
        // 數字處理
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3, 4.4, 5.5);
        List<Float> floats = Arrays.asList(1.0f, 2.0f, 3.0f);
        
        System.out.println("整數列表總和: " + calculateSum(integers));
        System.out.println("浮點數列表總和: " + calculateSum(doubles));
        System.out.println("Float 列表總和: " + calculateSum(floats));
        
        System.out.println("整數列表平均值: " + calculateAverage(integers));
        System.out.println("浮點數列表平均值: " + calculateAverage(doubles));
        
        // 查找最大值
        List<String> words = Arrays.asList("java", "python", "javascript", "go");
        System.out.println("字串列表中最長的: " + findMaximum(words));
        
        List<Integer> nums = Arrays.asList(10, 5, 8, 20, 15);
        System.out.println("數字列表中最大的: " + findMaximum(nums));
        
        // 範圍檢查
        NumberRange<Integer> intRange = new NumberRange<>(1, 100);
        System.out.println("50 是否在範圍內: " + intRange.contains(50));
        System.out.println("150 是否在範圍內: " + intRange.contains(150));
        
        NumberRange<Double> doubleRange = new NumberRange<>(0.0, 1.0);
        System.out.println("0.5 是否在範圍內: " + doubleRange.contains(0.5));
        System.out.println("1.5 是否在範圍內: " + doubleRange.contains(1.5));
        
        System.out.println();
    }
    
    /**
     * 示例 4：通配符
     */
    private static void demonstrateWildcards() {
        System.out.println("4. 通配符示例");
        System.out.println("================");
        
        // 無界通配符
        List<String> stringList = Arrays.asList("A", "B", "C");
        List<Integer> integerList = Arrays.asList(1, 2, 3);
        List<Double> doubleList = Arrays.asList(1.1, 2.2, 3.3);
        
        System.out.println("字串列表:");
        printAnyList(stringList);
        System.out.println("整數列表:");
        printAnyList(integerList);
        
        // 上界通配符 (? extends Number)
        System.out.println("數字列表總和:");
        System.out.println("整數總和: " + sumNumbers(integerList));
        System.out.println("浮點數總和: " + sumNumbers(doubleList));
        
        // 下界通配符 (? super Integer)
        List<Number> numberList = new ArrayList<>();
        List<Object> objectList = new ArrayList<>();
        
        System.out.println("添加整數到 Number 列表:");
        addIntegers(numberList);
        System.out.println("Number 列表: " + numberList);
        
        System.out.println("添加整數到 Object 列表:");
        addIntegers(objectList);
        System.out.println("Object 列表: " + objectList);
        
        // PECS 原則示例
        List<Integer> source = Arrays.asList(1, 2, 3, 4, 5);
        List<Number> destination = new ArrayList<>();
        
        System.out.println("複製前 - 來源: " + source + ", 目標: " + destination);
        copyElements(source, destination);
        System.out.println("複製後 - 來源: " + source + ", 目標: " + destination);
        
        System.out.println();
    }
    
    /**
     * 示例 5：泛型集合操作
     */
    private static void demonstrateGenericCollections() {
        System.out.println("5. 泛型集合操作示例");
        System.out.println("======================");
        
        // 創建和操作泛型集合
        GenericCollectionUtils collectionUtils = new GenericCollectionUtils();
        
        // List 操作
        List<String> fruits = Arrays.asList("蘋果", "香蕉", "橘子", "蘋果", "葡萄");
        System.out.println("原始水果列表: " + fruits);
        
        List<String> uniqueFruits = collectionUtils.removeDuplicates(fruits);
        System.out.println("去重後的水果列表: " + uniqueFruits);
        
        List<String> reversedFruits = collectionUtils.reverse(fruits);
        System.out.println("反轉後的水果列表: " + reversedFruits);
        
        // 過濾操作
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("原始數字列表: " + numbers);
        
        List<Integer> evenNumbers = collectionUtils.filter(numbers, n -> n % 2 == 0);
        System.out.println("偶數列表: " + evenNumbers);
        
        List<Integer> largeNumbers = collectionUtils.filter(numbers, n -> n > 5);
        System.out.println("大於 5 的數字: " + largeNumbers);
        
        // 轉換操作
        List<String> numberStrings = collectionUtils.map(numbers, Object::toString);
        System.out.println("數字轉字串: " + numberStrings);
        
        List<Integer> lengths = collectionUtils.map(fruits, String::length);
        System.out.println("水果名稱長度: " + lengths);
        
        // 統計操作
        OptionalInt maxNumber = collectionUtils.findMax(numbers);
        OptionalInt minNumber = collectionUtils.findMin(numbers);
        System.out.println("最大數字: " + (maxNumber.isPresent() ? maxNumber.getAsInt() : "無"));
        System.out.println("最小數字: " + (minNumber.isPresent() ? minNumber.getAsInt() : "無"));
        
        System.out.println();
    }
    
    /**
     * 示例 6：實際應用場景
     */
    private static void demonstratePracticalUseCases() {
        System.out.println("6. 實際應用場景示例");
        System.out.println("======================");
        
        // 快取系統
        System.out.println("-- 泛型快取系統 --");
        GenericCache<String, String> stringCache = new GenericCache<>();
        stringCache.put("user:1", "Alice");
        stringCache.put("user:2", "Bob");
        System.out.println("取得用戶 1: " + stringCache.get("user:1"));
        System.out.println("快取大小: " + stringCache.size());
        
        GenericCache<Integer, Double> scoreCache = new GenericCache<>();
        scoreCache.put(1001, 95.5);
        scoreCache.put(1002, 87.3);
        System.out.println("學生 1001 分數: " + scoreCache.get(1001));
        
        // 結果包裝器
        System.out.println("\n-- 泛型結果包裝器 --");
        Result<String> successResult = Result.success("操作成功!");
        Result<String> errorResult = Result.error("操作失敗: 文件不存在");
        
        System.out.println("成功結果: " + processResult(successResult));
        System.out.println("錯誤結果: " + processResult(errorResult));
        
        // 數據驗證器
        System.out.println("\n-- 泛型數據驗證器 --");
        Validator<String> emailValidator = email -> 
            email != null && email.contains("@") && email.contains(".");
        
        Validator<Integer> ageValidator = age -> 
            age != null && age >= 0 && age <= 150;
        
        System.out.println("驗證郵箱 'user@example.com': " + 
                          emailValidator.validate("user@example.com"));
        System.out.println("驗證郵箱 'invalid-email': " + 
                          emailValidator.validate("invalid-email"));
        System.out.println("驗證年齡 25: " + ageValidator.validate(25));
        System.out.println("驗證年齡 -5: " + ageValidator.validate(-5));
        
        // 事件系統
        System.out.println("\n-- 泛型事件系統 --");
        EventBus eventBus = new EventBus();
        
        // 註冊監聽器
        eventBus.register(String.class, message -> 
            System.out.println("收到字串事件: " + message));
        
        eventBus.register(Integer.class, number -> 
            System.out.println("收到數字事件: " + number));
        
        // 發送事件
        eventBus.fire("Hello, Event Bus!");
        eventBus.fire(42);
        eventBus.fire(3.14);  // 沒有監聽器，不會處理
        
        System.out.println();
    }
    
    // ==================== 泛型方法 ====================
    
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
     * 在陣列中查找元素的索引
     * @param <T> 元素類型
     * @param array 搜尋陣列
     * @param target 目標元素
     * @return 元素索引，找不到返回 -1
     */
    public static <T> int findIndex(T[] array, T target) {
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
     * 比較兩個 Comparable 物件
     * @param <T> 必須實現 Comparable 介面
     * @param a 第一個物件
     * @param b 第二個物件
     * @return 比較結果
     */
    public static <T extends Comparable<T>> int compare(T a, T b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return a.compareTo(b);
    }
    
    /**
     * 使用 varargs 創建陣列
     * @param <T> 元素類型
     * @param elements 元素
     * @return 創建的陣列
     */
    @SafeVarargs
    public static <T> T[] createArray(T... elements) {
        return elements;
    }
    
    /**
     * 計算數字列表的總和（有界類型參數）
     * @param <T> 必須是 Number 的子類型
     * @param numbers 數字列表
     * @return 總和
     */
    public static <T extends Number> double calculateSum(List<T> numbers) {
        double sum = 0.0;
        for (T number : numbers) {
            sum += number.doubleValue();
        }
        return sum;
    }
    
    /**
     * 計算數字列表的平均值
     * @param <T> 必須是 Number 的子類型
     * @param numbers 數字列表
     * @return 平均值
     */
    public static <T extends Number> double calculateAverage(List<T> numbers) {
        if (numbers.isEmpty()) {
            return 0.0;
        }
        return calculateSum(numbers) / numbers.size();
    }
    
    /**
     * 查找 Comparable 列表中的最大值
     * @param <T> 必須實現 Comparable 介面
     * @param list 列表
     * @return 最大值，空列表返回 null
     */
    public static <T extends Comparable<T>> T findMaximum(List<T> list) {
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
    
    // ==================== 通配符方法 ====================
    
    /**
     * 列印任何類型的列表（無界通配符）
     * @param list 任意類型的列表
     */
    public static void printAnyList(List<?> list) {
        for (Object item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
    
    /**
     * 計算數字列表總和（上界通配符）
     * @param numbers Number 及其子類型的列表
     * @return 總和
     */
    public static double sumNumbers(List<? extends Number> numbers) {
        double sum = 0.0;
        for (Number number : numbers) {
            sum += number.doubleValue();
        }
        return sum;
    }
    
    /**
     * 向列表添加整數（下界通配符）
     * @param numbers Integer 及其父類型的列表
     */
    public static void addIntegers(List<? super Integer> numbers) {
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
    }
    
    /**
     * 複製列表元素（PECS 原則）
     * @param <T> 元素類型
     * @param source 來源列表（Producer）
     * @param destination 目標列表（Consumer）
     */
    public static <T> void copyElements(List<? extends T> source, List<? super T> destination) {
        for (T item : source) {
            destination.add(item);
        }
    }
    
    /**
     * 處理 Result 物件
     * @param result 結果物件
     * @return 處理結果字串
     */
    private static String processResult(Result<String> result) {
        if (result.isSuccess()) {
            return "處理成功: " + result.getValue();
        } else {
            return "處理失敗: " + result.getError();
        }
    }
}

// ==================== 輔助類別 ====================

/**
 * 簡單的泛型容器
 * @param <T> 存儲的值類型
 */
class SimpleBox<T> {
    private T value;
    
    public SimpleBox(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "SimpleBox{value=" + value + "}";
    }
}

/**
 * 鍵值對容器
 * @param <K> 鍵的類型
 * @param <V> 值的類型
 */
class KeyValuePair<K, V> {
    private K key;
    private V value;
    
    public KeyValuePair(K key, V value) {
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
        return String.format("KeyValuePair{key=%s, value=%s}", key, value);
    }
}

/**
 * 數字範圍類別（有界類型參數）
 * @param <T> 必須是 Number 的子類型
 */
class NumberRange<T extends Number> {
    private final T min;
    private final T max;
    
    public NumberRange(T min, T max) {
        this.min = min;
        this.max = max;
    }
    
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
}

/**
 * 泛型集合工具類別
 */
class GenericCollectionUtils {
    
    /**
     * 移除列表中的重複元素
     */
    public <T> List<T> removeDuplicates(List<T> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }
    
    /**
     * 反轉列表
     */
    public <T> List<T> reverse(List<T> list) {
        List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }
    
    /**
     * 過濾列表元素
     */
    public <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    /**
     * 轉換列表元素
     */
    public <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(mapper.apply(item));
        }
        return result;
    }
    
    /**
     * 查找最大值
     */
    public OptionalInt findMax(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).max();
    }
    
    /**
     * 查找最小值
     */
    public OptionalInt findMin(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).min();
    }
}

/**
 * 泛型快取類別
 * @param <K> 鍵類型
 * @param <V> 值類型
 */
class GenericCache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    
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
}

/**
 * 泛型結果包裝器
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
    
    public T getValue() {
        return value;
    }
    
    public String getError() {
        return error;
    }
}

/**
 * 泛型驗證器介面
 * @param <T> 待驗證的類型
 */
@FunctionalInterface
interface Validator<T> {
    boolean validate(T item);
}

/**
 * 簡單的泛型事件總線
 */
class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();
    
    @SuppressWarnings("unchecked")
    public <T> void register(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                 .add((Consumer<Object>) listener);
    }
    
    public <T> void fire(T event) {
        Class<?> eventType = event.getClass();
        List<Consumer<Object>> eventListeners = listeners.get(eventType);
        
        if (eventListeners != null) {
            for (Consumer<Object> listener : eventListeners) {
                listener.accept(event);
            }
        }
    }
}