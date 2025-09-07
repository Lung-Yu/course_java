import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * 泛型進階示例
 * 
 * 本類別展示 Java 泛型的進階概念和應用，包括：
 * 1. 複雜的泛型類別設計
 * 2. 泛型介面的實現
 * 3. 類型擦除的深入理解
 * 4. 泛型與反射的結合
 * 5. 泛型設計模式
 * 6. 實際項目中的泛型應用
 * 
 * @author Java Course
 * @version 1.0
 */
public class GenericsAdvancedDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Java 泛型進階示例 ===\n");
        
        // 1. 複雜泛型類別示例
        demonstrateAdvancedGenericClasses();
        
        // 2. 泛型介面實現
        demonstrateGenericInterfaces();
        
        // 3. 類型擦除深入分析
        demonstrateTypeErasure();
        
        // 4. 泛型與反射
        demonstrateGenericsWithReflection();
        
        // 5. 泛型設計模式
        demonstrateGenericDesignPatterns();
        
        // 6. 實際應用場景
        demonstrateRealWorldUseCases();
    }
    
    /**
     * 示例 1：複雜泛型類別
     */
    private static void demonstrateAdvancedGenericClasses() {
        System.out.println("1. 複雜泛型類別示例");
        System.out.println("========================");
        
        // 泛型堆疊
        GenericStack<String> stringStack = new GenericStack<>();
        stringStack.push("First");
        stringStack.push("Second");
        stringStack.push("Third");
        
        System.out.println("字串堆疊:");
        while (!stringStack.isEmpty()) {
            System.out.println("  彈出: " + stringStack.pop());
        }
        
        // 泛型二元樹
        BinaryTree<Integer> intTree = new BinaryTree<>();
        intTree.insert(50);
        intTree.insert(30);
        intTree.insert(70);
        intTree.insert(20);
        intTree.insert(40);
        intTree.insert(60);
        intTree.insert(80);
        
        System.out.println("整數二元樹中序遍歷:");
        intTree.inorderTraversal(value -> System.out.print(value + " "));
        System.out.println();
        
        System.out.println("查找 40: " + intTree.contains(40));
        System.out.println("查找 90: " + intTree.contains(90));
        
        // 多類型參數的複雜類別
        TripleContainer<String, Integer, Double> triple = 
            new TripleContainer<>("Name", 25, 95.5);
        
        System.out.println("三元組容器: " + triple);
        System.out.println("第一個值: " + triple.getFirst());
        System.out.println("第二個值: " + triple.getSecond());
        System.out.println("第三個值: " + triple.getThird());
        
        System.out.println();
    }
    
    /**
     * 示例 2：泛型介面實現
     */
    private static void demonstrateGenericInterfaces() {
        System.out.println("2. 泛型介面實現示例");
        System.out.println("======================");
        
        // 泛型轉換器
        Transformer<String, Integer> stringToLength = String::length;
        Transformer<Integer, String> intToString = Object::toString;
        Transformer<String, String> toUpperCase = String::toUpperCase;
        
        System.out.println("字串轉長度: " + stringToLength.transform("Hello World"));
        System.out.println("整數轉字串: " + intToString.transform(42));
        System.out.println("轉大寫: " + toUpperCase.transform("hello"));
        
        // 組合轉換器
        Transformer<String, String> lengthToString = 
            stringToLength.andThen(intToString);
        System.out.println("字串長度轉字串: " + lengthToString.transform("Hello"));
        
        // 泛型工廠
        Factory<List<String>> listFactory = ArrayList::new;
        Factory<Map<String, Integer>> mapFactory = HashMap::new;
        
        List<String> newList = listFactory.create();
        Map<String, Integer> newMap = mapFactory.create();
        
        newList.add("Item 1");
        newMap.put("key", 42);
        
        System.out.println("工廠創建的列表: " + newList);
        System.out.println("工廠創建的對應表: " + newMap);
        
        // 泛型比較器
        GenericComparator<String> lengthComparator = 
            (s1, s2) -> Integer.compare(s1.length(), s2.length());
        
        GenericComparator<String> alphabeticalComparator = String::compareTo;
        
        List<String> words = new ArrayList<>(Arrays.asList("java", "python", "go", "javascript"));
        
        System.out.println("原始單詞列表: " + words);
        
        // 轉換為 Java 標準 Comparator 進行排序
        words.sort((s1, s2) -> lengthComparator.compare(s1, s2));
        System.out.println("按長度排序: " + words);
        
        words.sort((s1, s2) -> alphabeticalComparator.compare(s1, s2));
        System.out.println("按字母順序排序: " + words);
        
        // 組合比較器
        words.sort((s1, s2) -> {
            GenericComparator<String> combined = lengthComparator.thenComparing(alphabeticalComparator);
            return combined.compare(s1, s2);
        });
        System.out.println("先按長度再按字母排序: " + words);
        
        System.out.println();
    }
    
    /**
     * 示例 3：類型擦除深入分析
     */
    private static void demonstrateTypeErasure() {
        System.out.println("3. 類型擦除深入分析");
        System.out.println("======================");
        
        // 運行時類型資訊丟失
        List<String> stringList = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();
        
        System.out.println("String List 類型: " + stringList.getClass());
        System.out.println("Integer List 類型: " + integerList.getClass());
        System.out.println("兩個 List 類型是否相同: " + 
                          (stringList.getClass() == integerList.getClass()));
        
        // 橋接方法示例
        TypeErasureExample<String> example = new TypeErasureExample<>();
        example.process("Hello");
        
        // 顯示類型擦除後的方法簽名
        System.out.println("類別方法:");
        for (Method method : TypeErasureExample.class.getDeclaredMethods()) {
            System.out.println("  " + method);
        }
        
        // 類型參數限制
        demonstrateTypeParameterLimitations();
        
        System.out.println();
    }
    
    /**
     * 示例 4：泛型與反射
     */
    private static void demonstrateGenericsWithReflection() {
        System.out.println("4. 泛型與反射示例");
        System.out.println("====================");
        
        try {
            // 獲取泛型類型資訊
            Field listField = ReflectionExample.class.getDeclaredField("stringList");
            Type genericType = listField.getGenericType();
            
            System.out.println("字段類型: " + genericType);
            
            if (genericType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) genericType;
                System.out.println("原始類型: " + paramType.getRawType());
                System.out.println("類型參數: " + Arrays.toString(paramType.getActualTypeArguments()));
            }
            
            // 獲取方法的泛型資訊
            Method method = ReflectionExample.class.getMethod("processMap", Map.class);
            Type[] paramTypes = method.getGenericParameterTypes();
            
            System.out.println("方法參數類型:");
            for (Type paramType : paramTypes) {
                System.out.println("  " + paramType);
                if (paramType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) paramType;
                    System.out.println("    類型參數: " + Arrays.toString(pt.getActualTypeArguments()));
                }
            }
            
            // 泛型數組創建
            System.out.println("泛型數組創建:");
            String[] stringArray = GenericArrayCreator.createArray(String.class, 5);
            System.out.println("創建的字串數組長度: " + stringArray.length);
            
            Integer[] intArray = GenericArrayCreator.createArray(Integer.class, 3);
            System.out.println("創建的整數數組長度: " + intArray.length);
            
        } catch (Exception e) {
            System.err.println("反射操作失敗: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * 示例 5：泛型設計模式
     */
    private static void demonstrateGenericDesignPatterns() {
        System.out.println("5. 泛型設計模式示例");
        System.out.println("======================");
        
        // 泛型建造者模式
        System.out.println("-- 泛型建造者模式 --");
        Person person = new PersonBuilder()
            .setName("Alice")
            .setAge(30)
            .setEmail("alice@example.com")
            .build();
        
        System.out.println("建造的人物: " + person);
        
        // 泛型單例模式
        System.out.println("\n-- 泛型單例模式 --");
        DatabaseConnection db1 = GenericSingleton.getInstance(DatabaseConnection.class);
        DatabaseConnection db2 = GenericSingleton.getInstance(DatabaseConnection.class);
        
        System.out.println("資料庫連接 1: " + db1);
        System.out.println("資料庫連接 2: " + db2);
        System.out.println("是否為同一實例: " + (db1 == db2));
        
        CacheManager cache1 = GenericSingleton.getInstance(CacheManager.class);
        CacheManager cache2 = GenericSingleton.getInstance(CacheManager.class);
        
        System.out.println("快取管理器相同實例: " + (cache1 == cache2));
        
        // 泛型觀察者模式
        System.out.println("\n-- 泛型觀察者模式 --");
        GenericObservable<String> messageSubject = new GenericObservable<>();
        
        GenericObserver<String> observer1 = message -> 
            System.out.println("觀察者1收到: " + message);
        GenericObserver<String> observer2 = message -> 
            System.out.println("觀察者2收到: " + message);
        
        messageSubject.addObserver(observer1);
        messageSubject.addObserver(observer2);
        
        messageSubject.notifyObservers("Hello Observers!");
        messageSubject.removeObserver(observer1);
        messageSubject.notifyObservers("Second Message");
        
        // 泛型策略模式
        System.out.println("\n-- 泛型策略模式 --");
        SortingContext<Integer> sortContext = new SortingContext<>();
        
        List<Integer> numbers = Arrays.asList(64, 34, 25, 12, 22, 11, 90);
        System.out.println("原始數據: " + numbers);
        
        sortContext.setSortingStrategy(new BubbleSortStrategy<>());
        List<Integer> bubbleSorted = sortContext.sort(new ArrayList<>(numbers));
        System.out.println("氣泡排序結果: " + bubbleSorted);
        
        sortContext.setSortingStrategy(new QuickSortStrategy<>());
        List<Integer> quickSorted = sortContext.sort(new ArrayList<>(numbers));
        System.out.println("快速排序結果: " + quickSorted);
        
        System.out.println();
    }
    
    /**
     * 示例 6：實際應用場景
     */
    private static void demonstrateRealWorldUseCases() {
        System.out.println("6. 實際應用場景示例");
        System.out.println("======================");
        
        // 泛型 DAO 模式
        System.out.println("-- 泛型 DAO 模式 --");
        UserDAO userDAO = new UserDAO();
        
        User user1 = new User(1, "Alice", "alice@example.com");
        User user2 = new User(2, "Bob", "bob@example.com");
        
        userDAO.save(user1);
        userDAO.save(user2);
        
        System.out.println("儲存用戶: " + user1);
        System.out.println("根據 ID 查找: " + userDAO.findById(1));
        System.out.println("所有用戶: " + userDAO.findAll());
        
        // 泛型 API 響應包裝器
        System.out.println("\n-- API 響應包裝器 --");
        ApiResponse<List<String>> listResponse = ApiResponse.success(
            Arrays.asList("item1", "item2", "item3"));
        System.out.println("列表響應: " + listResponse);
        
        ApiResponse<User> userResponse = ApiResponse.error("用戶不存在", 404);
        System.out.println("錯誤響應: " + userResponse);
        
        // 泛型事件處理系統
        System.out.println("\n-- 事件處理系統 --");
        EventProcessor eventProcessor = new EventProcessor();
        
        eventProcessor.registerHandler(OrderCreatedEvent.class, 
            event -> System.out.println("處理訂單創建: " + event.getOrderId()));
        
        eventProcessor.registerHandler(UserRegisteredEvent.class,
            event -> System.out.println("處理用戶註冊: " + event.getUsername()));
        
        eventProcessor.processEvent(new OrderCreatedEvent("ORD-001"));
        eventProcessor.processEvent(new UserRegisteredEvent("newuser"));
        
        // 泛型數據管道
        System.out.println("\n-- 數據處理管道 --");
        Pipeline<String> pipeline = Pipeline.<String>create()
            .addStep(String::trim)
            .addStep(String::toLowerCase)
            .addStep(s -> s.replace(" ", "_"))
            .addStep(s -> s + ".txt");
        
        String result = pipeline.process("  Hello World  ");
        System.out.println("管道處理結果: " + result);
        
        Pipeline<Integer> mathPipeline = Pipeline.<Integer>create()
            .addStep(x -> x * 2)
            .addStep(x -> x + 10)
            .addStep(x -> x * x);
        
        Integer mathResult = mathPipeline.process(5);
        System.out.println("數學管道結果: " + mathResult);
        
        System.out.println();
    }
    
    /**
     * 展示類型參數的限制
     */
    private static void demonstrateTypeParameterLimitations() {
        System.out.println("類型參數限制示例:");
        
        // 無法獲取類型參數的 Class 物件
        @SuppressWarnings("unused")
        GenericContainer<String> container = new GenericContainer<>();
        // Class<String> clazz = T.class;  // 編譯錯誤！
        
        // 無法創建類型參數的實例
        // T instance = new T();  // 編譯錯誤！
        
        // 無法創建類型參數數組
        // T[] array = new T[10];  // 編譯錯誤！
        
        System.out.println("  - 無法直接獲取類型參數的 Class 物件");
        System.out.println("  - 無法直接創建類型參數的實例");
        System.out.println("  - 無法直接創建類型參數的數組");
    }
}

// ==================== 複雜泛型類別 ====================

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
}

