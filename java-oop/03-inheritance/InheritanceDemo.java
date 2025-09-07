/**
 * 繼承機制示範
 * 展示單一繼承、方法覆寫、super關鍵字使用
 */

// 基礎車輛類別
class Vehicle {
    protected String brand;
    protected String model;
    protected int year;
    protected double price;
    protected String color;
    
    // 建構子
    public Vehicle(String brand, String model, int year, double price) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.color = "白色";  // 預設顏色
    }
    
    // 基本方法
    public void start() {
        System.out.println(brand + " " + model + " 正在啟動...");
        System.out.println("引擎啟動完成！");
    }
    
    public void stop() {
        System.out.println(brand + " " + model + " 正在停止...");
        System.out.println("引擎已關閉！");
    }
    
    public void displayInfo() {
        System.out.println("=== 車輛資訊 ===");
        System.out.println("品牌: " + brand);
        System.out.println("型號: " + model);
        System.out.println("年份: " + year);
        System.out.println("價格: $" + price);
        System.out.println("顏色: " + color);
    }
    
    public double calculateTax() {
        return price * 0.05;  // 基本稅率5%
    }
    
    // Getter和Setter方法
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getPrice() { return price; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}

// 汽車類別 - 繼承Vehicle
class Car extends Vehicle {
    private int doors;
    private String fuelType;
    private boolean hasAircon;
    
    // 建構子
    public Car(String brand, String model, int year, double price, int doors, String fuelType) {
        super(brand, model, year, price);  // 呼叫父類別建構子
        this.doors = doors;
        this.fuelType = fuelType;
        this.hasAircon = true;  // 汽車通常都有冷氣
    }
    
    // 覆寫父類別方法
    @Override
    public void start() {
        System.out.println("=== 汽車啟動程序 ===");
        System.out.println("1. 繫好安全帶");
        System.out.println("2. 調整後視鏡");
        super.start();  // 呼叫父類別的start方法
        System.out.println("3. 檢查檔位");
        System.out.println("4. 鬆開手煞車");
        System.out.println("汽車準備就緒！");
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();  // 呼叫父類別的displayInfo
        System.out.println("車門數: " + doors);
        System.out.println("燃料類型: " + fuelType);
        System.out.println("空調: " + (hasAircon ? "有" : "無"));
        System.out.println("車輛類型: 汽車");
        System.out.println("================");
    }
    
    @Override
    public double calculateTax() {
        double baseTax = super.calculateTax();  // 使用父類別的基本計算
        // 汽車額外稅費
        double additionalTax = 0;
        if (doors > 4) additionalTax += 1000;  // 多門汽車額外稅
        if (fuelType.equals("汽油")) additionalTax += 500;  // 汽油車額外稅
        
        return baseTax + additionalTax;
    }
    
    // 汽車特有方法
    public void openTrunk() {
        System.out.println("後車廂已開啟");
    }
    
    public void honk() {
        System.out.println("嘟嘟！汽車按喇叭");
    }
    
    public void toggleAircon() {
        hasAircon = !hasAircon;
        System.out.println("冷氣已" + (hasAircon ? "開啟" : "關閉"));
    }
    
    // Getter方法
    public int getDoors() { return doors; }
    public String getFuelType() { return fuelType; }
    public boolean isHasAircon() { return hasAircon; }
}

// 機車類別 - 繼承Vehicle
class Motorcycle extends Vehicle {
    private boolean hasSidecar;
    private int engineCC;
    
    public Motorcycle(String brand, String model, int year, double price, int engineCC) {
        super(brand, model, year, price);
        this.engineCC = engineCC;
        this.hasSidecar = false;  // 預設沒有側車
    }
    
    @Override
    public void start() {
        System.out.println("=== 機車啟動程序 ===");
        System.out.println("1. 戴好安全帽");
        System.out.println("2. 檢查煞車");
        super.start();  // 呼叫父類別方法
        System.out.println("3. 踩踏啟動或電啟動");
        System.out.println("機車啟動完成！");
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("引擎CC數: " + engineCC);
        System.out.println("側車: " + (hasSidecar ? "有" : "無"));
        System.out.println("車輛類型: 機車");
        System.out.println("================");
    }
    
    @Override
    public double calculateTax() {
        double baseTax = super.calculateTax();
        // 機車稅費較低
        double motorcycleTax = baseTax * 0.3;  // 機車稅率較低
        if (engineCC > 250) motorcycleTax += 500;  // 大型機車額外稅
        
        return motorcycleTax;
    }
    
    // 機車特有方法
    public void wheelie() {
        if (engineCC > 150) {
            System.out.println("機車翹孤輪！超帥的！");
        } else {
            System.out.println("引擎太小，無法翹孤輪");
        }
    }
    
    public void addSidecar() {
        if (!hasSidecar) {
            hasSidecar = true;
            System.out.println("已安裝側車");
        } else {
            System.out.println("已經有側車了");
        }
    }
    
    public void removeSidecar() {
        if (hasSidecar) {
            hasSidecar = false;
            System.out.println("已移除側車");
        } else {
            System.out.println("沒有側車可以移除");
        }
    }
    
    // Getter方法
    public boolean isHasSidecar() { return hasSidecar; }
    public int getEngineCC() { return engineCC; }
}

