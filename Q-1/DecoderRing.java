public class DecoderRing {
    // Method to decipher the message using the given shifts
    public static String decipherMessage(String s, int[][] shifts) {
        // Convert the input string to a character array for easier manipulation
        char[] chars = s.toCharArray();

        // Iterate over each shift operation
        for (int[] shift : shifts) {
            int start = shift[0];
            int end = shift[1];
            int direction = shift[2];

            // Apply the shift to each character in the specified range
            for (int i = start; i <= end; i++) {
                chars[i] = shiftChar(chars[i], direction == 1 ? 1 : -1);
            }
        }

        // Convert the modified character array back to a string and return it
        return new String(chars);
    }

    // Helper method to shift a character by the specified amount
    private static char shiftChar(char c, int shift) {
        int base = 'a';
        int offset = c - base;
        // Ensure the result is within [0, 25] by adding 26 and then taking modulo 26
        offset = (offset + shift + 26) % 26;
        return (char) (base + offset);
    }

    // Main method to test the decipherMessage method
    public static void main(String[] args) {
        // Test case 1
        String s1 = "hello";
        int[][] shifts1 = {{0, 1, 1}, {2, 3, 0}, {0, 2, 1}};
        System.out.println(decipherMessage(s1, shifts1));  // Output: jglko
    }
}
