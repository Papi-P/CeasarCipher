/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caesarcipher;

import static caesarcipher.Dictionary.*;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Daniel Allen
 */
public class Decrypt_Simple {

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

    /**
     *
     * @param input
     * @param knownWords
     * @return
     */
    public static SortedSet<Map.Entry<String, Integer>> determineBestOption(String[] input, String... knownWords) {
        TreeMap<String, Integer> likelyhood = new TreeMap<>();
        for (String s : input) {
            int probability = 0;
            for (String word : s.split(" ")) {
                for (String known : knownWords) {
                    if (word.contains(known)) {
                        probability += 5;
                        break;
                    }
                }
                if (!word.matches(".*[AEIOUaeiou].*")) {
                    if (!word.matches(".*[AEIOUYaeiouy].*")) {
                        probability -= 2;
                    } else {
                        probability -= 1;
                    }
                } else {
                    probability += 2;
                    if (probabilities.containsKey(word.toUpperCase())) {
                        probability += probabilities.get(word.toUpperCase());
                        trainDictionary(word);
                    }
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

    /**
     *
     * @param s
     * @param shift
     * @return
     */
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
}
