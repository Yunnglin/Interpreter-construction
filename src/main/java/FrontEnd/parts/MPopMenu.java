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
        this.editMenu = new JPopupMenu();//��ĵ����˵�
        JMenuItem cut=new JMenuItem("����(X)");
        setShortcut(cut, KeyEvent.VK_X);
        JMenuItem copy=new JMenuItem("����(C)");
        setShortcut(copy, KeyEvent.VK_C);
        JMenuItem paste=new JMenuItem("ճ��(V)");
        setShortcut(paste, KeyEvent.VK_V);
        editMenu.add(cut);
        editMenu.add(copy);
        editMenu.add(paste);

        this.fileMenu = new JPopupMenu();
        JMenuItem open=new JMenuItem("��(O)");
        setShortcut(open, KeyEvent.VK_O);
        JMenuItem newFile=new JMenuItem("�½�(N)");
        setShortcut(newFile, KeyEvent.VK_N);
        JMenuItem saveFile=new JMenuItem("����(S)");
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
            path = newFile.toString();//��ȡ�ļ�·��
            filename = newFile.getName();//��ȡ�ļ���
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
        if(path!="")//�ļ��Ѵ��ڣ��򿪣�
        {
            BufferedWriter br;
            File newFile=new File(path);
            try {
                String content=mainWindow.getEditPane().getText();
                br = new BufferedWriter(new FileWriter(newFile));
                br.write(content+"");
                br.flush();
                br.close();
                //System.out.println("д��ɹ�");
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        else//�ļ�δ���ڣ��򿪣�
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
                if(newFile.exists())//�Ѵ���ͬ���ļ���ɾ��
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
                br.flush(); //ˢ�»����������ݵ��ļ�
                br.close();
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        return false;
    }

class MyFileFilter extends FileFilter {

    String ends; // �ļ���׺
    String description; // �ļ�����

    public MyFileFilter(String description,String ends) {
        this.ends = ends; // �����ļ���׺
        this.description = description; // �����ļ���������
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        String fileName = f.getName();
        if (fileName.toUpperCase().endsWith(this.ends.toUpperCase())) return true;//�ж��ļ�����׺
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