/**
 * 泛型二元樹
 * @param <T> 必須實現 Comparable 介面
 */
class BinaryTree<T extends Comparable<T>> {
    private TreeNode<T> root;
    
    private static class TreeNode<T> {
        T data;
        TreeNode<T> left;
        TreeNode<T> right;
        
        TreeNode(T data) {
            this.data = data;
        }
    }
    
    public void insert(T data) {
        root = insertRec(root, data);
    }
    
    private TreeNode<T> insertRec(TreeNode<T> root, T data) {
        if (root == null) {
            return new TreeNode<>(data);
        }
        
        int cmp = data.compareTo(root.data);
        if (cmp < 0) {
            root.left = insertRec(root.left, data);
        } else if (cmp > 0) {
            root.right = insertRec(root.right, data);
        }
        
        return root;
    }
    
    public boolean contains(T data) {
        return containsRec(root, data);
    }
    
    private boolean containsRec(TreeNode<T> root, T data) {
        if (root == null) {
            return false;
        }
        
        int cmp = data.compareTo(root.data);
        if (cmp == 0) {
            return true;
        } else if (cmp < 0) {
            return containsRec(root.left, data);
        } else {
            return containsRec(root.right, data);
        }
    }
    
    public void inorderTraversal(Consumer<T> visitor) {
        inorderRec(root, visitor);
    }
    
