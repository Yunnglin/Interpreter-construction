package FrontEnd.parts;

import FrontEnd.MainWindow;
import  FrontEnd.parts.MFoldersTree;
import FrontEnd.parts.conf.MFont;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileOperation {
    private JFileChooser jfc = new JFileChooser();
    private String filename;
    private MainWindow mainWindow;
    private MFoldersTree mfoldersTree;
    private MFoldersTree m;

    public FileOperation(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(new MFileFilter("C--", ".cmm"));
        jfc.addChoosableFileFilter(new MFileFilter("Java", ".java"));//����ļ�������
        jfc.addChoosableFileFilter(new MFileFilter("C", ".c"));
        jfc.addChoosableFileFilter(new MFileFilter("python", ".py"));
        jfc.addChoosableFileFilter(new MFileFilter("C++", ".cpp"));
        jfc.addChoosableFileFilter(new MFileFilter("txt", ".txt"));
        jfc.addChoosableFileFilter(new MFileFilter("HTML", ".html"));
        FileSystemView fsv = FileSystemView.getFileSystemView();
        jfc.setCurrentDirectory(fsv.getHomeDirectory());//����Ĭ��·��Ϊ����·��
    }

    private void setContent(String path, String content) {
        mainWindow.getOutputPane().setText("");
        mainWindow.getPathLabel().setText(path);
        mainWindow.getEditPane().setText(content);
//        mainWindow.getmScrollPane().updateLineNum();
    }

    public void setEmpty() {
        mainWindow.getEditPane().setText("");
        mainWindow.getOutputPane().setText("");
        mainWindow.getPathLabel().setText("");
        mainWindow.getmScrollPane().updateLineNum();
    }

    public String readFile(String path) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String temp;
            StringBuffer stringBuffer = new StringBuffer();

            while ((temp = bufferedReader.readLine()) != null) {
                stringBuffer.append(temp).append("\r\n");
            }

            if (stringBuffer.length() > 2)
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.length() - 2));
            return stringBuffer.toString().replaceAll("\\t", "   ");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }

    public void writeFile(String path) {
        //get file path
        File file = new File(path);
        //save the text
        String text = mainWindow.getEditPane().getText();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(text);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void open()//open file
    {
        if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(mainWindow.getEditPane())) {
            File newFile = jfc.getSelectedFile();
            String path = newFile.toString();//��ȡ�ļ�·��
            filename = newFile.getName();//��ȡ�ļ���
            setContent(path, readFile(path));
            mainWindow.getmFoldersTree().setFoldersTree();
            mainWindow.getSplitTreeEdit().setLeftComponent(mainWindow.getmFoldersTree().getTree());
        }
        //update tree
//        m = new MFoldersTree(mainWindow);
//        MFoldersTree mFoldersTree = m.setFoldersTree();
//        mainWindow.getSplitTreeEdit().setLeftComponent(m.setFoldersTree().getTree());

    }

    public void save() {
        // δ���ļ�
        String path = mainWindow.getPathLabel().getText();
        if (path.equals("")) {
            if (JFileChooser.APPROVE_OPTION == jfc.showSaveDialog(mainWindow.getEditPane())) {
                File newFile;
                File file = jfc.getSelectedFile();
                MFileFilter filter = (MFileFilter) jfc.getFileFilter();
                String ends = filter.getEnds();
                if (file.toString().contains(ends)) {
                    newFile = file;
                } else {
                    newFile = new File(file.getAbsolutePath() + ends);
                }
                String newPath = newFile.toString();
                writeFile(newPath);
                mainWindow.getPathLabel().setText(newPath);
            }
        } else {//�Ѵ��ļ�
            writeFile(path);
        }

    }

    public void creat() {
        int option = JOptionPane.showConfirmDialog(
                mainWindow.getEditPane(),
                "Save or not?",
                "Save",
                JOptionPane.YES_NO_CANCEL_OPTION
        );
        switch (option) {
            case JOptionPane.YES_OPTION:
                save();
                setEmpty();
                break;
            case JOptionPane.NO_OPTION:
                setEmpty();
                break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                break;
        }
    }

    public void treeOpen(String fileName)
    {
        File newFile;
        BufferedReader br;
        newFile=jfc.getSelectedFile();
        mainWindow.getEditPane().setFont(MFont.codeFont);//
        mainWindow.getEditPane().setText("");//
        try
        {
            String s;
            StringBuffer sbf=new StringBuffer();
            br=new BufferedReader(new FileReader(newFile));
            while((s=br.readLine())!=null)
            {
                sbf.append(s+"\r\n");
            }
            br.close();
            if(sbf.length()>2)
                sbf=new StringBuffer(sbf.substring(0, sbf.length()-2));
            String content=sbf.toString().replaceAll("\\t", "   ");
            mainWindow.getEditPane().setText(content);
        }catch (IOException e1) {
            e1.printStackTrace();
        }
        //update tree
//        MFoldersTree m = new MFoldersTree(mainWindow);
//        MFoldersTree mFoldersTree = m.setFoldersTree();
//        mainWindow.getSplitTreeEdit().setLeftComponent(m.setFoldersTree().getTree());
        mainWindow.getmFoldersTree().setFoldersTree();
        mainWindow.getSplitTreeEdit().setLeftComponent(mainWindow.getmFoldersTree().getTree());

    }

}

