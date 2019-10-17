package FrontEnd.parts;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class MFileFilter extends FileFilter {

    private String ends;
    private String description;

    public MFileFilter(String description, String ends) {
        this.ends = ends;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        String fileName = f.getName();
        return fileName.toUpperCase().endsWith(this.ends.toUpperCase());
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getEnds() {
        return this.ends;
    }

}