    private void inorderRec(TreeNode<T> root, Consumer<T> visitor) {
        if (root != null) {
            inorderRec(root.left, visitor);
            visitor.accept(root.data);
            inorderRec(root.right, visitor);
        }
    }
}

/**
 * 三元組容器
 * @param <T> 第一個值的類型
 * @param <U> 第二個值的類型
 * @param <V> 第三個值的類型
 */
class TripleContainer<T, U, V> {
    private final T first;
    private final U second;
    private final V third;
    
    public TripleContainer(T first, U second, V third) {
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
        return String.format("TripleContainer{first=%s, second=%s, third=%s}", 
                           first, second, third);
    }
}

// ==================== 泛型介面 ====================

/**
 * 泛型轉換器介面
 */
@FunctionalInterface
interface Transformer<T, R> {
    R transform(T input);
    
    default <V> Transformer<T, V> andThen(Transformer<R, V> after) {
        return input -> after.transform(transform(input));
    }
    
    default <V> Transformer<V, R> compose(Transformer<V, T> before) {
        return input -> transform(before.transform(input));
    }
}

/**
 * 泛型工廠介面
 */
@FunctionalInterface
interface Factory<T> {
    T create();
}

/**
 * 泛型比較器介面
 */
@FunctionalInterface
interface GenericComparator<T> {
    int compare(T o1, T o2);
    
