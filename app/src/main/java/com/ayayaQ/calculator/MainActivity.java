package com.ayayaQ.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.EmptyStackException;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    public static final char MULTIPLY = '×';
    public static final char DIVIDE = '÷';
    public static final char SUBTRACT = '-';
    public static final char ADD = '+';
    public static final char NEGATIVE = '~';
    public static final String EQUATION = "EQUATION";
    public static final String DECIMAL = "DECIMAL";
    private TextView equationView;
    private TextView resultsView;
    private boolean decimal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);
        resultsView = findViewById(R.id.results);
        equationView = findViewById(R.id.equation);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EQUATION, equationView.getText().toString());
        outState.putBoolean(DECIMAL, decimal);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        equationView.setText(savedInstanceState.getString(EQUATION));
        decimal = savedInstanceState.getBoolean(DECIMAL, false);
        if(!(equationView.getText().toString().equals(""))) {
            result(equationView.getText().toString(), resultsView);
        }
    }

    //handles button presses for all buttons
    public void buttonPress(View view) {

        switch(view.getId()) {

            case R.id.btn_1:
                appendSymbol('1');
                break;

            case R.id.btn_2:
                appendSymbol('2');
                break;

            case R.id.btn_3:
                appendSymbol('3');
                break;

            case R.id.btn_4:
                appendSymbol('4');
                break;

            case R.id.btn_5:
                appendSymbol('5');
                break;

            case R.id.btn_6:
                appendSymbol('6');
                break;

            case R.id.btn_7:
                appendSymbol('7');
                break;

            case R.id.btn_8:
                appendSymbol('8');
                break;

            case R.id.btn_9:
                appendSymbol('9');
                break;

            case R.id.btn_0:
                appendSymbol('0');
                break;

            case R.id.btn_decimal:
                appendSymbol('.');
                break;

            case R.id.btn_clear:
                equationView.setText("");
                resultsView.setText("");
                decimal = false;
                return;

            case R.id.btn_plus:
                appendSymbol(ADD);
                break;

            case R.id.btn_subtract:
                appendSymbol(SUBTRACT);
                break;

            case R.id.btn_times:
                appendSymbol(MULTIPLY);
                break;


            case R.id.btn_divide:
                appendSymbol(DIVIDE);
                break;

            case R.id.btn_equal:
                result(equationView.getText().toString(), equationView);
                decimal = equationView.getText().toString().contains(".");
                resultsView.setText("");
                return;
        }
        // if not empty give result to resultView
        if(!(equationView.getText().toString().equals(""))) {
            result(equationView.getText().toString(), resultsView);
        }




    }
    // adds char to equation string
    public void appendSymbol(char c) {

        if(equationView.getText().toString().equals("You can't divide by zero."))
            equationView.setText("");

        if(c == '.' && decimal)
            return;
        else if(c == '.') {
            decimal = true;
        }
        else if(isOp(c)) {

            try {
                decimal = false;
                String equation = equationView.getText().toString();
                if (c != SUBTRACT) {
                    if (isOp(equation.charAt(equation.length() - 1))) {
                        return;
                    }
                }
                if (c == SUBTRACT) {
                    if (equation.equals("") || equation.length() == 1) {
                        if (equation.length() == 1 && equation.charAt(0) == SUBTRACT) {
                            //Log.i("ARRAY", "IM HERE?");
                            return;
                        }
                        //Log.i("ARRAY", "IM HERE");
                    } else if (equation.length() > 1) {
                        if (isOp(equation.charAt(equation.length() - 1)) && isOp(equation.charAt(equation.length() - 2))) {
                            //Log.i("ARRAY", "IM HERE...");
                            return;
                        }
                    }
                }
            } catch (StringIndexOutOfBoundsException e) {
                Log.i("ARRAY", e.toString());
                return;
            }
        }

        String newEquation = equationView.getText().toString() + c;
        equationView.setText(newEquation);
    }

    // checks for negative symbols in the equation and sets them
    public String isUnaryCheck(String equation) {
        if(equation.equals("") || equation.equals(".")) return "0";
        if (equation.charAt(0) == SUBTRACT) {
            equation = "~" + equation.substring(1);
        }

        for(int i = 1; i < equation.length()-1; i++) {
            if(isOp(equation.charAt(i))) {
                if(equation.charAt(i+1) == SUBTRACT) {
                    equation = equation.substring(0, i+1) + "~" + equation.substring(i+2);
                }
            }
        }

        if(equation.equals("~0"))
            return "0";

        return equation;
    }

    // checks if char is operator
    public boolean isOp(char c){
        return c == ADD || c == SUBTRACT || c == MULTIPLY || c == DIVIDE;
    }

    // converts the equation to postfix notation
    public String toPostFix(String equation) {
        String result = "";

        Stack<Character> stack = new Stack<Character>();

        if(isOp(equation.charAt(equation.length()-1)))
        {
            equation = equation.substring(0, equation.length()-1);
        }

        for(int i = 0; i < equation.length(); i++) {
            char c = equation.charAt(i);

            if(Character.isDigit(c) || c == '.') {
                result += c;
            }
            else {
                while(!stack.isEmpty() && getPrecedence(c) <= getPrecedence(stack.peek())) {
                    result += stack.pop();
                }
                stack.push(c);
                result += " ";
            }
        }

        while(!stack.isEmpty()) {
            result += stack.pop();
        }

        return result;
    }

    // gets precedence of certain operators
    public int getPrecedence(char op) {
        if (op == NEGATIVE) {
            return 3;
        }
        else if(op == MULTIPLY || op == DIVIDE) {
            return 2;
        }
        else if (op == ADD || op == SUBTRACT) {
            return 1;
        }

        return 0;
    }

    // evaluates the string equation
    public double evalPostFix(String equation) throws ArithmeticException {
        try {
            Stack<Double> stack = new Stack<Double>();

            double numOne;
            double numTwo;

            for (int i = 0; i < equation.length(); i++) {
                char c = equation.charAt(i);


                if (Character.isDigit(c) || c == '.') {
                    boolean decimal = false;
                    int decimalPointCount = 0;
                    double x = 0;

                    while (Character.isDigit(c) || c == '.') {
                        if (Character.isDigit(c)) {
                            x = x * 10 + Integer.parseInt(c + "");
                            if (decimal) {
                                decimalPointCount++;
                            }

                        } else if (c == '.') {
                            decimal = true;
                        }
                        i++;
                        c = equation.charAt(i);
                    }
                    if (decimal) {
                        x = x / Math.pow(10, (double) decimalPointCount);
                    }
                    if (equation.charAt(i) == NEGATIVE) {
                        x = -x;
                    }
                    i--;


                    stack.push(x);
                } else if (c == ADD) {
                    numOne = stack.pop();
                    try {
                        numTwo = stack.pop();

                        stack.push(numTwo + numOne);
                    } catch (EmptyStackException e) {
                        return numOne;
                    }
                } else if (c == SUBTRACT) {
                    numOne = stack.pop();
                    try {
                        numTwo = stack.pop();

                        stack.push(numTwo - numOne);
                    } catch (EmptyStackException e) {
                        return numOne;
                    }
                } else if (c == MULTIPLY) {
                    numOne = stack.pop();
                    try {
                        numTwo = stack.pop();

                        stack.push(numTwo * numOne);
                    } catch (EmptyStackException e) {
                        return numOne;
                    }
                } else if (c == DIVIDE) {
                    numOne = stack.pop();
                    try {
                        numTwo = stack.pop();
                        if (numOne == 0) {
                            throw new ArithmeticException();
                        }
                        stack.push(numTwo / numOne);
                    } catch (EmptyStackException e) {
                        return numOne;
                    }
                }
            }

            if(stack.isEmpty()) return 0;

            return stack.pop();
        } catch (StringIndexOutOfBoundsException e){
            if(equation.charAt(0) == SUBTRACT){
                return -(Double.parseDouble(equation));
            }
            else if(equation.charAt(0) == '.'){
                return Double.parseDouble("0" + equation + "0");
            }
            else return Double.parseDouble(equation);
        }
    }

    // takes the string equation gets result and puts it into 4 decimal spaces rounding to ceiling
    public void result(String equation, TextView equationView) {
        try {
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);

            String resultEquation = df.format(evalPostFix(toPostFix(isUnaryCheck(equation))));

            equationView.setText(resultEquation);
        } catch (ArithmeticException e) {
            equationView.setText("You can't divide by zero.");
        }
    }

    // copies the text of equationView
    public void copyTextEquation(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clipData = ClipData.newPlainText("Equation or Result", equationView.getText().toString());

        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(this, "Copied to Clipboard.", Toast.LENGTH_SHORT).show();
    }

    // copies text of resultsView
    public void copyTextResults(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clipData = ClipData.newPlainText("Equation or Result", resultsView.getText().toString());

        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(this, "Copied to Clipboard.", Toast.LENGTH_SHORT).show();
    }
}
