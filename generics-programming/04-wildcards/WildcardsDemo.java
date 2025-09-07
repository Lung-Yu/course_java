import java.util.*;
import java.util.function.*;

/**
 * 通配符示例
 * 
 * 本類別展示 Java 泛型通配符的使用，包括：
 * 1. 無界通配符 (?)
 * 2. 上界通配符 (? extends T)
 * 3. 下界通配符 (? super T)
 * 4. PECS 原則應用
 * 5. 實用的通配符工具方法
 * 
 * 編譯：javac WildcardsDemo.java
 * 執行：java WildcardsDemo
 * 
 * @author Java Course
 * @version 1.0
 */
public class WildcardsDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 通配符示例 ===\n");
        
        // 1. 無界通配符示例
        demonstrateUnboundedWildcards();
        
        // 2. 上界通配符示例
        demonstrateUpperBoundedWildcards();
        
        // 3. 下界通配符示例
        demonstrateLowerBoundedWildcards();
        
        // 4. PECS 原則示例
        demonstratePECSPrinciple();
        
        // 5. 通配符工具方法示例
        demonstrateWildcardUtilities();
        
        // 6. 實際應用場景
        demonstrateRealWorldUseCases();
    }
    
    /**
     * 示例 1：無界通配符 (?)
     */
    private static void demonstrateUnboundedWildcards() {
        System.out.println("1. 無界通配符示例");
        System.out.println("==================");
        
        // 創建不同類型的列表
        List<String> stringList = Arrays.asList("Java", "Python", "JavaScript");
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        List<Double> doubleList = Arrays.asList(1.1, 2.2, 3.3);
        
        // 無界通配符可以接受任何類型的列表
        System.out.println("字串列表:");
        UnboundedWildcards.printAnyList(stringList);
        
        System.out.println("整數列表:");
        UnboundedWildcards.printAnyList(integerList);
        
        System.out.println("浮點數列表:");
        UnboundedWildcards.printAnyList(doubleList);
        
        // 獲取集合資訊
        System.out.println("字串列表大小: " + UnboundedWildcards.getSize(stringList));
        System.out.println("整數列表大小: " + UnboundedWildcards.getSize(integerList));
        System.out.println("浮點數列表是否為空: " + UnboundedWildcards.isEmpty(doubleList));
        
        // 比較列表
        List<String> anotherStringList = Arrays.asList("Java", "Python", "JavaScript");
        List<String> differentStringList = Arrays.asList("C++", "Go", "Rust");
        
        System.out.println("字串列表是否相等: " + 
                          UnboundedWildcards.listsEqual(stringList, anotherStringList));
        System.out.println("不同字串列表是否相等: " + 
                          UnboundedWildcards.listsEqual(stringList, differentStringList));
        
        // 轉換為陣列
        Object[] stringArray = UnboundedWildcards.toArray(stringList);
        Object[] intArray = UnboundedWildcards.toArray(integerList);
        
        System.out.println("字串陣列: " + Arrays.toString(stringArray));
        System.out.println("整數陣列: " + Arrays.toString(intArray));
        
        System.out.println();
    }
    
    /**
     * 示例 2：上界通配符 (? extends T)
     */
    private static void demonstrateUpperBoundedWildcards() {
        System.out.println("2. 上界通配符示例");
        System.out.println("====================");
        
        // 創建不同數字類型的列表
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3, 4.4, 5.5);
        List<Float> floats = Arrays.asList(1.0f, 2.0f, 3.0f);
        List<Long> longs = Arrays.asList(100L, 200L, 300L);
        
        // 上界通配符 - 讀取數據
        System.out.println("--- 數字操作 ---");
        System.out.println("整數總和: " + UpperBoundedWildcards.sumNumbers(integers));
        System.out.println("浮點數總和: " + UpperBoundedWildcards.sumNumbers(doubles));
        System.out.println("Float 總和: " + UpperBoundedWildcards.sumNumbers(floats));
        System.out.println("Long 總和: " + UpperBoundedWildcards.sumNumbers(longs));
        
        System.out.println("整數平均值: " + UpperBoundedWildcards.averageNumbers(integers));
        System.out.println("浮點數平均值: " + UpperBoundedWildcards.averageNumbers(doubles));
        
        System.out.println("整數最大值: " + UpperBoundedWildcards.findMaxNumber(integers));
        System.out.println("浮點數最大值: " + UpperBoundedWildcards.findMaxNumber(doubles));
        
        // Comparable 物件
        System.out.println("\n--- Comparable 物件操作 ---");
        List<String> words = Arrays.asList("Java", "Python", "Go", "JavaScript", "C++");
        List<Integer> numbers = Arrays.asList(10, 5, 8, 20, 15);
        
        System.out.println("字串列表: " + words);
        System.out.println("字串最大值: " + UpperBoundedWildcards.findMaxComparable(words));
        System.out.println("字串最小值: " + UpperBoundedWildcards.findMinComparable(words));
        
        System.out.println("數字列表: " + numbers);
        System.out.println("數字最大值: " + UpperBoundedWildcards.findMaxComparable(numbers));
        System.out.println("數字最小值: " + UpperBoundedWildcards.findMinComparable(numbers));
        
        // 檢查是否已排序
        List<Integer> sortedNumbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> unsortedNumbers = Arrays.asList(5, 2, 8, 1, 9);
        
        System.out.println("已排序數字列表: " + sortedNumbers);
        System.out.println("是否已排序: " + UpperBoundedWildcards.isSorted(sortedNumbers));
        System.out.println("未排序數字列表: " + unsortedNumbers);
        System.out.println("是否已排序: " + UpperBoundedWildcards.isSorted(unsortedNumbers));
        
        System.out.println();
    }
    
    /**
     * 示例 3：下界通配符 (? super T)
     */
    private static void demonstrateLowerBoundedWildcards() {
        System.out.println("3. 下界通配符示例");
        System.out.println("====================");
        
        // 創建不同層級的列表
        List<Number> numberList = new ArrayList<>();
        List<Object> objectList = new ArrayList<>();
        
        // 下界通配符 - 寫入數據
        System.out.println("--- 添加整數 ---");
        LowerBoundedWildcards.addIntegers(numberList);   // Number 是 Integer 的父類
        LowerBoundedWildcards.addIntegers(objectList);   // Object 是 Integer 的父類
        
        // 演示編譯限制：無法將 Integer 添加到 Integer 列表（需要父類型）
        List<Integer> integerList = new ArrayList<>();
        System.out.println("創建 Integer 列表: " + integerList);
        System.out.println("注意：無法對 List<Integer> 使用 ? super Integer，因為需要父類型");
        // LowerBoundedWildcards.addIntegers(integerList); // 這會編譯錯誤！
        
        System.out.println("Number 列表: " + numberList);
        System.out.println("Object 列表: " + objectList);
        
        // 添加字串
        List<Object> objList1 = new ArrayList<>();
        List<Object> objList2 = new ArrayList<>();
        
        LowerBoundedWildcards.addStrings(objList1);  // Object 是 String 的父類
        System.out.println("添加字串後: " + objList1);
        
        // 批量添加
        String[] fruits = {"蘋果", "香蕉", "橘子"};
        LowerBoundedWildcards.addAll(objList2, fruits);
        System.out.println("批量添加水果: " + objList2);
        
        // 填充集合
        List<Number> numberList2 = new ArrayList<>();
        List<Object> objectList2 = new ArrayList<>();
        
        LowerBoundedWildcards.fillWithNumbers(numberList2, 5);
        LowerBoundedWildcards.fillWithNumbers(objectList2, 3);
        
        System.out.println("填充的 Number 列表: " + numberList2);
        System.out.println("填充的 Object 列表: " + objectList2);
        
        // 替換操作
        List<Object> replaceList = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e"));
        System.out.println("替換前: " + replaceList);
        
        LowerBoundedWildcards.replaceAll(replaceList, "replaced");
        System.out.println("替換後: " + replaceList);
        
        System.out.println();
    }
    
    /**
     * 示例 4：PECS 原則 (Producer Extends, Consumer Super)
     */
    private static void demonstratePECSPrinciple() {
        System.out.println("4. PECS 原則示例");
        System.out.println("===================");
        
        // Producer Extends - 從來源讀取數據
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3);
        
        // Consumer Super - 向目標寫入數據
        List<Number> numbers1 = new ArrayList<>();
        List<Number> numbers2 = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        
        System.out.println("--- 複製操作 (PECS) ---");
        System.out.println("來源整數: " + integers);
        System.out.println("來源浮點數: " + doubles);
        
        // 使用 PECS 原則的複製方法
        PECSExamples.copy(integers, numbers1);  // Integer -> Number
        PECSExamples.copy(doubles, numbers2);   // Double -> Number
        PECSExamples.copy(integers, objects);   // Integer -> Object
        
        System.out.println("複製後 numbers1: " + numbers1);
        System.out.println("複製後 numbers2: " + numbers2);
        System.out.println("複製後 objects: " + objects);
        
        // 合併操作
        System.out.println("\n--- 合併操作 ---");
        List<Number> mergedNumbers = new ArrayList<>();
        List<String> strings = Arrays.asList("a", "b", "c");
        List<Object> mergedObjects = new ArrayList<>();
        
        PECSExamples.mergeInto(mergedNumbers, integers, doubles);
        System.out.println("合併的數字: " + mergedNumbers);
        
        PECSExamples.mergeInto(mergedObjects, integers, strings);
        System.out.println("合併的物件: " + mergedObjects);
        
        // 過濾並複製
        System.out.println("\n--- 過濾並複製 ---");
        List<Integer> sourceNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Number> evenNumbers = new ArrayList<>();
        List<Object> largeNumbers = new ArrayList<>();
        
        PECSExamples.filterAndCopy(sourceNumbers, evenNumbers, n -> n % 2 == 0);
        PECSExamples.filterAndCopy(sourceNumbers, largeNumbers, n -> n > 5);
        
        System.out.println("來源數字: " + sourceNumbers);
        System.out.println("偶數: " + evenNumbers);
        System.out.println("大於 5 的數字: " + largeNumbers);
        
        // 轉換並收集
        System.out.println("\n--- 轉換並收集 ---");
        List<String> words = Arrays.asList("Java", "Python", "Go");
        List<Object> lengths = new ArrayList<>();
        List<Object> upperWords = new ArrayList<>();
        
        PECSExamples.transformAndCollect(words, lengths, String::length);
        PECSExamples.transformAndCollect(words, upperWords, String::toUpperCase);
        
        System.out.println("原始單詞: " + words);
        System.out.println("單詞長度: " + lengths);
        System.out.println("大寫單詞: " + upperWords);
        
        System.out.println();
    }
    
    /**
     * 示例 5：通配符工具方法
     */
    private static void demonstrateWildcardUtilities() {
        System.out.println("5. 通配符工具方法示例");
        System.out.println("========================");
        
        // 集合工具
        List<String> fruits = Arrays.asList("蘋果", "香蕉", "橘子", "葡萄");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        System.out.println("--- 集合統計 ---");
        System.out.println("水果列表: " + fruits);
        System.out.println("長度大於 2 的水果數量: " + 
                          WildcardUtils.count(fruits, s -> s.length() > 2));
        
        System.out.println("數字列表: " + numbers);
        System.out.println("偶數數量: " + WildcardUtils.count(numbers, n -> n % 2 == 0));
        System.out.println("大於 5 的數字數量: " + WildcardUtils.count(numbers, n -> n > 5));
        
        // 查找操作
        System.out.println("\n--- 查找操作 ---");
        Optional<String> longFruit = WildcardUtils.findFirst(fruits, s -> s.length() > 2);
        Optional<Integer> evenNumber = WildcardUtils.findFirst(numbers, n -> n % 2 == 0);
        
        System.out.println("第一個長度大於 2 的水果: " + longFruit.orElse("未找到"));
        System.out.println("第一個偶數: " + evenNumber.orElse(-1));
        
        // 檢查條件
        System.out.println("\n--- 條件檢查 ---");
        System.out.println("所有水果長度都大於 1: " + 
                          WildcardUtils.allMatch(fruits, s -> s.length() > 1));
        System.out.println("任何數字大於 8: " + 
                          WildcardUtils.anyMatch(numbers, n -> n > 8));
        System.out.println("沒有數字等於 0: " + 
                          WildcardUtils.noneMatch(numbers, n -> n == 0));
        
        // 歸約操作
        System.out.println("\n--- 歸約操作 ---");
        String concatenated = WildcardUtils.reduce(fruits, "", (s1, s2) -> s1 + s2);
        Integer sum = WildcardUtils.reduce(numbers, 0, Integer::sum);
        
        System.out.println("連接的水果名稱: " + concatenated);
        System.out.println("數字總和: " + sum);
        
        // 分組操作
        System.out.println("\n--- 分組操作 ---");
        Map<Boolean, List<String>> fruitGroups = 
            WildcardUtils.partition(fruits, s -> s.length() > 2);
        Map<Boolean, List<Integer>> numberGroups = 
            WildcardUtils.partition(numbers, n -> n % 2 == 0);
        
        System.out.println("長水果名稱: " + fruitGroups.get(true));
        System.out.println("短水果名稱: " + fruitGroups.get(false));
        System.out.println("偶數: " + numberGroups.get(true));
        System.out.println("奇數: " + numberGroups.get(false));
        
        System.out.println();
    }
    
    /**
     * 示例 6：實際應用場景
     */
    private static void demonstrateRealWorldUseCases() {
        System.out.println("6. 實際應用場景示例");
        System.out.println("======================");
        
        // 事件處理系統
        System.out.println("--- 事件處理系統 ---");
        EventProcessor<String> stringEventProcessor = new EventProcessor<>();
        EventProcessor<Number> numberEventProcessor = new EventProcessor<>();
        
        // 添加事件處理器
        stringEventProcessor.addHandler(event -> 
            System.out.println("處理字串事件: " + event));
        numberEventProcessor.addHandler(event -> 
            System.out.println("處理數字事件: " + event));
        
        // 處理事件
        stringEventProcessor.processEvent("Hello World");
        stringEventProcessor.processEvent("Java Programming");
        
        numberEventProcessor.processEvent(42);
        numberEventProcessor.processEvent(3.14);
        
        // 資料轉換管道
        System.out.println("\n--- 資料轉換管道 ---");
        DataPipeline<String> stringPipeline = new DataPipeline<>();
        
        List<String> inputWords = Arrays.asList("java", "python", "go");
        List<Integer> outputLengths = new ArrayList<>();
        List<String> outputUpper = new ArrayList<>();
        
        stringPipeline.transform(inputWords, outputLengths, String::length);
        stringPipeline.transform(inputWords, outputUpper, String::toUpperCase);
        
        System.out.println("輸入單詞: " + inputWords);
        System.out.println("單詞長度: " + outputLengths);
        System.out.println("大寫單詞: " + outputUpper);
        
        // 快取系統
        System.out.println("\n--- 快取系統 ---");
        FlexibleCache<String, Number> cache = new FlexibleCache<>();
        
        // 添加不同類型的數字
        cache.put("int", 42);
        cache.put("double", 3.14);
        cache.put("long", 100L);
        
        System.out.println("快取內容:");
        cache.forEach((key, value) -> 
            System.out.println("  " + key + " -> " + value + " (" + value.getClass().getSimpleName() + ")"));
        
        // 批量操作工具
        System.out.println("\n--- 批量操作工具 ---");
        BatchProcessor processor = new BatchProcessor();
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<String> strings = Arrays.asList("a", "b", "c");
        List<Object> results = new ArrayList<>();
        
        processor.processAndCollect(numbers, results, n -> n * n);
        processor.processAndCollect(strings, results, String::toUpperCase);
        
        System.out.println("批量處理結果: " + results);
        
        System.out.println();
    }
}