    default GenericComparator<T> reversed() {
        return (o1, o2) -> compare(o2, o1);
    }
    
    default GenericComparator<T> thenComparing(GenericComparator<T> other) {
        return (o1, o2) -> {
            int result = compare(o1, o2);
            return result != 0 ? result : other.compare(o1, o2);
        };
    }
}

// ==================== 類型擦除示例 ====================

/**
 * 類型擦除示例類別
 */
class TypeErasureExample<T> {
    public void process(T item) {
        System.out.println("處理項目: " + item + " (類型: " + item.getClass() + ")");
    }
}

/**
 * 反射示例類別
 */
class ReflectionExample {
    @SuppressWarnings("unused")
    private List<String> stringList;
    
    public void processMap(Map<String, Integer> map) {
        // 方法實現
    }
}

/**
 * 泛型數組創建器
 */
class GenericArrayCreator {
    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(Class<T> type, int size) {
        return (T[]) Array.newInstance(type, size);
    }
}

// ==================== 設計模式示例 ====================

/**
 * 泛型建造者模式示例
 */
class PersonBuilder {
    private String name;
    private int age;
    private String email;
    
    public PersonBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public PersonBuilder setAge(int age) {
        this.age = age;
        return this;
    }
    
    public PersonBuilder setEmail(String email) {
        this.email = email;
        return this;
    }
    
    public Person build() {
        return new Person(name, age, email);
    }
}

/**
 * Person 類別
 */
class Person {
    private final String name;
    private final int age;
    private final String email;
    
    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    @Override
    public String toString() {
        return String.format("Person{name='%s', age=%d, email='%s'}", name, age, email);
    }
}

/**
 * 泛型單例模式
 */
class GenericSingleton {
    private static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        return (T) instances.computeIfAbsent(clazz, key -> {
            try {
                return key.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("無法創建實例: " + key, e);
            }
        });
    }
}

