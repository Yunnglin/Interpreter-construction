package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MTextPane {
    private MainWindow mainWindow;
    private JPopupMenu jPopMenu;
    private JTextPane editPane;
    private JTextPane rowPane;
    private JTextPane outputPane;
    private Font font=new Font("宋体",Font.PLAIN,20);


    public MTextPane(){
//        this.mainWindow=mainWindow;
        jPopMenu = new MPopMenu().getEditMenu();
    }
    public MTextPane(MainWindow mainWindow){
        this();
        this.mainWindow = mainWindow;
        this.editPane = mainWindow.getEditPane();
        this.rowPane = mainWindow.getRowPane();
        this.outputPane = mainWindow.getOutputPane();
        setRowPane();
        setTextPane();
        setOutputPane();
    }

    private void setTextPane(){
        editPane.addKeyListener(new MKeyListener(this.mainWindow));
        editPane.setFont(font);
        editPane.add(jPopMenu);
        editPane.addMouseListener(new MyMouseListener());
    }

    private void setRowPane(){
        rowPane.setFont(font);
        rowPane.setText("1");
        rowPane.setPreferredSize(new Dimension(50, 100));
        setRowContent();
        rowPane.setEditable(false);
    }

    private void setOutputPane(){
        outputPane.setEditable(false);
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

    public void setEditPaneContent(){

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
