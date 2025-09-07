import java.util.Arrays;

public class BubbleSort {
    /**
     * 泡泡排序
     * @param arr 要排序的整數陣列
     */
    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return; // 不需要排序
        }
        int n = arr.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交換元素
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            // 如果這一輪沒有發生交換，表示陣列已經排序完成
            if (!swapped) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        int[] numbers = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("原始陣列：" + Arrays.toString(numbers));

        bubbleSort(numbers);

        System.out.println("���序後陣列：" + Arrays.toString(numbers)); // 預期輸出: [11, 12, 22, 25, 34, 64, 90]
    }
}
