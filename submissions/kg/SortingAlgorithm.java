import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

// Student Number: 
public class SortingAlgorithm {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();
            // ========= DON'T MODIFY THE CODE ABOVE =========


            
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(new SortDriver(numbers, 0, numbers.length - 1));


            // ========= DON'T MODIFY THE CODE BELOW =========
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    private static class SortDriver extends RecursiveAction { // ForkJoin Abstract class
    private int[] array;
    private int start, end;
    private final static int THRESHOLD = 1000; // Threshold for using Arrays.sort() instead of merge sort

    public SortDriver(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start < THRESHOLD) {
            Arrays.sort(array, start, end); // Arrays.sort() is faster for small arrays
        } else {
            int mid = start + (end - start) / 2;
            SortDriver leftTask = new SortDriver(array, start, mid); // Split
            SortDriver rightTask = new SortDriver(array, mid + 1, end);

            invokeAll(leftTask, rightTask); // Concurrently sort both halves

            merge(array, start, mid, end); // Merge the sorted halves
        }
    }


    private void merge(int[] array, int start, int mid, int end) {
        int[] tempArray = new int[end - start + 1]; // merged subarrays
        int i = start, j = mid + 1, k = 0;

        while (i <= mid && j <= end) {
            if (array[i] <= array[j]) {
                tempArray[k++] = array[i++];
            } else {
                tempArray[k++] = array[j++];
            }
        }

        while (i <= mid) {
            tempArray[k++] = array[i++];
        }

        while (j <= end) {
            tempArray[k++] = array[j++];
        }

        System.arraycopy(tempArray, 0, array, start, tempArray.length);
    }

}


}