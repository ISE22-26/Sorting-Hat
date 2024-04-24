import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

// Student Number:22351434
public class SortingAlgorithm {
    private static final int INSERTION_SORT_THRESHOLD = 100;

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();
            // ========= DON'T MODIFY THE CODE ABOVE =========
            ForkJoinPool pool = new ForkJoinPool();
            long startTime = System.currentTimeMillis();
            pool.invoke(new QuickSortTask(numbers, 0, numbers.length - 1));
            long endTime = System.currentTimeMillis();

            System.out.println("Time taken: " + (endTime - startTime) / 1000.0 + " seconds");
            // ========= DON'T MODIFY THE CODE BELOW =========
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    private static class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int left;
        private final int right;

        QuickSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (right - left < INSERTION_SORT_THRESHOLD) {
                insertionSort(array, left, right);
            } else {
                int pivot = partition(array, left, right);
                QuickSortTask leftTask = new QuickSortTask(array, left, pivot - 1);
                QuickSortTask rightTask = new QuickSortTask(array, pivot + 1, right);
                invokeAll(leftTask, rightTask);
            }
        }
    }

    private static void insertionSort(int[] array, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= low && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }
}