import java.util.*;

// Class to find the longest consecutive hiking stretch
public class LongestHikeFinder {

    // Method to find the longest consecutive stretch of hiking within elevation gain limit
    public static int findLongestHike(int[] trail, int maxElevationGain) {
        int longestStretch = 0; // Variable to store the length of the longest valid stretch
        int start = 0; // Start index of the current valid stretch
        
        // Iterate through the trail to find valid stretches
        for (int end = 0; end < trail.length; end++) { // End index of the current valid stretch
            // Check if the hike from start to end is valid
            while (end > start && trail[end] - trail[end - 1] > maxElevationGain) {
                start++; // Move start index to the right to find a valid stretch
            }
            // Calculate the length of the current valid stretch
            longestStretch = Math.max(longestStretch, end - start + 1);
        }
        return longestStretch; // Return the length of the longest valid stretch
    }

    // Main method to run the example
    public static void main(String[] args) {
        int[] hikingTrail = {4, 2, 1, 4, 3, 4, 5, 8, 15}; // Example trail with altitudes
        int elevationLimit = 3; // Maximum allowed elevation gain

        int longestHike = findLongestHike(hikingTrail, elevationLimit); // Find the longest hike
        System.out.println("Longest Hike Length: " + longestHike); // Print the length of the longest hike
    }
}
