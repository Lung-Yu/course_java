# 方法定義與呼叫

## 📖 學習目標

通過本章學習，你將掌握：
- 方法的定義語法和調用方式
- 參數傳遞機制（值傳遞 vs 引用傳遞）
- 回傳值的使用和類型匹配
- 方法多載（Overloading）的原理和應用
- 方法設計的最佳實踐

---

## 🔍 方法基礎概念

### 什麼是方法？

方法（Method）是一段具有特定功能的程式碼塊，可以被重複調用。它是 Java 程式設計的基本組成單位，有助於：

- **程式碼重用**：避免重複編寫相同邏輯
- **模組化設計**：將複雜問題分解為小問題
- **易於維護**：修改功能只需修改一處
- **增強可讀性**：透過方法名表達程式意圖

### 方法的基本語法

```java
[存取修飾符] [修飾符] 回傳類型 方法名(參數列表) {
    // 方法體
    [return 回傳值;]
}
```

**語法元素說明：**
- **存取修飾符**：public、private、protected 或預設
- **修飾符**：static、final、abstract 等
- **回傳類型**：方法回傳的資料類型，void 表示無回傳值
- **方法名**：遵循駝峰命名法，動詞開頭
- **參數列表**：輸入參數，可以為空

---

## 💻 基本方法範例

### 無參數無回傳值方法

```java
public class BasicMethods {
    
    /**
     * 簡單的問候方法
     * 無參數，無回傳值
     */
    public static void sayHello() {
        System.out.println("Hello, World!");
    }
    
    /**
     * 打印分隔線
     */
    public static void printSeparator() {
        System.out.println("================================");
    }
    
    public static void main(String[] args) {
        sayHello();
        printSeparator();
        System.out.println("歡迎學習 Java 方法！");
    }
}
```

### 帶參數的方法

```java
public class ParameterMethods {
    
    /**
     * 個人化問候
     * @param name 姓名
     */
    public static void greetPerson(String name) {
        System.out.println("Hello, " + name + "!");
    }
    
    /**
     * 計算兩個數的和
     * @param a 第一個數
     * @param b 第二個數
     */
    public static void printSum(int a, int b) {
        int sum = a + b;
        System.out.println(a + " + " + b + " = " + sum);
    }
    
    /**
     * 打印指定字符的分隔線
     * @param character 分隔字符
     * @param length 長度
     */
    public static void printCustomSeparator(char character, int length) {
        for (int i = 0; i < length; i++) {
            System.out.print(character);
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        greetPerson("張三");
        printSum(5, 3);
        printCustomSeparator('*', 20);
        printCustomSeparator('-', 15);
    }
}
```

### 有回傳值的方法

```java
public class ReturnValueMethods {
    
    /**
     * 計算兩個數的和
     * @param a 第一個數
     * @param b 第二個數
     * @return 兩數之和
     */
    public static int add(int a, int b) {
        return a + b;
    }
    
    /**
     * 計算兩個數的最大值
     * @param a 第一個數
     * @param b 第二個數
     * @return 較大的數
     */
    public static int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
        // 簡化寫法：return (a > b) ? a : b;
    }
    
    /**
     * 檢查數字是否為偶數
     * @param number 要檢查的數字
     * @return true 如果是偶數，false 如果是奇數
     */
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }
    
    /**
     * 計算圓的面積
     * @param radius 半徑
     * @return 圓的面積
     */
    public static double calculateCircleArea(double radius) {
        return Math.PI * radius * radius;
    }
    
    /**
     * 生成問候訊息
     * @param name 姓名
     * @param isVip 是否為VIP
     * @return 問候訊息
     */
    public static String generateGreeting(String name, boolean isVip) {
        if (isVip) {
            return "尊敬的 " + name + " 先生/女士，歡迎您！";
        } else {
            return "Hello, " + name + "!";
        }
    }
    
    public static void main(String[] args) {
        // 使用有回傳值的方法
        int sum = add(10, 20);
        System.out.println("10 + 20 = " + sum);
        
        int maximum = max(15, 8);
        System.out.println("15 和 8 的最大值是：" + maximum);
        
        boolean even = isEven(6);
        System.out.println("6 是偶數嗎？" + even);
        
        double area = calculateCircleArea(5.0);
        System.out.printf("半徑為 5.0 的圓面積：%.2f%n", area);
        
        String greeting1 = generateGreeting("王小明", true);
        String greeting2 = generateGreeting("李小華", false);
        System.out.println(greeting1);
        System.out.println(greeting2);
    }
}
```

