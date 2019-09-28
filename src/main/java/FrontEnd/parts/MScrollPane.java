package FrontEnd.parts;

import FrontEnd.MainWindow;
import FrontEnd.parts.conf.MColor;
import FrontEnd.parts.conf.MFont;
import FrontEnd.parts.conf.MSize;

import javax.swing.*;


public class MScrollPane {
    private MainWindow mainWindow;
    private JScrollPane editScroll;
    private DefaultListModel lineList;

    public MScrollPane(MainWindow mainWindow){
        this.mainWindow = mainWindow;
    }
    public void init(){
        this.setEditScroll();
    }

    private void setEditScroll(){
        editScroll = mainWindow.getEditScrollPane();
        lineList = new DefaultListModel();
        JList lineNumList = new JList(lineList);
        lineNumList.setFixedCellWidth(MSize.lineCellWidth);
        lineNumList.setFixedCellHeight(MSize.lineCellHeight);
        lineNumList.setBackground(MColor.lineAreaColor);
        lineNumList.setFont(MFont.lineNumFont);
        editScroll.setRowHeaderView(lineNumList);
    }

    //ÐÐºÅ¸üÐÂ
    public void updateLineNum() {
        int line = mainWindow.getEditPane().getDocument().getDefaultRootElement().getElementCount();
        if (line == this.lineList.getSize()) {
            return;
        } else if (line > this.lineList.getSize()) {
            for (int i = this.lineList.getSize() + 1; i <= line; i++)
                this.lineList.addElement(i);
        } else {
            for (int i = this.lineList.getSize(); i > line; i--)
                this.lineList.removeElementAt(i - 1);
        }
    }

}
