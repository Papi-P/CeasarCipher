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

    static Future f;
    static ExecutorService es;
    public static GUI gui;

    public static void main(String[] args) {
        es = Executors.newCachedThreadPool();
        f = es.submit(new Callable() {
            @Override
            public TreeMap<String, Integer> call() throws Exception {
                TreeMap<String, Integer> reading = readWords(new File("src\\Training.txt"));
                return reading;
            }
        });
        try {
            probabilities = (TreeMap<String, Integer>) f.get();
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println(ex);
        }
        gui = new GUI();
        gui.setVisible(true);
    }

    public static void cryptClick() {
        String textToEncode = gui.textP.inputArea.getText();
        String equation = gui.settings.equationField.getText();
        if (textToEncode.length() > 0 && (equation.matches("[0-9-]+$") || (equation.length() > 1 && !equation.substring(1).matches("[^0-9]+$") && equation.charAt(0) == '-') || equation.trim().isEmpty())) {
            if (equation.trim().isEmpty()) {
                equation = "0";
            }
            try {
                String encoded = "";
                if (equation.charAt(0) != '-') {
                    encoded = encrypt(textToEncode, Integer.parseInt(equation));
                } else {
                    encoded = decrypt(textToEncode, Integer.parseInt(equation.substring(1)));
                }
                gui.textP.outputArea.setText(encoded);
            } catch (IllegalArgumentException iae) {
                gui.textP.outputArea.setText("Error: " + iae.getMessage());
            }
        } else {
            try {
                String encoded = encrypt_from_equation(textToEncode, equation);
                gui.textP.outputArea.setText(encoded);
            } catch (MathException e) {
                gui.textP.outputArea.setText(e.getMessage()+"\n"+e.getStylizedError());
            } catch (Exception e){
                gui.textP.outputArea.setText("Error: " + e.getMessage());
            }
        }
    }




}
