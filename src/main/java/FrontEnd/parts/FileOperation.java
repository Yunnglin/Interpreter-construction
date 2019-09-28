package FrontEnd.parts;

import FrontEnd.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileOperation {
    private JFileChooser jfc = new JFileChooser();
    private String path;
    private String filename;
    private MainWindow mainWindow;

    public FileOperation(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    private void setContent(String path, String content) {
        mainWindow.getPathLabel().setText(path);
        mainWindow.getEditPane().setText(content);
    }

    public String readFile(String path) {
        try {
            String temp;
            BufferedReader bufferedReader;
            StringBuffer stringBuffer = new StringBuffer();
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            while ((temp = bufferedReader.readLine()) != null) {
                stringBuffer.append(temp).append("\r\n");
            }
            bufferedReader.close();
            if (stringBuffer.length() > 2)
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.length() - 2));
            return stringBuffer.toString().replaceAll("\\t", "   ");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void writeFile(String path) {

    }

    public void open()//open file
    {
        if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(mainWindow.getEditPane())) {
            File newFile;
            newFile = jfc.getSelectedFile();
            path = newFile.toString();//��ȡ�ļ�·��
            filename = newFile.getName();//��ȡ�ļ���
            setContent(path, readFile(path));
        }
    }

    public boolean save() {
        if (path != "")//�ļ��Ѵ��ڣ��򿪣�
        {
            BufferedWriter br;
            File newFile = new File(path);
            try {
                String content = mainWindow.getEditPane().getText();
                br = new BufferedWriter(new FileWriter(newFile));
                br.write(content + "");
                br.flush();
                br.close();
                //System.out.println("д��ɹ�");
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else//�ļ�δ���ڣ��򿪣�
        {
            if (JFileChooser.APPROVE_OPTION == jfc.showSaveDialog(mainWindow.getEditPane())) {
                File newFile;
                File file = jfc.getSelectedFile();
                if (file.getName() == null) return false;
                BufferedWriter br;
                MyFileFilter filter = (MyFileFilter) jfc.getFileFilter();
                String ends = filter.getEnds();
                if (file.toString().indexOf(ends) != -1) {
                    newFile = file;
                } else {
                    newFile = new File(file.getAbsolutePath() + ends);
                }
                path = newFile.toString();
                filename = newFile.getName();
                if (newFile.exists())//�Ѵ���ͬ���ļ���ɾ��
                {
                    newFile.delete();
                }
                try {
                    String content = mainWindow.getEditPane().getText();
                    System.out.println(content.length());
                    br = new BufferedWriter(new FileWriter(newFile));
                    br.write(content + "");
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

    public boolean creat() {
        if (JFileChooser.APPROVE_OPTION == jfc.showSaveDialog(mainWindow.getEditPane())) {
            File newFile;
            File file = jfc.getSelectedFile();
            if (file.getName() == null) return false;
            BufferedWriter br;
            MyFileFilter filter = (MyFileFilter) jfc.getFileFilter();
            String ends = filter.getEnds();
            if (file.toString().indexOf(ends) != -1) {
                newFile = file;
            } else {
                newFile = new File(file.getAbsolutePath() + ends);
            }
            path = newFile.toString();
            filename = newFile.getName();
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

        public MyFileFilter(String description, String ends) {
            this.ends = ends; // �����ļ���׺
            this.description = description; // �����ļ���������
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
            String fileName = f.getName();
            return fileName.toUpperCase().endsWith(this.ends.toUpperCase());//�ж��ļ�����׺
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
