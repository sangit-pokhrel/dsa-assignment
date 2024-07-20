import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class to represent a TSP solver using Hill Climbing
public class HillClimbingTSP {

    // Method to solve the TSP problem using Hill Climbing
    public static List<Integer> solveTSP(int[][] distanceMatrix) {
        int numCities = distanceMatrix.length; // Number of cities in the distance matrix
        List<Integer> currentTour = generateInitialTour(numCities); // Generate an initial tour
        int currentDistance = calculateTourDistance(currentTour, distanceMatrix); // Calculate the distance of the initial tour
        boolean improved = true; // Flag to check if there's any improvement
        
        // Repeat until no further improvements can be made
        while (improved) {
            improved = false; // Assume no improvement until found otherwise
            // Try swapping every pair of cities in the current tour
            for (int i = 1; i < numCities - 1; i++) {
                for (int j = i + 1; j < numCities; j++) {
                    List<Integer> newTour = new ArrayList<>(currentTour); // Create a copy of the current tour
                    Collections.swap(newTour, i, j); // Swap two cities in the tour
                    int newDistance = calculateTourDistance(newTour, distanceMatrix); // Calculate the distance of the new tour
                    
                    // If the new tour is shorter, update the current tour
                    if (newDistance < currentDistance) {
                        currentTour = newTour; // Update to the better tour
                        currentDistance = newDistance; // Update the current distance
                        improved = true; // Set the flag to true indicating an improvement
                    }
                }
            }
        }
        return currentTour; // Return the best tour found
    }

    // Generate an initial tour by visiting cities in sequential order
    private static List<Integer> generateInitialTour(int numCities) {
        List<Integer> tour = new ArrayList<>(); // List to store the tour
        for (int i = 0; i < numCities; i++) {
            tour.add(i); // Add cities in order to the tour
        }
        return tour; // Return the initial tour
    }

    // Calculate the total distance of the given tour
    private static int calculateTourDistance(List<Integer> tour, int[][] distanceMatrix) {
        int totalDistance = 0; // Variable to store the total distance
        // Loop through each city in the tour to sum the distances
        for (int i = 0; i < tour.size() - 1; i++) {
            totalDistance += distanceMatrix[tour.get(i)][tour.get(i + 1)]; // Add the distance between consecutive cities
        }
        totalDistance += distanceMatrix[tour.get(tour.size() - 1)][tour.get(0)]; // Add the distance from last city to the starting city (return to start)
        return totalDistance; // Return the total distance
    }

    // Main method to run the example
    public static void main(String[] args) {
        // Example distance matrix
        int[][] distances = {
            {0, 10, 15, 20}, // Distances from city 0 to others
            {10, 0, 35, 25}, // Distances from city 1 to others
            {15, 35, 0, 30}, // Distances from city 2 to others
            {20, 25, 30, 0}  // Distances from city 3 to others
        };

        // Solve TSP and print the optimal tour
        List<Integer> optimalTour = solveTSP(distances);
        System.out.println("Optimal Tour: " + optimalTour);
    }
}
