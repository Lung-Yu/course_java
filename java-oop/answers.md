# Java 物件導向程式設計 - 解答

---

## 1. 物件導向設計原則 - 解答

### 題目1-1解答：Book類別

```java
public class Book {
    // 私有屬性 - 封裝
    private String title;
    private String author;
    private double price;
    
    // 建構子
    public Book(String title, String author, double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }
    
    // Getter方法
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public double getPrice() {
        return price;
    }
    
    // Setter方法
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }
    
    // 顯示書籍資訊
    public void displayInfo() {
        System.out.println("書名: " + title + ", 作者: " + author + ", 價格: $" + price);
    }
}
```

**編譯與執行：**
```bash
javac Book.java
java Book
```

### 題目1-2解答：動物類別系統

```java
// 抽象化 - 基礎動物類別
abstract class Animal {
    protected String name;
    protected int age;
    
    // 封裝 - 建構子
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // 抽象方法
    public abstract void makeSound();
    
    // 具體方法
    public void displayInfo() {
        System.out.println("名字: " + name + ", 年齡: " + age);
    }
}

// 繼承
class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);
        this.breed = breed;
    }
    
    // 多型 - 方法覆寫
    @Override
    public void makeSound() {
        System.out.println(name + " 汪汪叫!");
    }
    
    public void displayBreed() {
        System.out.println("品種: " + breed);
    }
}

class Cat extends Animal {
    public Cat(String name, int age) {
        super(name, age);
    }
    
    // 多型 - 方法覆寫
    @Override
    public void makeSound() {
        System.out.println(name + " 喵喵叫!");
    }
}
```

---

## 2. 類別設計與建構 - 解答

### 題目2-1解答：基本Student類別

```java
public class Student {
    // 私有屬性
    private String name;
    private int score;
    
    // 預設建構子
    public Student() {
        this.name = "未知";
        this.score = 0;
    }
    
    // 帶參數建構子
    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    // Getter方法
    public String getName() {
        return name;
    }
    
    public int getScore() {
        return score;
    }
    
    // Setter方法
    public void setName(String name) {
        this.name = name;
    }
    
    public void setScore(int score) {
        if (score >= 0 && score <= 100) {
            this.score = score;
        }
    }
    
    // 顯示學生資訊
    public void displayInfo() {
        System.out.println("姓名: " + name + ", 分數: " + score);
    }
    
    // 主方法測試
    public static void main(String[] args) {
        Student student1 = new Student("張三", 85);
        Student student2 = new Student();
        
        student1.displayInfo();
        student2.displayInfo();
        
        student2.setName("李四");
        student2.setScore(92);
        student2.displayInfo();
    }
}
```

### 題目2-2解答：多建構子設計

```java
public class StudentAdvanced {
    private String name;
    private int score;
    private String studentId;
    private String major;
    
    // 預設建構子
    public StudentAdvanced() {
        this("未知", 0, "000000", "未定");
    }
    
    // 建構子重載
    public StudentAdvanced(String name) {
        this(name, 0, "000000", "未定");
    }
    
    public StudentAdvanced(String name, int score) {
        this(name, score, "000000", "未定");
    }
    
    public StudentAdvanced(String name, int score, String studentId) {
        this(name, score, studentId, "未定");
    }
    
    // 完整建構子
    public StudentAdvanced(String name, int score, String studentId, String major) {
        this.name = name;
        this.score = score;
        this.studentId = studentId;
        this.major = major;
    }
    
    // 複製建構子
    public StudentAdvanced(StudentAdvanced other) {
        this.name = other.name;
        this.score = other.score;
        this.studentId = other.studentId;
        this.major = other.major;
    }
    
    // 方法重載示例
    public void updateScore(int newScore) {
        this.score = newScore;
    }
    
    public void updateScore(String letterGrade) {
        switch (letterGrade.toUpperCase()) {
            case "A": this.score = 90; break;
            case "B": this.score = 80; break;
            case "C": this.score = 70; break;
            case "D": this.score = 60; break;
            default: this.score = 0;
        }
    }
    
    public void displayInfo() {
        System.out.println("學號: " + studentId + ", 姓名: " + name + 
                         ", 分數: " + score + ", 科系: " + major);
    }
}
```

### 題目2-3解答：BankAccount封裝示例

