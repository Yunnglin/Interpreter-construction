package FrontEnd.parts;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MPopMenu {
    public JPopupMenu getEditMenu(){
        JPopupMenu jPopMenu = new JPopupMenu();//��ĵ����˵�
        JMenuItem cut=new JMenuItem("����(X)");
        setShortcut(cut, KeyEvent.VK_X);
        JMenuItem copy=new JMenuItem("����(C)");
        setShortcut(copy, KeyEvent.VK_C);
        JMenuItem paste=new JMenuItem("ճ��(V)");
        setShortcut(paste, KeyEvent.VK_V);
        jPopMenu.add(cut);
        jPopMenu.add(copy);
        jPopMenu.add(paste);
        return jPopMenu;
    }

    public JPopupMenu getFileMenu(){
        JPopupMenu jPopMenu = new JPopupMenu();
        JMenuItem open=new JMenuItem("��(O)");
        setShortcut(open, KeyEvent.VK_O);
        JMenuItem newFile=new JMenuItem("�½�(N)");
        setShortcut(newFile, KeyEvent.VK_N);
        JMenuItem saveFile=new JMenuItem("����(S)");
        setShortcut(saveFile, KeyEvent.VK_S);
        jPopMenu.add(open);
        jPopMenu.add(newFile);
        jPopMenu.add(saveFile);
        return jPopMenu;
    }

    private void setShortcut(JMenuItem jmi, int key)
    {
        KeyStroke ms1 = KeyStroke.getKeyStroke(key, InputEvent.CTRL_MASK);
        jmi.setMnemonic(key);
        jmi.setAccelerator(ms1);
    }
}
