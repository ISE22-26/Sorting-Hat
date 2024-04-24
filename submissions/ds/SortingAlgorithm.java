import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// Student Number: 
public class SortingAlgorithm {

    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    private static class SortThreads extends Thread {
        SortThreads(int[] array, int begin, int end) {
            super(() -> {
                mergeSort(array, begin, end);
            });
            this.start();
        }
    }

    public static void threadedSort(int[] array) {
        // get length of array
        final int length = array.length;
        // check if length is divisible by MAX_THREADS
        boolean exact = length % MAX_THREADS == 0;
        int maxlim = exact ? length / MAX_THREADS : length / (MAX_THREADS - 1);
        maxlim = Math.max(maxlim, MAX_THREADS);
        final ArrayList<SortThreads> threads = new ArrayList<>();
        for (int i = 0; i < length; i += maxlim) {
            int remain = (length) - i;
            int end = remain < maxlim ? i + (remain - 1) : i + (maxlim - 1);
            final SortThreads t = new SortThreads(array, i, end);
            threads.add(t);
        }
        for (Thread t : threads) {
            try {
                t.join();
                // System.out.println(t.getName() + " has finished");
            } catch (InterruptedException ignored) {
            }
        }
        for (int i = 0; i < length; i += maxlim) {
            int mid = i == 0 ? 0 : i - 1;
            int remain = (length) - i;
            int end = remain < maxlim ? i + (remain - 1) : i + (maxlim - 1);
            merge(array, 0, mid, end);
        }
    }

    public static void mergeSort(int[] array, int begin, int end) {
        if (begin < end) {
            int mid = (begin + end) / 2;
            mergeSort(array, begin, mid);
            mergeSort(array, mid + 1, end);
            merge(array, begin, mid, end);
        }
    }

    public static void merge(int[] array, int begin, int mid, int end) {
        int[] temp = new int[(end - begin) + 1];

        int i = begin, j = mid + 1;
        int k = 0;

        // merge the two arrays
        while (i <= mid && j <= end) {
            // check if the value at i is less than or equal to the value at j
            if (array[i] <= array[j]) {
                temp[k] = array[i];
                i += 1;
            } else {
                temp[k] = array[j];
                j += 1;
            }
            k += 1;
        }

        // check if there are any remaining elements in the first array
        while (i <= mid) {
            temp[k] = array[i];
            i += 1;
            k += 1;
        }
        // check if there are any remaining elements in the second array
        while (j <= end) {
            temp[k] = array[j];
            j += 1;
            k += 1;
        }
        // copy the sorted array back to the original array
        for (i = begin, k = 0; i <= end; i++, k++) {
            array[i] = temp[k];
        }
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();
            // ========= DON'T MODIFY THE CODE ABOVE =========

            // Example sorting operation (using Arrays.sort for simplicity)
            // Replace this with your own sorting algorithm
            // Arrays.sort(numbers);
            threadedSort(numbers);

            // ========= DON'T MODIFY THE CODE BELOW =========
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}