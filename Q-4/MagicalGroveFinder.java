
// Define a class to represent a node in the binary tree
class TreeNode {
    int value; // Value of the node (magical coin)
    TreeNode left; // Left child node
    TreeNode right; // Right child node

    TreeNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}

public class MagicalGroveFinder {

    // Result variable to store the maximum total value of the magical grove found
    private static int maxGroveValue = 0;

    // Main method to find the largest magical grove
    public static int findLargestMagicalGrove(TreeNode root) {
        // Call the helper method with the root node
        calculateGrove(root);
        return maxGroveValue; // Return the result
    }

    // Helper method to calculate the largest magical grove
    private static GroveInfo calculateGrove(TreeNode node) {
        if (node == null) {
            // Base case: If the node is null, return a GroveInfo with default values
            return new GroveInfo(true, 0, Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        // Recursively calculate grove information for the left and right subtrees
        GroveInfo leftGrove = calculateGrove(node.left);
        GroveInfo rightGrove = calculateGrove(node.right);

        // Check if the current subtree rooted at `node` is a valid BST
        boolean isBST = leftGrove.isBST && rightGrove.isBST &&
                         (node.left == null || leftGrove.maxValue < node.value) &&
                         (node.right == null || rightGrove.minValue > node.value);

        // Calculate the total value of the current subtree
        int totalValue = node.value + leftGrove.totalValue + rightGrove.totalValue;

        // Update the maximum grove value if the current subtree is a valid BST
        if (isBST) {
            maxGroveValue = Math.max(maxGroveValue, totalValue);
        }

        // Return the GroveInfo for the current subtree
        return new GroveInfo(isBST, totalValue,
                             Math.min(node.value, leftGrove.minValue),
                             Math.max(node.value, rightGrove.maxValue));
    }

    // Inner class to store information about a subtree
    static class GroveInfo {
        boolean isBST; // Indicates if the subtree is a valid BST
        int totalValue; // Total value of coins in the subtree
        int minValue; // Minimum value in the subtree
        int maxValue; // Maximum value in the subtree

        GroveInfo(boolean isBST, int totalValue, int minValue, int maxValue) {
            this.isBST = isBST;
            this.totalValue = totalValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
    }

    // Main method to run the example
    public static void main(String[] args) {
        // Construct the binary tree based on the given example
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(4);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(2);
        root.right.right = new TreeNode(5);
        root.right.right.right = new TreeNode(6);

        // Find the largest magical grove and print the result
        int result = findLargestMagicalGrove(root);
        System.out.println("Largest Magical Grove Value: " + result); // Output: 20
    }
}
