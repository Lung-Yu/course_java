/**
 * 方法多載演示
 * 
 * 本檔案演示：
 * 1. 方法多載的各種情況
 * 2. 參數類型、數量、順序的影響
 * 3. 編譯器如何選擇合適的方法
 * 4. 實際應用場景
 * 
 * @author Java Course
 * @version 1.0
 */

import java.util.Arrays;

public class MethodOverloadingDemo {
    
    // === 基本數學運算的多載 ===
    
    /**
     * 兩個整數相加
     */
    public static int add(int a, int b) {
        System.out.println("調用 add(int, int): " + a + " + " + b);
        return a + b;
    }
    
    /**
     * 三個整數相加
     */
    public static int add(int a, int b, int c) {
        System.out.println("調用 add(int, int, int): " + a + " + " + b + " + " + c);
        return a + b + c;
    }
    
    /**
     * 兩個雙精度數相加
     */
    public static double add(double a, double b) {
        System.out.println("調用 add(double, double): " + a + " + " + b);
        return a + b;
    }
    
    /**
     * 整數和雙精度數相加
     */
    public static double add(int a, double b) {
        System.out.println("調用 add(int, double): " + a + " + " + b);
        return a + b;
    }
    
    /**
     * 雙精度數和整數相加
     */
    public static double add(double a, int b) {
        System.out.println("調用 add(double, int): " + a + " + " + b);
        return a + b;
    }
    
    /**
     * 陣列元素相加
     */
    public static int add(int[] numbers) {
        System.out.print("調用 add(int[]): ");
        System.out.println(Arrays.toString(numbers));
        
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }
    
    // === 打印方法的多載 ===
    
    /**
     * 打印字串
     */
    public static void print(String message) {
        System.out.println("print(String): " + message);
    }
    
    /**
     * 打印整數
     */
    public static void print(int number) {
        System.out.println("print(int): " + number);
    }
    
    /**
     * 打印雙精度數
     */
    public static void print(double number) {
        System.out.println("print(double): " + number);
    }
    
    /**
     * 打印布林值
     */
    public static void print(boolean value) {
        System.out.println("print(boolean): " + value);
    }
    
    /**
     * 打印字串陣列
     */
    public static void print(String[] array) {
        System.out.println("print(String[]): " + Arrays.toString(array));
    }
    
    /**
     * 打印整數陣列
     */
    public static void print(int[] array) {
        System.out.println("print(int[]): " + Arrays.toString(array));
    }
    
    /**
     * 帶格式的打印
     */
    public static void print(String message, boolean newLine) {
        if (newLine) {
            System.out.println("print(String, boolean): " + message);
        } else {
            System.out.print("print(String, boolean): " + message + " ");
        }
    }
    
    // === 搜尋方法的多載 ===
    
