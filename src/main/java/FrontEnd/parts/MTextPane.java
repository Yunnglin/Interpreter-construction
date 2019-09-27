package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MTextPane {
    private JPopupMenu jPopMenu;
    private JTextPane textPane;
    private JTextPane rowPane;
    private Font font=new Font("����",Font.PLAIN,20);//Ĭ������

    public void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
    }

    public void setRowPane(JTextPane rowPane) {
        this.rowPane = rowPane;
    }

    public MTextPane(){
//        this.mainWindow=mainWindow;
        jPopMenu = new MPopMenu().getEditMenu();
    }
    public MTextPane(JTextPane textPane,JTextPane rowPane){
        this();
        this.textPane = textPane;
        this.rowPane = rowPane;
    }

    public void setTextPane(ArrayList tokens){
        JTextPane jtp = this.textPane;

        jtp.setFont(font);
        jtp.add(jPopMenu);
        jtp.addMouseListener(new MyMouseListener());
    }

    public void setRowPane(){
        JTextPane rowPane= this.rowPane;
        rowPane.setFont(font);
        rowPane.setText("1");
        rowPane.setPreferredSize(new Dimension(50, 100));
        setRowContent();
        rowPane.setEditable(false);
    }

    public void setRowContent()
    {
        StringBuilder rowContent=new StringBuilder();
        Element map = textPane.getDocument().getDefaultRootElement();
        int count=map.getElementCount();
        rowPane.setText("");
        for(int i=0;i<count;i++)
        {
            rowContent.append(i + 1).append("\n");
        }
        rowPane.setText(rowContent.toString());

    }

    private class MyMouseListener extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                jPopMenu.show(textPane,e.getX(),e.getY());
            }
        }
    }
}
