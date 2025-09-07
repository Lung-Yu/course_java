import java.util.*;

/**
 * ArrayList 詳細示範
 * 
 * 這個類別展示了 ArrayList 的各種特性和操作：
 * - 基本的 CRUD 操作
 * - 容量管理和性能優化
 * - 不同的創建方式
 * - 常用方法的使用
 */
public class ArrayListDemo {
    
    public static void main(String[] args) {
        System.out.println("=== ArrayList 詳細示範 ===\n");
        
        // 基本操作
        demonstrateBasicOperations();
        
        // 不同的創建方式
        demonstrateCreationMethods();
        
        // 批量操作
        demonstrateBulkOperations();
        
        // 查找和檢查操作
        demonstrateSearchOperations();
        
        // 子列表操作
        demonstrateSubListOperations();
        
        // 容量管理
        demonstrateCapacityManagement();
    }
    
    /**
     * 基本操作示範
     */
    public static void demonstrateBasicOperations() {
        System.out.println("1. ArrayList 基本操作:\n");
        
        // 創建 ArrayList
        ArrayList<String> fruits = new ArrayList<>();
        
        // 添加元素
        fruits.add("蘋果");
        fruits.add("香蕉");
        fruits.add("橘子");
        fruits.add("葡萄");
        System.out.println("初始水果列表: " + fruits);
        
        // 在指定位置插入
        fruits.add(2, "草莓");
        System.out.println("插入草莓後: " + fruits);
        
        // 修改元素
        fruits.set(1, "芒果");
        System.out.println("修改香蕉為芒果: " + fruits);
        
        // 存取元素
        String firstFruit = fruits.get(0);
        String lastFruit = fruits.get(fruits.size() - 1);
        System.out.println("第一個水果: " + firstFruit);
        System.out.println("最後一個水果: " + lastFruit);
        
        // 移除元素
        fruits.remove("草莓");           // 按值移除
        String removed = fruits.remove(1); // 按索引移除
        System.out.println("移除的元素: " + removed);
        System.out.println("移除後: " + fruits);
        
        // 清空列表
        System.out.println("列表大小: " + fruits.size());
        System.out.println("是否為空: " + fruits.isEmpty());
        
        System.out.println();
    }
    
    /**
     * 不同的創建方式
     */
    public static void demonstrateCreationMethods() {
        System.out.println("2. ArrayList 創建方式:\n");
        
        // 方式一：預設構造器
        ArrayList<Integer> list1 = new ArrayList<>();
        list1.add(1);
        list1.add(2);
        list1.add(3);
        System.out.println("預設構造器: " + list1);
        
        // 方式二：指定初始容量
        ArrayList<Integer> list2 = new ArrayList<>(20);
        list2.add(10);
        list2.add(20);
        System.out.println("指定容量: " + list2);
        
        // 方式三：從其他集合創建
        ArrayList<Integer> list3 = new ArrayList<>(list1);
        System.out.println("從集合創建: " + list3);
        
        // 方式四：使用 Arrays.asList() (注意：固定大小)
        List<String> fixedList = Arrays.asList("A", "B", "C");
        ArrayList<String> list4 = new ArrayList<>(fixedList);
        System.out.println("從 Arrays.asList: " + list4);
        
        // 方式五：Java 9+ 的 List.of() (不可變)
        List<String> immutableList = List.of("X", "Y", "Z");
        ArrayList<String> list5 = new ArrayList<>(immutableList);
        System.out.println("從 List.of: " + list5);
        
        // 證明 list4 和 list5 是可變的
        list4.add("D");
        list5.add("W");
        System.out.println("添加元素後 list4: " + list4);
        System.out.println("添加元素後 list5: " + list5);
        
        System.out.println();
    }
    
    /**
     * 批量操作示範
     */
    public static void demonstrateBulkOperations() {
        System.out.println("3. 批量操作:\n");
        
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);
        
        System.out.println("原始列表: " + numbers);
        
        // 批量添加
        List<Integer> moreNumbers = Arrays.asList(6, 7, 8, 9, 10);
        numbers.addAll(moreNumbers);
        System.out.println("批量添加後: " + numbers);
        
        // 在指定位置批量添加
        List<Integer> insertNumbers = Arrays.asList(100, 200);
        numbers.addAll(3, insertNumbers);
        System.out.println("指定位置批量添加: " + numbers);
        
        // 檢查是否包含所有元素
        List<Integer> checkList = Arrays.asList(1, 2, 3);
        boolean containsAll = numbers.containsAll(checkList);
        System.out.println("是否包含 [1,2,3]: " + containsAll);
        
        // 保留交集
        ArrayList<Integer> intersection = new ArrayList<>(numbers);
        List<Integer> keepList = Arrays.asList(1, 2, 3, 4, 5);
        intersection.retainAll(keepList);
        System.out.println("保留交集後: " + intersection);
        
