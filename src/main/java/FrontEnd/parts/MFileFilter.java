package FrontEnd.parts;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class MFileFilter extends FileFilter {

    private String ends; // �ļ���׺
    private String description; // �ļ�����

    public MFileFilter(String description, String ends) {
        this.ends = ends; // �����ļ���׺
        this.description = description; // �����ļ���������
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
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
