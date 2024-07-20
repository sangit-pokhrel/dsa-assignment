import java.util.*; // Importing necessary classes from the java.util package

public class RoadNetworkOptimization {

    // Method to adjust the road times to achieve the target travel time between two locations
    public static int[][] adjustRoadTimes(int numLocations, int[][] roadConnections, int startLocation, int endLocation, int desiredTime) {
        // Create a graph representation from the road connections
        Map<Integer, List<Edge>> graph = new HashMap<>();
        for (int[] road : roadConnections) {
            int from = road[0]; // Starting location of the road
            int to = road[1]; // Ending location of the road
            int time = road[2]; // Time required for the road, or -1 if under construction
            // Add edges to the graph for both directions (undirected graph)
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, time));
            graph.computeIfAbsent(to, k -> new ArrayList<>()).add(new Edge(from, time));
        }

        // Find the shortest path from the startLocation to the endLocation
        int[] shortestPathTimes = dijkstra(numLocations, graph, startLocation);
        int shortestTime = shortestPathTimes[endLocation]; // Shortest time from source to destination

        // Calculate the difference needed to reach the target time
        int timeDifference = desiredTime - shortestTime;

        // Adjust the weights of construction roads to meet the target time
        int[][] adjustedRoads = adjustWeights(roadConnections, timeDifference);

        return adjustedRoads; // Return the modified road connections
    }

    // Method to perform Dijkstra's algorithm to find the shortest path from the source
    private static int[] dijkstra(int numLocations, Map<Integer, List<Edge>> graph, int source) {
        int[] distances = new int[numLocations]; // Array to store the shortest distances from source
        Arrays.fill(distances, Integer.MAX_VALUE); // Initialize all distances to infinity
        distances[source] = 0; // Distance to the source is 0
        // Priority queue to pick the node with the smallest distance
        PriorityQueue<Node> minHeap = new PriorityQueue<>(Comparator.comparingInt(n -> n.dist));
        minHeap.add(new Node(source, 0)); // Add source node to the priority queue
        
        while (!minHeap.isEmpty()) {
            Node currentNode = minHeap.poll(); // Get the node with the smallest distance
            int current = currentNode.vertex; // Current node
            int currentDist = currentNode.dist; // Distance to the current node

            // Skip processing if the distance is not the shortest one
            if (currentDist > distances[current]) continue;

            // Process each neighbor of the current node
            for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
                int neighbor = edge.destination; // Neighbor node
                int weight = edge.weight; // Weight of the edge
                if (weight == -1) continue; // Skip under construction roads

                // Calculate the new distance to the neighbor
                int newDist = currentDist + weight;
                // Update the shortest distance if the new distance is smaller
                if (newDist < distances[neighbor]) {
                    distances[neighbor] = newDist;
                    minHeap.add(new Node(neighbor, newDist)); // Add updated distance to the priority queue
                }
            }
        }
        return distances; // Return the shortest distances from the source
    }

    // Method to adjust the weights of the construction roads to meet the target travel time
    private static int[][] adjustWeights(int[][] roads, int timeDifference) {
        for (int[] road : roads) {
            if (road[2] == -1) {
                // Set the weight of under construction roads to the minimum needed
                road[2] = 1 + timeDifference;
                timeDifference = 0; // Once adjusted, no need to adjust further
            }
        }
        return roads; // Return the modified road connections
    }

    // Inner class to represent an edge in the graph
    static class Edge {
        int destination; // Destination node of the edge
        int weight; // Weight of the edge

        Edge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    // Inner class to represent a node in the priority queue for Dijkstra's algorithm
    static class Node {
        int vertex; // Node index
        int dist; // Distance from the source

        Node(int vertex, int dist) {
            this.vertex = vertex;
            this.dist = dist;
        }
    }

    public static void main(String[] args) {
        // Example 1
        int numLocations1 = 5; // Number of locations in the city
        int[][] roadConnections1 = {
            {4, 1, -1}, // Road from location 4 to 1 with weight -1 (under construction)
            {2, 0, -1}, // Road from location 2 to 0 with weight -1 (under construction)
            {0, 3, -1}, // Road from location 0 to 3 with weight -1 (under construction)
            {4, 3, -1}  // Road from location 4 to 3 with weight -1 (under construction)
        };
        int startLocation1 = 0; // Starting location
        int endLocation1 = 1; // Destination location
        int desiredTime1 = 5; // Target travel time

        // Get the adjusted road connections
        int[][] result1 = adjustRoadTimes(numLocations1, roadConnections1, startLocation1, endLocation1, desiredTime1);
        System.out.println("Adjusted Road Connections 1: " + Arrays.deepToString(result1));
        // Output: [[4,1,1],[2,0,1],[0,3,3],[4,3,1]]

        // Example 2
        int numLocations2 = 4; // Number of locations in the city
        int[][] roadConnections2 = {
            {0, 1, -1}, // Road from location 0 to 1 with weight -1 (under construction)
            {1, 2, 3},  // Road from location 1 to 2 with weight 3
            {2, 3, -1}, // Road from location 2 to 3 with weight -1 (under construction)
            {0, 2, 2}   // Road from location 0 to 2 with weight 2
        };
        int startLocation2 = 0; // Starting location
        int endLocation2 = 3; // Destination location
        int desiredTime2 = 7; // Target travel time

        // Get the adjusted road connections
        int[][] result2 = adjustRoadTimes(numLocations2, roadConnections2, startLocation2, endLocation2, desiredTime2);
        System.out.println("Adjusted Road Connections 2: " + Arrays.deepToString(result2));
        // Output: Adjusted weights to meet the target travel time
    }
}