// ==================== 無界通配符示例 ====================

/**
 * 無界通配符使用示例
 */
class UnboundedWildcards {
    
    /**
     * 列印任何類型的列表
     * @param list 任意類型的列表
     */
    public static void printAnyList(List<?> list) {
        for (Object item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
    
    /**
     * 獲取集合大小
     * @param collection 任意類型的集合
     * @return 集合大小
     */
    public static int getSize(Collection<?> collection) {
        return collection != null ? collection.size() : 0;
    }
    
    /**
     * 檢查集合是否為空
     * @param collection 任意類型的集合
     * @return 如果為空返回 true
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * 比較兩個列表是否相等
     * @param list1 第一個列表
     * @param list2 第二個列表
     * @return 如果相等返回 true
     */
    public static boolean listsEqual(List<?> list1, List<?> list2) {
        if (list1 == null || list2 == null) {
            return list1 == list2;
        }
        
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
     * 將列表轉換為陣列
     * @param list 任意類型的列表
     * @return 物件陣列
     */
    public static Object[] toArray(List<?> list) {
        if (list == null) {
            return new Object[0];
        }
        return list.toArray();
    }
}

// ==================== 上界通配符示例 ====================

/**
 * 上界通配符使用示例
 */
class UpperBoundedWildcards {
    
    /**
     * 計算數字列表總和
     * @param numbers Number 或其子類型的列表
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
     * 計算數字列表平均值
     * @param numbers Number 或其子類型的列表
     * @return 平均值
     */
    public static double averageNumbers(List<? extends Number> numbers) {
        if (numbers.isEmpty()) {
            return 0.0;
        }
        return sumNumbers(numbers) / numbers.size();
    }
    
    /**
     * 查找數字列表最大值
     * @param numbers 實現 Comparable 的 Number 子類型列表
     * @return 最大值
     */
    public static <T extends Number & Comparable<T>> T findMaxNumber(List<? extends T> numbers) {
        if (numbers.isEmpty()) {
            return null;
        }
        
        T max = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            T current = numbers.get(i);
            if (current.compareTo(max) > 0) {
                max = current;
            }
        }
        return max;
    }
    
    /**
     * 查找 Comparable 物件的最大值
     * @param <T> 實現 Comparable 的類型
     * @param items Comparable 或其子類型的列表
     * @return 最大值
     */
    public static <T extends Comparable<T>> T findMaxComparable(List<? extends T> items) {
        if (items.isEmpty()) {
            return null;
        }
        
        T max = items.get(0);
        for (int i = 1; i < items.size(); i++) {
            T current = items.get(i);
            if (current.compareTo(max) > 0) {
                max = current;
            }
        }
        return max;
    }
    
    /**
     * 查找 Comparable 物件的最小值
     * @param <T> 實現 Comparable 的類型
     * @param items Comparable 或其子類型的列表
     * @return 最小值
     */
    public static <T extends Comparable<T>> T findMinComparable(List<? extends T> items) {
        if (items.isEmpty()) {
            return null;
        }
        
        T min = items.get(0);
        for (int i = 1; i < items.size(); i++) {
            T current = items.get(i);
            if (current.compareTo(min) < 0) {
                min = current;
            }
        }
        return min;
    }
    
    /**
     * 檢查列表是否已排序
     * @param <T> 實現 Comparable 的類型
     * @param items 檢查的列表
     * @return 如果已排序返回 true
     */
    public static <T extends Comparable<T>> boolean isSorted(List<? extends T> items) {
        for (int i = 1; i < items.size(); i++) {
            if (items.get(i - 1).compareTo(items.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }
}

// ==================== 下界通配符示例 ====================

/**
 * 下界通配符使用示例
 */
class LowerBoundedWildcards {
    
    /**
     * 向列表添加整數
     * @param numbers Integer 或其父類型的列表
     */
    public static void addIntegers(List<? super Integer> numbers) {
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);
    }
    
    /**
     * 向列表添加字串
     * @param strings String 或其父類型的列表
     */
    public static void addStrings(List<? super String> strings) {
        strings.add("Hello");
        strings.add("World");
        strings.add("Java");
    }
    
    /**
     * 批量添加元素
     * @param <T> 元素類型
     * @param collection T 或其父類型的集合
     * @param items 要添加的元素
     */
    @SafeVarargs
    public static <T> void addAll(Collection<? super T> collection, T... items) {
        for (T item : items) {
            collection.add(item);
        }
    }
    
    /**
     * 用數字填充列表
     * @param numbers Integer 或其父類型的列表
     * @param count 填充數量
     */
    public static void fillWithNumbers(List<? super Integer> numbers, int count) {
        for (int i = 1; i <= count; i++) {
            numbers.add(i * 10);
        }
    }
    
    /**
     * 替換列表中的所有元素
     * @param <T> 元素類型
     * @param list T 或其父類型的列表
     * @param replacement 替換值
     */
    public static <T> void replaceAll(List<? super T> list, T replacement) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, replacement);
        }
    }
}

// ==================== PECS 原則示例 ====================

/**
 * PECS 原則應用示例
 */
class PECSExamples {
    
