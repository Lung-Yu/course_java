public class BinarySearch {
    /**
     * 二元搜尋
     * @param arr 已排序的整數陣列
     * @param target 要尋找的目標值
     * @return 如果找到，回傳目標值的索引；否則回傳 -1
     */
    public static int binarySearch(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2; // 避免 (left + right) 溢位

            if (arr[mid] == target) {
                return mid; // 找到目標
            } else if (arr[mid] < target) {
                left = mid + 1; // 目標在右半邊
            } else {
                right = mid - 1; // 目標在左半邊
            }
        }
        return -1; // 未找到目標
    }

    public static void main(String[] args) {
        int[] sortedNumbers = {1, 2, 5, 6, 8, 9};
        int target1 = 6;
        int target2 = 10;

        // 測試案例1：目標存在
        int index1 = binarySearch(sortedNumbers, target1);
        System.out.println("目標 " + target1 + " 的索引是：" + index1); // 預期輸出: 3

        // ���試案例2：目標不存在
        int index2 = binarySearch(sortedNumbers, target2);
        System.out.println("目標 " + target2 + " 的索引是：" + index2); // 預期輸出: -1
    }
}
