import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Student Number: 22355561
public class SortingAlgorithm {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();

            class QuickSort implements Callable<Void> {
                private int[] arr;
                private int low;
                private int high;

                public QuickSort(int[] arr, int low, int high) {
                    this.arr = arr;
                    this.low = low;
                    this.high = high;
                }

                private int partition(int[] arr, int low, int high) {
                    int pivot = arr[high];
                    int i = (low - 1);

                    for (int j = low; j < high; j++) {
                        if (arr[j] < pivot) {
                            i++;
                            int temp = arr[i];
                            arr[i] = arr[j];
                            arr[j] = temp;
                        }
                    }

                    int temp = arr[i + 1];
                    arr[i + 1] = arr[high];
                    arr[high] = temp;

                    return i + 1;
                }

                public Void call() {
                    if (low < high) {
                        int pi = partition(arr, low, high);
                        QuickSort left = new QuickSort(arr, low, pi - 1);
                        QuickSort right = new QuickSort(arr, pi + 1, high);

                        ExecutorService executor = Executors.newFixedThreadPool(2);
                        Future<Void> leftResult = executor.submit(left);
                        Future<Void> rightResult = executor.submit(right);

                        try {
                            leftResult.get();
                            rightResult.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                        executor.shutdown();
                    }
                    return null;
                }
            }
            Arrays.sort(numbers);

            // ========= DON'T MODIFY THE CODE BELOW =========
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}
