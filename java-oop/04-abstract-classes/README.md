# 抽象類別與抽象方法

## 學習目標
- 理解抽象類別的概念和用途
- 掌握抽象方法的設計和實作
- 學會何時使用抽象類別
- 理解抽象類別與介面的差異

## 核心概念

### 1. 抽象類別 (Abstract Class)
- 使用 `abstract` 關鍵字定義
- 不能被實例化（無法使用new建立物件）
- 可以包含抽象方法和具體方法
- 可以有建構子、屬性和具體方法

```java
public abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // 抽象方法 - 子類別必須實作
    public abstract double calculateArea();
    
    // 具體方法 - 子類別可以直接使用
    public void displayColor() {
        System.out.println("顏色: " + color);
    }
}
```

### 2. 抽象方法 (Abstract Method)
- 只有方法簽名，沒有實作內容
- 子類別必須覆寫所有抽象方法
- 如果子類別沒有實作所有抽象方法，子類別也必須是抽象類別

### 3. 使用時機
- 當多個類別有共同行為，但實作方式不同時
- 想要提供部分實作，讓子類別完成剩餘部分
- 需要強制子類別實作特定方法時

## 實務範例

### 範例1：幾何圖形系統
```java
abstract class Shape {
    protected String color;
    protected double x, y;  // 位置座標
    
    public Shape(String color, double x, double y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }
    
    // 抽象方法 - 每個圖形計算面積的方式不同
    public abstract double calculateArea();
    public abstract double calculatePerimeter();
    
    // 具體方法 - 所有圖形都可以使用
    public void move(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }
    
    public void displayInfo() {
        System.out.println("圖形顏色: " + color);
        System.out.println("位置: (" + x + ", " + y + ")");
        System.out.println("面積: " + calculateArea());
        System.out.println("周長: " + calculatePerimeter());
    }
}

class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double x, double y, double radius) {
        super(color, x, y);
        this.radius = radius;
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }
}

class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(String color, double x, double y, double width, double height) {
        super(color, x, y);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double calculateArea() {
        return width * height;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * (width + height);
    }
}
```

### 範例2：員工管理系統
```java
abstract class Employee {
    protected String name;
    protected String id;
    protected double baseSalary;
    
    public Employee(String name, String id, double baseSalary) {
        this.name = name;
        this.id = id;
        this.baseSalary = baseSalary;
    }
    
    // 抽象方法 - 不同類型員工薪資計算方式不同
    public abstract double calculateSalary();
    public abstract String getEmployeeType();
    
    // 具體方法 - 所有員工都有的基本行為
    public void displayBasicInfo() {
        System.out.println("員工姓名: " + name);
        System.out.println("員工編號: " + id);
        System.out.println("基本薪資: $" + baseSalary);
        System.out.println("員工類型: " + getEmployeeType());
        System.out.println("實際薪資: $" + calculateSalary());
    }
    
    public void work() {
        System.out.println(name + " 正在工作");
    }
}

class FullTimeEmployee extends Employee {
    private double bonus;
    
    public FullTimeEmployee(String name, String id, double baseSalary, double bonus) {
        super(name, id, baseSalary);
        this.bonus = bonus;
    }
    
    @Override
    public double calculateSalary() {
        return baseSalary + bonus;
    }
    
    @Override
    public String getEmployeeType() {
        return "全職員工";
    }
}

class PartTimeEmployee extends Employee {
    private int hoursWorked;
    private double hourlyRate;
    
    public PartTimeEmployee(String name, String id, double hourlyRate, int hoursWorked) {
        super(name, id, 0);  // 兼職員工沒有基本薪資
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }
    
    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked;
    }
    
    @Override
    public String getEmployeeType() {
        return "兼職員工";
    }
}
```

## 設計原則

### 1. 何時使用抽象類別
- 想要在相關類別間共享程式碼
- 需要宣告非static或non-final的欄位
- 需要提供某些方法的預設實作
- 類別繼承關係明確時

### 2. 抽象類別 vs 介面
| 特性 | 抽象類別 | 介面 |
|------|----------|------|
| 實例化 | 不可以 | 不可以 |
| 具體方法 | 可以有 | Java 8+可以有預設方法 |
| 屬性 | 可以有各種類型 | 只能有static final |
| 繼承/實作 | 單一繼承 | 多重實作 |
| 建構子 | 可以有 | 不可以有 |

## 實務練習

### 練習1：動物分類系統
設計Animal抽象類別，包含Mammal、Bird等子類別。

### 練習2：車輛管理系統
設計Vehicle抽象類別，實作不同類型的交通工具。

### 練習3：遊戲角色系統
設計Character抽象類別，實作戰士、法師等不同職業。

## 編譯與執行
```bash
javac Shape.java Circle.java Rectangle.java
java TestShapes
```