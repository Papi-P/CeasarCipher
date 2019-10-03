/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caesarcipher;

import static caesarcipher.Decrypt_Simple.*;
import static caesarcipher.Dictionary.*;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Encrypts and decrypts messages, and determines the best and most likely
 * result.
 *
 * @author Daniel Allen
 */
public class CaesarCipher {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Expression exp = new Expression("15+3");
        //System.out.println(exp.getAnswer());
        System.exit(0);
        ExecutorService es = Executors.newCachedThreadPool();
        Future<TreeMap<String, Integer>> f = es.submit(new Callable() {
            @Override
            public TreeMap<String, Integer> call() throws Exception {
                TreeMap<String, Integer> reading = readWords(new File("src\\Training.txt"));
                return reading;
            }
        });
        try {
            probabilities = f.get();
        } catch (InterruptedException | ExecutionException ex) {

        }
        Scanner scan = new Scanner(System.in);
        System.out.print("String: ");
        String sToCrypt = scan.nextLine();
        System.out.println("Shift. Use 0 to save words to the dictionary, +# to encrypt, or -# to decrypt.");
        int shift = 0;
        boolean valid = false;
        while (!valid) {
            String input = scan.nextLine();
            if (input.equalsIgnoreCase("reload")) {
                f = es.submit(new Callable() {
                    @Override
                    public TreeMap<String, Integer> call() throws Exception {
                        TreeMap<String, Integer> reading = reloadDictionary(new URL("http://m.uploadedit.com/bbtc/1569095384146.txt"));
                        return reading;
                    }
                });
                try {
                    probabilities = f.get();
                    valid = true;
                } catch (InterruptedException | ExecutionException ex) {

                }
            } else if (input.equalsIgnoreCase("clear")) {
                probabilities.clear();
                valid = true;
                updateFileWithProbabilities(new File("src\\Training.txt"), probabilities.entrySet());
                es.shutdown();
                System.exit(0);
            } else {
                try {
                    shift = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    valid = false;
                }
            }
        }

        if (shift == 0) {
            trainDictionary(sToCrypt);
            updateFileWithProbabilities(new File("src\\Training.txt"), probabilities.entrySet());
            es.shutdown();
            System.exit(0);
        }
        String encrypted = shiftCharacter(sToCrypt, shift);
        System.out.println("Encrypted: " + encrypted);
        SortedSet<Map.Entry<String, Integer>> likelyStrings = determineBestOption(breakCode(encrypted));
        String outputString = "";
        Iterator<Entry<String, Integer>> it = likelyStrings.iterator();
        StringBuilder sb = new StringBuilder("");
        for (int curPos = likelyStrings.size() - 1; curPos >= 0; curPos--) {
            Entry<String, Integer> next = it.next();
            sb.append(next.getKey()).append(": ").append(curPos);
            if (curPos == likelyStrings.size() - 1) {
                outputString = next.getKey();
            }
            if (curPos > 0) {
                sb.append("\n");
            }
        }
        System.out.println("-------------------------------------------------------");
        System.out.println("Most likely:\n" + outputString);
        long initTime = System.currentTimeMillis();
        updateFileWithProbabilities(new File("src\\Training.txt"), probabilities.entrySet());
        System.out.println(System.currentTimeMillis() - initTime);
        es.shutdown();
    }

    /**
     *
     * @param s
     * @param shift
     * @return
     */
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

    /**
     *
     * @param s
     * @param shift
     * @return
     */
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

    
}