    /**
     * 在整數陣列中搜尋
     */
    public static int search(int[] array, int target) {
        System.out.println("search(int[], int): 在整數陣列中搜尋 " + target);
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 在字串陣列中搜尋
     */
    public static int search(String[] array, String target) {
        System.out.println("search(String[], String): 在字串陣列中搜尋 \"" + target + "\"");
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 在整數陣列中搜尋（指定起始位置）
     */
    public static int search(int[] array, int target, int startIndex) {
        System.out.printf("search(int[], int, int): 從索引 %d 開始搜尋 %d%n", 
                         startIndex, target);
        
        for (int i = startIndex; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 在字串陣列中搜尋（忽略大小寫）
     */
    public static int search(String[] array, String target, boolean ignoreCase) {
        System.out.printf("search(String[], String, boolean): 搜尋 \"%s\"，忽略大小寫: %b%n", 
                         target, ignoreCase);
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                boolean matches = ignoreCase ? 
                    array[i].equalsIgnoreCase(target) : 
                    array[i].equals(target);
                
                if (matches) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    // === 格式化方法的多載 ===
    
    /**
     * 格式化個人資訊（僅姓名）
     */
    public static String formatPersonInfo(String name) {
        System.out.println("formatPersonInfo(String): 格式化姓名");
        return "姓名: " + name;
    }
    
    /**
     * 格式化個人資訊（姓名和年齡）
     */
    public static String formatPersonInfo(String name, int age) {
        System.out.println("formatPersonInfo(String, int): 格式化姓名和年齡");
        return String.format("姓名: %s, 年齡: %d", name, age);
    }
    
    /**
     * 格式化個人資訊（姓名、年齡和職業）
     */
    public static String formatPersonInfo(String name, int age, String occupation) {
        System.out.println("formatPersonInfo(String, int, String): 完整格式化");
        return String.format("姓名: %s, 年齡: %d, 職業: %s", name, age, occupation);
    }
    
    /**
     * 格式化個人資訊（年齡和姓名，參數順序不同）
     */
    public static String formatPersonInfo(int age, String name) {
        System.out.println("formatPersonInfo(int, String): 年齡在前的格式化");
        return String.format("年齡: %d, 姓名: %s", age, name);
    }
    
    // === 幾何計算的多載 ===
    
    /**
     * 計算正方形面積
     */
    public static double calculateArea(double side) {
        System.out.println("calculateArea(double): 計算正方形面積，邊長 = " + side);
        return side * side;
    }
    
    /**
     * 計算矩形面積
     */
    public static double calculateArea(double length, double width) {
        System.out.printf("calculateArea(double, double): 計算矩形面積，長 = %.2f, 寬 = %.2f%n", 
                         length, width);
        return length * width;
    }
    
    /**
     * 計算三角形面積
     */
    public static double calculateArea(double base, double height, boolean isTriangle) {
        if (isTriangle) {
            System.out.printf("calculateArea(double, double, boolean): 計算三角形面積，底 = %.2f, 高 = %.2f%n", 
                             base, height);
            return 0.5 * base * height;
        } else {
            // 如果不是三角形，當作矩形處理
            return calculateArea(base, height);
        }
    }
    
    /**
     * 計算圓形面積
     */
    public static double calculateArea(double radius, String shape) {
        if ("circle".equalsIgnoreCase(shape)) {
            System.out.printf("calculateArea(double, String): 計算圓形面積，半徑 = %.2f%n", radius);
            return Math.PI * radius * radius;
        } else {
            throw new IllegalArgumentException("不支援的形狀: " + shape);
        }
    }
    
    // === 可變參數的多載 ===
    
    /**
     * 計算多個數字的最大值
     */
    public static int max(int... numbers) {
        System.out.println("max(int...): 計算可變參數的最大值");
        System.out.println("參數個數: " + numbers.length);
        
        if (numbers.length == 0) {
            throw new IllegalArgumentException("至少需要一個參數");
        }
        
        int maximum = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > maximum) {
                maximum = numbers[i];
            }
        }
        return maximum;
    }
    
    /**
     * 固定參數版本的最大值（兩個數）
     * 注意：這個方法和可變參數方法可能會產生歧義
     */
    public static int max(int a, int b) {
        System.out.println("max(int, int): 計算兩個數的最大值");
        return (a > b) ? a : b;
    }
    
    // === 主方法：演示所有多載 ===
    
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("          Java 方法多載演示             ");
        System.out.println("=========================================");
        
        // 1. 數學運算多載
        System.out.println("\n1. === 數學運算多載 ===");
        System.out.println("結果: " + add(5, 3));
        System.out.println("結果: " + add(1, 2, 3));
        System.out.println("結果: " + add(2.5, 3.7));
        System.out.println("結果: " + add(5, 2.5));
        System.out.println("結果: " + add(2.5, 5));
        System.out.println("結果: " + add(new int[]{1, 2, 3, 4, 5}));
        
        // 2. 打印方法多載
        System.out.println("\n2. === 打印方法多載 ===");
        print("Hello World");
        print(42);
        print(3.14159);
        print(true);
        print(new String[]{"Java", "Python", "C++"});
        print(new int[]{1, 2, 3, 4, 5});
        print("不換行的訊息", false);
        print("換行的訊息", true);
        
        // 3. 搜尋方法多載
        System.out.println("\n3. === 搜尋方法多載 ===");
        int[] numbers = {10, 20, 30, 20, 40};
        String[] languages = {"Java", "Python", "C++", "JavaScript"};
        
        System.out.println("找到 20 在索引: " + search(numbers, 20));
        System.out.println("找到 \"Python\" 在索引: " + search(languages, "Python"));
        System.out.println("從索引 2 開始找 20: " + search(numbers, 20, 2));
        System.out.println("忽略大小寫找 \"java\": " + search(languages, "java", true));
        System.out.println("不忽略大小寫找 \"java\": " + search(languages, "java", false));
        
        // 4. 格式化方法多載
        System.out.println("\n4. === 格式化方法多載 ===");
        System.out.println(formatPersonInfo("張三"));
        System.out.println(formatPersonInfo("李四", 25));
        System.out.println(formatPersonInfo("王五", 30, "工程師"));
        System.out.println(formatPersonInfo(28, "趙六"));
        
        // 5. 幾何計算多載
        System.out.println("\n5. === 幾何計算多載 ===");
        System.out.printf("正方形面積: %.2f%n", calculateArea(5.0));
        System.out.printf("矩形面積: %.2f%n", calculateArea(4.0, 6.0));
        System.out.printf("三角形面積: %.2f%n", calculateArea(8.0, 5.0, true));
        System.out.printf("圓形面積: %.2f%n", calculateArea(3.0, "circle"));
        
        // 6. 可變參數多載
        System.out.println("\n6. === 可變參數多載 ===");
        System.out.println("兩個數的最大值: " + max(10, 20));
        System.out.println("多個數的最大值: " + max(5, 12, 8, 20, 3));
        System.out.println("單個數的最大值: " + max(42));
        
        // 7. 演示編譯器選擇
        System.out.println("\n7. === 編譯器方法選擇演示 ===");
        // 編譯器會選擇最精確匹配的方法
        add(1, 2);           // 調用 add(int, int)
        add(1.0, 2.0);       // 調用 add(double, double) 
        add(1, 2.0);         // 調用 add(int, double)
        add(1.0, 2);         // 調用 add(double, int)
        
        // 8. 自動類型提升示例
        System.out.println("\n8. === 自動類型提升 ===");
        byte b1 = 10, b2 = 20;
        add(b1, b2);  // byte 會自動提升為 int，調用 add(int, int)
        
        short s1 = 100, s2 = 200;
        add(s1, s2);  // short 會自動提升為 int，調用 add(int, int)
        
        float f1 = 1.5f, f2 = 2.5f;
        add(f1, f2);  // float 會自動提升為 double，調用 add(double, double)
        
        System.out.println("\n=========================================");
        System.out.println("           演示結束，感謝學習！          ");
        System.out.println("=========================================");
    }
}