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
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/*
    @author Daniel Allen
    4-Oct-2019
 */
public class Decrypt {

    public static String decrypt(String s, int shift) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Attemped to encrypt an empty string!");
        }
        if (shift > 25 || shift < 0) {
            throw new IllegalArgumentException("Attemped to shift out of bounds!");
        }
        String output = "";
        for (char c : s.toCharArray()) {
            if (Character.getType(c) != Character.OTHER_PUNCTUATION && Character.getType(c) != Character.SPACE_SEPARATOR) {
                char shiftedChar = (char) (c - shift);
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

    public static void attemptDecode() {
        String textToDecode = gui.textP.inputArea.getText();
        SortedSet<Map.Entry<String, Integer>> likelyStrings;
        try {
            likelyStrings = determineBestOption(breakCode(textToDecode));
        } catch (IllegalArgumentException e) {
            gui.textP.outputArea.setText("Error: " + e.getMessage());
            return;
        }
        String outputString = "";
        Iterator<Map.Entry<String, Integer>> it = likelyStrings.iterator();
        StringBuilder sb = new StringBuilder("");
        //for (int curPos = likelyStrings.size() - 1; curPos >= 0; curPos--) {
           // Map.Entry<String, Integer> next = it.next();
           // sb.append(next.getKey()).append(": ").append(next.getValue());
           // if (curPos == likelyStrings.size() - 1) {
               outputString = it.next().getKey();
          //  }
          //  if (curPos > 0) {
          //      sb.append("\n");
         //   }
       // }
        // System.out.println("-------------------------------------------------------");
       // System.out.println(sb.toString());
        // System.out.println("Most likely:\n" + outputString);
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

            likelyhood.putIfAbsent(s, probability);
        }
        SortedSet<Map.Entry<String, Integer>> output = new TreeSet<>(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int comp = o2.getValue().compareTo(o1.getValue());
                if (comp == 0) {
                    return o2.getKey().compareTo(o1.getKey());
                } else {
                    return comp;
                }
            }
        });
        output.addAll(likelyhood.entrySet());
        return output;
    }
}
