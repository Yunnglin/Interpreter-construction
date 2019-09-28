package FrontEnd.parts;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;

import FrontEnd.MainWindow;

public class MPopMenu {
    private JFileChooser jfc = new JFileChooser();
    private String path;
    private String filename;
    private MainWindow mainWindow;
    private JPopupMenu editMenu;
    private JPopupMenu fileMenu;

    public MPopMenu(MainWindow mainWindow){
        this.mainWindow = mainWindow;

    }

    public void init(){
        this.editMenu = new JPopupMenu();//你的弹出菜单
        JMenuItem cut=new JMenuItem("剪切(X)");
        setShortcut(cut, KeyEvent.VK_X);
        JMenuItem copy=new JMenuItem("复制(C)");
        setShortcut(copy, KeyEvent.VK_C);
        JMenuItem paste=new JMenuItem("粘贴(V)");
        setShortcut(paste, KeyEvent.VK_V);
        editMenu.add(cut);
        editMenu.add(copy);
        editMenu.add(paste);

        this.fileMenu = new JPopupMenu();
        JMenuItem open=new JMenuItem("打开(O)");
        setShortcut(open, KeyEvent.VK_O);
        JMenuItem newFile=new JMenuItem("新建(N)");
        setShortcut(newFile, KeyEvent.VK_N);
        JMenuItem saveFile=new JMenuItem("保存(S)");
        setShortcut(saveFile, KeyEvent.VK_S);
        fileMenu.add(open);
        fileMenu.add(newFile);
        fileMenu.add(saveFile);
    }

    public JPopupMenu getEditMenu(){
        return editMenu;
    }

    public JPopupMenu getFileMenu(){
        return fileMenu;
    }


    public boolean open()//open file
    {
        if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(mainWindow.getEditPane())) {
            File newFile;
            File file = jfc.getSelectedFile();
            if (file.getName() == null) return false;
            BufferedReader br;
            newFile = file;
            path = newFile.toString();//获取文件路径
            filename = newFile.getName();//获取文件名
            mainWindow.getEditPane().setText(null);
            try {
                String temp;
                StringBuffer sbf = new StringBuffer();
                br = new BufferedReader(new FileReader(newFile));
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp + "\r\n");
                }
                br.close();
                if (sbf.length() > 2)
                    sbf = new StringBuffer(sbf.substring(0, sbf.length() - 2));
                String content = sbf.toString().replaceAll("\\t", "   ");
                mainWindow.getEditPane().setText(content);
                //mainWindow.getEditPane().setRowContent();
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public boolean save()
    {
        if(path!="")//文件已存在（打开）
        {
            BufferedWriter br;
            File newFile=new File(path);
            try {
                String content=mainWindow.getEditPane().getText();
                br = new BufferedWriter(new FileWriter(newFile));
                br.write(content+"");
                br.flush();
                br.close();
                //System.out.println("写入成功");
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        else//文件未存在（打开）
        {
            if(JFileChooser.APPROVE_OPTION == jfc.showSaveDialog(mainWindow.getEditPane()))
            {
                File newFile;
                File file = jfc.getSelectedFile();
                if(file.getName()==null) return false;
                BufferedWriter br;
                MyFileFilter filter = (MyFileFilter) jfc.getFileFilter();
                String ends = filter.getEnds();
                if (file.toString().indexOf(ends)!=-1)
                {
                    newFile = file;
                }
                else
                {
                    newFile = new File(file.getAbsolutePath() + ends);
                }
                path=newFile.toString();
                filename=newFile.getName();
                if(newFile.exists())//已存在同名文件，删除
                {
                    newFile.delete();
                }
                try {
                    String content=mainWindow.getEditPane().getText();
                    System.out.println(content.length());
                    br = new BufferedWriter(new FileWriter(newFile));
                    br.write(content+"");
                    br.flush();
                    br.close();
                    return true;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }
    private void setShortcut(JMenuItem jmi, int key)
    {
        KeyStroke ms1 = KeyStroke.getKeyStroke(key, InputEvent.CTRL_MASK);
        jmi.setMnemonic(key);
        jmi.setAccelerator(ms1);
    }


    public boolean creat()
    {
        if(JFileChooser.APPROVE_OPTION == jfc.showSaveDialog(mainWindow.getEditPane()))
        {
            File newFile;
            File file = jfc.getSelectedFile();
            if(file.getName()==null) return false;
            BufferedWriter br;
            MyFileFilter filter = (MyFileFilter) jfc.getFileFilter();
            String ends = filter.getEnds();
            if (file.toString().indexOf(ends)!=-1)
            {
                newFile = file;
            }
            else
            {
                newFile = new File(file.getAbsolutePath() + ends);
            }
            path=newFile.toString();
            filename=newFile.getName();
            mainWindow.getEditPane().setText(null);
            try {
                br = new BufferedWriter(new FileWriter(newFile));
                br.write("");
                br.flush(); //刷新缓冲区的数据到文件
                br.close();
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        return false;
    }

class MyFileFilter extends FileFilter {

    String ends; // 文件后缀
    String description; // 文件描述

    public MyFileFilter(String description,String ends) {
        this.ends = ends; // 设置文件后缀
        this.description = description; // 设置文件描述文字
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        String fileName = f.getName();
        if (fileName.toUpperCase().endsWith(this.ends.toUpperCase())) return true;//判断文件名后缀
        return false;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getEnds() {
        return this.ends;
    }

    }
}