    /**
     * 複製列表元素 (完美的 PECS 示例)
     * @param <T> 元素類型
     * @param source 來源列表 (Producer - 生產數據)
     * @param destination 目標列表 (Consumer - 消費數據)
     */
    public static <T> void copy(List<? extends T> source, List<? super T> destination) {
        for (T item : source) {
            destination.add(item);
        }
    }
    
    /**
     * 合併多個列表到目標列表
     * @param <T> 元素類型
     * @param target 目標列表 (Consumer)
     * @param sources 來源列表 (Producers)
     */
    @SafeVarargs
    public static <T> void mergeInto(List<? super T> target, List<? extends T>... sources) {
        for (List<? extends T> source : sources) {
            for (T item : source) {
                target.add(item);
            }
        }
    }
    
    /**
     * 過濾並複製元素
     * @param <T> 元素類型
     * @param source 來源集合 (Producer)
     * @param target 目標集合 (Consumer)
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
     * 轉換並收集結果
     * @param <T> 源類型
     * @param <R> 目標類型
     * @param source 來源集合 (Producer)
     * @param target 目標集合 (Consumer)
     * @param mapper 轉換函數
     */
    public static <T, R> void transformAndCollect(Collection<? extends T> source,
                                                Collection<? super R> target,
                                                Function<? super T, ? extends R> mapper) {
        for (T item : source) {
            R result = mapper.apply(item);
            target.add(result);
        }
    }
    