---

## 🔄 參數傳遞機制

### 值傳遞 (Pass by Value)

Java 中所有參數都是**值傳遞**，但需要理解基本型別和物件引用的差異：

```java
public class ParameterPassing {
    
    /**
     * 基本型別參數傳遞
     * 方法內的修改不會影響原變數
     */
    public static void modifyPrimitive(int x) {
        System.out.println("方法內修改前：x = " + x);
        x = 100;
        System.out.println("方法內修改後：x = " + x);
    }
    
    /**
     * 陣列參數傳遞
     * 傳遞的是陣列引用的副本，可以修改陣列內容
     */
    public static void modifyArray(int[] arr) {
        System.out.println("方法內修改前：arr[0] = " + arr[0]);
        arr[0] = 999;
        System.out.println("方法內修改後：arr[0] = " + arr[0]);
    }
    
    /**
     * 字串參數傳遞
     * String 是不可變物件，方法內無法修改原字串
     */
    public static void modifyString(String str) {
        System.out.println("方法內修改前：str = " + str);
        str = "Modified";
        System.out.println("方法內修改後：str = " + str);
    }
    
    /**
     * 物件參數傳遞
     */
    static class Person {
        String name;
        int age;
        
        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
    
    public static void modifyObject(Person p) {
        System.out.println("方法內修改前：" + p);
        p.name = "Modified Name";
        p.age = 999;
        System.out.println("方法內修改後：" + p);
    }
    
    public static void reassignObject(Person p) {
        System.out.println("方法內重新賦值前：" + p);
        p = new Person("New Person", 100);
        System.out.println("方法內重新賦值後：" + p);
    }
    
    public static void main(String[] args) {
        // 測試基本型別
        System.out.println("=== 基本型別參數傳遞 ===");
        int number = 10;
        System.out.println("調用方法前：number = " + number);
        modifyPrimitive(number);
        System.out.println("調用方法後：number = " + number);
        
        System.out.println("\n=== 陣列參數傳遞 ===");
        int[] array = {1, 2, 3};
        System.out.println("調用方法前：array[0] = " + array[0]);
        modifyArray(array);
        System.out.println("調用方法後：array[0] = " + array[0]);
        
        System.out.println("\n=== 字串參數傳遞 ===");
        String text = "Original";
        System.out.println("調用方法前：text = " + text);
        modifyString(text);
        System.out.println("調用方法後：text = " + text);
        
        System.out.println("\n=== 物件參數傳遞 - 修改屬性 ===");
        Person person1 = new Person("張三", 25);
        System.out.println("調用方法前：" + person1);
        modifyObject(person1);
        System.out.println("調用方法後：" + person1);
        
        System.out.println("\n=== 物件參數傳遞 - 重新賦值 ===");
        Person person2 = new Person("李四", 30);
        System.out.println("調用方法前：" + person2);
        reassignObject(person2);
        System.out.println("調用方法後：" + person2);
    }
}
```

**重要概念總結：**
- **基本型別**：傳遞值的副本，方法內修改不影響原變數
- **物件引用**：傳遞引用的副本，可以修改物件屬性，但無法改變引用本身
- **陣列**：特殊的物件，可以修改陣列內容
- **字串**：不可變物件，無法修改原字串

---

## 🔀 方法多載 (Method Overloading)

方法多載允許在同一個類別中定義多個同名方法，只要它們的**參數列表**不同：

