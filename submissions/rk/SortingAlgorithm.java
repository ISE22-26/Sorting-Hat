import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Student Number: 22340238
public class SortingAlgorithm {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();
            // ========= DON'T MODIFY THE CODE ABOVE =========

            int numThreads = 4;

            try {
                MergeSort.sort(numbers, numThreads);
            } catch (InterruptedException e) {
                System.err.println("Error sorting array: " + e.getMessage());
            }

            // ========= DON'T MODIFY THE CODE BELOW =========
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}

class MergeSort {

    private int[] array;
    private int numThreads;
    private int finishedThreads;

    private MergeSort(int numThreads, int[] array) {
        this.numThreads = numThreads;
        this.array = array;
    }

    public static void sort(int[] array, int numThreads) throws InterruptedException {
        new MergeSort(numThreads, array).mergeSort();
    }

    private synchronized void mergeSort() throws InterruptedException {
        ExecutorService executors = Executors.newFixedThreadPool(numThreads);
        int start = 0;
        int end = 0;
        finishedThreads = 0;

        for (int i = 1; i <= numThreads; i++) {
            start = end;
            end = (array.length * i) / numThreads;
            executors.execute(new MergeSortTask(start, end));
        }

        while (finishedThreads < numThreads) {
            wait();
        }

        recursiveMergeSort(array, 0, array.length);
        executors.shutdown();
    }

    private static void recursiveMergeSort(int[] array, int start, int end) {
        if (end - start > 1) {
            int middle = (start + end) / 2;
            recursiveMergeSort(array, start, middle);
            recursiveMergeSort(array, middle, end);
            merge(array, start, middle, end);
        }
    }

    private static void merge(int[] array, int start, int middle, int end) {
        int[] leftSegment = Arrays.copyOfRange(array, start, middle);
        int leftIndex = 0;
        int rightIndex = middle;
        int currentIndex = start;
        while (leftIndex < leftSegment.length && rightIndex < end) {
            if (leftSegment[leftIndex] <= array[rightIndex]) {
                array[currentIndex++] = leftSegment[leftIndex++];
            } else {
                array[currentIndex++] = array[rightIndex++];
            }
        }
        if (leftIndex < leftSegment.length) {
            System.arraycopy(leftSegment, leftIndex, array, currentIndex, leftSegment.length - leftIndex);
        }
    }

    private class MergeSortTask implements Runnable {
        int start;
        int end;

        public MergeSortTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            recursiveMergeSort(array, start, end);
            synchronized (MergeSort.this) {
                finishedThreads++;
                MergeSort.this.notify();
            }
        }
    }
}