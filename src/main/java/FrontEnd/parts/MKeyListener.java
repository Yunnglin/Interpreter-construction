package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import javax.swing.text.Element;
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
        setRowContent();

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void setRowContent()
    {
        StringBuilder rowContent=new StringBuilder();
        Element map = editPane.getDocument().getDefaultRootElement();
        int count=map.getElementCount();
        rowPane.setText("");
        for(int i=0;i<count;i++)
        {
            rowContent.append(i + 1).append("\n");
        }
        rowPane.setText(rowContent.toString());

    }
}
