/*
 * © 2019 Daniel Allen
 */
package cipher;

import static cipher.Driver.gui;

/*
    @author Daniel Allen
    4-Oct-2019
 */
public class Encrypt {

    /**
     * Encrypt a String using the given shift. It is strongly recommended to use
     * {@link cipher.Encrypt#shiftCharacter(String, int)} instead.
     *
     * @deprecated
     * @param s String to decrypt
     * @param shift amount to shift the String by
     *
     * @see shiftCharacter(String, int)
     *
     * @return Encrypted String
     */
    public static String encrypt(String s, int shift) {
        //validate the input by checking if its empty or null
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Attemped to encrypt an empty string!");
        }
        //validate the input by making sure its within the accepted range
        if (shift > 25 || shift < 0) {
            throw new IllegalArgumentException("Attemped to shift out of bounds!");
        }
        String output = "";
        for (char c : s.toCharArray()) {
            if (Character.getType(c) != Character.OTHER_PUNCTUATION && Character.getType(c) != Character.SPACE_SEPARATOR) {
                //shift the character
                char shiftedChar = (char) (c + shift);
                //if the maintainCharacters is selected, make sure the shifted character is the same type of character as the original.
                if (gui.settings.maintainCharacters.isSelected()) {
                    if (Character.isLowerCase(c) && shiftedChar < 'a') {
                        while (shiftedChar < 'a') {
                            shiftedChar += 26;
                        }
                    } else if (Character.isLowerCase(c) && shiftedChar > 'z') {
                        while (shiftedChar > 'z') {
                            shiftedChar -= 26;
                        }
                    } else if (Character.isUpperCase(c) && shiftedChar < 'A') {
                        while (shiftedChar < 'A') {
                            shiftedChar += 26;
                        }
                    } else if (Character.isUpperCase(c) && shiftedChar > 'Z') {
                        while (shiftedChar > 'Z') {
                            shiftedChar -= 26;
                        }
                    }
                }
                //add the shifted character to the output
                output += shiftedChar;
            } else {
                output += c;
            }
        }
        //return the output
        return output;
    }

    /**
     * Shifts a String by the given amount. This works left (-) and right (+)
     * without needing multiple methods.<br>
     * This basically merges both the
     * {@link cipher.Decrypt#decrypt(String, int)} and the
     * {@link cipher.Decrypt#decrypt(String, int)} methods into one.
     *
     * @param s The String to shift
     * @param shift The amount to shift by.
     *
     * @see encrypt(String, int)
     * @see cipher.Decrypt#decrypt(String, int)
     * @return The shifted String
     */
    public static String shiftCharacter(String s, int shift) {
        //validate the input by checking if its empty or null
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Attemped to encrypt an empty string!");
        }
        //validate the input by making sure its within the accepted range
        if (shift > 25 || shift < -25) {
            throw new IllegalArgumentException("Attemped to shift out of bounds!");
        }
        String output = "";
        for (char c : s.toCharArray()) {
            //make sure the character is not a space or other punctuation
            if (Character.getType(c) != Character.OTHER_PUNCTUATION && Character.getType(c) != Character.SPACE_SEPARATOR) {
                //shift the character
                char shiftedChar = (char) (c + shift);
                //if the maintainCharacters is selected, make sure the shifted character is the same type of character as the original.
                if (gui.settings.maintainCharacters.isSelected()) {
                    if (Character.isLowerCase(c) && shiftedChar < 'a') {
                        while (shiftedChar < 'a') {
                            shiftedChar += 26;
                        }
                    } else if (Character.isLowerCase(c) && shiftedChar > 'z') {
                        while (shiftedChar > 'z') {
                            shiftedChar -= 26;
                        }
                    } else if (Character.isUpperCase(c) && shiftedChar < 'A') {
                        while (shiftedChar < 'A') {
                            shiftedChar += 26;
                        }
                    } else if (Character.isUpperCase(c) && shiftedChar > 'Z') {
                        while (shiftedChar > 'Z') {
                            shiftedChar -= 26;
                        }
                    }
                }
                //add the shifted character to the output
                output += shiftedChar;
            } else {
                output += c;
            }
        }
        //return the output
        return output;
    }

    /**
     * Encrypts the input String using an Expression made from the equation
     * String.
     *
     * @param s The input String
     * @param eq The equation
     * @return Encrypted String
     * @throws MathException if the equation is invalid
     */
    public static String encrypt_from_equation(String s, String eq) throws MathException {
        //validate the input by checking if its empty or null
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Attemped to encrypt an empty string!");
        }
        String output = "";
        for (char c : s.toCharArray()) {
            if (Character.getType(c) != Character.OTHER_PUNCTUATION && Character.getType(c) != Character.SPACE_SEPARATOR) {
                //shift the character based on the answer to a new Expression with the character's id as a variable
                char shiftedChar = (char) Math.round(new Expression(eq, (double) c).getAnswer());
                //add the shifted character to the output
                output += shiftedChar;
            } else {
                output += c;
            }
        }
        //return the output
        return output;
    }
}
