import java.util.Arrays;
import java.util.Random;

/**
 * 多維陣列示範
 * 
 * 這個類別展示了多維陣列的各種操作：
 * - 二維陣列的創建和操作
 * - 三維陣列的使用
 * - 不規則陣列 (Jagged Arrays)
 * - 多維陣列的實際應用範例
 */
public class MultiDimensionalArrayDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 多維陣列示範 ===\n");
        
        // 示範二維陣列
        demonstrateTwoDimensionalArray();
        
        // 示範學生成績管理
        demonstrateGradeManagement();
        
        // 示範不規則陣列
        demonstrateJaggedArray();
        
        // 示範三維陣列
        demonstrateThreeDimensionalArray();
        
        // 示範矩陣運算
        demonstrateMatrixOperations();
    }
    
    /**
     * 示範二維陣列基本操作
     */
    public static void demonstrateTwoDimensionalArray() {
        System.out.println("1. 二維陣列基本操作:\n");
        
        // 創建 3x4 的二維陣列
        int[][] matrix = new int[3][4];
        
        // 初始化陣列
        int value = 1;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = value++;
            }
        }
        
        // 顯示二維陣列
        System.out.println("3x4 矩陣:");
        printMatrix(matrix);
        
        // 計算每行的總和
        System.out.println("每行總和:");
        for (int i = 0; i < matrix.length; i++) {
            int rowSum = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                rowSum += matrix[i][j];
            }
            System.out.println("第 " + (i + 1) + " 行: " + rowSum);
        }
        
        // 計算每列的總和
        System.out.println("\n每列總和:");
        for (int j = 0; j < matrix[0].length; j++) {
            int colSum = 0;
            for (int i = 0; i < matrix.length; i++) {
                colSum += matrix[i][j];
            }
            System.out.println("第 " + (j + 1) + " 列: " + colSum);
        }
        
        System.out.println();
    }
    
    /**
     * 示範學生成績管理系統
     */
    public static void demonstrateGradeManagement() {
        System.out.println("2. 學生成績管理系統:\n");
        
        // 4個學生，5個科目的成績
        int[][] grades = {
            {85, 92, 78, 96, 88},  // 學生1
            {79, 85, 91, 82, 87},  // 學生2
            {92, 88, 85, 90, 94},  // 學生3
            {88, 91, 79, 85, 92}   // 學生4
        };
        
        String[] subjects = {"數學", "英文", "物理", "化學", "生物"};
        String[] students = {"張三", "李四", "王五", "趙六"};
        
        // 顯示成績表
        System.out.println("學生成績表:");
        System.out.print("學生\\科目\t");
        for (String subject : subjects) {
            System.out.print(subject + "\t");
        }
        System.out.println("平均");
        
        for (int i = 0; i < grades.length; i++) {
            System.out.print(students[i] + "\t");
            int total = 0;
            for (int j = 0; j < grades[i].length; j++) {
                System.out.print(grades[i][j] + "\t");
                total += grades[i][j];
            }
            double average = total / (double) grades[i].length;
            System.out.printf("%.1f\n", average);
        }
        
        // 計算每科平均成績
        System.out.println("\n各科平均成績:");
        for (int j = 0; j < subjects.length; j++) {
            int total = 0;
            for (int i = 0; i < grades.length; i++) {
                total += grades[i][j];
            }
            double average = total / (double) grades.length;
            System.out.printf("%s: %.1f\n", subjects[j], average);
        }
        
        // 找出最高分和最低分
        int maxScore = grades[0][0];
        int minScore = grades[0][0];
        String maxStudent = "", minStudent = "";
        String maxSubject = "", minSubject = "";
        
        for (int i = 0; i < grades.length; i++) {
            for (int j = 0; j < grades[i].length; j++) {
                if (grades[i][j] > maxScore) {
                    maxScore = grades[i][j];
                    maxStudent = students[i];
                    maxSubject = subjects[j];
                }
                if (grades[i][j] < minScore) {
                    minScore = grades[i][j];
                    minStudent = students[i];
                    minSubject = subjects[j];
                }
            }
        }
        
        System.out.println("\n最高分: " + maxScore + " (" + maxStudent + " 的 " + maxSubject + ")");
        System.out.println("最低分: " + minScore + " (" + minStudent + " 的 " + minSubject + ")");
        
        System.out.println();
    }
    
    /**
     * 示範不規則陣列
     */
    public static void demonstrateJaggedArray() {
        System.out.println("3. 不規則陣列 (Jagged Array):\n");
        
        // 創建不規則陣列 - 每行有不同的元素數量
        int[][] jaggedArray = new int[4][];
        jaggedArray[0] = new int[3];  // 第一行：3個元素
        jaggedArray[1] = new int[5];  // 第二行：5個元素
        jaggedArray[2] = new int[2];  // 第三行：2個元素
        jaggedArray[3] = new int[4];  // 第四行：4個元素
        
        // 填充不規則陣列
        Random random = new Random();
        for (int i = 0; i < jaggedArray.length; i++) {
            for (int j = 0; j < jaggedArray[i].length; j++) {
                jaggedArray[i][j] = random.nextInt(100) + 1;
            }
        }
        
        // 顯示不規則陣列
        System.out.println("不規則陣列內容:");
        for (int i = 0; i < jaggedArray.length; i++) {
            System.out.printf("第 %d 行 (%d 個元素): %s\n", 
                i + 1, jaggedArray[i].length, Arrays.toString(jaggedArray[i]));
        }
        
        // 計算每行的統計資訊
        System.out.println("\n每行統計:");
        for (int i = 0; i < jaggedArray.length; i++) {
            int sum = 0;
            int max = jaggedArray[i][0];
            int min = jaggedArray[i][0];
            
            for (int value : jaggedArray[i]) {
                sum += value;
                max = Math.max(max, value);
                min = Math.min(min, value);
            }
            
            double average = sum / (double) jaggedArray[i].length;
            System.out.printf("第 %d 行 - 總和: %d, 平均: %.1f, 最大: %d, 最小: %d\n", 
                i + 1, sum, average, max, min);
        }
        
        System.out.println();
    }
    
    /**
     * 示範三維陣列
     */
    public static void demonstrateThreeDimensionalArray() {
        System.out.println("4. 三維陣列:\n");
        
        // 創建 2x3x4 的三維陣列
        int[][][] cube = new int[2][3][4];
        
        // 初始化三維陣列
        int value = 1;
        for (int i = 0; i < cube.length; i++) {
            for (int j = 0; j < cube[i].length; j++) {
                for (int k = 0; k < cube[i][j].length; k++) {
                    cube[i][j][k] = value++;
                }
            }
        }
        
        // 顯示三維陣列
        System.out.println("2x3x4 三維陣列:");
        for (int i = 0; i < cube.length; i++) {
            System.out.println("層 " + (i + 1) + ":");
            printMatrix(cube[i]);
            System.out.println();
        }
        
        // 計算三維陣列的總和
        int totalSum = 0;
        for (int[][] layer : cube) {
            for (int[] row : layer) {
                for (int element : row) {
                    totalSum += element;
                }
            }
        }
        
        System.out.println("三維陣列所有元素總和: " + totalSum);
        System.out.println();
    }
    
    /**
     * 示範矩陣運算
     */
    public static void demonstrateMatrixOperations() {
        System.out.println("5. 矩陣運算:\n");
        
        int[][] matrixA = {
            {1, 2, 3},
            {4, 5, 6}
        };
        
        int[][] matrixB = {
            {7, 8, 9},
            {10, 11, 12}
        };
        
        System.out.println("矩陣 A:");
        printMatrix(matrixA);
        
        System.out.println("矩陣 B:");
        printMatrix(matrixB);
        
        // 矩陣加法
        int[][] sum = addMatrices(matrixA, matrixB);
        System.out.println("A + B =");
        printMatrix(sum);
        
        // 矩陣轉置
        int[][] transposeA = transposeMatrix(matrixA);
        System.out.println("A 的轉置:");
        printMatrix(transposeA);
        
        // 矩陣乘法範例
        int[][] matrixC = {{1, 2}, {3, 4}, {5, 6}};
        int[][] matrixD = {{7, 8, 9}, {10, 11, 12}};
        
        System.out.println("矩陣 C (3x2):");
        printMatrix(matrixC);
        
        System.out.println("矩陣 D (2x3):");
        printMatrix(matrixD);
        
        int[][] product = multiplyMatrices(matrixC, matrixD);
        System.out.println("C × D =");
        printMatrix(product);
        
        System.out.println();
    }
    
    /**
     * 列印矩陣
     */
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.printf("%4d", element);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * 矩陣加法
     */
    public static int[][] addMatrices(int[][] a, int[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new IllegalArgumentException("矩陣維度不匹配");
        }
        
        int[][] result = new int[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }
    
    /**
     * 矩陣轉置
     */
    public static int[][] transposeMatrix(int[][] matrix) {
        int[][] result = new int[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }
    
    /**
     * 矩陣乘法
     */
    public static int[][] multiplyMatrices(int[][] a, int[][] b) {
        if (a[0].length != b.length) {
            throw new IllegalArgumentException("矩陣維度不適合相乘");
        }
        
        int[][] result = new int[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
}