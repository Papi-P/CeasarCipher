/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caesarcipher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JOptionPane;

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
            valid = true;
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
                } catch (InterruptedException | ExecutionException ex) {

                }
            } else {
                try {
                    shift = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    valid = false;
                }
            }
        }

        if (shift == 0) {
            trainNetwork(sToCrypt);
            updateFileWithProbabilities(new File("src\\Training.txt"), probabilities);
            System.exit(0);
        }
        String encrypted = encrypt(sToCrypt, shift);
        System.out.println(encrypted);
        //System.out.println("------------------------------------------");
        String[] broken = breakCode(encrypted);
        for (String s : broken) {
            //System.out.println(s);
        }
        SortedSet<Map.Entry<String, Integer>> likelyStrings = determineBestOption(broken);
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
        updateFileWithProbabilities(new File("src\\Training.txt"), probabilities);
        System.out.println(System.currentTimeMillis() - initTime);
        es.shutdown();
    }

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

    public static String[] breakCode(String input) {
        String[] output = new String[26];
        for (int i = 0; i < 26; i++) {
            output[i] = decrypt(input, i);
        }
        return output;
    }

    static TreeMap<String, Integer> probabilities;

    //Dictionary stored at http://m.uploadedit.com/bbtc/1569095384146.txt
    public static TreeMap<String, Integer> reloadDictionary(URL url) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        TreeMap<String, Integer> words = new TreeMap<>();
        while ((line = br.readLine()) != null) {
            words.putIfAbsent(line, 1);
        }

        return words;
    }

    public static TreeMap<String, Integer> readWords(File f) throws IOException, InterruptedException {
        //make sure the file and directory exist to prevent a FileNotFoundException
        f.getParentFile().mkdirs();
        if (!f.exists()) {
            f.createNewFile();
            JOptionPane alerter = new JOptionPane();
            alerter.setMessage("Location not found. An empty file has been created at " + f.getAbsolutePath() + "<br>Use a shift of 0 to define new words, or type \"Reload\" to attempt to reload the file with a dictionary.");
            alerter.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            alerter.createDialog("Information").setVisible(true);
        }
        //Thread.sleep(100);
        //create a file reader
        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedReader read = new BufferedReader(new InputStreamReader(bis));
        Stream<String> lines = read.lines();
        TreeMap<String, Integer> words = new TreeMap<>();
        Iterator<String> it = lines.iterator();
        //int curPos = 0;
        while (it.hasNext()) {
            String[] line = it.next().toUpperCase().split("\\s");
            //System.out.println(line);
            String word = line[0];
            int probability = 1;
            if (line.length > 1) {
                try {
                    probability = Integer.parseInt(line[1]);
                } catch (NumberFormatException e) {
                    probability = 1;
                }
            }
            //System.out.println(word);
            //Integer probability = curPos;
            words.putIfAbsent(word, probability);
            //curPos++;
        }
        return words;
    }

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
                        trainNetwork(word);
                    }
                }
            }
            likelyhood.putIfAbsent(s, probability);
            //System.out.println(s+": "+probability);
        }
        SortedSet<Map.Entry<String, Integer>> output = new TreeSet<>(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
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

    public static void trainNetwork(String words) {
        for (String w : words.toUpperCase().trim().replaceAll("\\s\\s", "\\s").split("\\s")) {
            if (w.isEmpty()) {
                continue;
            }
            String s = w.toUpperCase();
            if (probabilities.containsKey(s)) {
                probabilities.replace(s, probabilities.get(s) + 1);
            } else {
                probabilities.put(s, 1);
            }
        }
    }

    public static boolean updateFileWithProbabilities(File f, TreeMap<String, Integer> map) {
        //make any directories or files needed to write
        f.getParentFile().mkdirs();
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return false;
            }
        }
        try {
            FileWriter fw = new FileWriter(f);
            //BufferedWriter bw = new BufferedWriter(fw);
            Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Integer> pair = it.next();
                fw.write(pair.getKey() + "\t" + pair.getValue() + "\n");
            }
            fw.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

}
