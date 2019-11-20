package FrontEnd;

import javax.swing.*;

public class DebuggerForm {
    private JPanel debugerPane;
    private JTextField inputTextField;
    private JTextArea outTextArea;

    public void init(){
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Variables");
            frame.setContentPane(this.debugerPane);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setSize(400, 300);
            frame.setVisible(true);
        });
    }
}
