package FrontEnd.parts;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class MFileFilter extends FileFilter {

    private String ends; // 文件后缀
    private String description; // 文件描述

    public MFileFilter(String description, String ends) {
        this.ends = ends; // 设置文件后缀
        this.description = description; // 设置文件描述文字
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        String fileName = f.getName();
        return fileName.toUpperCase().endsWith(this.ends.toUpperCase());//判断文件名后缀
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getEnds() {
        return this.ends;
    }

}
