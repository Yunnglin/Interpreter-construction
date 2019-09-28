package FrontEnd.parts;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import FrontEnd.MainWindow;


public class MPopMenu extends JPopupMenu implements ActionListener {

    private MainWindow mainWindow;
    private JPopupMenu editMenu;
    private JPopupMenu fileMenu;
    private FileOperation fileOperation;

    public MPopMenu(MainWindow mainWindow){
        this.mainWindow = mainWindow;
    }

    public void init(){
        this.editMenu = new JPopupMenu();//��ĵ����˵�
        JMenuItem cut=new JMenuItem("����");
        setShortcut(cut, KeyEvent.VK_X);
        JMenuItem copy=new JMenuItem("����");
        setShortcut(copy, KeyEvent.VK_C);
        JMenuItem paste=new JMenuItem("ճ��");
        setShortcut(paste, KeyEvent.VK_V);
        editMenu.add(cut);
        editMenu.add(copy);
        editMenu.add(paste);

        this.fileMenu = new JPopupMenu();
        JMenuItem open=new JMenuItem("��");
        setShortcut(open, KeyEvent.VK_O);
        open.addActionListener(this);
        JMenuItem newFile=new JMenuItem("�½�");
        setShortcut(newFile, KeyEvent.VK_N);
        newFile.addActionListener(this);
        JMenuItem saveFile=new JMenuItem("����");
        setShortcut(saveFile, KeyEvent.VK_S);
        saveFile.addActionListener(this);
        fileMenu.add(open);
        fileMenu.add(newFile);
        fileMenu.add(saveFile);

        fileOperation = new FileOperation(mainWindow);
    }

    public JPopupMenu getEditMenu(){
        return editMenu;
    }

    public JPopupMenu getFileMenu(){
        return fileMenu;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("����"))
        {
            fileOperation.save();
        }
        if(e.getActionCommand().equals("�½�"))
        {
            fileOperation.creat();
        }
        if(e.getActionCommand().equals("��"))
        {
            fileOperation.open();
        }
    }

    private void setShortcut(JMenuItem jmi, int key)
    {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key, InputEvent.CTRL_MASK);
        jmi.setMnemonic(key);
        jmi.setAccelerator(keyStroke);
    }

}
