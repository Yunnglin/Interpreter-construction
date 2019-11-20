package FrontEnd.parts;

import FrontEnd.MainWindow;
import FrontEnd.parts.conf.MColor;
import FrontEnd.parts.conf.MFont;
import FrontEnd.parts.conf.MSize;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;


public class MScrollPane {
    private MainWindow mainWindow;
    private JScrollPane editScroll;
    private DefaultListModel lineList;
    private JList lineNumList;
    private List<Integer> selectedItems = new ArrayList<>();
    private MyCellRender myCellRender;

    public MScrollPane(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void init() {
        this.setEditScroll();
    }


    public JList getLineNumList() {
        return lineNumList;
    }

    private void setEditScroll() {
        editScroll = mainWindow.getEditScrollPane();
        lineList = new DefaultListModel();

        lineNumList = new JList(lineList);
        myCellRender = new MyCellRender();
        lineNumList.setCellRenderer(myCellRender);
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

    public int curLine;

    private class MyCellRender extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setText(value + "");
            setBackground(MColor.lineAreaColor);
            setFont(MFont.lineNumFont);

            if (isSelected) {
                setBackground(MColor.breakPointColor);
            }
            int nextLine = mainWindow.getmButton().curLine;
            if (curLine != nextLine) {//下一行
                if ((index + 1) == nextLine) {
                    curLine = nextLine;
                    setBackground(MColor.curPointColor);
                }
            }
            return this;
        }
    }


    public void freshList() {
        lineNumList.repaint();
    }

    private class ListClickListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            JList theList = (JList) e.getSource();

            int selectedIndex = theList.getSelectedIndex();
            if (selectedItems.contains(selectedIndex)) {
                selectedItems.remove((Integer) selectedIndex);
            } else {
                selectedItems.add((Integer) selectedIndex);
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