```java
public class MethodOverloading {
    
    /**
     * 計算兩個整數的和
     */
    public static int add(int a, int b) {
        System.out.println("調用 add(int, int)");
        return a + b;
    }
    
    /**
     * 計算三個整數的和
     */
    public static int add(int a, int b, int c) {
        System.out.println("調用 add(int, int, int)");
        return a + b + c;
    }
    
    /**
     * 計算兩個雙精度數的和
     */
    public static double add(double a, double b) {
        System.out.println("調用 add(double, double)");
        return a + b;
    }
    
    /**
     * 計算整數和雙精度數的和
     */
    public static double add(int a, double b) {
        System.out.println("調用 add(int, double)");
        return a + b;
    }
    
    /**
     * 計算雙精度數和整數的和
     */
    public static double add(double a, int b) {
        System.out.println("調用 add(double, int)");
        return a + b;
    }
    
    /**
     * 打印訊息 - 無參數版本
     */
    public static void print() {
        System.out.println("調用 print() - 無參數");
        System.out.println("Hello, World!");
    }
    
    /**
     * 打印訊息 - 單一字串參數
     */
    public static void print(String message) {
        System.out.println("調用 print(String)");
        System.out.println(message);
    }
    
    /**
     * 打印訊息 - 字串和重複次數
     */
    public static void print(String message, int times) {
        System.out.println("調用 print(String, int)");
        for (int i = 0; i < times; i++) {
            System.out.println((i + 1) + ": " + message);
        }
    }
    
    /**
     * 打印陣列
     */
    public static void print(int[] array) {
        System.out.println("調用 print(int[])");
        System.out.print("陣列內容：[");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
    
    public static void main(String[] args) {
        System.out.println("=== 方法多載演示 ===\n");
        
        // add 方法的多載版本
        System.out.println("--- add 方法多載 ---");
        System.out.println("結果：" + add(5, 3));
        System.out.println("結果：" + add(1, 2, 3));
        System.out.println("結果：" + add(2.5, 3.7));
        System.out.println("結果：" + add(5, 2.5));
        System.out.println("結果：" + add(2.5, 5));
        
        System.out.println("\n--- print 方法多載 ---");
        print();
        print("歡迎學習 Java！");
        print("重複訊息", 3);
        print(new int[]{1, 2, 3, 4, 5});
    }
}
```

### 方法多載的規則

1. **參數數量不同**：`method(int a)` vs `method(int a, int b)`
2. **參數類型不同**：`method(int a)` vs `method(double a)`
3. **參數順序不同**：`method(int a, double b)` vs `method(double a, int b)`

**注意事項：**
- 僅回傳型別不同**不能**構成多載
- 僅參數名稱不同**不能**構成多載
- 編譯器根據參數列表選擇最匹配的方法

---

## 🛠 可變參數 (Varargs)

Java 5 引入了可變參數，允許方法接受任意數量的同類型參數：

```java
public class VarargsDemo {
    
    /**
     * 計算任意數量整數的和
     * @param numbers 可變參數
     * @return 所有數字的和
     */
    public static int sum(int... numbers) {
        System.out.println("接收到 " + numbers.length + " 個參數");
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }
    
    /**
     * 打印任意數量的字串
     * @param messages 可變參數
     */
    public static void printMessages(String... messages) {
        System.out.println("共有 " + messages.length + " 條訊息：");
        for (int i = 0; i < messages.length; i++) {
            System.out.println((i + 1) + ". " + messages[i]);
        }
    }
    
    /**
     * 格式化字串（類似 printf）
     * @param format 格式字串
     * @param args 可變參數
     */
    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }
    
    /**
     * 找出最大值
     * @param first 至少需要一個參數
     * @param others 其他參數
     * @return 最大值
     */
    public static int max(int first, int... others) {
        int maximum = first;
        for (int num : others) {
            if (num > maximum) {
                maximum = num;
            }
        }
        return maximum;
    }
    
    public static void main(String[] args) {
        System.out.println("=== 可變參數演示 ===\n");
        
        // sum 方法測試
        System.out.println("--- sum 方法 ---");
        System.out.println("sum() = " + sum());
        System.out.println("sum(5) = " + sum(5));
        System.out.println("sum(1, 2, 3) = " + sum(1, 2, 3));
        System.out.println("sum(1, 2, 3, 4, 5) = " + sum(1, 2, 3, 4, 5));
        
        // 傳遞陣列
        int[] numbers = {10, 20, 30};
        System.out.println("sum(numbers) = " + sum(numbers));
        
        System.out.println("\n--- printMessages 方法 ---");
        printMessages();
        printMessages("Hello");
        printMessages("Hello", "World", "Java");
        
        System.out.println("\n--- printf 方法 ---");
        printf("姓名：%s，年齡：%d，分數：%.2f%n", "張三", 25, 95.5);
        printf("今天是 %d 年 %d 月 %d 日%n", 2024, 3, 15);
        
        System.out.println("\n--- max 方法 ---");
        System.out.println("max(5) = " + max(5));
        System.out.println("max(1, 9, 3, 7) = " + max(1, 9, 3, 7));
    }
}
```

