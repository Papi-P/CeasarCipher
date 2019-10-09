package cipher;

/*
    @author Daniel Allen
    4-Oct-2019
 */
public class Encrypt {

    public static String encrypt(String s, int shift) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Attemped to encrypt an empty string!");
        }
        if (shift > 25 || shift < 0) {
            throw new IllegalArgumentException("Attemped to shift out of bounds!");
        }
        String output = "";
        for (char c : s.toCharArray()) {
            if (Character.getType(c) != Character.OTHER_PUNCTUATION && Character.getType(c) != Character.SPACE_SEPARATOR) {
                char shiftedChar = 0;
                shiftedChar = (char) (c + shift);
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

                output += shiftedChar;
            } else {
                output += c;
            }
        }
        return output;
    }

    public static String shiftCharacter(String s, int shift) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Attemped to encrypt an empty string!");
        }
        if (shift > 25 || shift < -25) {
            throw new IllegalArgumentException("Attemped to shift out of bounds!");
        }
        String output = "";
        for (char c : s.toCharArray()) {
            if (Character.getType(c) != Character.OTHER_PUNCTUATION && Character.getType(c) != Character.SPACE_SEPARATOR) {
                char shiftedChar = (char) (c + shift);
                if (Character.isLowerCase(c) && shiftedChar < 'a') {
                    shiftedChar += 26;
                } else if (Character.isLowerCase(c) && shiftedChar > 'z') {
                    shiftedChar -= 26;
                } else if (Character.isUpperCase(c) && shiftedChar < 'A') {
                    shiftedChar += 26;
                } else if (Character.isUpperCase(c) && shiftedChar > 'Z') {
                    shiftedChar -= 26;
                }

                output += shiftedChar;
            } else {
                output += c;
            }
        }
        return output;
    }

    public static String encrypt_from_equation(String s, String eq) throws MathException {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Attemped to encrypt an empty string!");
        }
        String output = "";
        for (char c : s.toCharArray()) {
            if (Character.getType(c) != Character.OTHER_PUNCTUATION && Character.getType(c) != Character.SPACE_SEPARATOR) {
                char shiftedChar = (char) Math.round(new Expression(eq, (double) c).getAnswer());
                output += shiftedChar;
            } else {
                output += c;
            }
        }
        return output;
    }
}
