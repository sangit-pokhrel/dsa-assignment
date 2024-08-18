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
        private int[] parent;  // Array to track the parent of each node
        private int[] rank;    // Array to track the rank (height) of each tree in the disjoint set

        // Constructor to initialize the disjoint set with 'size' elements
        public DisjointSet(int size) {
            parent = new int[size];  // Initialize parent array
            rank = new int[size];    // Initialize rank array
            for (int i = 0; i < size; i++) {
                parent[i] = i;       // Each element is its own parent initially
                rank[i] = 1;         // Each tree has a rank of 1 initially
            }
        }

        // Find the root of the set containing 'node'
        public int find(int node) {
            if (parent[node] != node) {              // If the node is not its own parent
                parent[node] = find(parent[node]);   // Path compression: set the parent of the node to its root
            }
            return parent[node];                     // Return the root of the set
        }

        // Union two sets containing 'node1' and 'node2'
        public void union(int node1, int node2) {
            int root1 = find(node1);  // Find the root of the set containing 'node1'
            int root2 = find(node2);  // Find the root of the set containing 'node2'
            if (root1 != root2) {     // If they are in different sets, union them
                if (rank[root1] > rank[root2]) {       // If root1's tree is taller, attach root2's tree under root1
                    parent[root2] = root1;
                } else if (rank[root1] < rank[root2]) { // If root2's tree is taller, attach root1's tree under root2
                    parent[root1] = root2;
                } else {                                // If both trees have the same height, attach root2 under root1
                    parent[root2] = root1;
                    rank[root1]++;                      // Increase the rank of root1's tree
                }
            }
        }
    }

    public static List<String> evaluateRequests(int numHouses, int[][] restrictions, int[][] requests) {
        // Initialize a DisjointSet to manage connected components of houses
        DisjointSet ds = new DisjointSet(numHouses);

        // Setup restrictions: Union the houses that have restrictions
        for (int[] restriction : restrictions) {
            ds.union(restriction[0], restriction[1]);
        }

        // Create a map to track restricted pairs
        Map<Integer, Set<Integer>> restrictionMap = new HashMap<>();
        for (int[] restriction : restrictions) {
            // Add each restricted pair to the map
            restrictionMap.computeIfAbsent(restriction[0], k -> new HashSet<>()).add(restriction[1]);
            restrictionMap.computeIfAbsent(restriction[1], k -> new HashSet<>()).add(restriction[0]);
        }

        // List to store the results of each request
        List<String> result = new ArrayList<>();

        // Evaluate each friend request
        for (int[] request : requests) {
            int houseA = request[0];  // Get the first house in the request
            int houseB = request[1];  // Get the second house in the request

            if (ds.find(houseA) == ds.find(houseB)) {  // Check if both houses are in the same group
                boolean canBeFriends = true;  // Flag to determine if the request can be approved

                // Check if adding this friendship would violate any restrictions
                for (int restrictedHouse : restrictionMap.getOrDefault(houseA, Collections.emptySet())) {
                    if (ds.find(restrictedHouse) == ds.find(houseB)) {  // If restricted house is in the same group as houseB
                        canBeFriends = false;  // Friendship request is denied
                        break;
                    }
                }
                result.add(canBeFriends ? "approved" : "denied");  // Add the result based on the flag
            } else {
                // Houses are not in the same group, so the request is approved
                result.add("approved");
                ds.union(houseA, houseB);  // Union the two houses to connect them
            }
        }

        return result;  // Return the list of results for all requests
    }

    public static void main(String[] args) {
        // Example 1
        int numHouses1 = 3;
        int[][] restrictions1 = {{0, 1}};
        int[][] requests1 = {{0, 2}, {2, 1}};
        System.out.println(evaluateRequests(numHouses1, restrictions1, requests1)); // Output: [approved, denied]

        // Example 2
        int numHouses2 = 5;
        int[][] restrictions2 = {{0, 1}, {1, 2}, {2, 3}};
        int[][] requests2 = {{0, 4}, {1, 2}, {3, 1}, {3, 4}};
        System.out.println(evaluateRequests(numHouses2, restrictions2, requests2)); // Output: [approved, denied, approved, denied]
    }
}
