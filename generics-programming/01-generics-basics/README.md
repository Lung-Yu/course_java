# 泛型基礎使用 (Generics Basics)

## 📖 學習目標

通過本節學習，你將掌握：
- 泛型的基本概念和語法
- 泛型的優勢和必要性
- 基本的泛型語法和類型參數
- 泛型與 Java 集合框架的關係

---

## 🎯 什麼是泛型？

泛型（Generics）是 Java 5 引入的重要特性，允許在定義類別、介面和方法時使用類型參數，從而實現：

### 🔍 泛型的優勢

1. **類型安全**：在編譯時檢查類型錯誤
2. **消除強制轉換**：避免手動類型轉換
3. **程式碼重用**：一套程式碼適用多種類型
4. **更好的效能**：避免運行時的類型檢查

### ❌ 沒有泛型的問題

```java
// Java 5 之前的寫法
List list = new ArrayList();
list.add("Hello");
list.add(123);  // 可以添加任何類型，危險！

// 需要強制轉換，可能出現 ClassCastException
String str = (String) list.get(0);  // OK
String str2 = (String) list.get(1); // 運行時異常！💥
```

### ✅ 使用泛型的解決方案

```java
// Java 5 之後的寫法
List<String> list = new ArrayList<String>();
list.add("Hello");
// list.add(123);  // 編譯錯誤！類型不匹配 ✋

// 不需要強制轉換
String str = list.get(0);  // 編譯時保證類型安全 ✅
```

---

## 🔤 泛型基本語法

### 類型參數命名約定

| 符號 | 含義 | 範例 |
|------|------|------|
| `T` | Type（類型） | `List<T>` |
| `E` | Element（元素） | `Set<E>` |
| `K` | Key（鍵） | `Map<K, V>` |
| `V` | Value（值） | `Map<K, V>` |
| `N` | Number（數字） | `Comparable<N>` |
| `S, U, V` | 額外的類型 | `Function<T, R>` |

### 基本使用範例

```java
// 1. 泛型集合
List<String> names = new ArrayList<String>();
Set<Integer> numbers = new HashSet<Integer>();
Map<String, Integer> scores = new HashMap<String, Integer>();

// Java 7+ 鑽石運算符
List<String> names = new ArrayList<>();
Set<Integer> numbers = new HashSet<>();
Map<String, Integer> scores = new HashMap<>();

// 2. 基本操作
names.add("Alice");
names.add("Bob");
// names.add(123);  // 編譯錯誤！

// 3. 類型安全的取值
String firstName = names.get(0);  // 不需要強制轉換
```

---

## 🏗️ 泛型的使用場景

### 1. 集合框架

```java
// List
List<String> fruits = Arrays.asList("蘋果", "香蕉", "橘子");
List<Integer> ages = Arrays.asList(25, 30, 35);

// Set
Set<String> uniqueWords = new HashSet<>();
uniqueWords.add("Java");
uniqueWords.add("Python");

// Map
Map<String, Integer> studentGrades = new HashMap<>();
studentGrades.put("Alice", 95);
studentGrades.put("Bob", 87);
```

### 2. 比較器

```java
// 泛型比較器
Comparator<String> lengthComparator = 
    (s1, s2) -> Integer.compare(s1.length(), s2.length());

List<String> words = Arrays.asList("java", "python", "go", "javascript");
words.sort(lengthComparator);
System.out.println(words);  // [go, java, python, javascript]
```

### 3. Optional 類別

```java
// 安全處理可能為 null 的值
Optional<String> optional = Optional.of("Hello");
if (optional.isPresent()) {
    String value = optional.get();  // 類型安全
    System.out.println(value);
}

// 使用 lambda 表達式
optional.ifPresent(System.out::println);
```

---

## 💡 實用範例

### 範例 1：類型安全的容器

```java
public class SafeContainer<T> {
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
}

// 使用
SafeContainer<String> stringContainer = new SafeContainer<>();
stringContainer.set("Hello World");
String value = stringContainer.get();  // 類型安全

SafeContainer<Integer> intContainer = new SafeContainer<>();
intContainer.set(42);
Integer number = intContainer.get();  // 類型安全
```

### 範例 2：泛型工具方法

```java
public class CollectionUtils {
    
    // 檢查集合是否為空
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
    
    // 獲取集合第一個元素
    public static <T> Optional<T> getFirst(List<T> list) {
        if (isEmpty(list)) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }
    
    // 獲取集合最後一個元素
    public static <T> Optional<T> getLast(List<T> list) {
        if (isEmpty(list)) {
            return Optional.empty();
        }
        return Optional.of(list.get(list.size() - 1));
    }
}

// 使用
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Optional<String> first = CollectionUtils.getFirst(names);
Optional<String> last = CollectionUtils.getLast(names);

first.ifPresent(name -> System.out.println("第一個: " + name));
last.ifPresent(name -> System.out.println("最後一個: " + name));
```

---

## 📚 編譯和執行

### 編譯指令
```bash
javac *.java
```

### 執行指令
```bash
java GenericsBasicsDemo
```

---

## 🎯 重點回顧

1. **泛型提供編譯時類型安全**
2. **消除了強制類型轉換**
3. **提高程式碼可讀性和重用性**
4. **主要用於集合框架和 API 設計**
5. **使用標準的類型參數命名約定**

---

## 📖 相關文件

- [泛型類別 →](../02-generic-classes/)
- [泛型方法 →](../03-generic-methods/)
- [通配符 →](../04-wildcards/)
- [練習題 →](../exercises/)

---

**記住：泛型是現代 Java 程式設計的基礎，掌握它對寫出高品質的程式碼至關重要！** 🎯