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
import javax.swing.UIManager;

public class BasicCalculatorGUI extends JFrame {
    private JTextField inputField;
    private JButton calculateButton;
    private JTextField resultField;

    public BasicCalculatorGUI() {
        setTitle("Calculator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Set a clean look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Header Label
        JLabel headerLabel = new JLabel("Calculator", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);

        // Main Panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input Label and Field
        JLabel inputLabel = new JLabel("Input", JLabel.LEFT);
        inputLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(inputLabel);

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 18));
        inputField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        panel.add(inputField);

        // Calculate Button
        calculateButton = new JButton("Calculate");
        calculateButton.setFont(new Font("Arial", Font.BOLD, 18));
        calculateButton.setBackground(Color.BLACK);
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        calculateButton.addActionListener(new CalculateButtonListener());
        panel.add(calculateButton);

        // Output Label and Field
        JLabel outputLabel = new JLabel("Output", JLabel.LEFT);
        outputLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(outputLabel);

        resultField = new JTextField();
        resultField.setFont(new Font("Arial", Font.PLAIN, 18));
        resultField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        resultField.setHorizontalAlignment(JTextField.CENTER);
        resultField.setEditable(false);
        panel.add(resultField);

        add(panel, BorderLayout.CENTER);
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String expression = inputField.getText();
            try {
                int result = evaluateExpression(preprocessExpression(expression));
                resultField.setText(String.valueOf(result));
            } catch (Exception ex) {
                resultField.setText("Error");
            }
        }
    }

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

    private int evaluateExpression(String expression) throws Exception {
        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ')
                continue;

            if (Character.isDigit(c)) {
                int num = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num = num * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                values.push(num);
                i--;
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!ops.isEmpty() && hasPrecedence(c, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

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
        SwingUtilities.invokeLater(() -> {
            BasicCalculatorGUI calculator = new BasicCalculatorGUI();
            calculator.setVisible(true);
        });
    }
}
