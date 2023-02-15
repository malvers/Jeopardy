import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class AutoSelectingTextField extends JTextField implements FocusListener {

    private String initialText;

    AutoSelectingTextField(String text) {
        super(text);
        this.initialText = text;
        addFocusListener(this);
    }

    public void focusLost(FocusEvent fe) {
        select(0, 0);
    }

    public void focusGained(FocusEvent fe) {
        selectAll();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = f.getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(new JLabel("Click on one text field then the other to focus and autoselect:"));
        contentPane.add(new AutoSelectingTextField("Enter your text here"));
        contentPane.add(new AutoSelectingTextField("Enter your text here"));
        f.pack();
        f.setVisible(true);
    }
}