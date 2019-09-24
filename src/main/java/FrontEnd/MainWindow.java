package FrontEnd;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow {
    private JPanel mainPanel;
    private JButton fileButton;
    private JToolBar fileToolBar;
    private JScrollPane treePane;
    private JTree foldersTree;
    private JButton lexerBtn;
    private JButton parserBtn;
    private JButton complainBtn;
    private JLabel path1;
    private JLabel pathLabel;
    private JPanel inputPanel;
    private JPanel outputPanel;
    private JPanel funcPanel;
    private JLabel funcLabel;
    private JTextPane rowPane;
    private JTextPane editPane;
    private JScrollPane editScrollPane;
    private JScrollPane rowScrollPane;
    private JScrollPane outputScrollPane;
    private JTextPane outputPane;
    private JToolBar funcToolBar;

    // ¹¹Ôìº¯Êý
    private MainWindow() {
        funcToolBar.setFloatable(false);
        fileButton.addActionListener(e -> {
            JDialog jd = new JDialog();
            jd.setVisible(true);
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(new MainWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1400, 1000);
        frame.setVisible(true);

    }

}
