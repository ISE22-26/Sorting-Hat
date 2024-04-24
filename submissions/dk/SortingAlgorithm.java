package for_fionn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

// Student Number: 22340017
public class SortingAlgorithm {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();
            // ========= DON'T MODIFY THE CODE ABOVE =========
            // DAN KENNEDY 22340017
            ParallelMergeSort.sort(numbers);

            // ========= DON'T MODIFY THE CODE BELOW =========
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}

class ParallelMergeSort extends Thread {
    private final int threadID; // 0 to numberOfThreads-1, used to determine the range of the array to sort
    private final CyclicBarrier barrier; // Used to sync threads
    private final int[] array; // The array to sort
    private final int[] auxiliaryArray; // The auxiliary array used for merging
    private final int numberOfThreads; // The number of threads to use

    public ParallelMergeSort(int threadID, CyclicBarrier barrier, int[] array, int[] auxiliaryArray,
            int numberOfThreads) {
        super("thread " + threadID);
        this.threadID = threadID;
        this.barrier = barrier;
        this.array = array;
        this.auxiliaryArray = auxiliaryArray;
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public void run() {
        try {
            int sliceSize = array.length / numberOfThreads;
            int left = threadID * sliceSize;
            int right = left + sliceSize;
            if (threadID == numberOfThreads - 1) {
                right = array.length;
            }

            SortUtil.quickInsertionSort(array, left, right - 1);
            barrier.await();

            int numberOfSlices = numberOfThreads;

            // two threads for each pair of blocks.
            // either even already or last block is not merged
            int activeThreads = (numberOfSlices % 2 == 0) ? numberOfSlices : numberOfSlices - 1;

            while (numberOfSlices > 1) {
                if (threadID >= activeThreads) {
                    barrier.await();
                    continue;
                }

                if (threadID % 2 == 0) {
                    int leftStartIndex = threadID * sliceSize;
                    int rightStartIndex = leftStartIndex + sliceSize;
                    int endIndex = rightStartIndex + sliceSize;
                    if (threadID + 2 == numberOfSlices) {
                        endIndex = array.length;
                    }

                    int mergedElements = SortUtil.mergeMins(array, auxiliaryArray, leftStartIndex, rightStartIndex,
                            endIndex);
                    barrier.await();

                    System.arraycopy(auxiliaryArray, leftStartIndex, array, leftStartIndex, mergedElements);
                } else {
                    int leftStartIndex = (threadID - 1) * sliceSize;
                    int rightStartIndex = leftStartIndex + sliceSize;
                    int endIndex = rightStartIndex + sliceSize;
                    if (threadID + 1 == numberOfSlices) {
                        endIndex = array.length;
                    }

                    int mergedElements = SortUtil.mergeMaxes(array, auxiliaryArray, leftStartIndex, rightStartIndex,
                            endIndex);
                    barrier.await();

                    System.arraycopy(auxiliaryArray, endIndex - mergedElements, array, endIndex - mergedElements,
                            mergedElements);
                }

                sliceSize *= 2;
                numberOfSlices = (int) Math.ceil(numberOfSlices / 2.0);

                activeThreads = (numberOfSlices % 2 == 0) ? numberOfSlices : numberOfSlices - 1;
                barrier.await();
            }

        } catch (InterruptedException | BrokenBarrierException ex) {
            System.out.println("exception error message: " + ex.getMessage());
        }
    }

    public static void sort(int[] arr) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();

        int[] auxiliaryArray = new int[arr.length];

        // Fixed thread count, need to sync before proceeding
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        ParallelMergeSort[] threads = new ParallelMergeSort[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ParallelMergeSort(i, barrier, arr, auxiliaryArray, numberOfThreads);
            threads[i].start();
        }

        // Main thread waits for the first thread to finish (they all finish
        // simultaneously)
        try {
            threads[0].join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

class SortUtil {

    /**
     * mergeMins: Merge smaller values in the first half of the auxiliary array.
     * (Merges half of the two sorted sub arrays (slices))
     * 
     * @param array           the array to be sorted
     * @param auxiliaryArray  the array to be used as auxiliary space
     * @param leftStartIndex  start index of the left slice
     * @param rightStartIndex start index of the right slice
     * @param endIndex        first element after the end of the right slice
     *
     * @return the number of merged elements
     */
    public static int mergeMins(int[] array, int[] auxiliaryArray, int leftStartIndex, int rightStartIndex,
            int endIndex) {

        int leftSlicePointer = leftStartIndex;
        int rightSlicePointer = rightStartIndex;
        int writePointer = leftStartIndex;

        int elementsToMerge = (endIndex - leftStartIndex) / 2;
        int elementsMerged = 0;

        while (leftSlicePointer < rightStartIndex && rightSlicePointer < endIndex && elementsMerged < elementsToMerge) {
            if (array[leftSlicePointer] < array[rightSlicePointer]) {
                auxiliaryArray[writePointer] = array[leftSlicePointer];
                leftSlicePointer++;
            } else {
                auxiliaryArray[writePointer] = array[rightSlicePointer];
                rightSlicePointer++;
            }
            writePointer++;
            elementsMerged++;
        }

        // if no element left in right slice, copy rest from left slice
        while (leftSlicePointer < rightStartIndex && elementsMerged < elementsToMerge) {
            auxiliaryArray[writePointer++] = array[leftSlicePointer++];
            elementsMerged++;
        }

        // if no element left in left slice, copy rest from right slice
        while (rightSlicePointer < endIndex && elementsMerged < elementsToMerge) {
            auxiliaryArray[writePointer++] = array[rightSlicePointer++];
            elementsMerged++;
        }

        return (writePointer - leftStartIndex);
    }

    /**
     * mergeMaxes: Merge larger values and put in the second half of the auxiliary
     * array.
     * If the total number of elements is an odd number,
     * merge one extra here.
     * (Merges half of the two sorted sub arrays (slices))
     * 
     * @param array           the array to be sorted
     * @param auxiliaryArray  the array to be used as auxiliary space
     * @param leftStartIndex  start index of the left slice
     * @param rightStartIndex start index of the right slice
     * @param endIndex        first element after the end of the right slice
     *
     * @return the number of merged elements
     */
    public static int mergeMaxes(int[] array, int[] auxiliaryArray, int leftStartIndex, int rightStartIndex,
            int endIndex) {
        int leftSlicePointer = rightStartIndex - 1;
        int rightSlicePointer = endIndex - 1;
        int writePointer = endIndex - 1;

        int elementsToMerge = (int) Math.ceil((endIndex - leftStartIndex) / 2.0);
        int elementsMerged = 0;

        while (leftSlicePointer >= leftStartIndex && rightSlicePointer >= rightStartIndex
                && elementsMerged < elementsToMerge) {
            if (array[leftSlicePointer] > array[rightSlicePointer]) {
                auxiliaryArray[writePointer] = array[leftSlicePointer];
                leftSlicePointer--;
            } else {
                auxiliaryArray[writePointer] = array[rightSlicePointer];
                rightSlicePointer--;
            }
            writePointer--;
            elementsMerged++;
        }

        // if no element left in right slice, copy rest from left slice
        while (leftSlicePointer >= leftStartIndex && elementsMerged < elementsToMerge) {
            auxiliaryArray[writePointer--] = array[leftSlicePointer--];
            elementsMerged++;
        }

        // if no element left in the left slice, copy rest from right slice
        while (rightSlicePointer >= rightStartIndex && elementsMerged < elementsToMerge) {
            auxiliaryArray[writePointer--] = array[rightSlicePointer--];
            elementsMerged++;
        }

        return (endIndex - writePointer - 1);
    }

    /**
     * insertionSort: sort the array using insertion sort algorithm
     * 
     * @param arr   the array to be sorted
     * @param left  start index of the array
     * @param right end index of the array (inclusive)
     */
    public static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
    }

    /**
     * partition: partition the array into two parts
     * 
     * @param arr   the array to be partitioned
     * @param left  start index of the array
     * @param right end index of the array (inclusive)
     * @return the index of the pivot
     */
    public static int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = (left - 1);
        for (int j = left; j < right; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[right];
        arr[right] = temp;
        return i + 1;
    }

    public static void quickInsertionSort(int[] arr, int left, int right) {
        if (left < right) {
            if (right - left <= 7) { // Magic number 7 for insertion sort (small arrays)
                SortUtil.insertionSort(arr, left, right);
                return;
            }
            int partitionIndex = SortUtil.partition(arr, left, right);
            quickInsertionSort(arr, left, partitionIndex - 1);
            quickInsertionSort(arr, partitionIndex + 1, right);
        }
    }
}
