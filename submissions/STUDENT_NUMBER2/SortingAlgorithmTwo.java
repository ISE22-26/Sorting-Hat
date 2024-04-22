import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class SortingAlgorithmTwo {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            // read all lines into array efficiently
            String[] input = reader.lines().toArray(String[]::new);
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();

            // Example sorting operation (using Arrays.sort for simplicity)
            Arrays.sort(numbers);

            // Output the sorted numbers, each on a new line
            for (int number : numbers) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}