**可變參數規則：**
- 每個方法最多只能有一個可變參數
- 可變參數必須是最後一個參數
- 在方法內部，可變參數被當作陣列處理
- 可以傳遞 0 個或多個參數，也可以直接傳遞陣列

---

## 🎯 方法設計最佳實踐

### 1. 命名規範

```java
public class NamingConventions {
    
    // ✅ 好的方法命名
    public static int calculateTotal(int price, int quantity) {
        return price * quantity;
    }
    
    public static boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    
    public static void printUserInfo(String name, int age) {
        System.out.println("姓名：" + name + "，年齡：" + age);
    }
    
    // ❌ 不好的方法命名
    public static int calc(int a, int b) {  // 名稱不清楚
        return a * b;
    }
    
    public static boolean check(String s) {  // 檢查什麼？
        return s.contains("@");
    }
    
    public static void print(String x, int y) {  // 參數名稱不清楚
        System.out.println(x + y);
    }
}
```

### 2. 方法長度和複雜度

```java
public class MethodComplexity {
    
    // ✅ 好的設計：方法功能單一、清晰
    public static double calculateCircleArea(double radius) {
        validateRadius(radius);
        return Math.PI * radius * radius;
    }
    
    public static double calculateCircleCircumference(double radius) {
        validateRadius(radius);
        return 2 * Math.PI * radius;
    }
    
    private static void validateRadius(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("半徑必須大於 0");
        }
    }
    
    // ❌ 不好的設計：方法過於複雜
    public static void processCircle(double radius, boolean calculateArea, 
                                   boolean calculateCircumference, boolean validate) {
        if (validate && radius <= 0) {
            throw new IllegalArgumentException("半徑必須大於 0");
        }
        
        if (calculateArea) {
            double area = Math.PI * radius * radius;
            System.out.println("面積：" + area);
        }
        
        if (calculateCircumference) {
            double circumference = 2 * Math.PI * radius;
            System.out.println("周長：" + circumference);
        }
    }
}
```

### 3. 參數驗證和錯誤處理

