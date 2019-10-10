/*
 * © 2019 Daniel Allen
 */
package cipher;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Math expression parser based on Dijkstra's Shunting Yard Algorithm.
 *
 * @author Daniel Allen
 */
public class Expression {

    double answer = 0;
    private boolean solved = false;
    private String expression = "";
    HashMap<String, Double> variables = new HashMap<>();
    static final HashMap<Character, Integer> OPERATOR_PRIORITIES = new HashMap<>(5);

    static {
        OPERATOR_PRIORITIES.put('^', 3);
        OPERATOR_PRIORITIES.put('*', 2);
        OPERATOR_PRIORITIES.put('/', 2);
        OPERATOR_PRIORITIES.put('+', 1);
        OPERATOR_PRIORITIES.put('-', 1);
    }


    Stack<Double> values = new Stack<>();
    Stack<Character> operators = new Stack<>();

    /**
     * Creates a math expression from a String
     * @param expression The equation
     */
    public Expression(String expression) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null!");
        }
        this.expression = expression;
    }

    /**
     * Creates a math expression from a String and allows the use of variables.
     * @param expression The equation
     * @param vars Map of variable names and their values.
     */
    public Expression(String expression, Map<String, Double> vars) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null!");
        }
        this.expression = expression;
        this.variables = new HashMap(vars);
    }

    /**
     * Creates a math expression from a String and a variable <code>x</code>
     * @param expression The equation
     * @param x A variable
     */
    public Expression(String expression, Double x) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null!");
        }
        this.expression = expression;//.replaceAll("x", ""+x);
        HashMap<String, Double> vars = new HashMap<>();
        vars.put("x", x);
        this.variables = vars;

    }

    /**
     * Reverse the equation.<br>
     * i.e: <code>x^2</code> into <code>sqrt(x)</code>
     * @return
     */
    public Expression inverse(){
        throw new UnsupportedOperationException("This has not yet been implemented.");
    }

    /**
     * Solves the equation. This can be called at any time to skip the calculation with <code>getAnswer()</code>, immediately returning the result.
     * @see getAnswer()
     * @throws MathException if the Expression is not valid.
     */
    public void solve() throws MathException {
        System.out.println(this.expression);
        String curNum = "";
        String curVar = "";
        for (int i = 0; i < expression.length(); i++) {
            char curChar = expression.charAt(i);

            if (Character.isSpaceChar(curChar)) {
                continue;
            }
            if (Character.isDigit(curChar) || curChar == '.') {
                curNum += curChar;
            }
            if (Character.isLetter(curChar)) {
                curVar += curChar;
            }
            if ((!Character.isLetter(curChar) || i == expression.length() - 1) && !curVar.isEmpty()) {
                if (variables.containsKey(curVar)) {
                    values.push(variables.get(curVar));
                }
                curVar = "";
            }

            if (((!Character.isDigit(curChar) && curChar != '.') || i == expression.length() - 1) && !curNum.isEmpty()) {
                try{
                    values.push(Double.parseDouble(curNum));
                } catch (NumberFormatException e){
                    throw new MathException(expression, i, "Error parsing number!");
                } finally {
                    curNum = "";
                }
            }
            if (curChar == '(') {
                int openBracket = i+1;
                int closeBracket = 0;
                int depth = 1;
                for (int z = i + 1; z < expression.length() && depth != 0; z++) {
                    if (expression.charAt(z) == '(') {
                        depth++;
                    }
                    if (expression.charAt(z) == ')') {
                        depth--;
                    }
                    if (depth == 0) {
                        closeBracket = z - 1;
                        values.push(new Expression(expression.substring(openBracket, z), variables).getAnswer());
                    }
                    if((z == expression.length()-1 && depth != 0 && expression.charAt(z) != ')') || (z == expression.length()-1 && depth < 0)){
                        throw new MathException(expression, z+1, "Mismatched Braces");
                    }
                }
                i = closeBracket;
            } else if (OPERATOR_PRIORITIES.containsKey(curChar)) {
                while (!operators.isEmpty() && (!OPERATOR_PRIORITIES.containsKey(operators.peek()) || OPERATOR_PRIORITIES.get(curChar) <= OPERATOR_PRIORITIES.get(operators.peek()))) {
                    double result = getResultFromOperation();
                    values.push(result);

                }
                operators.push(curChar);
            }
        }
        while (!operators.isEmpty()) {
            values.push(getResultFromOperation());
        }
        while (values.size() > 1) {
            values.push(getResultFromOperation());
        }
        this.answer = values.peek();
        this.solved = true;
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
                break;
            case '-':
                num1 = values.pop();
                if (values.isEmpty()) {
                    return num1;
                }
                num2 = values.pop();
                result = num2 - num1;
                break;
            case '*':
                num1 = values.pop();
                if (values.isEmpty()) {
                    return num1;
                }
                num2 = values.pop();
                result = num2 * num1;
                break;
            case '/':
                num1 = values.pop();
                if (values.isEmpty()) {
                    return num1;
                }
                num2 = values.pop();
                result = num2 / num1;
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

    /**
     * Sets the equation in this Expression. If the equation is not identical to the current one, it will need to be solved before use.
     * @param eq The equation
     */
    public void setEquation(String eq){
        if(!eq.equals(this.expression))
            this.solved = false;
        this.expression = eq;

    }

    /**
     * Gets the equation created in the constructor.
     * @return the equation.
     */
    public String getEquation() {
        return this.expression;
    }

    /**
     * Returns the answer of the expression. If the expression has not been solved yet, it will be solved before being returned.
     * @return the answer of the expression.
     * @throws MathException
     */
    public Double getAnswer() throws MathException {
        if(!solved)
            this.solve();
        return this.answer;
    }

    /**
     * Returns the answer of the expression as an Integer. If the expression has not been solved yet, it will be solved before being returned.
     * @return the answer of the expression.
     * @throws MathException
     */
    public Integer getAnswerAsInt() throws MathException {
        if(!solved)
            this.solve();
        return (int)Math.round(this.answer);
    }

}
