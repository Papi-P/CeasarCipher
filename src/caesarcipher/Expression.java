/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caesarcipher;

import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author Daniel Allen
 */
public class Expression {

    double answer = 0;
    String expression = "";

    public Expression(String expression) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null!");
        }
        this.expression = expression;
        this.solve();
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
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();
        String curNum = "";
        //While there are still tokens to be read in,
        for (int i = 0; i < expression.length(); i++) {
            //Get the next token.
            char curChar = expression.charAt(i);
            //If the token is:
            if (Character.isSpaceChar(curChar)) {
                continue;
            }
            //A number: push it onto the value stack.
            if (Character.isDigit(curChar)) {
                curNum += curChar;
            } else {
                values.push(Double.parseDouble(curNum));
                curNum = "";
            } 
            //A variable: get its value, and push onto the value stack.
            //A left parenthesis: push it onto the operator stack.
            if (curChar == '(') {
                operators.push(curChar);
            } 
            //A right parenthesis:
            if (curChar == ')') {
                //While the thing on top of the operator stack is not a left parenthesis,
                while(operators.peek()!= '(') {
                    //Pop the value stack twice, getting two operands.
                    Double num1 = values.pop();
                    Double num2 = values.pop();
                    //Pop the operator from the operator stack.
                    Character operation = operators.pop();
                    double result = 0;
                    //Apply the operator to the operands, in the correct order.
                    switch (operation) {
                        case '+':
                            result = num2 + num1;
                            break;
                        case '-':
                            result = num2 - num1;
                            break;
                        case '*':
                            result = num2 * num1;
                            break;
                        case '/':
                            result = num2 / num1;
                            break;
                        case '^':
                            result = Math.pow(num2, num1);
                            break;
                    }
                    //Push the result onto the value stack.
                    values.push(result);
                }
                //Pop the left parenthesis from the operator stack, and discard it.
                operators.pop();
            } if (OPERATOR_PRIORITIES.containsKey(curChar)){
                //An operator (call it thisOp):
                //While the operator stack is not empty, and the top thing on the operator stack has the same or greater precedence as thisOp,
                //Pop the operator from the operator stack.
                //Pop the value stack twice, getting two operands.
                //Apply the operator to the operands, in the correct order.
                //Push the result onto the value stack.
            }
        }
        while (!operators.isEmpty()) {
            Double num1 = values.pop();
            Double num2 = values.pop();
            Character operation = operators.pop();
            double result = 0;
            switch (operation) {
                case '+':
                    result = num2 + num1;
                    break;
                case '-':
                    result = num2 - num1;
                    break;
                case '*':
                    result = num2 * num1;
                    break;
                case '/':
                    result = num2 / num1;
                    break;
                case '^':
                    result = Math.pow(num2, num1);
                    break;
            }
            values.push(result);
        }
        this.answer = values.firstElement();
    }

    public String getExpression() {
        return this.expression;
    }

    public Double getAnswer() {
        this.solve();
        return this.answer;
    }
}
