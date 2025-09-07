/**
 * 設計模式示範 - Singleton, Factory, Observer
 * 展示三個基礎設計模式的實際應用
 */

import java.util.*;

// ================================
// 1. Singleton 模式示範
// ================================

/**
 * 配置管理器 - 使用Singleton模式
 * 確保整個應用程式只有一個配置管理實例
 */
class ConfigManager {
    // 靜態實例變數
    private static volatile ConfigManager instance;
    private Map<String, String> config;
    
    // 私有建構子
    private ConfigManager() {
        config = new HashMap<>();
        loadDefaultConfig();
    }
    
    // 雙重檢查鎖定的getInstance方法
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
    
    private void loadDefaultConfig() {
        config.put("app.name", "我的應用程式");
        config.put("app.version", "1.0.0");
        config.put("database.url", "localhost:3306");
        config.put("cache.size", "1000");
    }
    
    public String getConfig(String key) {
        return config.get(key);
    }
    
    public void setConfig(String key, String value) {
        config.put(key, value);
        System.out.println("配置已更新: " + key + " = " + value);
    }
    
    public void displayAllConfig() {
        System.out.println("=== 當前配置 ===");
        for (Map.Entry<String, String> entry : config.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("================");
    }
}

// ================================
// 2. Factory 模式示範
// ================================

/**
 * 圖形工廠模式示範
 */
interface Shape {
    void draw();
    double calculateArea();
    String getShapeType();
}

class Circle implements Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        System.out.println("繪製圓形，半徑: " + radius);
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public String getShapeType() {
        return "圓形";
    }
    
    public double getRadius() {
        return radius;
    }
}

class Rectangle implements Shape {
    private double width;
    private double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void draw() {
        System.out.println("繪製矩形，寬: " + width + ", 高: " + height);
    }
    
    @Override
    public double calculateArea() {
        return width * height;
    }
    
    @Override
    public String getShapeType() {
        return "矩形";
    }
    
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}

class Triangle implements Shape {
    private double base;
    private double height;
    
    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    
    @Override
    public void draw() {
        System.out.println("繪製三角形，底: " + base + ", 高: " + height);
    }
    
    @Override
    public double calculateArea() {
        return 0.5 * base * height;
    }
    
    @Override
    public String getShapeType() {
        return "三角形";
    }
    
    public double getBase() { return base; }
    public double getHeight() { return height; }
}

/**
 * 圖形工廠類別
 */
class ShapeFactory {
    public static Shape createShape(String shapeType, double... parameters) {
        switch (shapeType.toLowerCase()) {
            case "circle":
            case "圓形":
                if (parameters.length >= 1) {
                    return new Circle(parameters[0]);
                }
                throw new IllegalArgumentException("圓形需要1個參數：半徑");
                
            case "rectangle":
            case "矩形":
                if (parameters.length >= 2) {
                    return new Rectangle(parameters[0], parameters[1]);
                }
                throw new IllegalArgumentException("矩形需要2個參數：寬度和高度");
                
            case "triangle":
            case "三角形":
                if (parameters.length >= 2) {
                    return new Triangle(parameters[0], parameters[1]);
                }
                throw new IllegalArgumentException("三角形需要2個參數：底邊和高度");
                
            default:
                throw new IllegalArgumentException("不支援的圖形類型: " + shapeType);
        }
    }
    
    public static List<String> getSupportedShapes() {
        return Arrays.asList("圓形", "矩形", "三角形");
    }
}

// ================================
// 3. Observer 模式示範
// ================================

/**
 * 股價監控系統 - Observer模式
 */
interface Observer {
    void update(String stockSymbol, double price, double change);
}

interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String stockSymbol, double price, double change);
}

/**
 * 股價監控中心
 */
class StockPriceMonitor implements Subject {
    private List<Observer> observers;
    private Map<String, Double> stockPrices;
    private Map<String, Double> previousPrices;
    
    public StockPriceMonitor() {
        observers = new ArrayList<>();
        stockPrices = new HashMap<>();
        previousPrices = new HashMap<>();
        
        // 初始化一些股票價格
        stockPrices.put("AAPL", 150.0);
        stockPrices.put("GOOGL", 2800.0);
        stockPrices.put("TSLA", 800.0);
        
        // 複製到前一次價格
        previousPrices.putAll(stockPrices);
    }
    
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
        System.out.println("新的觀察者已加入股價監控");
    }
    
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        System.out.println("觀察者已離開股價監控");
    }
    
    @Override
    public void notifyObservers(String stockSymbol, double price, double change) {
        for (Observer observer : observers) {
            observer.update(stockSymbol, price, change);
        }
    }
    
    public void updateStockPrice(String stockSymbol, double newPrice) {
        double oldPrice = stockPrices.getOrDefault(stockSymbol, 0.0);
        double change = newPrice - oldPrice;
        
        previousPrices.put(stockSymbol, oldPrice);
        stockPrices.put(stockSymbol, newPrice);
        
        System.out.println("股價更新: " + stockSymbol + " $" + oldPrice + " -> $" + newPrice);
        notifyObservers(stockSymbol, newPrice, change);
    }
    
    public double getCurrentPrice(String stockSymbol) {
        return stockPrices.getOrDefault(stockSymbol, 0.0);
    }
    
    public void displayAllPrices() {
        System.out.println("=== 當前股價 ===");
        for (Map.Entry<String, Double> entry : stockPrices.entrySet()) {
            System.out.println(entry.getKey() + ": $" + entry.getValue());
        }
        System.out.println("================");
    }
}

/**
 * 投資者 - 觀察者
 */
class Investor implements Observer {
    private String name;
    private Map<String, Integer> portfolio;  // 持股數量
    
