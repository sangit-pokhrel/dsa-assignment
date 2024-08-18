import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class BasicCalculatorGUI extends JFrame {
    private JTextField inputField;          // Input field for user to enter the expression
    private JButton calculateButton;        // Button to trigger the calculation
    private JLabel resultLabel;             // Label to display the result of the calculation

    public BasicCalculatorGUI() {
        // Set up the frame
        setTitle("Basic Calculator");       // Set the title of the window
        setSize(400, 300);                  // Set the size of the window (width x height)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close application when the window is closed
        setLocationRelativeTo(null);        // Center the window on the screen
        setLayout(new BorderLayout(10, 10)); // Use BorderLayout with padding between components

        // Header Label
        JLabel headerLabel = new JLabel("Basic Calculator", JLabel.CENTER); // Label to display header text centered
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font for header label
        add(headerLabel, BorderLayout.NORTH); // Add header label to the top (NORTH) of the layout

        // Panel for Input and Button
        JPanel panel = new JPanel();         // Create a panel to hold input field and button
        panel.setLayout(new GridLayout(2, 1, 10, 10)); // Use GridLayout with 2 rows, 1 column, and padding
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add empty border (padding) around panel

        // Input field
        inputField = new JTextField();       // Create text field for user input
        inputField.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font for input field
        inputField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Set border color and thickness for input field
        panel.add(inputField);               // Add input field to the panel

        // Calculate button
        calculateButton = new JButton("Calculate"); // Create button with "Calculate" label
        calculateButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set font for button text
        calculateButton.setBackground(Color.LIGHT_GRAY); // Set background color of button
        calculateButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2)); // Set border color and thickness for button
        calculateButton.addActionListener(new CalculateButtonListener()); // Add action listener to handle button click
        panel.add(calculateButton);          // Add button to the panel

        add(panel, BorderLayout.CENTER);     // Add panel to the center of the layout

        // Result label
        resultLabel = new JLabel("Result: ", JLabel.CENTER); // Create label to display results centered
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font for result label
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add empty border (padding) around result label
        add(resultLabel, BorderLayout.SOUTH); // Add result label to the bottom (SOUTH) of the layout
    }

    // Inner class to handle button click events
    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String expression = inputField.getText(); // Get the text (expression) from the input field
            try {
                int result = evaluateExpression(preprocessExpression(expression)); // Evaluate the expression
                resultLabel.setText("Result: " + result); // Display the result in the result label
            } catch (Exception ex) {
                resultLabel.setText("Error: Invalid expression"); // Display an error message if expression is invalid
            }
        }
    }

    // Preprocess the expression to handle implicit multiplication (e.g., 2(3) -> 2*(3))
    private String preprocessExpression(String expression) {
        StringBuilder sb = new StringBuilder(); // Use StringBuilder to build the processed expression
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i); // Get the current character in the expression
            sb.append(c); // Append the current character to the StringBuilder

            // If the current character is a digit or closing parenthesis, and the next character is an opening parenthesis
            if (c == ')' || Character.isDigit(c)) {
                if (i + 1 < expression.length() && expression.charAt(i + 1) == '(') {
                    sb.append('*'); // Add an asterisk (*) to handle implicit multiplication
                }
            }
        }
        return sb.toString(); // Return the processed expression as a string
    }

    // Evaluate the mathematical expression
    private int evaluateExpression(String expression) throws Exception {
        Stack<Integer> values = new Stack<>(); // Stack to store integer values
        Stack<Character> ops = new Stack<>(); // Stack to store operators
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i); // Get the current character in the expression

            if (c == ' ') // Skip spaces
                continue;

            if (Character.isDigit(c)) { // If the character is a digit, form the full number
                int num = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num = num * 10 + (expression.charAt(i) - '0'); // Build the full number from digits
                    i++;
                }
                values.push(num); // Push the number to the values stack
                i--; // Adjust index to account for the last increment in the while loop
            } else if (c == '(') { // If the character is an opening parenthesis
                ops.push(c); // Push the opening parenthesis to the ops stack
            } else if (c == ')') { // If the character is a closing parenthesis
                while (ops.peek() != '(') { // Evaluate the expression inside the parentheses
                    values.push(applyOp(ops.pop(), values.pop(), values.pop())); // Apply the operator to the top two values
                }
                ops.pop(); // Remove the opening parenthesis from the ops stack
            } else if (c == '+' || c == '-' || c == '*' || c == '/') { // If the character is an operator
                while (!ops.isEmpty() && hasPrecedence(c, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop())); // Apply operators with higher or same precedence
                }
                ops.push(c); // Push the current operator to the ops stack
            }
        }

        while (!ops.isEmpty()) { // Apply remaining operators to remaining values
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop(); // The top of the values stack contains the result
    }

    // Returns true if 'op2' has higher or same precedence as 'op1'
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    // Apply an operator 'op' to operands 'a' and 'b'
    private int applyOp(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b; // Addition
            case '-':
                return a - b; // Subtraction
            case '*':
                return a * b; // Multiplication
            case '/':
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero"); // Handle division by zero
                return a / b; // Division
        }
        return 0; // Default return value (should never be reached)
    }

    public static void main(String[] args) {
        // Run the GUI in the Event-Dispatching Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            BasicCalculatorGUI calculator = new BasicCalculatorGUI(); // Create the calculator GUI instance
            calculator.setVisible(true); // Make the calculator window visible
        });
    }
}