        // 移除指定元素
        ArrayList<Integer> difference = new ArrayList<>(numbers);
        List<Integer> removeList = Arrays.asList(100, 200, 6, 7);
        difference.removeAll(removeList);
        System.out.println("移除指定元素後: " + difference);
        
        System.out.println();
    }
    
    /**
     * 查找和檢查操作
     */
    public static void demonstrateSearchOperations() {
        System.out.println("4. 查找和檢查操作:\n");
        
        ArrayList<String> colors = new ArrayList<>();
        colors.add("紅色");
        colors.add("綠色");
        colors.add("藍色");
        colors.add("黃色");
        colors.add("紅色"); // 重複元素
        
        System.out.println("顏色列表: " + colors);
        
        // 檢查是否包含
        boolean hasRed = colors.contains("紅色");
        System.out.println("是否包含紅色: " + hasRed);
        
        // 查找索引
        int firstRedIndex = colors.indexOf("紅色");
        int lastRedIndex = colors.lastIndexOf("紅色");
        System.out.println("紅色第一次出現索引: " + firstRedIndex);
        System.out.println("紅色最後一次出現索引: " + lastRedIndex);
        
        // 查找不存在的元素
        int purpleIndex = colors.indexOf("紫色");
        System.out.println("紫色的索引: " + purpleIndex + " (不存在返回-1)");
        
        // 轉換為陣列
        String[] colorArray = colors.toArray(new String[0]);
        System.out.println("轉換為陣列: " + Arrays.toString(colorArray));
        
        // 使用 Object[] 陣列
        Object[] objectArray = colors.toArray();
        System.out.println("轉換為 Object 陣列長度: " + objectArray.length);
        
        System.out.println();
    }
    
    /**
     * 子列表操作
     */
    public static void demonstrateSubListOperations() {
        System.out.println("5. 子列表操作:\n");
        
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numbers.add(i);
        }
        System.out.println("原始列表: " + numbers);
        
        // 獲取子列表 (注意：這是原列表的視圖，不是獨立拷貝)
        List<Integer> subList = numbers.subList(3, 7);
        System.out.println("子列表 [3,7): " + subList);
        
        // 修改子列表會影響原列表
        subList.set(0, 999);
        System.out.println("修改子列表後原列表: " + numbers);
        System.out.println("修改子列表後子列表: " + subList);
        
        // 在子列表中添加元素
        subList.add(888);
        System.out.println("子列表添加元素後原列表: " + numbers);
        
        // 清空子列表會從原列表中移除對應元素
        subList.clear();
        System.out.println("清空子列表後原列表: " + numbers);
        
        // 創建子列表的獨立拷貝
        numbers.clear();
        for (int i = 0; i < 10; i++) {
            numbers.add(i);
        }
        List<Integer> independentCopy = new ArrayList<>(numbers.subList(2, 6));
        System.out.println("獨立拷貝: " + independentCopy);
        
        independentCopy.set(0, 777);
        System.out.println("修改獨立拷貝後原列表: " + numbers);
        System.out.println("修改獨立拷貝後拷貝: " + independentCopy);
        
        System.out.println();
    }
    
    /**
     * 容量管理示範
     */
    public static void demonstrateCapacityManagement() {
        System.out.println("6. 容量管理:\n");
        
        // 創建指定容量的 ArrayList
        ArrayList<Integer> list = new ArrayList<>(5);
        System.out.println("初始大小: " + list.size());
        
        // 添加元素並觀察擴容
        for (int i = 0; i < 12; i++) {
            list.add(i);
            System.out.println("添加 " + i + " 後大小: " + list.size());
        }
        
        // 手動調整容量 (Java 8+)
        System.out.println("\n手動調整容量前大小: " + list.size());
        list.ensureCapacity(20);
        System.out.println("ensureCapacity(20) 後大小: " + list.size());
        
        // 釋放多餘容量
        list.trimToSize();
        System.out.println("trimToSize() 後大小: " + list.size());
        
        // 性能測試：預設容量 vs 指定容量
        testCapacityPerformance();
    }
    
    /**
     * 容量性能測試
     */
    public static void testCapacityPerformance() {
        System.out.println("\n容量性能測試:");
        final int SIZE = 1000000;
        
        // 測試預設容量
        long startTime = System.currentTimeMillis();
        ArrayList<Integer> defaultList = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            defaultList.add(i);
        }
        long defaultTime = System.currentTimeMillis() - startTime;
        
        // 測試指定容量
        startTime = System.currentTimeMillis();
        ArrayList<Integer> capacityList = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            capacityList.add(i);
        }
        long capacityTime = System.currentTimeMillis() - startTime;
        
        System.out.println("添加 " + SIZE + " 個元素:");
        System.out.println("預設容量耗時: " + defaultTime + "ms");
        System.out.println("指定容量耗時: " + capacityTime + "ms");
        
        if (defaultTime > capacityTime) {
            System.out.println("指定容量快 " + (defaultTime - capacityTime) + "ms");
        } else {
            System.out.println("差異不大或預設容量更快");
        }
        
        System.out.println();
    }
}