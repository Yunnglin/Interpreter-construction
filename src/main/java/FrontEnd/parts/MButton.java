package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MButton {
    private MainWindow mainWindow;
    public MButton(MainWindow mainWindow){
        this.mainWindow = mainWindow;

    }
    public void setFileButton(JButton button){
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MPopMenu().getFileMenu().show(button,button.getX(),button.getY()+button.getHeight());
            }
        });
    }

    public void setEditButton(JButton button){
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MPopMenu().getEditMenu().show(button,button.getX()-button.getWidth(),button.getY()+button.getHeight());
            }
        });
    }

}
