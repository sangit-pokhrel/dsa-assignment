import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class BasicCalculatorGUI extends JFrame {
    private JTextField inputField;
    private JButton calculateButton;
    private JLabel resultLabel;

    public BasicCalculatorGUI() {
        // Set up the frame
        setTitle("Basic Calculator GUI");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Input field
        inputField = new JTextField();
        add(inputField, BorderLayout.NORTH);

        // Calculate button
        calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(new CalculateButtonListener());
        add(calculateButton, BorderLayout.CENTER);

        // Result label
        resultLabel = new JLabel("Result: ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(resultLabel, BorderLayout.SOUTH);
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String expression = inputField.getText();
            try {
                int result = evaluateExpression(preprocessExpression(expression));
                resultLabel.setText("Result: " + result);
            } catch (Exception ex) {
                resultLabel.setText("Error: Invalid expression");
            }
        }
    }

    // Preprocess the expression to handle implicit multiplication
    private String preprocessExpression(String expression) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            sb.append(c);

            if (c == ')' || Character.isDigit(c)) {
                if (i + 1 < expression.length() && expression.charAt(i + 1) == '(') {
                    sb.append('*');
                }
            }
        }
        return sb.toString();
    }

    // Evaluate the mathematical expression
    private int evaluateExpression(String expression) throws Exception {
        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            // Skip whitespaces
            if (c == ' ')
                continue;

            // If the character is a digit, form the full number
            if (Character.isDigit(c)) {
                int num = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num = num * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                values.push(num);
                i--; // to account for the last increment in the while loop
            }
            // If the character is an opening brace, push it to 'ops'
            else if (c == '(') {
                ops.push(c);
            }
            // If the character is a closing brace, solve the entire brace
            else if (c == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            }
            // If the character is an operator
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                // While the top of 'ops' has the same or greater precedence to current token, which is an operator.
                // Apply the operator on top of 'ops' to top two elements in values stack.
                while (!ops.isEmpty() && hasPrecedence(c, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                // Push current token to 'ops'.
                ops.push(c);
            }
        }

        // Entire expression has been parsed at this point, apply remaining ops to remaining values.
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        // Top of 'values' contains the result.
        return values.pop();
    }

    // Returns true if 'op2' has higher or same precedence as 'op1', otherwise returns false.
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    // A utility method to apply an operator 'op' on operands 'a' and 'b'.
    private int applyOp(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

    public static void main(String[] args) {
        // Run the GUI in the Event-Dispatching Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            BasicCalculatorGUI calculator = new BasicCalculatorGUI();
            calculator.setVisible(true);
        });
    }
}
