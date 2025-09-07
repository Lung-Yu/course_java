# 基本設計模式

## 學習目標
- 理解設計模式的概念和重要性
- 掌握三個基礎設計模式：Singleton、Factory、Observer
- 學會在實際專案中應用設計模式
- 理解設計模式解決的問題

## 核心概念

### 1. 什麼是設計模式
- 在特定情境下，針對常見問題的可重用解決方案
- 提供程式設計的最佳實務
- 改善程式碼的可讀性、可維護性和可擴展性

### 2. 設計模式的分類
- **創建型模式**：關注物件的創建過程（如Singleton、Factory）
- **結構型模式**：關注類別和物件的組合（如Adapter、Decorator）
- **行為型模式**：關注物件間的通信和責任分配（如Observer、Strategy）

## 1. Singleton 模式（單例模式）

### 目的
確保一個類別只有一個實例，並提供全域存取點。

### 使用場景
- 資料庫連接池
- 日誌記錄器
- 配置管理器
- 快取系統

### 實作方式

#### 餓漢式（Eager Initialization）
```java
public class EagerSingleton {
    // 在類別載入時就創建實例
    private static final EagerSingleton instance = new EagerSingleton();
    
    // 私有建構子，防止外部實例化
    private EagerSingleton() {}
    
    public static EagerSingleton getInstance() {
        return instance;
    }
    
    public void doSomething() {
        System.out.println("Singleton 執行某項操作");
    }
}
```

#### 懶漢式（Lazy Initialization）
```java
public class LazySingleton {
    private static LazySingleton instance;
    
    private LazySingleton() {}
    
    // 同步方法確保線程安全
    public static synchronized LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
```

#### 雙重檢查鎖定（Double-Checked Locking）
```java
public class ThreadSafeSingleton {
    private static volatile ThreadSafeSingleton instance;
    
    private ThreadSafeSingleton() {}
    
    public static ThreadSafeSingleton getInstance() {
        if (instance == null) {
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) {
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
}
```

## 2. Factory 模式（工廠模式）

### 目的
封裝物件創建過程，根據條件創建不同類型的物件。

### 使用場景
- 根據用戶輸入創建不同類型的物件
- 資料庫驅動程式選擇
- GUI元件創建

### 簡單工廠模式
```java
// 產品介面
interface Animal {
    void makeSound();
    void move();
}

// 具體產品
class Dog implements Animal {
    @Override
    public void makeSound() {
        System.out.println("汪汪叫");
    }
    
    @Override
    public void move() {
        System.out.println("狗在跑");
    }
}

class Cat implements Animal {
    @Override
    public void makeSound() {
        System.out.println("喵喵叫");
    }
    
    @Override
    public void move() {
        System.out.println("貓在走");
    }
}

class Bird implements Animal {
    @Override
    public void makeSound() {
        System.out.println("啾啾叫");
    }
    
    @Override
    public void move() {
        System.out.println("鳥在飛");
    }
}

// 工廠類別
class AnimalFactory {
    public static Animal createAnimal(String type) {
        switch (type.toLowerCase()) {
            case "dog":
                return new Dog();
            case "cat":
                return new Cat();
            case "bird":
                return new Bird();
            default:
                throw new IllegalArgumentException("未知的動物類型: " + type);
        }
    }
}

// 使用範例
public class FactoryPatternDemo {
    public static void main(String[] args) {
        Animal dog = AnimalFactory.createAnimal("dog");
        dog.makeSound();
        dog.move();
        
        Animal cat = AnimalFactory.createAnimal("cat");
        cat.makeSound();
        cat.move();
    }
}
```

### 工廠方法模式
```java
// 抽象產品
abstract class Vehicle {
    public abstract void start();
    public abstract void stop();
}

// 具體產品
class Car extends Vehicle {
    @Override
    public void start() {
        System.out.println("汽車啟動");
    }
    
    @Override
    public void stop() {
        System.out.println("汽車停止");
    }
}

class Motorcycle extends Vehicle {
    @Override
    public void start() {
        System.out.println("機車啟動");
    }
    
    @Override
    public void stop() {
        System.out.println("機車停止");
    }
}

// 抽象工廠
abstract class VehicleFactory {
    public abstract Vehicle createVehicle();
}

// 具體工廠
class CarFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new Car();
    }
}

class MotorcycleFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new Motorcycle();
    }
}
```

## 3. Observer 模式（觀察者模式）

### 目的
定義物件間一對多的依賴關係，當一個物件狀態改變時，所有依賴它的物件都會自動被通知。

### 使用場景
- GUI事件處理
- 模型-視圖架構
- 消息系統
- 股價監控系統

### 實作範例
```java
import java.util.*;

// 觀察者介面
interface Observer {
    void update(String message);
}

// 主題介面
interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String message);
}

// 具體主題 - 新聞發布者
class NewsAgency implements Subject {
    private List<Observer> observers;
    private String news;
    
    public NewsAgency() {
        this.observers = new ArrayList<>();
    }
    
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
    
    public void setNews(String news) {
        this.news = news;
        notifyObservers("新聞更新: " + news);
    }
    
    public String getNews() {
        return news;
    }
}

// 具體觀察者 - 新聞頻道
class NewsChannel implements Observer {
    private String channelName;
    
    public NewsChannel(String channelName) {
        this.channelName = channelName;
    }
    
    @Override
    public void update(String message) {
        System.out.println(channelName + " 收到通知: " + message);
    }
}

// 具體觀察者 - 手機應用
class MobileApp implements Observer {
    private String appName;
    
    public MobileApp(String appName) {
        this.appName = appName;
    }
    
    @Override
    public void update(String message) {
        System.out.println(appName + " 推送通知: " + message);
    }
}

// 使用範例
public class ObserverPatternDemo {
    public static void main(String[] args) {
        NewsAgency agency = new NewsAgency();
        
        NewsChannel cnn = new NewsChannel("CNN");
        NewsChannel bbc = new NewsChannel("BBC");
        MobileApp newsApp = new MobileApp("新聞APP");
        
        agency.addObserver(cnn);
        agency.addObserver(bbc);
        agency.addObserver(newsApp);
        
        agency.setNews("重大科技突破！");
        agency.setNews("股市大漲！");
        
        // 移除一個觀察者
        agency.removeObserver(bbc);
        agency.setNews("天氣預報更新");
    }
}
```

## 設計模式的優缺點

### 優點
1. **提高程式碼重用性**：標準化的解決方案
2. **改善可維護性**：結構清晰，易於理解
3. **增強可擴展性**：便於添加新功能
4. **促進團隊溝通**：共同的設計詞彙

### 缺點
1. **增加複雜性**：可能過度設計
2. **學習成本**：需要時間理解和掌握
3. **效能考量**：某些模式可能影響效能

## 實務練習

### 練習1：配置管理器
使用Singleton模式實作應用程式配置管理器。

### 練習2：圖形繪製系統
使用Factory模式創建不同類型的圖形物件。

### 練習3：股價監控系統
使用Observer模式實作股價變動通知系統。

## 編譯與執行
```bash
javac *.java
java ObserverPatternDemo
```