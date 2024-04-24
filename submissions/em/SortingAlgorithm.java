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

            MergeSort.entryPoint(numbers);

            // ========= DON'T MODIFY THE CODE BELOW =========
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}

class MergeSort extends RecursiveAction {
    int MAX_THREADS = java.lang.Thread.activeCount(); // Maximum number of threads to be used for sorting

    // initialize variables for the array, starting point and ending point
    int[] array;
    int low;
    int high;

    // constructor for the MergeSort class
    public MergeSort(int[] array, int low, int high) {
        this.array = array;
        this.low = low;
        this.high = high;
    }

    // compute method for the MergeSort class
    @Override
    protected void compute() {
        // set the threshold to the length of the array divided by the number of threads
        final int THRESHOLD = array.length / MAX_THREADS;

        // if the difference between the high and low is less than or equal to the
        // threshold
        if (high - low <= THRESHOLD) {
            // sort the array on the current thread
            Arrays.sort(array, low, high);

        } else {
            // calculate the mid point
            int mid = low + (high - low) / 2;

            // invoke all the threads to split the array into two halves
            invokeAll(
                    new MergeSort(array, low, mid),
                    new MergeSort(array, mid, high));
            // merge the two halves
            merge(array, low, mid, high);
        }
    }

    // merge method to merge the two halves of the array
    private void merge(int[] array, int low, int mid, int high) {
        // create two new arrays to store the left and right halves of the array
        int[] left = Arrays.copyOfRange(array, low, mid);
        int[] right = Arrays.copyOfRange(array, mid, high);

        // initialize variables for the left, right and original arrays
        int i = 0, j = 0, k = low;

        // merge the two halves of the array
        // loop through the left and right arrays
        while (i < left.length && j < right.length) {
            // if the element in the left array is less than or equal to the element in the
            // right array
            if (left[i] <= right[j]) {
                // set the element in the original array to the element in the left array
                array[k++] = left[i++];
            } else {
                // set the element in the original array to the element in the right array
                array[k++] = right[j++];
            }
        }

        // add the remaining elements from the left and right arrays to the original
        // array
        while (i < left.length) {
            array[k++] = left[i++];
        }

        while (j < right.length) {
            array[k++] = right[j++];
        }
    }

    // entry point for the MergeSort class
    public static int[] entryPoint(int[] array) {
        // create a ForkJoinPool object
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        // create a MergeSort object
        MergeSort parallelMergeSort = new MergeSort(array, 0, array.length);
        // invoke the object
        forkJoinPool.invoke(parallelMergeSort);

        return array;
    }
}
