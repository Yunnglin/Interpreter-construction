package FrontEnd.parts;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MButton {
    public static void setFileButton(JButton button){
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu jPopMenu = new JPopupMenu();
                JMenuItem open=new JMenuItem("��(X)");
                JMenuItem newFile=new JMenuItem("�½�(C)");
                JMenuItem saveFile=new JMenuItem("����(S)");
                jPopMenu.add(open);
                jPopMenu.add(newFile);
                jPopMenu.add(saveFile);
                jPopMenu.show(button,button.getX(),button.getY()+button.getHeight());
            }
        });
    }

    public static void setEditButton(JButton button){
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu jPopMenu = new JPopupMenu();//��ĵ����˵�
                JMenuItem cut=new JMenuItem("����(X)");
                JMenuItem copy=new JMenuItem("����(C)");
                JMenuItem paste=new JMenuItem("ճ��(V)");
                jPopMenu.add(cut);
                jPopMenu.add(copy);
                jPopMenu.add(paste);
                jPopMenu.show(button,button.getX()-button.getWidth(),button.getY()+button.getHeight());
            }
        });
    }
}
