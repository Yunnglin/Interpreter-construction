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
    StringBuilder stringBuilder = new StringBuilder();

    public MKeyListener(MainWindow mainWindow){
        this.mainWindow = mainWindow;
        editPane = mainWindow.getEditPane();
        rowPane = mainWindow.getRowPane();
    }
    @Override
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        if(key =='\n'){
            System.out.println(stringBuilder.toString());
            stringBuilder = new StringBuilder();
        }
        if(key == '\b'){
            System.out.println(stringBuilder.toString());
            System.out.println(stringBuilder.length());
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            System.out.println(stringBuilder.length());
            System.out.println(stringBuilder.toString());
        }
        stringBuilder.append(key);

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