    /**
     * 條件性複製
     * @param <T> 元素類型
     * @param source 來源列表
     * @param target 目標列表
     * @param predicate 複製條件
     * @return 複製的元素數量
     */
    public static <T> int copyIf(List<? extends T> source,
                               List<? super T> target,
                               Predicate<? super T> predicate) {
        int count = 0;
        for (T item : source) {
            if (predicate.test(item)) {
                target.add(item);
                count++;
            }
        }
        return count;
    }
}

// ==================== 通配符工具類別 ====================

/**
 * 通配符工具方法
 */
class WildcardUtils {
    
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
    
    /**
     * 查找第一個滿足條件的元素
     * @param <T> 元素類型
     * @param collection 搜尋的集合
     * @param predicate 查找條件
     * @return Optional 包裝的結果
     */
    public static <T> Optional<T> findFirst(Collection<? extends T> collection,
                                          Predicate<? super T> predicate) {
        for (T item : collection) {
            if (predicate.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
    
    /**
     * 檢查所有元素是否都滿足條件
     * @param <T> 元素類型
     * @param collection 檢查的集合
     * @param predicate 檢查條件
     * @return 如果所有元素都滿足條件返回 true
     */
    public static <T> boolean allMatch(Collection<? extends T> collection,
                                     Predicate<? super T> predicate) {
        for (T item : collection) {
            if (!predicate.test(item)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 檢查是否有任何元素滿足條件
     * @param <T> 元素類型
     * @param collection 檢查的集合
     * @param predicate 檢查條件
     * @return 如果有元素滿足條件返回 true
     */
    public static <T> boolean anyMatch(Collection<? extends T> collection,
                                     Predicate<? super T> predicate) {
        for (T item : collection) {
            if (predicate.test(item)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 檢查沒有元素滿足條件
     * @param <T> 元素類型
     * @param collection 檢查的集合
     * @param predicate 檢查條件
     * @return 如果沒有元素滿足條件返回 true
     */
    public static <T> boolean noneMatch(Collection<? extends T> collection,
                                      Predicate<? super T> predicate) {
        return !anyMatch(collection, predicate);
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
    
    /**
     * 分割集合
     * @param <T> 元素類型
     * @param collection 源集合
     * @param predicate 分割條件
     * @return 分割後的 Map
     */
    public static <T> Map<Boolean, List<T>> partition(Collection<? extends T> collection,
                                                    Predicate<? super T> predicate) {
        Map<Boolean, List<T>> result = new HashMap<>();
        result.put(true, new ArrayList<>());
        result.put(false, new ArrayList<>());
        
        for (T item : collection) {
            result.get(predicate.test(item)).add(item);
        }
        
        return result;
    }
}

// ==================== 實際應用場景 ====================

/**
 * 事件處理器
 * @param <T> 事件類型
 */
class EventProcessor<T> {
    private final List<Consumer<? super T>> handlers = new ArrayList<>();
    
    public void addHandler(Consumer<? super T> handler) {
        handlers.add(handler);
    }
    
    public void processEvent(T event) {
        for (Consumer<? super T> handler : handlers) {
            handler.accept(event);
        }
    }
}

/**
 * 資料轉換管道
 * @param <T> 處理的資料類型
 */
class DataPipeline<T> {
    
    /**
     * 轉換資料
     * @param <R> 結果類型
     * @param source 來源集合
     * @param target 目標集合
     * @param transformer 轉換器
     */
    public <R> void transform(Collection<? extends T> source,
                            Collection<? super R> target,
                            Function<? super T, ? extends R> transformer) {
        for (T item : source) {
            R result = transformer.apply(item);
            target.add(result);
        }
    }
}

/**
 * 靈活的快取系統
 * @param <K> 鍵類型
 * @param <V> 值類型
 */
class FlexibleCache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    
    public void put(K key, V value) {
        cache.put(key, value);
    }
    
    public V get(K key) {
        return cache.get(key);
    }
    
    public void putAll(Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }
    
    public void forEach(BiConsumer<? super K, ? super V> action) {
        cache.forEach(action);
    }
}

/**
 * 批量處理器
 */
class BatchProcessor {
    
    /**
     * 處理並收集結果
     * @param <T> 源類型
     * @param <R> 結果類型
     * @param source 來源集合
     * @param target 目標集合
     * @param processor 處理器
     */
    public <T, R> void processAndCollect(Collection<? extends T> source,
                                       Collection<? super R> target,
                                       Function<? super T, ? extends R> processor) {
        for (T item : source) {
            R result = processor.apply(item);
            target.add(result);
        }
    }
}