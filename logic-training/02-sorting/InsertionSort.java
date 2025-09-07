import java.util.Arrays;

public class InsertionSort {
    /**
     * 插入排序
     * @param arr 要排序的整數陣列
     */
    public static void insertionSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return; // 不需要排序
        }
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;

            // 將比 key 大的元素往後移動
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
    }

    public static void main(String[] args) {
        int[] numbers = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("原始陣列：" + Arrays.toString(numbers));

        insertionSort(numbers);

        System.out.println("排序後陣列：" + Arrays.toString(numbers)); // 預期輸出: [11, 12, 22, 25, 34, 64, 90]
    }
}
