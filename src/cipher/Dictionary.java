/*
 * © 2019 Daniel Allen
 */
package cipher;

import static cipher.Driver.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import javax.swing.JOptionPane;

/**
 *
 * @author Daniel Allen
 */
public class Dictionary {

    //Stores a dictionary. The stored key is a word, and the associated value is the likelyhood of that word being used.
    public static TreeMap<String, Integer> probabilities = new TreeMap<>();

    public static TreeMap<String, Integer> getProbabilityMap() {
        return new TreeMap<>(probabilities);
    }

    /**
     * Obtains a new dictionary from
     * <a href="http://m.uploadedit.com/bbtc/1569095384146.txt">
     * http://m.uploadedit.com/bbtc/1569095384146.txt</a> to reset the local
     * file.
     *
     * @param url The URL to read from
     * @return TreeMap of each line downloaded from the URL
     * @throws IOException
     */
    public static TreeMap<String, Integer> reloadDictionary(URL url) throws IOException {
        //open a reader from the specified url
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        //add each line obtained from the url stream to a TreeMap with a default probability of 1
        String line;
        TreeMap<String, Integer> words = new TreeMap<>();
        while ((line = br.readLine()) != null) {
            words.putIfAbsent(line.toUpperCase(), 1);
        }
        //return the TreeMap filled with the dictionary
        return words;
    }

    /**
     * Trains the dictionary by adding a word to the dictionary if it isn't
     * there, and adding 1 to its probability if it is.
     *
     * @param words
     */
    public static void trainDictionary(String words) {
        //remove extra whitespaces and split the words, then loop through them
        for (String w : words.trim().replaceAll("\\s\\s", "\\s").split("\\s")) {
            //ignore the text if it's empty
            if (w.isEmpty()) {
                continue;
            }
            //convert the string to uppercase
            String s = w.toUpperCase();

            //if the dictionary contains the word, add 1 to the probability. If it doesn't, add the word and then set the probability to 1.
            if (probabilities.containsKey(s)) {
                probabilities.replace(s, probabilities.get(s) + 1);
            } else {
                probabilities.put(s, 1);
            }
        }
    }

    /**
     * Sends the list provided to the file specified.
     *
     * @param f The file to write to
     * @param set The list to write
     * @return if the operation was successful.
     */
    public static boolean updateFileWithProbabilities(File f, Set<Entry<String, Integer>> set) {
        //sort entries first by probability, and then by name.
        SortedSet sorted = new TreeSet(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int intCompare = o2.getValue().compareTo(o1.getValue());
                if (intCompare == 0) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return intCompare;
            }

        });
        sorted.addAll(set);
        //make any directories or files needed to write to prevent errors
        f.getParentFile().mkdirs();
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                return false;
            }
        }
        try {
            //create a filewriter and iterator
            FileWriter fw = new FileWriter(f);
            Iterator<Map.Entry<String, Integer>> it = sorted.iterator();
            while (it.hasNext()) {
                //loop all elements in the iterator and write them to the file
                Map.Entry<String, Integer> pair = it.next();
                fw.write(pair.getKey().toUpperCase() + "\t" + pair.getValue() + "\n");
            }
            //close the writer
            fw.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    /**
     * Obtains the dictionary from the local file
     *
     * @param f The file to read from
     * @return TreeMap of each line loaded from file
     * @throws IOException
     * @throws InterruptedException
     */
    public static TreeMap<String, Integer> readWords(File f) throws IOException, InterruptedException {
        //make sure the file and directory exist to prevent a FileNotFoundException
        f.getParentFile().mkdirs();
        if (!f.exists()) {
            f.createNewFile();
            JOptionPane alerter = new JOptionPane();
            //inform the user that the dictionary has been created, but it is empty.
            alerter.setMessage("Location not found. An empty file has been created at:\n" + f.getAbsolutePath());
            alerter.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            alerter.createDialog("Information").setVisible(true);
        }
        //create a file reader
        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedReader read = new BufferedReader(new InputStreamReader(bis));

        //read from readers
        Stream<String> lines = read.lines();
        TreeMap<String, Integer> words = new TreeMap<>();
        Iterator<String> it = lines.iterator();

        //loop through each line from the Iterator
        while (it.hasNext()) {

            //split the line using whitespace as the delimiter
            String[] line = it.next().trim().toUpperCase().split("\\s");

            //set the word to the first token in the array
            String word = line[0];

            //set the probability of the word to the second token, or 1 if it is absent.
            int probability = 1;
            if (line.length > 1) {
                try {
                    probability = Integer.parseInt(line[1]);
                } catch (NumberFormatException e) {
                    probability = 1;
                }
            }
            //add the word and probability to the TreeMap
            words.putIfAbsent(word, probability);
        }
        //close all readers and streams
        bis.close();
        fis.close();
        read.close();
        lines.close();

        //return the TreeMap
        return words;
    }

    /**
     * Sets the probabilities to the default dictionary from a github file
     */
    public static void newDictionary() {
        f = es.submit(new Callable() {
            @Override
            public TreeMap<String, Integer> call() throws Exception {
                TreeMap<String, Integer> reading = reloadDictionary(new URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt"));
                return reading;
            }
        });
        try {
            probabilities = (TreeMap<String, Integer>) f.get();
            updateFileWithProbabilities(new File("src\\Training.txt"), probabilities.entrySet());
        } catch (InterruptedException | ExecutionException ex) {
            gui.textP.outputArea.setText("Error: " + ex.getMessage());
        }
    }

    /**
     * Erases all words from the dictionary
     */
    public static void clearDictionary() {
        probabilities.clear();
        updateFileWithProbabilities(new File("src\\Training.txt"), probabilities.entrySet());
    }
}
