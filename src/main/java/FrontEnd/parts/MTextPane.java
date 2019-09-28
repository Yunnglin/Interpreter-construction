package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MTextPane {
    private MainWindow mainWindow;
    private JPopupMenu jPopMenu;
    private JTextPane editPane;
    private JTextPane rowPane;
    private JTextPane outputPane;
    private Font font=new Font("宋体",Font.PLAIN,20);



    public MTextPane(MainWindow mainWindow){
        this.mainWindow = mainWindow;
        this.editPane = mainWindow.getEditPane();
        this.rowPane = mainWindow.getRowPane();
        this.outputPane = mainWindow.getOutputPane();
        this.jPopMenu = mainWindow.getmPopMenu().getEditMenu();
    }
    public void init(){
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

    public void setEditPaneContent(String path){
        File file = new File(path);
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String strLine = null;
            while(null != (strLine = reader.readLine())){
                stringBuilder.append(strLine);
            }
            editPane.setText(stringBuilder.toString());
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

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
