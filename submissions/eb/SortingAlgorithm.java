import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Student Number: 
public class SortingAlgorithm {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();
            // ========= DON'T MODIFY THE CODE ABOVE =========

            // Split the array into partitions
            int numThreads = Runtime.getRuntime().availableProcessors();
            int partitionSize = numbers.length / numThreads;
            List<Thread> threads = new ArrayList<>();

            for (int i = 0; i < numThreads; i++) {
                int start = i * partitionSize;
                int end = (i == numThreads - 1) ? numbers.length : (i + 1) * partitionSize;
                int[] partition = Arrays.copyOfRange(numbers, start, end);

                Thread thread = new Thread(() -> {
                    Arrays.sort(partition);
                });
                threads.add(thread);
                thread.start();
            }

            // Wait for all threads to finish
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + e.getMessage());
                }
            }

            // Merge the sorted partitions
            int[] sortedNumbers = new int[numbers.length];
            int[] partitionIndices = new int[numThreads];
            for (int i = 0; i < numbers.length; i++) {
                int minIndex = -1;
                int minValue = Integer.MAX_VALUE;

                for (int j = 0; j < numThreads; j++) {
                    int partitionIndex = partitionIndices[j];
                    if (partitionIndex < (j + 1) * partitionSize && numbers[partitionIndex] < minValue) {
                        minIndex = j;
                        minValue = numbers[partitionIndex];
                    }
                }

                sortedNumbers[i] = minValue;
                partitionIndices[minIndex]++;
            }

            numbers = sortedNumbers;

            // ========= DON'T MODIFY THE CODE BELOW =========
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}
