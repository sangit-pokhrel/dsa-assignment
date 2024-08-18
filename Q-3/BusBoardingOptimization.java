import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BusBoardingOptimization {

    // Method to reverse segments of a list in groups of size 'k'
    public static List<Integer> optimizeBoarding(List<Integer> passengers, int interval) {
        // Result list to store the final arrangement of passengers
        List<Integer> optimizedList = new ArrayList<>(passengers);

        // Iterate over the list in steps of 'interval'
        for (int start = 0; start + interval <= optimizedList.size(); start += interval) {
            // Calculate the end index for the current segment
            int end = start + interval - 1;

            // Reverse the segment in-place
            reverseSegment(optimizedList, start, end);
        }

        // Return the optimized list
        return optimizedList;
    }

    // Helper method to reverse a segment of the list from index 'start' to 'end'
    private static void reverseSegment(List<Integer> list, int start, int end) {
        while (start < end) {
            // Swap elements at 'start' and 'end' indices
            Collections.swap(list, start, end);
            start++;
            end--;
        }
    }

    public static void main(String[] args) {
        // Example 1
        List<Integer> boardingSequence1 = Arrays.asList(1, 2, 3, 4, 5);
        int interval1 = 2;
        System.out.println("Optimized Boarding Sequence 1: " + optimizeBoarding(boardingSequence1, interval1)); 
        // Output: [2, 1, 4, 3, 5]

        // Example 2
        List<Integer> boardingSequence2 = Arrays.asList(1, 2, 3, 4, 5);
        int interval2 = 3;
        System.out.println("Optimized Boarding Sequence 2: " + optimizeBoarding(boardingSequence2, interval2));
        // Output: [3, 2, 1, 4, 5]
    }
}
