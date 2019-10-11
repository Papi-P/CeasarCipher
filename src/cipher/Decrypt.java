/*
 * © 2019 Daniel Allen
 */
package cipher;

import static cipher.Dictionary.probabilities;
import static cipher.Dictionary.trainDictionary;
import static cipher.Driver.gui;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/*
    @author Daniel Allen
    4-Oct-2019
 */
public class Decrypt {

    /**
     * Decodes a String based on a shift provided. It is strongly recommended to
     * use {@link cipher.Encrypt#shiftCharacter(String, int)} instead.
     *
     * @deprecated
     * @param s String to decrypt
     * @param shift Character shift to use
     *
     * @see cipher.Encrypt#shiftCharacter(String, int)
     *
     * @return Decoded String
     */
    public static String decrypt(String s, int shift) {
        //validate input string and throw a new IllegalArgumentException if its invalid
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Attemped to encrypt an empty string!");
        }
        //validate the shift and throw a new IllegalArgumentException if its invalid
        if (shift > 25 || shift < 0) {
            throw new IllegalArgumentException("Attemped to shift out of bounds!");
        }
        String output = "";
        for (char c : s.toCharArray()) {
            //make sure the character is not a space or other punctuation
            if (Character.getType(c) != Character.OTHER_PUNCTUATION && Character.getType(c) != Character.SPACE_SEPARATOR) {
                //shift the character
                char shiftedChar = (char) (c - shift);

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
                output += shiftedChar;
            } else {
                output += c;
            }
        }
        return output;
    }

    /**
     * Attempts to decode the input field. This only works for shifts, not
     * complex expressions.
     *
     * @see Expression
     */
    public static void attemptDecode() {
        //get the text from the input field
        String textToDecode = gui.textP.inputArea.getText();
        SortedSet<Map.Entry<String, Integer>> likelyStrings;
        try {
            //call determineBestOption() on breakCode() on the text from the field
            likelyStrings = determineBestOption(breakCode(textToDecode));
        } catch (IllegalArgumentException e) {
            //if there is an error, display it in the output
            gui.textP.outputArea.setText("Error: " + e.getMessage());
            return;
        }
        String outputString = "";
        Iterator<Map.Entry<String, Integer>> it = likelyStrings.iterator();

        //Ghost code was made for debugging. It just prints all possible decodings and their probabilies in order. (Yes this is messy but I didn't want to create a toggle-able debugging mode or get rid of this)

        //StringBuilder sb = new StringBuilder("");
        //for (int curPos = likelyStrings.size() - 1; curPos >= 0; curPos--) {
        // Map.Entry<String, Integer> next = it.next();
        // sb.append(next.getKey()).append(": ").append(next.getValue());
        // if (curPos == likelyStrings.size() - 1) {
        //set the output to the best option
        outputString = it.next().getKey();
        //  }
        //  if (curPos > 0) {
        //      sb.append("\n");
        //   }
        // }
        // System.out.println("-------------------------------------------------------");
        // System.out.println(sb.toString());
        // System.out.println("Most likely:\n" + outputString);
        //set the output field to the best option
        gui.textP.outputArea.setText(outputString);
    }

    /**
     * Calculates all possible decode options
     *
     * @param input String to decode
     * @return String[] of each possible option
     */
    public static String[] breakCode(String input) {
        //declare an empty array with the number of possible options
        String[] output = new String[26];
        //decrypt each possible option and add it to the array
        for (int i = 0; i < 26; i++) {
            output[i] = decrypt(input, i);
        }
        //return the array
        return output;
    }

    public static SortedSet<Map.Entry<String, Integer>> determineBestOption(String[] input, String... knownWords) {
        TreeMap<String, Integer> likelyhood = new TreeMap<>();
        for (String s : input) {
            int probability = 0;
            //significantly increase the probability if a known word is found
            for (String known : knownWords) {
                if (s.toUpperCase().contains(known.toUpperCase())) {
                    probability += 10;
                }
            }
            for (String word : s.split(" ")) {
                //decrease the probability by 2 if a word has no vowels, or by 1 if the only vowel is 'y'
                if (!word.matches(".*[AEIOUaeiou].*")) {
                    if (!word.matches(".*[AEIOUYaeiouy].*")) {
                        probability -= 2;
                    } else {
                        probability -= 1;
                    }
                }
                //if the word is in the dictionary, add that word's probability to the current probability
                if (probabilities.containsKey(word.toUpperCase())) {
                    probability += probabilities.get(word.toUpperCase());
                    trainDictionary(word);
                }
            }
            //add the String to the TreeMap
            likelyhood.putIfAbsent(s, probability);
        }
        //define a new SortedSet with a comparator
        SortedSet<Map.Entry<String, Integer>> output = new TreeSet<>(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                //if the probabilities are the same, compare Strings. If not, return the comparison of the probabilties. Inverted to sort highest first, 'a' first.
                int comp = o2.getValue().compareTo(o1.getValue());
                if (comp == 0) {
                    return o2.getKey().compareTo(o1.getKey());
                } else {
                    return comp;
                }
            }
        });
        //add all entries from the TreeMap to the output and return it.
        output.addAll(likelyhood.entrySet());
        return output;
    }
}
