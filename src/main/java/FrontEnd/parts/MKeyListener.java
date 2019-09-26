package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
        char key = e.getKeyChar();//当前键入值
        MTextPane pane = new MTextPane(editPane,rowPane);
        pane.setRowContent();

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
