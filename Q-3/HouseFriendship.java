import java.util.*;

public class HouseFriendship {

    // Union-Find class to manage groups of connected houses
    static class DisjointSet {
        private int[] parent;
        private int[] rank;

        public DisjointSet(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 1;
            }
        }

        // Find the root of the set containing 'node'
        public int find(int node) {
            if (parent[node] != node) {
                parent[node] = find(parent[node]); // Path compression
            }
            return parent[node];
        }

        // Union two sets containing 'node1' and 'node2'
        public void union(int node1, int node2) {
            int root1 = find(node1);
            int root2 = find(node2);
            if (root1 != root2) {
                if (rank[root1] > rank[root2]) {
                    parent[root2] = root1;
                } else if (rank[root1] < rank[root2]) {
                    parent[root1] = root2;
                } else {
                    parent[root2] = root1;
                    rank[root1]++;
                }
            }
        }
    }

    public static List<String> evaluateRequests(int numHouses, int[][] restrictions, int[][] requests) {
        DisjointSet ds = new DisjointSet(numHouses);

        // Setup restrictions
        for (int[] restriction : restrictions) {
            ds.union(restriction[0], restriction[1]);
        }

        // Create a map to track restricted pairs
        Map<Integer, Set<Integer>> restrictionMap = new HashMap<>();
        for (int[] restriction : restrictions) {
            restrictionMap.computeIfAbsent(restriction[0], k -> new HashSet<>()).add(restriction[1]);
            restrictionMap.computeIfAbsent(restriction[1], k -> new HashSet<>()).add(restriction[0]);
        }

        List<String> result = new ArrayList<>();

        // Evaluate each friend request
        for (int[] request : requests) {
            int houseA = request[0];
            int houseB = request[1];

            if (ds.find(houseA) == ds.find(houseB)) {
                // Houses are in the same group
                boolean canBeFriends = true;
                // Check if adding this friendship would violate any restrictions
                for (int restrictedHouse : restrictionMap.getOrDefault(houseA, Collections.emptySet())) {
                    if (ds.find(restrictedHouse) == ds.find(houseB)) {
                        canBeFriends = false;
                        break;
                    }
                }
                result.add(canBeFriends ? "approved" : "denied");
            } else {
                // Houses are not in the same group, so the request is approved
                result.add("approved");
                ds.union(houseA, houseB);
            }
        }

        return result;
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
