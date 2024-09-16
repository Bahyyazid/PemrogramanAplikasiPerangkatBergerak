package com.example.pemrogramanmobilebegerak;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView resultsTextView;
    private TextView previousOperationTextView;
    private StringBuilder currentInput = new StringBuilder();
    private StringBuilder previousOperation = new StringBuilder();
    private boolean isOperandLast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the result and previous operation TextViews
        resultsTextView = findViewById(R.id.Results);
        previousOperationTextView = findViewById(R.id.PreviousOperation);

        // Initialize all buttons
        Button btn0 = findViewById(R.id.btn0);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);
        Button addButton = findViewById(R.id.AddButton);
        Button subtractButton = findViewById(R.id.SubtractButton);
        Button multiplyButton = findViewById(R.id.MultiplyButton);
        Button divideButton = findViewById(R.id.DivideButton);
        Button clearButton = findViewById(R.id.ClearButton);
        Button calculateButton = findViewById(R.id.CalculateButton);

        // Set up listeners for number buttons
        View.OnClickListener numberListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                // If an operand was previously entered, clear current input
                if (isOperandLast) {
                    currentInput.setLength(0);
                }

                // Append number to the current input
                currentInput.append(button.getText().toString());
                updateResults();
                isOperandLast = false;
            }
        };

        // Set up listeners for operator buttons
        View.OnClickListener operatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                String operator = button.getText().toString();

                if (currentInput.length() == 0 && previousOperation.length() == 0) {
                    showToast("Please insert the first number");
                    return;
                }

                if (isOperandLast) {
                    showToast("Invalid input");
                    return;
                }

                if (currentInput.length() > 0) {
                    previousOperation.append(currentInput.toString()).append(" ");
                }

                previousOperation.append(operator).append(" ");
                currentInput.setLength(0);
                isOperandLast = true;
                updateResults();
            }
        };

        // Add listeners to number buttons
        btn0.setOnClickListener(numberListener);
        btn1.setOnClickListener(numberListener);
        btn2.setOnClickListener(numberListener);
        btn3.setOnClickListener(numberListener);
        btn4.setOnClickListener(numberListener);
        btn5.setOnClickListener(numberListener);
        btn6.setOnClickListener(numberListener);
        btn7.setOnClickListener(numberListener);
        btn8.setOnClickListener(numberListener);
        btn9.setOnClickListener(numberListener);

        // Add listeners to operator buttons
        addButton.setOnClickListener(operatorListener);
        subtractButton.setOnClickListener(operatorListener);
        multiplyButton.setOnClickListener(operatorListener);
        divideButton.setOnClickListener(operatorListener);

        // Clear button functionality
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentInput.setLength(0);  // Clear the input
                resultsTextView.setText("");
                previousOperationTextView.setText("");
                previousOperation.setLength(0);
                isOperandLast = false;
            }
        });

        // Calculate button functionality
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOperandLast) {
                    showToast("Please insert a number");
                    return;
                }

                // Append the last input to previousOperation if it exists
                if (currentInput.length() > 0) {
                    previousOperation.append(currentInput.toString());
                }

                String inputExpression = previousOperation.toString();
                if (inputExpression.isEmpty()) {
                    showToast("Please insert the first number");
                    return;
                }

                try {
                    double result = evaluateExpression(inputExpression);  // Evaluate the expression
                    previousOperationTextView.setText(inputExpression); // Set previous operation
                    resultsTextView.setText(formatResult(String.valueOf(result)));  // Display the result
                    currentInput.setLength(0);                            // Clear current input after calculation
                    previousOperation.setLength(0);                       // Clear previous operation after calculation
                    isOperandLast = false;
                } catch (Exception e) {
                    resultsTextView.setText("Error");
                    currentInput.setLength(0);  // Clear the input in case of error
                }
            }
        });
    }

    // Update Results TextView to show current operation
    private void updateResults() {
        String currentOperation = previousOperation.toString() + currentInput.toString();
        resultsTextView.setText(currentOperation);
    }

    // Custom method to evaluate a basic mathematical expression
    private double evaluateExpression(String expression) {
        // Replace the symbols for multiplication and division with valid operators
        expression = expression.replace("×", "*").replace("÷", "/");

        // Create stacks to hold numbers and operations
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            // If the current character is a digit, parse the full number
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                numbers.push(Double.parseDouble(sb.toString()));
                continue;
            }

            // If it's an operator, push to operator stack
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
            i++;
        }

        // Apply remaining operators to remaining numbers
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        // The result is in the last element of the numbers stack
        return numbers.pop();
    }

    // Method to determine operator precedence
    private boolean hasPrecedence(char currentOp, char stackOp) {
        if ((currentOp == '*' || currentOp == '/') && (stackOp == '+' || stackOp == '-')) {
            return false;
        }
        return true;
    }

    // Method to apply an operator to two numbers
    private double applyOperation(char operation, double b, double a) {
        switch (operation) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }

    // Format result to avoid ".0" for whole numbers
    private String formatResult(String result) {
        if (result.contains(".") && result.endsWith("0")) {
            result = result.replaceAll("\\.0$", "");
        }
        return result;
    }

    // Show a toast message for warnings
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
