package FrontEnd;

import FrontEnd.parts.*;

import javax.swing.*;

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
    private JButton editButton;
    private JSplitPane splitTreeEdit;
    private JSplitPane splitInOut;


    private MTextPane mTextPane;

    public MTextPane getmTextPane() {
        return mTextPane;
    }

    public JButton getEditButton() {
        return editButton;
    }

    private MainWindow() {
        MToolBar mToolBar = new MToolBar();
        mToolBar.setToolBar(funcToolBar);

        MButton mButton = new MButton(this);
        mButton.init();

        MSplitPane mSplitPane = new MSplitPane();
        mSplitPane.setSplitInOut(splitInOut);
        mSplitPane.setSplitTreeEdit(splitTreeEdit);

        mTextPane = new MTextPane(this);
        mTextPane.init();

        MScrollPane mScrollPane = new MScrollPane(this);
        mScrollPane.init();

//        fileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fd = new JFileChooser();
//                fd.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
//                fd.showOpenDialog(null);
//                File f = fd.getSelectedFile();
//                if (f != null) {
//                    System.out.println(f.getPath());
//                }
//            }
//        });
//        editButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JTextPane getRowPane() {
        return rowPane;
    }

    public JTextPane getEditPane() {
        return editPane;
    }

    public JScrollPane getEditScrollPane() {
        return editScrollPane;
    }

    public JScrollPane getRowScrollPane() {
        return rowScrollPane;
    }

    public JTree getFoldersTree(){
        return foldersTree;
    }

    public JButton getLexerBtn() {
        return lexerBtn;
    }

    public JButton getParserBtn() {
        return parserBtn;
    }

    public JLabel getPathLabel() {
        return pathLabel;
    }

    public JTextPane getOutputPane() {
        return outputPane;
    }

    public JButton getFileButton() {
        return fileButton;
    }

    public JButton getComplainBtn() {
        return complainBtn;
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
