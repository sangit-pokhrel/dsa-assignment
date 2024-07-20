import java.util.TreeSet;

public class TheaterSeating {

    public static boolean areFriendsComfortable(int[] seats, int maxIndexDiff, int maxValueDiff) {
        // TreeSet to store seat numbers within the allowable range
        TreeSet<Integer> seatSet = new TreeSet<>();
        
        for (int pos = 0; pos < seats.length; pos++) {
            // Remove elements that are out of the index difference range
            if (pos > maxIndexDiff) {
                seatSet.remove(seats[pos - maxIndexDiff - 1]);
            }
            
            // Check if there is any seat number in the set that satisfies the value difference condition
            Integer lower = seatSet.floor(seats[pos] + maxValueDiff);
            Integer upper = seatSet.ceiling(seats[pos] - maxValueDiff);
            
            if ((lower != null && lower >= seats[pos] - maxValueDiff) || (upper != null && upper <= seats[pos] + maxValueDiff)) {
                return true;
            }
            
            // Add the current seat number to the set
            seatSet.add(seats[pos]);
        }
        
        return false;
    }

    public static void main(String[] args) {
        int[] seats = {2, 3, 5, 4, 9};
        int maxIndexDiff = 2;
        int maxValueDiff = 1;
        
        boolean result = areFriendsComfortable(seats, maxIndexDiff, maxValueDiff);
        System.out.println(result); // Output: true
    }
}