```java
public class BankAccount {
    private String accountNumber;
    private double balance;
    private String ownerName;
    
    public BankAccount(String accountNumber, String ownerName) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = 0.0;
    }
    
    // 存款
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("存款成功，金額: $" + amount);
        } else {
            System.out.println("存款金額必須大於0");
        }
    }
    
    // 提款
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("提款成功，金額: $" + amount);
            return true;
        } else {
            System.out.println("提款失敗：金額不足或無效");
            return false;
        }
    }
    
    // 查詢餘額（只提供查詢，不允許直接修改）
    public double getBalance() {
        return balance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void displayAccountInfo() {
        System.out.println("帳戶號碼: " + accountNumber + 
                         ", 戶名: " + ownerName + 
                         ", 餘額: $" + balance);
    }
}
```

---

## 3. 繼承機制 - 解答

### 題目3-1解答：車輛繼承體系

```java
// 基礎車輛類別
class Vehicle {
    protected String brand;
    protected String model;
    protected int year;
    protected double price;
    
    public Vehicle(String brand, String model, int year, double price) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
    }
    
    public void start() {
        System.out.println(brand + " " + model + " 啟動了!");
    }
    
    public void stop() {
        System.out.println(brand + " " + model + " 停止了!");
    }
    
    public void displayInfo() {
        System.out.println("品牌: " + brand + ", 型號: " + model + 
                         ", 年份: " + year + ", 價格: $" + price);
    }
}

// 汽車類別
class Car extends Vehicle {
    private int doors;
    private String fuelType;
    
    public Car(String brand, String model, int year, double price, int doors, String fuelType) {
        super(brand, model, year, price);
        this.doors = doors;
        this.fuelType = fuelType;
    }
    
    public void openTrunk() {
        System.out.println("後車廂已開啟");
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("車門數: " + doors + ", 燃料類型: " + fuelType);
    }
}

// 機車類別
class Motorcycle extends Vehicle {
    private boolean hasSidecar;
    
    public Motorcycle(String brand, String model, int year, double price, boolean hasSidecar) {
        super(brand, model, year, price);
        this.hasSidecar = hasSidecar;
    }
    
    public void wheelie() {
        System.out.println("機車正在翹孤輪!");
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("是否有側車: " + (hasSidecar ? "是" : "否"));
    }
}
```

### 題目3-2解答：方法覆寫與super關鍵字

```java
class VehicleAdvanced {
    protected String brand;
    protected String model;
    protected double engineSize;
    
    public VehicleAdvanced(String brand, String model, double engineSize) {
        this.brand = brand;
        this.model = model;
        this.engineSize = engineSize;
    }
    
    public void start() {
        System.out.println("車輛啟動程序：");
        System.out.println("1. 檢查安全帶");
        System.out.println("2. 啟動引擎");
    }
    
    public double calculateTax() {
        return engineSize * 1000; // 基本稅額計算
    }
}

class CarAdvanced extends VehicleAdvanced {
    private String transmission;
    
    public CarAdvanced(String brand, String model, double engineSize, String transmission) {
        super(brand, model, engineSize);
        this.transmission = transmission;
    }
    
    @Override
    public void start() {
        super.start(); // 呼叫父類別方法
        System.out.println("3. 檢查檔位");
        System.out.println("4. 鬆開手煞車");
        System.out.println("汽車啟動完成！");
    }
    
    @Override
    public double calculateTax() {
        double baseTax = super.calculateTax(); // 使用父類別的計算
        // 汽車額外稅費
        return baseTax + (transmission.equals("自動") ? 500 : 0);
    }
}
```

### 題目3-3解答：多層繼承

