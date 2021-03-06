package FrontEnd;

import FrontEnd.parts.*;
import FrontEnd.parts.Utils.GUIPrintStream;

import javax.swing.*;


public class MainWindow {
    private JPanel mainPanel;
    private JButton fileButton;
    private JToolBar fileToolBar;
    private JScrollPane treePane;
    private JTree foldersTree;
    private JButton lexerBtn;
    private JButton parserBtn;
    private JButton executeBtn;
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

    public JTabbedPane getOutputTabbedPane() {
        return outputTabbedPane;
    }

    private JTabbedPane outputTabbedPane;

    public JScrollPane getParseOutputJSP() {
        return parseOutputJSP;
    }

    private JScrollPane parseOutputJSP;
    private JTextPane parseOutputPane;
    private JScrollPane excuteOutputJSP;
    private JTextPane executeOutputPane;
    private JPanel executeJPanel;
    private JPanel paramJPanel;
    private JTextField paramTextField;
    private JLabel promptLabel;
    private JButton debugBtn;
    private JButton stepOverBtn;
    private JButton stepInBtn;


    private JButton continueBtn;

    public JButton getStopBtn() {
        return stopBtn;
    }

    private JButton stopBtn;


    private MTextPane mTextPane;
    private MPopMenu mPopMenu;
    private MScrollPane mScrollPane;
    private FileOperation fileOperation;
    private MFoldersTree mFoldersTree;

    public MButton getmButton() {
        return mButton;
    }

    private MButton mButton;



    private JTextPane[] outputPanes = {
            outputPane,
            parseOutputPane,
            executeOutputPane
    };

    public JButton getContinueBtn() {
        return continueBtn;
    }
    private MainWindow() {
        //更新顺序
        mPopMenu = new MPopMenu(this);
        mPopMenu.init();

        MToolBar mToolBar = new MToolBar();
        mToolBar.setToolBar(funcToolBar);
        mToolBar.setToolBar(fileToolBar);


        MSplitPane mSplitPane = new MSplitPane();
        mSplitPane.setSplitInOut(splitInOut);
        mSplitPane.setSplitTreeEdit(splitTreeEdit);

        mButton = new MButton(this);
        mButton.init();

        mTextPane = new MTextPane(this);
        mTextPane.init();


        mScrollPane = new MScrollPane(this);
        mScrollPane.init();

        fileOperation = new FileOperation(this);


        mFoldersTree = new MFoldersTree(this);
//        mFoldersTree.setFoldersTree(new File(System.getProperty("user.home")));
        mFoldersTree.setTree();
//        pathLabel.setText(System.getProperty("user.home"));
        foldersTree = mFoldersTree.getTree();
        splitTreeEdit.setLeftComponent(new JScrollPane(foldersTree));

        //输出重定向
        GUIPrintStream.setMainWindow(this);
//        System.setOut(GUIPrintStream.getPrintStream());
//        System.out.println("Hello");
//        executeOutputPane.addKeyListener(new MKeyListener(this));
//        executeOutputPane.setEditable(true);
//        outputTabbedPane.setSelectedComponent(executeJPanel);
    }


    public JButton getDebugBtn() {
        return debugBtn;
    }

    public JButton getStepOverBtn() {
        return stepOverBtn;
    }

    public JButton getStepInBtn() {
        return stepInBtn;
    }

    public JScrollPane getTreePane() {
        return treePane;
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

    public JTree getFoldersTree() {
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

    public JButton getExecuteBtn() {
        return executeBtn;
    }

    public MPopMenu getmPopMenu() {
        return mPopMenu;
    }

    public MTextPane getmTextPane() {
        return mTextPane;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public FileOperation getFileOperation() {
        return fileOperation;
    }

    public MScrollPane getmScrollPane() {
        return mScrollPane;
    }

    public JSplitPane getSplitTreeEdit() {
        return splitTreeEdit;
    }

    public MFoldersTree getmFoldersTree() {
        return mFoldersTree;
    }

    public JTextPane getParseOutputPane() {
        return parseOutputPane;
    }

    public JTextPane getExecuteOutputPane() {
        return executeOutputPane;
    }


    public JPanel getExecuteJPanel() {
        return executeJPanel;
    }

    public JTextField getParamTextField() {
        return paramTextField;
    }

    public JLabel getPromptLabel() {
        return promptLabel;
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(new MainWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1400, 1000);
        frame.setVisible(true);

    }

    public JTextPane[] getOutputPanes() {
        return outputPanes;
    }
}
