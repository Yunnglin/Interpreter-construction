package FrontEnd;

import FrontEnd.parts.JTextFieldHintListener;
import FrontEnd.parts.conf.MColor;
import interpreter.Interpreter;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
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
    private ArrayList<SymTblEntry> symTblEntries;
    private Interpreter interpreter;

    public DebuggerForm(MainWindow mainWindow){
        this.mainWindow = mainWindow;
    }

    public void init() {
        inputTextField.addFocusListener(new JTextFieldHintListener("请输入变量名,按回车键查看", inputTextField));
        inputTextField.addKeyListener(new DebuggerMessageListener());
        outTextArea.setEditable(false);
        this.interpreter = mainWindow.getmButton().getInterpreter();
        this.interpreter.addMessageListener(new DebuggerMessageListener());
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

    private void findEntries(){
        //symTblEntries = new ArrayList<>();
        String[] strings = inputTextField.getText().trim().split(";");
        for(String s : strings){
            SymTblEntry entry =this.interpreter.findSymTblEntry(s.trim());
            if(entry!=null){
                appendLine(entryToString(entry));
            }else{
                appendLine("未找到变量: "+s);
            }
        }
    }

    private String entryToString(SymTblEntry symTblEntry){
        DataType type = (DataType) symTblEntry.getValue(SymTbl.SymTblKey.TYPE);
        Object valie = symTblEntry.getValue(SymTbl.SymTblKey.VALUE);
        String name = symTblEntry.getName();
        StringBuilder builder = new StringBuilder();
//        switch (type.getBasicType()){
//            case BasicType.INT:{
//                break;
//            }
//        }
        return "";
    }


    private void appendLine(String s){
        outTextArea.setText(outTextArea.getText()+s+'\n');
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
