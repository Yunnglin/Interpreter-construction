package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileOperation {
    private JFileChooser jfc = new JFileChooser();
    private String filename;
    private MainWindow mainWindow;

    public FileOperation(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(new MFileFilter("C--", ".cmm"));
        jfc.addChoosableFileFilter(new MFileFilter("Java", ".java"));//添加文件过滤器
        jfc.addChoosableFileFilter(new MFileFilter("C", ".c"));
        jfc.addChoosableFileFilter(new MFileFilter("python", ".py"));
        jfc.addChoosableFileFilter(new MFileFilter("C++", ".cpp"));
        jfc.addChoosableFileFilter(new MFileFilter("txt", ".txt"));
        jfc.addChoosableFileFilter(new MFileFilter("HTML", ".html"));
        FileSystemView fsv = FileSystemView.getFileSystemView();
        jfc.setCurrentDirectory(fsv.getHomeDirectory());//设置默认路径为桌面路径
    }

    private void setContent(String path, String content) {
        mainWindow.getOutputPane().setText("");
        mainWindow.getPathLabel().setText(path);
        mainWindow.getEditPane().setText(content);
        mainWindow.getmScrollPane().updateLineNum();
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
            String path = newFile.toString();//获取文件路径
            filename = newFile.getName();//获取文件名
            setContent(path, readFile(path));
        }
    }

    public void save() {
        // 未打开文件
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
        } else {//已打开文件
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
                break;
            case JOptionPane.CLOSED_OPTION:
                break;
        }
    }

}

