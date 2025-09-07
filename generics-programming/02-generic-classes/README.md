# 泛型類別 (Generic Classes)

## 📖 學習目標

通過本節學習，你將掌握：
- 如何定義泛型類別
- 單一類型參數和多類型參數
- 泛型類別的繼承
- 有界類型參數
- 泛型類別的最佳實踐

---

## 🎯 什麼是泛型類別？

泛型類別是使用一個或多個類型參數定義的類別，這些類型參數在創建實例時被具體類型替換。

### 基本語法

```java
// 單一類型參數
class Container<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
}

// 多類型參數
class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    // getter 和 setter 方法...
}
```

---

## 🔤 類型參數命名約定

| 符號 | 用途 | 範例 |
|------|------|------|
| `T` | Type（一般類型） | `Box<T>` |
| `E` | Element（集合元素） | `List<E>` |
| `K` | Key（鍵） | `Map<K, V>` |
| `V` | Value（值） | `Map<K, V>` |
| `N` | Number（數字類型） | `NumberBox<N>` |
| `S, U, V` | 第 2、3、4 個類型 | `Triple<T, U, V>` |

---

## 🏗️ 單一類型參數的泛型類別

### 基本泛型容器

```java
/**
 * 簡單的泛型容器
 * @param <T> 儲存的資料類型
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

### 使用範例

```java
// 字串容器
GenericBox<String> stringBox = new GenericBox<>();
stringBox.set("Hello World");
String value = stringBox.get();  // 類型安全

// 整數容器
GenericBox<Integer> intBox = new GenericBox<>(42);
Integer number = intBox.get();

// 自定義類型容器
GenericBox<List<String>> listBox = new GenericBox<>();
listBox.set(Arrays.asList("a", "b", "c"));
```

---

## 📦 多類型參數的泛型類別

### 鍵值對容器

```java
/**
 * 泛型鍵值對
 * @param <K> 鍵的類型
 * @param <V> 值的類型
 */
public class GenericPair<K, V> {
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
        return Objects.equals(key, pair.key) && 
               Objects.equals(value, pair.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
```

### 三元組容器

```java
/**
 * 三元組容器
 * @param <T> 第一個值的類型
 * @param <U> 第二個值的類型
 * @param <V> 第三個值的類型
 */
public class Triple<T, U, V> {
    private final T first;
    private final U second;
    private final V third;
    
    public Triple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    
    public T getFirst() { return first; }
    public U getSecond() { return second; }
    public V getThird() { return third; }
    
    @Override
    public String toString() {
        return String.format("Triple{%s, %s, %s}", first, second, third);
    }
}
```

---

## 🔗 泛型類別的繼承

### 1. 子類別保持泛型

```java
/**
 * 帶顏色的泛型容器
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
        return String.format("ColoredBox{content=%s, color=%s}", 
                           get(), color);
    }
}
```

### 2. 子類別指定具體類型

```java
/**
 * 專門儲存字串的容器
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
}
```

### 3. 子類別添加新的類型參數

```java
/**
 * 擴展的容器，添加了額外的類型參數
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
}
```

---

## 🎯 有界類型參數

### 上界限制（extends）

```java
/**
 * 只接受 Number 及其子類型的容器
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
    
    public double getDoubleValue() {
        return number.doubleValue();
    }
    
    public int getIntValue() {
        return number.intValue();
    }
    
    // 可以使用 Number 的方法
    public boolean isZero() {
        return number.doubleValue() == 0.0;
    }
    
    public boolean isPositive() {
        return number.doubleValue() > 0.0;
    }
}
```

### 多重界限

```java
/**
 * 既要實現 Comparable 又要是 Serializable
 * @param <T> 類型參數
 */
class SortableBox<T extends Comparable<T> & Serializable> {
    private T item;
    
    public SortableBox(T item) {
        this.item = item;
    }
    
    public T getItem() {
        return item;
    }
    
    public int compareTo(SortableBox<T> other) {
        return this.item.compareTo(other.item);
    }
    
    // 可以進行序列化操作
    public byte[] serialize() throws Exception {
        // 序列化邏輯
        return new byte[0];  // 簡化實現
    }
}
```

---

## 🔧 實用的泛型類別範例

### 1. 泛型堆疊

```java
/**
 * 泛型堆疊實現
 * @param <T> 元素類型
 */
public class GenericStack<T> {
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
```

### 2. 泛型快取

```java
/**
 * 簡單的泛型快取實現
 * @param <K> 鍵類型
 * @param <V> 值類型
 */
public class GenericCache<K, V> {
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
}
```

---

## 💡 最佳實踐

### 1. 使用有意義的類型參數名稱

```java
// ✅ 好的命名
class UserRepository<User, UserId> { }
class EventHandler<EventType> { }

// ❌ 不好的命名
class Repository<X, Y> { }
class Handler<Thing> { }
```

### 2. 提供適當的構造方法

```java
class GenericContainer<T> {
    private T item;
    
    // 默認構造方法
    public GenericContainer() {
    }
    
    // 帶參數的構造方法
    public GenericContainer(T item) {
        this.item = item;
    }
    
    // 靜態工廠方法
    public static <T> GenericContainer<T> of(T item) {
        return new GenericContainer<>(item);
    }
}
```

### 3. 實現合適的方法

```java
class GenericPair<K, V> {
    // 實現 equals, hashCode, toString
    @Override
    public boolean equals(Object obj) { /* ... */ }
    
    @Override
    public int hashCode() { /* ... */ }
    
    @Override
    public String toString() { /* ... */ }
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
java GenericClassesDemo
```

---

## 🎯 重點回顧

1. **泛型類別提供類型安全的容器**
2. **可以有單一或多個類型參數**
3. **支援繼承和多型**
4. **有界類型參數增強類型約束**
5. **遵循命名約定和最佳實踐**

---

## 📖 相關文件

- [泛型基礎 ←](../01-generics-basics/)
- [泛型方法 →](../03-generic-methods/)
- [通配符 →](../04-wildcards/)
- [練習題 →](../exercises/)

---

**記住：好的泛型類別設計是可重用、類型安全且易於理解的！** 🎯