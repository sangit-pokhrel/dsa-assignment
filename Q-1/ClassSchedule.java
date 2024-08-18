// Import all utility classes including ArrayList, PriorityQueue, etc.
import java.util.Arrays;
import java.util.PriorityQueue;

class ClassSchedule {
    // Method to find the most used classroom
    public static int mostUsedClassroom(int n, int[][] classes) {
        // Sort the classes by start time, and if start time is the same, sort by end time
        Arrays.sort(classes, (a, b) -> {
            if (a[0] == b[0]) {
                return Integer.compare(a[1], b[1]); // If start times are equal, sort by end time
            }
            return Integer.compare(a[0], b[0]); // Otherwise, sort by start time
        });

        // Min-heap to track the availability of rooms (end_time, room_number)
        PriorityQueue<int[]> availableRooms = new PriorityQueue<>((a, b) -> {
            if (a[0] == b[0]) {
                return Integer.compare(a[1], b[1]); // If end times are equal, sort by room number
            }
            return Integer.compare(a[0], b[0]); // Otherwise, sort by end time
        });

        // Initialize all rooms as available with end time 0
        for (int i = 0; i < n; i++) {
            availableRooms.offer(new int[] { 0, i }); // (end_time, room_number)
        }

        // Array to count how many classes each room has hosted
        int[] roomUsage = new int[n];

        // Process each class
        for (int[] cls : classes) {
            int start = cls[0]; // Start time of the class
            int end = cls[1];   // End time of the class

            // Check if there's a room available at the start time
            if (availableRooms.peek()[0] <= start) {
                int[] room = availableRooms.poll(); // Get the room that becomes available first
                availableRooms.offer(new int[] { end, room[1] }); // Update the room's end time and put it back in the heap
                roomUsage[room[1]]++; // Increment the usage count for that room
            } else {
                // If no room is available at the start time, adjust the start and end times
                int[] room = availableRooms.poll(); // Get the next available room
                start = room[0]; // Delay the class start to the end time of the next available room
                end = start + (end - cls[0]); // Maintain the class duration
                availableRooms.offer(new int[] { end, room[1] }); // Update the room's end time and put it back in the heap
                roomUsage[room[1]]++; // Increment the usage count for that room
            }
        }

        // Find the room with the most classes
        int maxClasses = Arrays.stream(roomUsage).max().orElse(0); // Find the maximum number of classes hosted by any room
        for (int i = 0; i < n; i++) {
            if (roomUsage[i] == maxClasses) { // Return the room index with the maximum classes
                return i;
            }
        }
        return -1; // Default case, should not happen given the problem constraints
    }

    // Main method to test the functionality
    public static void main(String[] args) {
        int n1 = 2; // Number of classrooms
        int[][] classes1 = { { 0, 10 }, { 1, 5 }, { 2, 7 }, { 3, 4 } }; // Array of classes with start and end times
        System.out.println(mostUsedClassroom(n1, classes1)); // Output: 0, as classroom 0 is used the most

        int n2 = 3; // Number of classrooms
        int[][] classes2 = { { 1, 20 }, { 2, 10 }, { 3, 5 }, { 4, 9 }, { 6, 8 } }; // Array of classes with start and end times
        System.out.println(mostUsedClassroom(n2, classes2)); // Output: 1, as classroom 1 is used the most
    }
}
