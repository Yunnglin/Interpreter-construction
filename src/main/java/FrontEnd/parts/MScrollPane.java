package FrontEnd.parts;

import FrontEnd.MainWindow;
import FrontEnd.parts.conf.MColor;
import FrontEnd.parts.conf.MFont;
import FrontEnd.parts.conf.MSize;

import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;


public class MScrollPane {
    private MainWindow mainWindow;
    private JScrollPane editScroll;
    private DefaultListModel lineList;
    private JList lineNumList;
    private List<Integer> selectedItems=new ArrayList<>();;

    public MScrollPane(MainWindow mainWindow){
        this.mainWindow = mainWindow;
        this.lineNumList = mainWindow.getLineNumList();
    }
    public void init(){
        this.setEditScroll();
    }

    private void setEditScroll(){
        editScroll = mainWindow.getEditScrollPane();
        lineList = new DefaultListModel();

        lineNumList = new JList(lineList);
        lineNumList.setFixedCellWidth(MSize.lineCellWidth);
        lineNumList.setFixedCellHeight(MSize.lineCellHeight);
        lineNumList.setBackground(MColor.lineAreaColor);
        lineNumList.setFont(MFont.lineNumFont);
        lineNumList.setSelectionBackground(MColor.breakPointColor);
        lineNumList.addMouseListener(new ListClickListener());
        editScroll.setRowHeaderView(lineNumList);
    }

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

    private class ListClickListener implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            JList theList = (JList) e.getSource();
            int selectedIndex = theList.getSelectedIndex();
            if(selectedItems.contains(selectedIndex)){
                selectedItems.remove((Integer)selectedIndex);
            }
            else{
                selectedItems.add((Integer)selectedIndex);
            }
            //List<Integer>转int[]
            theList.setSelectedIndices(selectedItems.stream().mapToInt(Integer::valueOf).toArray());
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}
