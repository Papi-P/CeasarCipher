/*
 * © 2019 Daniel Allen
 */
package cipher;

/*
    @author Daniel Allen
 */

/**
 *
 * @author Daniel
 */

public class MathException extends Exception {

    int location = 0;
    String expression = "";

    /**
     *
     */
    public MathException() {
        super();
    }

    /**
     *
     * @param msg
     */
    public MathException(String msg) {
        super(msg);
    }

    /**
     *
     * @param loc
     */
    public MathException(int loc) {
        super();
        this.location = loc;
    }

    /**
     *
     * @param loc
     * @param msg
     */
    public MathException(int loc, String msg) {
        super(msg);
        this.location = loc;
    }

    /**
     *
     * @param expression
     * @param loc
     * @param msg
     */
    public MathException(String expression, int loc, String msg) {
        super(msg);
        this.location = loc;
        this.expression = expression;
    }

    /**
     *
     * @param expression
     * @param loc
     */
    public MathException(String expression, int loc) {
        super();
        this.location = loc;
        this.expression = expression;
    }

    /**
     *
     * @return
     */
    public int getErrorLocation() {
        return this.location;
    }

    /**
     *
     * @return
     */
    public String getProblematicExpression() {
        return this.expression;
    }

    /**
     * Returns the string surrounding the source of the error, the source identified with &gt;&lt;
     * @return String marking location of error
     */
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