// 跑車類別 - 繼承Car（多層繼承）
class SportsCar extends Car {
    private int maxSpeed;
    private boolean hasTurbo;
    private String performanceMode;
    
    public SportsCar(String brand, String model, int year, double price, 
                    int doors, String fuelType, int maxSpeed, boolean hasTurbo) {
        super(brand, model, year, price, doors, fuelType);
        this.maxSpeed = maxSpeed;
        this.hasTurbo = hasTurbo;
        this.performanceMode = "舒適模式";
    }
    
    @Override
    public void start() {
        System.out.println("=== 跑車啟動程序 ===");
        System.out.println("1. 調整駕駛座位");
        System.out.println("2. 設定性能模式");
        super.start();  // 呼叫父類別（Car）的start方法
        System.out.println("5. 暖車完成");
        System.out.println("跑車準備展現性能！");
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();  // 呼叫父類別（Car）的displayInfo
        System.out.println("最高時速: " + maxSpeed + " km/h");
        System.out.println("渦輪增壓: " + (hasTurbo ? "有" : "無"));
        System.out.println("性能模式: " + performanceMode);
        System.out.println("車輛類型: 跑車");
        System.out.println("================");
    }
    
    @Override
    public double calculateTax() {
        double carTax = super.calculateTax();  // 使用父類別（Car）的計算
        // 跑車豪華稅
        double luxuryTax = price * 0.1;  // 10%豪華稅
        return carTax + luxuryTax;
    }
    
    // 跑車特有方法
    public void turboBoost() {
        if (hasTurbo) {
            System.out.println("渦輪增壓啟動！引擎咆哮聲響起！");
        } else {
            System.out.println("此跑車沒有渦輪增壓系統");
        }
    }
    
    public void setPerformanceMode(String mode) {
        String[] validModes = {"舒適模式", "運動模式", "賽道模式"};
        for (String validMode : validModes) {
            if (validMode.equals(mode)) {
                this.performanceMode = mode;
                System.out.println("性能模式已切換為: " + mode);
                return;
            }
        }
        System.out.println("無效的性能模式");
    }
    
    public void launch() {
        if (performanceMode.equals("賽道模式")) {
            System.out.println("彈射起步！0-100km/h加速中...");
            System.out.println("達到最高時速 " + maxSpeed + " km/h！");
        } else {
            System.out.println("請切換到賽道模式才能使用彈射起步");
        }
    }
    
    // Getter方法
    public int getMaxSpeed() { return maxSpeed; }
    public boolean isHasTurbo() { return hasTurbo; }
    public String getPerformanceMode() { return performanceMode; }
}

// 主測試類別
public class InheritanceDemo {
    public static void main(String[] args) {
        System.out.println("=== Java 繼承機制示範 ===\n");
        
        // 創建不同類型的車輛
        Vehicle basicVehicle = new Vehicle("通用", "基本型", 2020, 15000);
        Car familyCar = new Car("豐田", "Camry", 2022, 25000, 4, "汽油");
        Motorcycle sportBike = new Motorcycle("川崎", "Ninja", 2023, 18000, 636);
        SportsCar superCar = new SportsCar("法拉利", "F8", 2023, 300000, 2, "汽油", 340, true);
        
        // 測試多型 - 使用父類別參考指向子類別物件
        Vehicle[] vehicles = {basicVehicle, familyCar, sportBike, superCar};
        
        System.out.println("=== 多型示範：所有車輛啟動 ===");
        for (Vehicle vehicle : vehicles) {
            vehicle.start();
            System.out.println();
        }
        
        System.out.println("=== 各車輛詳細資訊 ===");
        for (Vehicle vehicle : vehicles) {
            vehicle.displayInfo();
        }
        
        System.out.println("=== 稅金計算 ===");
        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle.getBrand() + " " + vehicle.getModel() + 
                             " 稅金: $" + String.format("%.2f", vehicle.calculateTax()));
        }
        
        System.out.println("\n=== 子類別特有功能測試 ===");
        
        // 測試汽車特有功能
        System.out.println("--- 汽車功能 ---");
        familyCar.honk();
        familyCar.openTrunk();
        familyCar.toggleAircon();
        
        // 測試機車特有功能
        System.out.println("\n--- 機車功能 ---");
        sportBike.wheelie();
        sportBike.addSidecar();
        sportBike.wheelie();  // 再試一次
        
        // 測試跑車特有功能
        System.out.println("\n--- 跑車功能 ---");
        superCar.turboBoost();
        superCar.setPerformanceMode("運動模式");
        superCar.launch();  // 應該失敗
        superCar.setPerformanceMode("賽道模式");
        superCar.launch();  // 應該成功
        
        // 演示向下轉型（需要謹慎使用）
        System.out.println("\n=== 向下轉型示範 ===");
        if (vehicles[1] instanceof Car) {
            Car car = (Car) vehicles[1];
            System.out.println("成功轉型為Car，車門數: " + car.getDoors());
        }
        
        if (vehicles[3] instanceof SportsCar) {
            SportsCar sCar = (SportsCar) vehicles[3];
            System.out.println("成功轉型為SportsCar，最高時速: " + sCar.getMaxSpeed());
        }
    }
}