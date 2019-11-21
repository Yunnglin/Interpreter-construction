package FrontEnd;

import FrontEnd.parts.JTextFieldHintListener;
import FrontEnd.parts.conf.MColor;
import message.Message;
import message.MessageListener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DebuggerForm {
    private JPanel debugerPane;
    private JTextField inputTextField;
    private JTextArea outTextArea;
    private JFrame frame;

    public void init() {
        inputTextField.addFocusListener(new JTextFieldHintListener("请输入变量名,按回车键查看", inputTextField));
        inputTextField.addKeyListener(new DebuggerMessageListener());
        outTextArea.setEditable(false);
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Variables");
            frame.setContentPane(this.debugerPane);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setSize(500, 300);
            frame.setVisible(true);
        });
    }
    public void close(){
        this.frame.dispose();
    }

    private class DebuggerMessageListener implements MessageListener, KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            char key = e.getKeyChar();
            if (key == '\n' && inputTextField.hasFocus()) {

            }
        }


        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void onMessageReceived(Message message) {

        }
    }
}
