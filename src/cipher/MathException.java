/*
 * © 2019 Daniel Allen
 */
package cipher;

/*
    @author Daniel Allen
 */
public class MathException extends Exception {

    int location = 0;
    String expression = "";

    public MathException() {
        super();
    }

    public MathException(String msg) {
        super(msg);
    }

    public MathException(int loc) {
        super();
        this.location = loc;
    }

    public MathException(int loc, String msg) {
        super(msg);
        this.location = loc;
    }

    public MathException(String expression, int loc, String msg) {
        super(msg);
        this.location = loc;
        this.expression = expression;
    }

    public MathException(String expression, int loc) {
        super();
        this.location = loc;
        this.expression = expression;
    }

    public int getErrorLocation() {
        return this.location;
    }

    public String getProblematicExpression() {
        return this.expression;
    }

    public String getStylizedError() {
        System.out.println(this.location);
        System.out.println(this.expression.length());
        if(this.expression == null || this.expression.isEmpty()){
            return "><";
        }
        String s = expression.substring(Math.max(0, this.location - 5), this.location)
                + ">" + expression.charAt(Math.min(this.location, this.expression.length()-1)) +
                "<"
                + expression.substring(Math.min(this.location+1, this.expression.length()));
        return s;
    }
}
