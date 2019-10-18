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
    private JTextPane[] outputPanes;


    public MTextPane(MainWindow mainWindow){
        this.mainWindow = mainWindow;
        this.editPane = mainWindow.getEditPane();
        this.outputPanes = mainWindow.getOutputPanes();
        this.jPopMenu = mainWindow.getmPopMenu().getEditMenu();
    }
    public void init(){
        setTextPane();
        setOutputPane();

    }


    private void setTextPane(){
        editPane.setFont(MFont.codeFont);
        editPane.setBackground(MColor.editAreaColor);
        editPane.getDocument().addDocumentListener(new MDocumentListener(this.mainWindow));
        editPane.addKeyListener(new MKeyListener(this.mainWindow));
        editPane.add(jPopMenu);
        editPane.addMouseListener(new MyMouseListener());
    }


    private void setOutputPane(){
        for(JTextPane outputPane: outputPanes){
            outputPane.setBackground(MColor.consoleAreaColor);
            outputPane.setFont(MFont.consoleFont);
            outputPane.setEditable(false);
        }

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
