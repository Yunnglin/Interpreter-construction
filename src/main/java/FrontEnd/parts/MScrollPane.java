package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MScrollPane {
    private MainWindow mainWindow;
    private JScrollPane rowScroll;
    private JScrollPane editScroll;
    private BoundedRangeModel editScrollModel;
    public MScrollPane(MainWindow mainWindow){
        this.mainWindow = mainWindow;
    }
    public void init(){
        this.setRowScroll();
        this.setEditScroll();
    }
    public void setRowScroll(){
        rowScroll = mainWindow.getRowScrollPane();
        rowScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rowScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    }

    public void setEditScroll(){
        editScroll = mainWindow.getEditScrollPane();
        editScrollModel = editScroll.getVerticalScrollBar().getModel();
        editScrollModel.addChangeListener(e -> {
            if(e.getSource()==editScrollModel)
            {
                JScrollBar sBar = editScroll.getVerticalScrollBar();
                int x=sBar.getValue();
                JScrollBar sBar2 = rowScroll.getVerticalScrollBar();
                sBar2.setValue(x);
                rowScroll.setVerticalScrollBar(sBar2);
            }
        });
    }
}
