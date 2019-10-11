/*
 * © 2019 Daniel Allen
 */
package guiComponents;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import static cipher.Dictionary.*;
import cipher.Driver;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.*;

/**
 *
 * @author Daniel Allen
 */
public class GUI extends JFrame {
    private GridBagLayout gbl = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();

    /**
     * TextPanel to contain the input area
     * @see guiComponents.TextPanel
     */
    public TextPanel textP = new TextPanel();

    /**
     * SettingsPanel to contain the settings
     * @see guiComponents.SettingsPanel
     */
    public SettingsPanel settings = new SettingsPanel();

    /**
     * Default constructor
     */
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

                    System.out.println("Saving dictionary...");
                    updateFileWithProbabilities(new File("src\\Training.txt"), getProbabilityMap().entrySet());
                    System.out.println("Dictionary saved");
                    Driver.es.shutdown();
                    System.exit(0);
                } else {

                }
            }

        });
    }
}




