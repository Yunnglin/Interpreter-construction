package FrontEnd.parts;

import FrontEnd.MainWindow;
import FrontEnd.parts.conf.*;



import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MTextPane {
    private MainWindow mainWindow;
    private JPopupMenu jPopMenu;
    private JTextPane editPane;
    private JTextPane outputPane;




    public MTextPane(MainWindow mainWindow){
        this.mainWindow = mainWindow;
        this.editPane = mainWindow.getEditPane();
        this.outputPane = mainWindow.getOutputPane();
        this.jPopMenu = mainWindow.getmPopMenu().getEditMenu();
    }
    public void init(){
        setTextPane();
        setOutputPane();

    }

    public void setFont(Font font){
        editPane.setFont(font);
        outputPane.setFont(font);
    }

    private void setTextPane(){
        editPane.setFont(MFont.codeFont);
        editPane.getDocument().addDocumentListener(new MDocumentListener(this.mainWindow));
        editPane.addKeyListener(new MKeyListener(this.mainWindow));
        editPane.add(jPopMenu);
        editPane.addMouseListener(new MyMouseListener());

    }


    private void setOutputPane(){
        outputPane.setFont(MFont.consoleFont);
        outputPane.setEditable(false);
    }


    public void setEditPaneContent(String path){

    }

    public void setOutputPaneContent(){

    }

    private class MyMouseListener extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                jPopMenu.show(editPane,e.getX(),e.getY());
            }
        }
    }
}
