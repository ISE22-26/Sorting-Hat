import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SortingAgorithm {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String[] input = reader.readLine().split(",");
            int[] numbers = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();

            // Example sorting operation (using Arrays.sort for simplicity)
            Arrays.sort(numbers);

            // Output the sorted numbers
            for (int i = 0; i < numbers.length; i++) {
                System.out.print(numbers[i]);
                if (i < numbers.length - 1) {
                    System.out.print(",");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }
}