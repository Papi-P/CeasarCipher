package guiComponents;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
    @author Daniel Allen
    7-Oct-2019
*/
public class ButtonPanel extends JPanel {

    public InputField equationField = new InputField(150, 25, "Equation");
    public InputCheckbox reverseCode = new InputCheckbox(80, 40);

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.decode("#38A1F3").darker());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    public ButtonPanel() {
        this.setLayout(gbl);
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
        reverseLabel.setForeground(Color.WHITE);
        toggleContainer.setLayout(new FlowLayout());
        toggleContainer.add(reverseLabel);
        toggleContainer.add(reverseCode);

        JPanel eqContainer = new JPanel();
        eqContainer.setOpaque(false);
        JLabel eqLabel = new JLabel("?");
        eqLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane jop = new JOptionPane();
                jop.setMessage(
                  "-Use a number (-25 <= n <= 25) to encrypt or decrypt\n"
                + "-Use an equation.\n"
                + "    -Use 'x' for a variable, i.e to shift by 3 you could use \"x+3\"\n"
                + "    *Use parentheses and functional operators (*/+-^). Using something such as 2x or 2^3^4^5 will not work as expected.");
                jop.setMessageType(JOptionPane.INFORMATION_MESSAGE);
                jop.createDialog("Help").setVisible(true);
            }

        });
        eqLabel.setForeground(Color.WHITE);
        eqContainer.setLayout(new FlowLayout());
        eqContainer.add(equationField);
        eqContainer.add(eqLabel);

        //gbl.rowWeights = new double[]{0.5, 0,5};
        gbc.gridx = 0;
        gbc.gridy = 0;

        this.add(eqContainer, gbc);
        gbc.gridy++;
        this.add(toggleContainer, gbc);
    }
}