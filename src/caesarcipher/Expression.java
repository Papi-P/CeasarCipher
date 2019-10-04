/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caesarcipher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author Daniel Allen
 */
public class Expression {

    double answer = 0;
    String expression = "";
    HashMap<String, Double> variables = new HashMap<>();

    Stack<Double> values = new Stack<>();
    Stack<Character> operators = new Stack<>();
    Stack<Double> powers = new Stack<>();

    public Expression(String expression) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null!");
        }
        this.expression = expression;
    }
    static final HashMap<Character, Integer> OPERATOR_PRIORITIES = new HashMap<>();

    static {
        OPERATOR_PRIORITIES.put('^', 3);
        OPERATOR_PRIORITIES.put('*', 2);
        OPERATOR_PRIORITIES.put('/', 2);
        OPERATOR_PRIORITIES.put('+', 1);
        OPERATOR_PRIORITIES.put('-', 1);
    }

    /* 1. While there are still tokens to be read in,
     *    1.1 Get the next token.
     *      1.2 If the token is:
     *           1.2.1 A number: push it onto the value stack.
     *           1.2.2 A variable: get its value, and push onto the value stack.
     *           1.2.3 A left parenthesis: push it onto the operator stack.
     *           1.2.4 A right parenthesis:
     *              1.2.4.1 While the thing on top of the operator stack is not a left parenthesis,
     *                 1.2.4.1.1 Pop the operator from the operator stack.
     *                 1.2.4.1.2 Pop the value stack twice, getting two operands.
     *                 1.2.4.1.3 Apply the operator to the operands, in the correct order.
     *                  1.2.4.1.4 Push the result onto the value stack.
     *                 1.2.4.1.5 Pop the left parenthesis from the operator stack, and discard it.
     *          1.2.5 An operator (call it thisOp):
     *              1.2.5.1 While the operator stack is not empty, and the top
     *                      thing on the operator stack has the same or greater
     *                      precedence as thisOp,
     *                 1.2.5.1.1 Pop the operator from the operator stack.
     *                 1.2.5.1.2 Pop the value stack twice, getting two operands.
     *                 1.2.5.1.3 Apply the operator to the operands, in the correct order.
     *                 1.2.5.1.4 Push the result onto the value stack.
     * 2. Push thisOp onto the operator stack.
     *    2.1 While the operator stack is not empty,
     *        2.1.1 Pop the operator from the operator stack.
     *        2.1.2 Pop the value stack twice, getting two operands.
     *        2.1.3 Apply the operator to the operands, in the correct order.
     *        2.1.4 Push the result onto the value stack.
     * 3. At this point the operator stack should be empty, and the value stack
     *    should have only one value in it, which is the final result.
     */
    private void solve() {
        String curNum = "";
        for (int i = 0; i < expression.length(); i++) {
            char curChar = expression.charAt(i);

            if (Character.isSpaceChar(curChar)) {
                continue;
            }
            System.out.println("curChar: " + curChar);
            if (Character.isDigit(curChar)) {
                curNum += curChar;
            }
            if ((!Character.isDigit(curChar) || i == expression.length() - 1) && !curNum.isEmpty()) {
                values.push(Double.parseDouble(curNum));
                curNum = "";
            }
            if (curChar == '(') {
                int openBracket = i+1;
                int closeBracket = 0;
                int depth = 1;
                for(int z = i+1; z < expression.length(); z++){
                    if(expression.charAt(z) == '('){
                        depth++;
                    }
                    if(expression.charAt(z) == ')'){
                        depth--;
                    }
                    if(depth == 0){
                        closeBracket = z-1;
                        values.add(new Expression(expression.substring(openBracket, z)).getAnswer());
                        break;
                    }
                }
                i = closeBracket;
           // } //else if (curChar == ')') {
              //  if(i > 0 && Character.isDigit(expression.charAt(i-1))){
               //     operators.push('*');
              //  }
              // while (!operators.empty() && operators.peek() != '(') {
              //      values.push(getResultFromOperation());
              //  }
            } else if (OPERATOR_PRIORITIES.containsKey(curChar)) {
                while (!operators.isEmpty() && (!OPERATOR_PRIORITIES.containsKey(operators.peek()) || OPERATOR_PRIORITIES.get(curChar) <= OPERATOR_PRIORITIES.get(operators.peek()))) {
                    double result = getResultFromOperation();
                    values.push(result);

                }
                operators.push(curChar);

                if (curChar != '^') {
                    if (powers.size() > 0) {
                        while (powers.size() > 1) {
                            Double pow1 = powers.pop();
                            Double pow2 = powers.pop();
                            System.out.println("1: " + pow1 + "\n2: " + pow2);
                            powers.add(0, Math.pow(pow1, pow2));
                        }
                        values.push(powers.pop());
                    }
                }

            }
        }
        while (!operators.isEmpty()) {
            values.push(getResultFromOperation());
        }
        this.answer = values.peek();
    }

    public String getExpression() {
        return this.expression;
    }

    public Double getAnswer() {
        this.solve();
        return this.answer;
    }

    private Double getResultFromOperation() {
        double result = 0;
        Character operation = operators.isEmpty() ? '+' : operators.pop();
        Double num1, num2;
        switch (operation) {
            case '+':
                num1 = values.pop();
                if (values.isEmpty()) {
                    return num1;
                }
                num2 = values.pop();
                result = num2 + num1;
                //System.out.println(num2 + "+" + num1 + "=" + result);
                break;
            case '-':
                num1 = values.pop();
                if (values.isEmpty()) {
                    return num1;
                }
                num2 = values.pop();
                result = num2 - num1;
                //System.out.println(num2 + "-" + num1 + "=" + result);
                break;
            case '*':
                num1 = values.pop();
                if (values.isEmpty()) {
                    return num1;
                }
                num2 = values.pop();
                result = num2 * num1;
                //System.out.println(num2 + "x" + num1 + "=" + result);
                break;
            case '/':
                num1 = values.pop();
                if (values.isEmpty()) {
                    return num1;
                }
                num2 = values.pop();
                result = num2 / num1;
                //System.out.println(num2 + "/" + num1 + "=" + result);
                break;
            case '^':
                num1 = values.pop();
                if (values.isEmpty()) {
                    return num1;
                }
                num2 = values.pop();
                result = Math.pow(num2, num1);
        }

        return result;
    }
}
