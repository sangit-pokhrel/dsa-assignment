// Import the java.util package for using lists, maps, sets, etc.
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HouseFriendship {

    // Union-Find class to manage groups of connected houses
    static class DisjointSet {
        private int[] parent;  // Array to store the parent of each node (house)
        private int[] rank;    // Array to store the rank (depth) of each tree

        // Constructor to initialize the Union-Find data structure
        public DisjointSet(int size) {
            parent = new int[size];  // Initialize the parent array with the size of houses
            rank = new int[size];    // Initialize the rank array with the size of houses
            for (int i = 0; i < size; i++) {  // Loop through each house
                parent[i] = i;  // Set each house as its own parent (initially, each house is its own group)
                rank[i] = 1;    // Set the initial rank of each house to 1
            }
        }

        // Method to find the root of the set containing 'node'
        public int find(int node) {
            if (parent[node] != node) {  // If the node is not its own parent, it means it's not the root
                parent[node] = find(parent[node]); // Recursively find the root and apply path compression
            }
            return parent[node];  // Return the root of the node
        }

        // Method to union (merge) two sets containing 'node1' and 'node2'
        public void union(int node1, int node2) {
            int root1 = find(node1);  // Find the root of node1
            int root2 = find(node2);  // Find the root of node2
            if (root1 != root2) {  // If they are not in the same set, merge them
                if (rank[root1] > rank[root2]) {  // If root1 has a higher rank, make root1 the parent of root2
                    parent[root2] = root1;
                } else if (rank[root1] < rank[root2]) {  // If root2 has a higher rank, make root2 the parent of root1
                    parent[root1] = root2;
                } else {  // If they have the same rank, choose one as the parent and increment its rank
                    parent[root2] = root1;
                    rank[root1]++;
                }
            }
        }
    }

    // Method to evaluate friend requests based on restrictions and the current friendship network
    public static List<String> evaluateRequests(int numHouses, int[][] restrictions, int[][] requests) {
        DisjointSet ds = new DisjointSet(numHouses);  // Create a Union-Find structure for the houses

        // Loop through each restriction to union the restricted houses
        for (int[] restriction : restrictions) {
            ds.union(restriction[0], restriction[1]);  // Union the two restricted houses
        }

        // Create a map to track restricted pairs
        Map<Integer, Set<Integer>> restrictionMap = new HashMap<>();
        for (int[] restriction : restrictions) {
            // Add each restriction to the map for quick lookup
            restrictionMap.computeIfAbsent(restriction[0], k -> new HashSet<>()).add(restriction[1]);
            restrictionMap.computeIfAbsent(restriction[1], k -> new HashSet<>()).add(restriction[0]);
        }

        List<String> result = new ArrayList<>();  // List to store the results of the friend requests

        // Loop through each friend request
        for (int[] request : requests) {
            int houseA = request[0];  // Extract the first house in the request
            int houseB = request[1];  // Extract the second house in the request

            if (ds.find(houseA) == ds.find(houseB)) {  // If both houses are in the same group
                boolean canBeFriends = true;  // Assume the request can be approved

                // Check all houses connected to houseA for any indirect restriction
                for (int restrictedHouse : restrictionMap.getOrDefault(houseA, Collections.emptySet())) {
                    if (ds.find(restrictedHouse) == ds.find(houseB)) {  // If houseB is connected to a restricted house
                        canBeFriends = false;  // The request must be denied
                        break;  // Exit the loop as the request cannot be approved
                    }
                }
                result.add(canBeFriends ? "approved" : "denied");  // Add the result of the request to the list
            } else {  // If houses are not in the same group, the request can be approved
                result.add("approved");
                ds.union(houseA, houseB);  // Union the two houses to connect them
            }
        }

        return result;  // Return the list of results for all friend requests
    }

    public static void main(String[] args) {
        // Example 1
        int numHouses1 = 3;  // Number of houses
        int[][] restrictions1 = {{0, 1}};  // Restriction list
        int[][] requests1 = {{0, 2}, {2, 1}};  // Friend request list
        System.out.println(evaluateRequests(numHouses1, restrictions1, requests1)); // Output: [approved, denied]

        // Example 2
        int numHouses2 = 5;  // Number of houses
        int[][] restrictions2 = {{0, 1}, {1, 2}, {2, 3}};  // Restriction list
        int[][] requests2 = {{0, 4}, {1, 2}, {3, 1}, {3, 4}};  // Friend request list
        System.out.println(evaluateRequests(numHouses2, restrictions2, requests2)); // Output: [approved, denied, approved, denied]
    }
}