// 單例測試類別
class DatabaseConnection {
    public DatabaseConnection() {
        System.out.println("創建資料庫連接");
    }
}

class CacheManager {
    public CacheManager() {
        System.out.println("創建快取管理器");
    }
}

/**
 * 泛型觀察者模式
 */
@FunctionalInterface
interface GenericObserver<T> {
    void update(T data);
}

class GenericObservable<T> {
    private final List<GenericObserver<T>> observers = new ArrayList<>();
    
    public void addObserver(GenericObserver<T> observer) {
        observers.add(observer);
    }
    
    public void removeObserver(GenericObserver<T> observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(T data) {
        for (GenericObserver<T> observer : observers) {
            observer.update(data);
        }
    }
}

/**
 * 泛型策略模式
 */
interface SortingStrategy<T extends Comparable<T>> {
    void sort(List<T> list);
}

class BubbleSortStrategy<T extends Comparable<T>> implements SortingStrategy<T> {
    @Override
    public void sort(List<T> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).compareTo(list.get(j + 1)) > 0) {
                    Collections.swap(list, j, j + 1);
                }
            }
        }
    }
}

class QuickSortStrategy<T extends Comparable<T>> implements SortingStrategy<T> {
    @Override
    public void sort(List<T> list) {
        quickSort(list, 0, list.size() - 1);
    }
    
    private void quickSort(List<T> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }
    
    private int partition(List<T> list, int low, int high) {
        T pivot = list.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
}

class SortingContext<T extends Comparable<T>> {
    private SortingStrategy<T> strategy;
    
    public void setSortingStrategy(SortingStrategy<T> strategy) {
        this.strategy = strategy;
    }
    
    public List<T> sort(List<T> list) {
        if (strategy == null) {
            throw new IllegalStateException("未設置排序策略");
        }
        strategy.sort(list);
        return list;
    }
}

// ==================== 實際應用示例 ====================

/**
 * 泛型 DAO 基類
 */
abstract class GenericDAO<T, ID> {
    protected final Map<ID, T> storage = new HashMap<>();
    
    public void save(T entity) {
        ID id = extractId(entity);
        storage.put(id, entity);
    }
    
    public T findById(ID id) {
        return storage.get(id);
    }
    
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    public void deleteById(ID id) {
        storage.remove(id);
    }
    
    protected abstract ID extractId(T entity);
}

/**
 * User 實體
 */
class User {
    private final Integer id;
    private final String name;
    private final String email;
    
    public User(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public Integer getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s'}", id, name, email);
    }
}

/**
 * User DAO 實現
 */
class UserDAO extends GenericDAO<User, Integer> {
    @Override
    protected Integer extractId(User entity) {
        return entity.getId();
    }
}

/**
 * API 響應包裝器
 */
class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final int code;
    
    private ApiResponse(boolean success, T data, String message, int code) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "成功", 200);
    }
    
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(false, null, message, code);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public T getData() {
        return data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return String.format("ApiResponse{success=%s, data=%s, message='%s', code=%d}", 
                           success, data, message, code);
    }
}

/**
 * 事件處理系統
 */
class EventProcessor {
    private final Map<Class<?>, Consumer<Object>> handlers = new HashMap<>();
    
    @SuppressWarnings("unchecked")
    public <T> void registerHandler(Class<T> eventType, Consumer<T> handler) {
        handlers.put(eventType, (Consumer<Object>) handler);
    }
    
    public <T> void processEvent(T event) {
        Consumer<Object> handler = handlers.get(event.getClass());
        if (handler != null) {
            handler.accept(event);
        }
    }
}

// 事件類別
class OrderCreatedEvent {
    private final String orderId;
    
    public OrderCreatedEvent(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderId() {
        return orderId;
    }
}

class UserRegisteredEvent {
    private final String username;
    
    public UserRegisteredEvent(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
}

/**
 * 數據處理管道
 */
class Pipeline<T> {
    private final List<Function<T, T>> steps = new ArrayList<>();
    
    public static <T> Pipeline<T> create() {
        return new Pipeline<>();
    }
    
    public Pipeline<T> addStep(Function<T, T> step) {
        steps.add(step);
        return this;
    }
    
    public T process(T input) {
        T result = input;
        for (Function<T, T> step : steps) {
            result = step.apply(result);
        }
        return result;
    }
}

/**
 * 簡單的泛型容器（用於展示限制）
 */
class GenericContainer<T> {
    private T item;
    
    public void setItem(T item) {
        this.item = item;
    }
    
    public T getItem() {
        return item;
    }
}