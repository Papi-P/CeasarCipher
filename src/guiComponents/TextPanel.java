/*
 * © 2019 Daniel Allen
 */
package guiComponents;

import static cipher.Decrypt.*;
import static cipher.Dictionary.*;
import static cipher.Driver.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
    @author Daniel Allen
    7-Oct-2019
*/
public class TextPanel extends JPanel {

    public JTextArea inputArea = new JTextArea("Input");
    public JScrollPane inputScroller = new JScrollPane(inputArea);
    public JTextArea outputArea = new JTextArea("Output");
    public JScrollPane outputScroller = new JScrollPane(outputArea);
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    public InputButton cryptButton = new InputButton() {
        @Override
        public void onClick() {
            cryptClick();
        }
    };
    public InputButton autoDecodeButton = new InputButton() {
        @Override
        public void onClick() {
            attemptDecode();
        }
    };
    public InputButton getNewDictionaryButton = new InputButton() {
        @Override
        public void onClick() {
            newDictionary();
        }
    };
    public InputButton eraseDictionaryButton = new InputButton() {
        @Override
        public void onClick() {
            clearDictionary();
        }
    };
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(51, 51, 51));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);
        g.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight());
    }

    public TextPanel() {
        this.setSize(622, 622);
        Dimension fieldSize = new Dimension(500, 250);
        inputArea.setLineWrap(true);
        inputScroller.setPreferredSize(fieldSize);
        inputScroller.setSize(fieldSize);
        inputScroller.setMinimumSize(fieldSize);
        inputScroller.setMaximumSize(fieldSize);

        outputArea.setLineWrap(true);
        outputArea.setEditable(false);
        outputScroller.setPreferredSize(fieldSize);
        outputScroller.setSize(fieldSize);
        outputScroller.setMinimumSize(fieldSize);
        outputScroller.setMaximumSize(fieldSize);

        cryptButton.setText("Encrypt");
        cryptButton.setCurve(20)
                .setBg(Color.decode("#38A1F3"))
                .setFg(Color.WHITE)
                .setAntialiased(true)
                .setHoverColor(Color.decode("#38A1F3").darker());

        autoDecodeButton.setText("Auto-decode");
        autoDecodeButton.setCurve(20)
                .setBg(Color.decode("#38A1F3"))
                .setFg(Color.WHITE)
                .setAntialiased(true)
                .setHoverColor(Color.decode("#38A1F3").darker());

        getNewDictionaryButton.setText("Reset Dictionary");
        getNewDictionaryButton.setCurve(20)
                .setBg(Color.decode("#38A1F3"))
                .setFg(Color.WHITE)
                .setAntialiased(true)
                .setHoverColor(Color.decode("#38A1F3").darker());

        eraseDictionaryButton.setText("Erase Dictionary");
        eraseDictionaryButton.setCurve(20)
                .setBg(Color.decode("#38A1F3"))
                .setFg(Color.WHITE)
                .setAntialiased(true)
                .setHoverColor(Color.decode("#38A1F3").darker());

        this.setLayout(gbl);
        JPanel container = new JPanel();
        container.add(cryptButton);
        container.add(autoDecodeButton);
        container.add(getNewDictionaryButton);
        container.add(eraseDictionaryButton);
        container.setOpaque(false);
        gbc.gridy = 0;
        this.add(inputScroller, gbc);
        gbc.gridy++;
        this.add(container, gbc);
        gbc.gridy++;
        this.add(outputScroller, gbc);
    }
}