```java
public class ParameterValidation {
    
    /**
     * 計算除法，包含完整的參數驗證
     */
    public static double divide(double dividend, double divisor) {
        // 參數驗證
        if (Double.isNaN(dividend) || Double.isNaN(divisor)) {
            throw new IllegalArgumentException("參數不能是 NaN");
        }
        
        if (Double.isInfinite(dividend) || Double.isInfinite(divisor)) {
            throw new IllegalArgumentException("參數不能是無限大");
        }
        
        if (divisor == 0.0) {
            throw new ArithmeticException("除數不能為零");
        }
        
        return dividend / divisor;
    }
    
    /**
     * 安全的陣列存取
     */
    public static int getArrayElement(int[] array, int index) {
        // null 檢查
        if (array == null) {
            throw new IllegalArgumentException("陣列不能為 null");
        }
        
        // 邊界檢查
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException(
                "索引 " + index + " 超出範圍 [0, " + (array.length - 1) + "]");
        }
        
        return array[index];
    }
    
    /**
     * 字串處理的防禦性程式設計
     */
    public static String formatName(String firstName, String lastName) {
        // null 和空字串檢查
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("名字不能為空");
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("姓氏不能為空");
        }
        
        // 去除前後空白並格式化
        String formattedFirstName = firstName.trim();
        String formattedLastName = lastName.trim();
        
        // 首字母大寫
        formattedFirstName = capitalizeFirstLetter(formattedFirstName);
        formattedLastName = capitalizeFirstLetter(formattedLastName);
        
        return formattedLastName + ", " + formattedFirstName;
    }
    
    private static String capitalizeFirstLetter(String str) {
        if (str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    public static void main(String[] args) {
        // 測試參數驗證
        try {
            System.out.println("10 ÷ 2 = " + divide(10, 2));
            System.out.println("10 ÷ 0 = " + divide(10, 0)); // 會拋出異常
        } catch (ArithmeticException e) {
            System.out.println("錯誤：" + e.getMessage());
        }
        
        // 測試陣列存取
        int[] numbers = {1, 2, 3, 4, 5};
        try {
            System.out.println("numbers[2] = " + getArrayElement(numbers, 2));
            System.out.println("numbers[10] = " + getArrayElement(numbers, 10)); // 會拋出異常
        } catch (IndexOutOfBoundsException e) {
            System.out.println("錯誤：" + e.getMessage());
        }
        
        // 測試姓名格式化
        try {
            System.out.println(formatName("john", "DOE"));
            System.out.println(formatName("", "smith")); // 會拋出異常
        } catch (IllegalArgumentException e) {
            System.out.println("錯誤：" + e.getMessage());
        }
    }
}
```

---

## 📚 進階主題

### 遞歸方法

```java
public class RecursiveMethods {
    
    /**
     * 計算階乘
     */
    public static long factorial(int n) {
        // 基礎情況
        if (n < 0) {
            throw new IllegalArgumentException("階乘不支援負數");
        }
        if (n == 0 || n == 1) {
            return 1;
        }
        
        // 遞歸情況
        return n * factorial(n - 1);
    }
    
    /**
     * 計算費波那契數列
     */
    public static long fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("費波那契數列不支援負數");
        }
        if (n <= 1) {
            return n;
        }
        
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    /**
     * 優化的費波那契數列（避免重複計算）
     */
    public static long fibonacciOptimized(int n) {
        return fibonacciHelper(n, new long[n + 1]);
    }
    
    private static long fibonacciHelper(int n, long[] memo) {
        if (n <= 1) {
            return n;
        }
        
        if (memo[n] != 0) {
            return memo[n];
        }
        
        memo[n] = fibonacciHelper(n - 1, memo) + fibonacciHelper(n - 2, memo);
        return memo[n];
    }
    
    public static void main(String[] args) {
        // 測試階乘
        System.out.println("5! = " + factorial(5));
        System.out.println("10! = " + factorial(10));
        
        // 測試費波那契數列
        System.out.println("fibonacci(10) = " + fibonacci(10));
        System.out.println("fibonacciOptimized(10) = " + fibonacciOptimized(10));
    }
}
```

---

## 🎓 學習檢查點

完成本章學習後，你應該能夠：

### ✅ 基本能力
- [ ] 理解方法的語法結構和組成元素
- [ ] 能夠編寫有參數和無參數的方法
- [ ] 理解回傳值的作用和使用方式
- [ ] 掌握 Java 中的參數傳遞機制

### ✅ 進階能力
- [ ] 熟練運用方法多載
- [ ] 理解和使用可變參數
- [ ] 能夠進行適當的參數驗證
- [ ] 遵循方法設計的最佳實踐

### ✅ 實踐能力
- [ ] 能夠設計清晰、可讀的方法
- [ ] 適當處理異常情況
- [ ] 理解遞歸的概念和應用
- [ ] 能夠優化方法的效能

---

## 🚀 下一步學習

掌握了方法的基礎後，建議繼續學習：

1. **[泛型程式設計](../generics-programming/)** - 學習如何編寫型別安全的通用方法
2. **[物件導向程式設計](../object-oriented/)** - 了解類別和物件中的方法
3. **[異常處理](../exception-handling/)** - 學習更完善的錯誤處理機制
4. **[集合框架](../arrays-and-collections/)** - 運用方法操作集合資料結構

**記住：好的方法設計是編寫高品質程式的基礎！** 🎯