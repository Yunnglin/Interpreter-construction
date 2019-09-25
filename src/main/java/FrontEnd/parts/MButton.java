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
                JMenuItem open=new JMenuItem("打开(X)");
                JMenuItem newFile=new JMenuItem("新建(C)");
                JMenuItem saveFile=new JMenuItem("保存(S)");
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
                JPopupMenu jPopMenu = new JPopupMenu();//你的弹出菜单
                JMenuItem cut=new JMenuItem("剪切(X)");
                JMenuItem copy=new JMenuItem("复制(C)");
                JMenuItem paste=new JMenuItem("粘贴(V)");
                jPopMenu.add(cut);
                jPopMenu.add(copy);
                jPopMenu.add(paste);
                jPopMenu.show(button,button.getX()-button.getWidth(),button.getY()+button.getHeight());
            }
        });
    }
}