    public Investor(String name) {
        this.name = name;
        this.portfolio = new HashMap<>();
    }
    
    @Override
    public void update(String stockSymbol, double price, double change) {
        if (portfolio.containsKey(stockSymbol)) {
            int shares = portfolio.get(stockSymbol);
            double totalValue = shares * price;
            double totalChange = shares * change;
            
            System.out.println("[投資者 " + name + "] " + stockSymbol + 
                             " 價格變動: " + String.format("%.2f", change) + 
                             " | 持股: " + shares + " 股" +
                             " | 總值: $" + String.format("%.2f", totalValue) + 
                             " | 變動: " + String.format("%.2f", totalChange));
        }
    }
    
    public void buyStock(String stockSymbol, int shares) {
        portfolio.put(stockSymbol, portfolio.getOrDefault(stockSymbol, 0) + shares);
        System.out.println(name + " 買入 " + shares + " 股 " + stockSymbol);
    }
    
    public void sellStock(String stockSymbol, int shares) {
        int currentShares = portfolio.getOrDefault(stockSymbol, 0);
        if (currentShares >= shares) {
            portfolio.put(stockSymbol, currentShares - shares);
            System.out.println(name + " 賣出 " + shares + " 股 " + stockSymbol);
        } else {
            System.out.println(name + " 持股不足，無法賣出");
        }
    }
    
    public String getName() {
        return name;
    }
}

/**
 * 交易系統 - 觀察者
 */
class TradingSystem implements Observer {
    private String systemName;
    private Map<String, Double> alertPrices;
    
    public TradingSystem(String systemName) {
        this.systemName = systemName;
        this.alertPrices = new HashMap<>();
    }
    
    @Override
    public void update(String stockSymbol, double price, double change) {
        // 檢查是否觸發警告
        if (alertPrices.containsKey(stockSymbol)) {
            double alertPrice = alertPrices.get(stockSymbol);
            if (price >= alertPrice) {
                System.out.println("[" + systemName + "] 警告! " + stockSymbol + 
                                 " 達到目標價格 $" + alertPrice);
            }
        }
        
        // 檢查大幅變動
        double changePercent = Math.abs(change / (price - change)) * 100;
        if (changePercent > 5) {  // 5%以上變動
            System.out.println("[" + systemName + "] 注意! " + stockSymbol + 
                             " 大幅變動: " + String.format("%.2f%%", changePercent));
        }
    }
    
    public void setAlert(String stockSymbol, double targetPrice) {
        alertPrices.put(stockSymbol, targetPrice);
        System.out.println(systemName + " 設定 " + stockSymbol + " 目標價格警告: $" + targetPrice);
    }
}

// ================================
// 主要測試類別
// ================================
public class DesignPatternsDemo {
    public static void main(String[] args) {
        System.out.println("=== Java 設計模式示範 ===\n");
        
        // ================================
        // 測試 Singleton 模式
        // ================================
        System.out.println("=== 1. Singleton 模式測試 ===");
        
        ConfigManager config1 = ConfigManager.getInstance();
        ConfigManager config2 = ConfigManager.getInstance();
        
        System.out.println("兩個實例是否相同: " + (config1 == config2));
        
        config1.displayAllConfig();
        config1.setConfig("database.timeout", "30");
        config2.displayAllConfig();  // config2 也會看到變更
        
        // ================================
        // 測試 Factory 模式
        // ================================
        System.out.println("\n=== 2. Factory 模式測試 ===");
        
        System.out.println("支援的圖形類型: " + ShapeFactory.getSupportedShapes());
        
        try {
            Shape circle = ShapeFactory.createShape("圓形", 5.0);
            Shape rectangle = ShapeFactory.createShape("矩形", 4.0, 6.0);
            Shape triangle = ShapeFactory.createShape("三角形", 3.0, 4.0);
            
            List<Shape> shapes = Arrays.asList(circle, rectangle, triangle);
            
            for (Shape shape : shapes) {
                shape.draw();
                System.out.println("面積: " + String.format("%.2f", shape.calculateArea()));
                System.out.println();
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("工廠錯誤: " + e.getMessage());
        }
        
        // ================================
        // 測試 Observer 模式
        // ================================
        System.out.println("=== 3. Observer 模式測試 ===");
        
        // 創建股價監控中心
        StockPriceMonitor monitor = new StockPriceMonitor();
        
        // 創建觀察者
        Investor alice = new Investor("Alice");
        Investor bob = new Investor("Bob");
        TradingSystem tradingSystem = new TradingSystem("智能交易系統");
        
        // 投資者買股票
        alice.buyStock("AAPL", 100);
        alice.buyStock("GOOGL", 10);
        bob.buyStock("TSLA", 50);
        bob.buyStock("AAPL", 200);
        
        // 設定警告
        tradingSystem.setAlert("AAPL", 160.0);
        tradingSystem.setAlert("TSLA", 850.0);
        
        // 註冊觀察者
        monitor.addObserver(alice);
        monitor.addObserver(bob);
        monitor.addObserver(tradingSystem);
        
        System.out.println("\n--- 開始股價變動 ---");
        
        // 模擬股價變動
        monitor.updateStockPrice("AAPL", 155.0);
        System.out.println();
        
        monitor.updateStockPrice("TSLA", 780.0);
        System.out.println();
        
        monitor.updateStockPrice("AAPL", 165.0);  // 觸發警告
        System.out.println();
        
        monitor.updateStockPrice("GOOGL", 2600.0);  // 大幅下跌
        System.out.println();
        
        // 移除一個觀察者
        monitor.removeObserver(alice);
        
        System.out.println("--- Alice 離開監控後 ---");
        monitor.updateStockPrice("AAPL", 170.0);
        
        System.out.println("\n=== 設計模式示範完成 ===");
    }
}