package FrontEnd.parts.Utils;

import FrontEnd.MainWindow;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.*;


/**
 * 输出到文本组件的流。
 */
public class GUIPrintStream extends PrintStream {

    private JTextPane outPane;
    private StringBuffer sb = new StringBuffer();
    private static GUIPrintStream guiPrintStream=null;
    private static MainWindow mainWindow;

    public static GUIPrintStream getPrintStream()
    {
        if(guiPrintStream==null)
        {
            guiPrintStream=new GUIPrintStream(System.out);
        }
        return guiPrintStream;
    }
    private GUIPrintStream(OutputStream out) {
        super(out);
        this.outPane = mainWindow.getExecuteOutputPane();
    }

    public static void setMainWindow(MainWindow mainWindow){
        GUIPrintStream.mainWindow = mainWindow;
    }

    /**
     * 重写println()方法，将输出到GUI
     * @param x 输入的字符串
     */
    @Override
    public void println(String x) {
        SwingUtilities.invokeLater(() -> {
            sb.append(x).append('\n');
            outPane.setText(sb.toString());
        });
    }

    /**
     * 重写write()方法，将输出信息填充到GUI组件。
     * @param buf
     * @param off
     * @param len
     */
    @Override
    public void write(byte[] buf, int off, int len) {
        final String message = new String(buf, off, len);

        SwingUtilities.invokeLater(() -> {
            sb.append(message);
            outPane.setText(sb.toString());
        });
    }
}
