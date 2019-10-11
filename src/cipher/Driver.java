/*
 * © 2019 Daniel Allen
 */
package cipher;

import static cipher.Decrypt.*;
import static cipher.Dictionary.*;
import static cipher.Encrypt.*;
import guiComponents.GUI;
import java.io.File;
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
public class Driver {

    //change this variable to use either the encode() and decode() methods, or just to use the shiftCharacter() method.
    final static boolean useMultiDirectionalMethod = true;

    /**
     * Future to retrieve and store the dictionary as its being read.
     */
    static Future f;

    /**
     * Cached thread pool to call callables, and manage Future {@link cipher.Driver#f}
     * @see cipher.Driver#f
     */
    public static ExecutorService es = Executors.newCachedThreadPool();

    /**
     * GUI for the user
     */
    public static GUI gui;

    /**
     * Main method run from command line
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        //load a dictionary from a file
        f = es.submit(new Callable() {
            @Override
            public TreeMap<String, Integer> call() throws Exception {
                TreeMap<String, Integer> reading = readWords(new File("src\\Training.txt"));
                return reading;
            }
        });
        try {
            //set the TreeMap<> to the Future once its available
            probabilities = (TreeMap<String, Integer>) f.get();
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println(ex);
        }
        //create a GUI and display it
        gui = new GUI();
        gui.setVisible(true);
    }

    /**
     * Called from the GUI when the encrypt button is clicked.
     * @see guiComponents.InputButton
     */
    public static void cryptClick() {
        //get the input and equation as a string
        String textToEncode = gui.textP.inputArea.getText();
        String equation = gui.settings.equationField.getText();
        //detect if the equation is simple. If so, pass it to the shiftCharacter(), encrypt(), or decrypt() method.
        if (textToEncode.length() > 0 && (equation.matches("[0-9-]+$") || (equation.length() > 1 && !equation.substring(1).matches("[^0-9]+$") && equation.charAt(0) == '-') || equation.trim().isEmpty())) {
            if (equation.trim().isEmpty()) {
                equation = "0";
            }
            try {
                String encoded;
                //if the multi-directional method should be used, use it. If not, determine whether to use the encrypt() or decrypt() method.
                if (useMultiDirectionalMethod) {
                    encoded = shiftCharacter(textToEncode, Integer.parseInt(equation));
                } else {
                    //detect if the shift value is negative or not to determine if it should be encrypted or decrypted.
                    if (equation.charAt(0) != '-') {
                        encoded = encrypt(textToEncode, Integer.parseInt(equation));
                    } else {
                        encoded = decrypt(textToEncode, Integer.parseInt(equation.substring(1)));
                    }
                }
                //set the text to the output
                gui.textP.outputArea.setText(encoded);
            } catch (IllegalArgumentException iae) {
                gui.textP.outputArea.setText("Error: " + iae.getMessage());
            }
        } else {
            //if the equation is not simple, instead pass it to the encrypt_from_equation() method.
            try {
                String encoded = encrypt_from_equation(textToEncode, equation);
                //set the text to the output or to the error message is there is one
                gui.textP.outputArea.setText(encoded);
            } catch (MathException e) {
                gui.textP.outputArea.setText(e.getMessage() + "\n" + e.getStylizedError());
            } catch (Exception e) {
                gui.textP.outputArea.setText("Error: " + e.getMessage());
            }
        }
    }
}
