/*
 * © 2019 Daniel Allen
 */
package guiComponents;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import static cipher.Dictionary.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.*;

/**
 *
 * @author Daniel Allen
 */
public class GUI extends JFrame {

    protected static boolean overlayEnabled = false;
    private GridBagLayout gbl = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();

    public TextPanel textP = new TextPanel();
    public SettingsPanel settings = new SettingsPanel();

    public GUI() {
        this.setLayout(gbl);
        gbl.columnWeights = new double[]{0.622, 0.288};
        gbl.rowWeights = new double[]{1};
        gbl.columnWidths = new int[]{622, 288};

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(textP, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;

        this.add(settings, gbc);
        this.setSize(900, 658);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                if(JOptionPane.showConfirmDialog(null, "Are you sure you want to close?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    e.getWindow().dispose();
                    System.out.println("Saving dictionary...");
                    updateFileWithProbabilities(new File("src\\Training.txt"), getProbabilityMap().entrySet());
                    System.out.println("Dictionary saved");
                    System.exit(0);
                } else {

                }
            }

        });
    }
}




