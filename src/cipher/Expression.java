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

    public Expression(String expression, Map<String, Double> vars) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null!");
        }
        this.expression = expression;
        this.variables = new HashMap(vars);
    }

    public Expression(String expression, Double x) {
        if (expression == null) {
            throw new NullPointerException("Expression cannot be null!");
        }
        this.expression = expression;//.replaceAll("x", ""+x);
        HashMap<String, Double> vars = new HashMap<>();
        vars.put("x", x);
        this.variables = vars;

    }
    static final HashMap<Character, Integer> OPERATOR_PRIORITIES = new HashMap<>();

    static {
        OPERATOR_PRIORITIES.put('^', 3);
        OPERATOR_PRIORITIES.put('*', 2);
        OPERATOR_PRIORITIES.put('/', 2);
        OPERATOR_PRIORITIES.put('+', 1);
        OPERATOR_PRIORITIES.put('-', 1);
    }


    public Expression inverse(){

        return this;
    }
    private void solve() throws MathException {
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
                values.push(Double.parseDouble(curNum));
                curNum = "";
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
                 //       System.out.println("Expression before: "+expression);
                        //expression = expression.substring(0, openBracket-1) + ((int)(double)new Expression(expression.substring(openBracket, z)).getAnswer()) + expression.substring(closeBracket+2);
                  //      System.out.println("Expression to parse in brackets: "+expression.substring(openBracket, z));
                        values.push(new Expression(expression.substring(openBracket, z), variables).getAnswer());
                   //     System.out.println("Expression after: "+expression);
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
        System.out.println(operators.size() +" : "+values.size());
        while (!operators.isEmpty()) {
            values.push(getResultFromOperation());
        }
        while (values.size() > 1) {
            values.push(getResultFromOperation());
        }
        this.answer = values.peek();
    }

    public String getExpression() {
        return this.expression;
    }

    public Double getAnswer() throws MathException {
        this.solve();
       // System.out.println(this.answer);
        return this.answer;
    }
    public Integer getAnswerAsInt() throws MathException {
        this.solve();
        return (int)Math.round(this.answer);
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
}
