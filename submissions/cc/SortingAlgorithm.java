import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

class InsertionSort {
    public static void sort(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];

            int j = i - 1;

            while (j >= low && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}

class ConcurrentQuickSort {
    public static void sort(int[] arr) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new Sort(arr, 0, arr.length - 1));
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static int partition(int[] arr, int low, int high) {
        int i = low;

        for (int j = low; j < high; j++) {
            if (arr[j] < arr[high]) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, high);
        return i;
    }

    static class Sort extends RecursiveAction {

        final int low, high;
        int[] arr;

        Sort(int[] arr, int low, int high) {
            this.low = low;
            this.high = high;
            this.arr = arr;
        }

        @Override
        protected void compute() {
            if (low < high) {
                if ((high - low) < 100_000) {
                    InsertionSort.sort(arr, low, high);
                }
                int pi = partition(arr, low, high);
                invokeAll(new Sort(arr, low, pi - 1), new Sort(arr, pi + 1, high));
            }
        }
    }
}

public class SortingAlgorithm {

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();

            ConcurrentQuickSort.sort(numbers);

            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}