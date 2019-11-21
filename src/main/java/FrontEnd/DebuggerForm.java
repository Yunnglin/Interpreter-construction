package FrontEnd;

import FrontEnd.parts.JTextFieldHintListener;
import FrontEnd.parts.conf.MColor;
import FrontEnd.parts.conf.MFont;
import interpreter.Interpreter;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;
import message.Message;
import message.MessageListener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Map;

public class DebuggerForm {
    private JPanel debugerPane;
    private JTextField inputTextField;
    private JTextArea outTextArea;
    private JFrame frame;
    private MainWindow mainWindow;
    //private ArrayList<SymTblEntry> symTblEntries;
    private Interpreter interpreter;
    private String PLACE_HOLDER = "Please Input NAME of the Variable and PRESS ENTER";
    private String seprator = ";  ";

    public DebuggerForm(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void init() {
        inputTextField.addFocusListener(new JTextFieldHintListener(PLACE_HOLDER, inputTextField));
        inputTextField.addKeyListener(new DebuggerMessageListener());
        inputTextField.setFont(MFont.variableFont);
        inputTextField.setBackground(MColor.variableInputColor);
        outTextArea.setFont(MFont.lineNumFont);
        outTextArea.setBackground(MColor.consoleAreaColor);
        outTextArea.setEditable(false);
        this.interpreter = mainWindow.getmButton().getInterpreter();
        this.interpreter.addMessageListener(new DebuggerMessageListener());
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Variables");
            frame.setContentPane(this.debugerPane);
            frame.setAlwaysOnTop(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setSize(650, 400);
            frame.setVisible(true);
        });
    }

    public void close() {
        this.frame.dispose();
    }

    private void findEntries() {
        //symTblEntries = new ArrayList<>();
        outTextArea.setText("");
        if (inputTextField.getText().equals(PLACE_HOLDER) || inputTextField.getText().equals("")) {
            return;
        }
        String[] strings = inputTextField.getText().trim().split(";");
        for (String s : strings) {
            SymTblEntry entry = this.interpreter.findSymTblEntry(s.trim());
            if (entry != null) {
                appendLine(entryToString(entry));
            } else {
                appendLine("Cannot Find Variable: " + s);
            }
        }
    }

    private String entryToString(SymTblEntry symTblEntry) {
        DataType type = (DataType) symTblEntry.getValue(SymTbl.SymTblKey.TYPE);
        Object value = symTblEntry.getValue(SymTbl.SymTblKey.VALUE);
        String name = symTblEntry.getName();
        StringBuilder builder = new StringBuilder();
        builder.append("Name: " + name + seprator);
        if (type.getBasicType().equals(BasicType.INT)) {
            if (type.getForm().equals(TypeForm.SCALAR)) {
                int curValue = (int) value;
                builder.append("Type: Int" + seprator);
                builder.append("Value: " + curValue);
            } else {
                int size = (int) symTblEntry.getValue(SymTbl.SymTblKey.ARRAY_SIZE);
                Object[] curValue = (Object[]) value;
                builder.append("Type: Int Array" + seprator);
                builder.append("Length: " + size + seprator);
                builder.append("Values: ");
                for (Object cur : curValue) {
                    builder.append(cur == null ? 0 : (int) cur).append(seprator);
                }
            }
        } else if (type.getBasicType().equals(BasicType.REAL)) {
            if (type.getForm().equals(TypeForm.SCALAR)) {
                double curValue = (double) value;
                builder.append("Type: Real" + seprator);
                builder.append("Value: " + curValue);
            } else {
                int size = (int) symTblEntry.getValue(SymTbl.SymTblKey.ARRAY_SIZE);
                Object[] curValue = (Object[]) value;
                builder.append("Type: Real Array" + seprator);
                builder.append("Length: " + size + seprator);
                builder.append("Values: ");
                for (Object cur : curValue) {
                    builder.append(cur == null ? 0 : (double) cur).append(seprator);
                }
            }
        }
        return builder.toString();
    }


    private void appendLine(String s) {
        outTextArea.setText(outTextArea.getText() + s + '\n');
    }

    private class DebuggerMessageListener implements MessageListener, KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            char key = e.getKeyChar();
            if (key == '\n' && inputTextField.hasFocus()) {
                findEntries();
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
            Message.MessageType type = message.getType();
            if (type == Message.MessageType.SUSPEND_ON_TRAP) {
                findEntries();
            }
        }
    }
}
