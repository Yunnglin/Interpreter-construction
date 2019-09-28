package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MDocumentListener implements DocumentListener {

    private MainWindow mainWindow;

    public MDocumentListener(MainWindow mainWindow){
        this.mainWindow = mainWindow;

    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        mainWindow.getmScrollPane().updateLineNum();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        mainWindow.getmScrollPane().updateLineNum();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
