/*
 * © 2019 Daniel Allen
 */
package guiComponents;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * JPanel to hold the equation and other options
 *
 * @author Daniel
 */
public class SettingsPanel extends JPanel {

    /**
     * Generates a random equation when clicked and puts it in the equationField
     *
     * @see guiComponents.SettingsPanel#equationField
     */
    public InputButton randomizeEquationButton = new InputButton() {
        @Override
        public void onClick() {
            //generate the number of terms
            int terms = (int) Math.floor((Math.random() * 4) + 1);
            System.out.println("Terms: " + terms);
            String output = "";
            for (int i = terms; i >= 0; i--) {
                //for each term, generate a coefficient.
                int coefficient = (int) (Math.round(Math.random() * 10));
                //if the coefficient is not 0
                if (coefficient != 0) {
                    output += (coefficient);
                    switch (i) {
                        case 0:
                            //if i is 0, only add the coefficient
                            break;
                        case 1:
                            //if i is 1, don't add any exponent. Add only the coefficient and 'x'
                            output += 'x';
                            break;
                        default:
                            //otherwise, add the coefficient, 'x^', and i (the current exponent)
                            output += "x^" + i;
                            break;
                    }
                    //if i is greater than 0, add either a + or - to the output, randomly.
                    if (i > 0) {
                        switch ((int) (Math.random() * 2)) {
                            case 0:
                                output += "+";
                                break;
                            case 1:
                                output += "-";
                                break;
                        }
                    }
                }
            }
            //if the output is empty (all coefficients were 0), rerun the method to try again.
            if (output.trim().isEmpty()) {
                onClick();
            }
            //if the final character is not a number or 'x', remove the last character from the output
            if (!Character.isDigit(output.charAt(output.length() - 1)) && output.charAt(output.length() - 1) != 'x') {
                output = output.substring(0, output.length() - 1);
            }
            //set the equationField to hold the output
            equationField.setText(output);
        }
    };

    /**
     * Holds a custom equation to be used in an Expression when encoded
     *
     * @see cipher.Encrypt#encrypt_from_equation(String, String)
     * @see cipher.Expression
     */
    public InputField equationField = new InputField(150, 25, "Equation");

    /**
     * Checkbox that controls if the equation is reversed.<br>
     * <b>This is locked to 'disabled' and cannot be changed.</b>
     *
     * @see cipher.Expression#inverse()
     */
    public InputCheckbox reverseCode = new InputCheckbox(80, 40);

    /**
     * Checkbox that controls whether to maintain character within their
     * origin's type. <br>
     * i.e.: <code>'Z' + 1 = 'A'</code> vs <code>'Z' + 1 = '['</code>
     */
    public InputCheckbox maintainCharacters = new InputCheckbox(80, 40);

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.decode("#38A1F3"));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    /**
     * Default constructor
     */
    public SettingsPanel() {
        this.setLayout(gbl);

        randomizeEquationButton.setText("Randomize Equation");
        randomizeEquationButton.setCurve(20)
                .setBg(Color.decode("#38A1F3"))
                .setFg(Color.BLACK)
                .setAntialiased(true)
                .setHoverColor(Color.decode("#38A1F3").darker());

        equationField.setRegex("[0-9.?x+-/*^()]+$")
                .setPlaceholder("Equation")
                .setPadding(0, 5, 0, 0)
                .setCurve(25)
                .setDisabledColor(new Color(120, 120, 120))
                .setScrollingPlaceholder(false)
                .setAntialiased(true);

        reverseCode.setAntialiased(true)
                .setCurve(30)
                .setSwitch(true)
                .setAllowRapidUse(true)
                .setSwitch(true)
                .setLock(true, true);

        maintainCharacters.setAntialiased(true)
                .setCurve(30)
                .setSwitch(true)
                .setAllowRapidUse(true)
                .setSwitch(true);

        JPanel toggleContainer = new JPanel();
        toggleContainer.setOpaque(false);
        JLabel reverseLabel = new JLabel("Reverse Equation?");
        reverseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane jop = new JOptionPane();
                jop.setMessage("<html>*Unsupported Operation. Currently does nothing.<br>i.e:<br>y = x^2 &mdash;> y = sqrt(x)</html>");
                jop.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                jop.createDialog("Help").setVisible(true);
            }

        });
        reverseLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        reverseLabel.setForeground(Color.WHITE);
        toggleContainer.setLayout(new FlowLayout());
        toggleContainer.add(reverseLabel);
        toggleContainer.add(reverseCode);

        JPanel eqContainer = new JPanel();
        eqContainer.setOpaque(false);
        JLabel eqLabel = new JLabel("?");
        eqLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eqLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane jop = new JOptionPane();
                jop.setMessage(
                        "-Use a number (-25 <= n <= 25) to encrypt or decrypt\n"
                        + "-Use an equation.\n"
                        + "    -Use 'x' for a variable, i.e to shift by 3 you could use \"x+3\"\n"
                        + "    *Use parentheses and functional operators (*/+-^).\n"
                        + "         Using something such as 2x or 2^3^4^5 will not work as expected.\n"
                        + "    *For root functions, use the inverse power instead.\n"
                        + "         i.e instead of sqrt(x) use x^(1/2), or instead of cbrt(x) use x^(1/3).");
                jop.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                jop.createDialog("Help").setVisible(true);
            }
        });
        eqLabel.setForeground(Color.WHITE);
        eqContainer.setLayout(new FlowLayout());
        eqContainer.add(equationField);
        eqContainer.add(eqLabel);

        JPanel keepContainer = new JPanel();
        keepContainer.setOpaque(false);
        JLabel keepLabel = new JLabel("Maintain Characters?");
        keepLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        keepLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane jop = new JOptionPane();
                jop.setMessage(
                        "Keeps characters within a range. If this is selected, \n"
                        + "    shifting 'A' left 1 will return 'Z'\n"
                        + "    shifting 'Z' right 2 will return 'B'\n"
                        + "    shifting 'a' left 2 will return 'y'\n"
                        + "    shifting 'z' right 1 will return 'a'");
                jop.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                jop.createDialog("Help").setVisible(true);
            }
        });
        keepLabel.setForeground(Color.WHITE);
        keepContainer.setLayout(new FlowLayout());
        keepContainer.add(keepLabel);
        keepContainer.add(maintainCharacters);

        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(randomizeEquationButton);
        gbc.gridy++;
        this.add(eqContainer, gbc);
        gbc.gridy++;
        this.add(toggleContainer, gbc);
        gbc.gridy++;
        this.add(keepContainer, gbc);
    }
}
