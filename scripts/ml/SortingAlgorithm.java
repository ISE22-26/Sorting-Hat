import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

// Student Number: 22340475
public class SortingAlgorithm {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();

            quickSortRandomArray(numbers);

            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    public static int[] quickSortRandomArray(int[] inArray) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new ParallelQuickSort(inArray, 0, inArray.length - 1));
        return inArray;
    }
}

class ParallelQuickSort extends RecursiveAction {
    private final int[] array;
    private final int low;
    private final int high;

    public ParallelQuickSort(int[] array, int low, int high) {
        this.array = array;
        this.low = low;
        this.high = high;
    }

    @Override
    protected void compute() {
        if (low < high) {
            int partitionIndex = partition(array, low, high);

            ParallelQuickSort leftTask = new ParallelQuickSort(array, low, partitionIndex - 1);
            ParallelQuickSort rightTask = new ParallelQuickSort(array, partitionIndex + 1, high);

            invokeAll(leftTask, rightTask);
        }
    }

    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, high);
        return i + 1;
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