```java
// 第一層：車輛
class Vehicle {
    protected String brand;
    protected int year;
    
    public Vehicle(String brand, int year) {
        this.brand = brand;
        this.year = year;
    }
    
    public void move() {
        System.out.println("車輛正在移動");
    }
}

// 第二層：汽車
class Car extends Vehicle {
    protected int doors;
    
    public Car(String brand, int year, int doors) {
        super(brand, year);
        this.doors = doors;
    }
    
    @Override
    public void move() {
        System.out.println("汽車在路上行駛");
    }
    
    public void honk() {
        System.out.println("汽車按喇叭：嘟嘟！");
    }
}

// 第三層：跑車
class SportsCar extends Car {
    private int maxSpeed;
    private boolean hasTurbo;
    
    public SportsCar(String brand, int year, int doors, int maxSpeed, boolean hasTurbo) {
        super(brand, year, doors);
        this.maxSpeed = maxSpeed;
        this.hasTurbo = hasTurbo;
    }
    
    @Override
    public void move() {
        System.out.println("跑車高速行駛，最高時速: " + maxSpeed + " km/h");
    }
    
    public void turboBoost() {
        if (hasTurbo) {
            System.out.println("渦輪增壓啟動！");
        } else {
            System.out.println("此跑車沒有渦輪增壓");
        }
    }
    
    public void displaySpecs() {
        System.out.println("品牌: " + brand + ", 年份: " + year + 
                         ", 車門: " + doors + ", 最高時速: " + maxSpeed + 
                         ", 渦輪: " + (hasTurbo ? "有" : "無"));
    }
}
```

---

## 編譯與執行指令

```bash
# 編譯單一檔案
javac Student.java

# 執行
java Student

# 編譯多個相關檔案
javac *.java

# 編譯包含繼承關係的檔案
javac Vehicle.java Car.java Motorcycle.java

# 執行主類別
java MainClass
```

---

*註：此為課程解答的第一部分，包含前3個主題的完整解答。其餘主題的解答將在各自的子目錄中提供更詳細的內容。*

---

## 4-12. 進階主題解答

### 主題4：抽象類別與方法
詳細解答請參考：`04-abstract-classes/README.md`

### 主題5：介面設計與實作  
詳細解答請參考：`05-interfaces/README.md`

### 主題6：泛型程式設計
詳細解答請參考：`06-generics/README.md`

### 主題7：設計模式
**重要設計模式實作範例：**
- 完整實作請參考：`07-design-patterns/DesignPatternsDemo.java`
- 包含：Singleton、Factory、Observer、Builder、Strategy 模式

### 主題8：檔案輸入輸出
**檔案 I/O 處理完整示範：**
- 完整實作請參考：`08-file-io/FileIODemo.java`
- 功能：文字檔案、二進位檔案、目錄操作、檔案監控、CSV處理、日誌系統

### 主題9：Stream API 與函數式程式設計
**現代 Java 函數式程式設計：**
- 完整實作請參考：`09-stream-processing/StreamProcessingDemo.java`
- 內容：Lambda 表達式、Stream 操作、並行處理、Collectors 使用

### 主題10：例外處理
**完整例外處理機制：**
- 完整實作請參考：`10-exception-handling/ExceptionHandlingDemo.java`
- 特色：自訂例外、try-with-resources、例外傳播、最佳實務

### 主題11：多執行緒程式設計
**多執行緒程式設計完整指南：**
- 完整實作請參考：`11-multithreading/MultithreadingDemo.java`
- 涵蓋：Thread/Runnable、ExecutorService、執行緒安全、同步機制、並行計算

### 主題12：JSON 和 XML 處理
**資料交換格式處理：**
- 完整實作請參考：`12-json-xml-processing/JsonXmlProcessingDemo.java`
- 功能：JSON/XML序列化、設定檔處理、API回應處理、批次資料處理

---

## 完整課程編譯執行範例

### 編譯所有示範程式
```bash
# 進入各主題目錄並編譯
cd 07-design-patterns && javac DesignPatternsDemo.java
cd ../08-file-io && javac FileIODemo.java
cd ../09-stream-processing && javac StreamProcessingDemo.java
cd ../10-exception-handling && javac ExceptionHandlingDemo.java
cd ../11-multithreading && javac MultithreadingDemo.java
cd ../12-json-xml-processing && javac JsonXmlProcessingDemo.java
```

### 執行所有示範程式
```bash
# 執行各主題的示範程式
java DesignPatternsDemo
java FileIODemo
java StreamProcessingDemo
java ExceptionHandlingDemo
java MultithreadingDemo
java JsonXmlProcessingDemo
```

### 課程學習進度檢核
- ✅ 主題1-3：基礎 OOP 概念和實作
- ✅ 主題4-6：進階語言特性
- ✅ 主題7：設計模式應用
- ✅ 主題8：檔案和 I/O 處理
- ✅ 主題9：現代 Java 特性
- ✅ 主題10：錯誤處理機制
- ✅ 主題11：並行程式設計
- ✅ 主題12：資料格式處理

**課程完成！所有主題都包含完整的理論說明、實作範例和可執行程式碼。**