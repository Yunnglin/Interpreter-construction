package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.Key;

public class MKeyListener implements KeyListener {
    private JTextPane editPane;
    private JTextPane rowPane;
    private MainWindow mainWindow;

    public MKeyListener(MainWindow mainWindow){
        this.mainWindow = mainWindow;
        editPane = mainWindow.getEditPane();
        rowPane = mainWindow.getRowPane();
    }
    @Override
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();//��ǰ����ֵ
        if(key == '\n' || key == '\b'){
//            mainWindow.getmScrollPane().updateLineNum();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
