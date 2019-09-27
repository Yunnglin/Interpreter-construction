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

    // ??????
    private MainWindow() {
        MToolBar mToolBar = new MToolBar();
        mToolBar.setToolBar(funcToolBar);

        MButton mButton = new MButton(this);
        mButton.setFileButton(fileButton);
        mButton.setEditButton(editButton);

        MSplitPane mSplitPane = new MSplitPane();
        mSplitPane.setSplitInOut(splitInOut);
        mSplitPane.setSplitTreeEdit(splitTreeEdit);

        MTextPane mTextPane = new MTextPane(editPane,rowPane);
        mTextPane.setTextPane();
        mTextPane.setRowPane();
        editPane.addKeyListener(new MKeyListener(this));

        MScrollPane mScrollPane = new MScrollPane(this);
        mScrollPane.init();


        lexerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get file path
                String path = fileToolBar.toString();
                File file=new File(path);
                //save the text
                String text = editPane.getText();
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(file));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //write to the file
                try {
                    writer.write(text) ;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //read the file
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //initialize a lexer
                Lexer lex = new Lexer(reader);
                ArrayList<Token> tokens = null;
                try {
                    tokens = lex.getAllToken();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (SyntaxError syntaxError) {
                    syntaxError.printStackTrace();
                }
                //show the result
                mTextPane.setTextPane(tokens);
            }
        });
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
                fd.showOpenDialog(null);
                File f = fd.getSelectedFile();
                if (f != null) {
                    System.out.println(f.getPath());
                }
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(new MainWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1400, 1000);
        frame.setVisible(true);

    }

}
