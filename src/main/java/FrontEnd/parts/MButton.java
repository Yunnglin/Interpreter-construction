package FrontEnd.parts;

import FrontEnd.DebuggerForm;
import FrontEnd.MainWindow;
import FrontEnd.parts.conf.MColor;
import interpreter.Interpreter;
import interpreter.debugger.Breakpoint;
import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.exception.SyntaxError;
import interpreter.intermediate.node.INode;
import interpreter.lexer.Lexer;
import interpreter.lexer.token.Token;
import interpreter.parser.Parser;
import message.Message;
import message.MessageListener;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MButton {
    private MainWindow mainWindow;
    private Interpreter interpreter;
    private DebuggerForm debuggerForm;
    private Thread curThread = null;
    private ExecutorMessageListener executorMessageListener;
    public int curLine;

    private Icon debugIcon = new ImageIcon("src/main/java/FrontEnd/resource/debug.png");
    private Icon stepOverIcon = new ImageIcon("src/main/java/FrontEnd/resource/stepOver.png");
    private Icon stepIntoIcon = new ImageIcon("src/main/java/FrontEnd/resource/stepInto.png");
    private Icon continueIcon = new ImageIcon("src/main/java/FrontEnd/resource/continue.png");
    private Icon stopIcon = new ImageIcon("src/main/java/FrontEnd/resource/stop.png");

    public MButton(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void init() {
        setEditButton(mainWindow.getEditButton());
        setFileButton(mainWindow.getFileButton());
        setLexerButton(mainWindow.getLexerBtn());
        setParserButton(mainWindow.getParserBtn());
        setExecuteButton(mainWindow.getExecuteBtn(), false);

        setExecuteButton(mainWindow.getDebugBtn(), true);
        setStepOverButton(mainWindow.getStepOverBtn());
        setStepInButton(mainWindow.getStepInBtn());
        setContinueButton(mainWindow.getContinueBtn());
        setStopButton(mainWindow.getStopBtn());

        mainWindow.getDebugBtn().setIcon(debugIcon);
        setDebugEnabled(false);
    }

    private void setDebugEnabled(boolean enabled) {
        mainWindow.getStepOverBtn().setEnabled(enabled);
        mainWindow.getStepInBtn().setEnabled(enabled);
        mainWindow.getContinueBtn().setEnabled(enabled);
        //mainWindow.getStopBtn().setEnabled(enabled);
    }

    private void setFileButton(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainWindow.getmPopMenu().getFileMenu().show(button, button.getX(), button.getY() + button.getHeight());
            }
        });
    }

    private void setEditButton(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainWindow.getmPopMenu().getEditMenu().show(button, button.getX() - button.getWidth(), button.getY() + button.getHeight());
            }
        });
    }

    private void setStepOverButton(JButton button) {
        button.setIcon(stepOverIcon);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                interpreter.stepOver();
            }
        });
    }

    private void setStepInButton(JButton button) {
        button.setIcon(stepIntoIcon);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                interpreter.stepIn();
            }
        });
    }

    private void setContinueButton(JButton button) {
        button.setIcon(continueIcon);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                interpreter.continueExecution();
            }
        });
    }

    private void setStopButton(JButton button) {
        button.setIcon(stopIcon);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (curThread != null && curThread.isAlive()) {
                    // interrupt the executing thread
                    debugOver();
                    curThread.interrupt();
                    curThread = null;
                }
            }
        });
    }

    private void setLexerButton(JButton button) {
        button.addActionListener(e -> {
            // 多线程
            new Thread(() -> {
                if (mainWindow.getEditPane().getText().isEmpty())//输入区为空，返回
                    return;
                if (!mainWindow.getFileOperation().save())//保存
                    return;
                mainWindow.getFileOperation().setOutputEmpty();//清空输出区

                String path = mainWindow.getPathLabel().getText();//获取路径
                //initialize a lexer
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                    Lexer lex = new Lexer(reader);
                    lex.addMessageListener(new ParserMessageListener());
                    lex.lex();
                } catch (IOException e1) {
                    mainWindow.getOutputPane().setText(e1.getMessage());
                    //e1.printStackTrace();
                }
            }).start();
        });
    }

    private void setParserButton(JButton button) {
        button.addActionListener(e -> new Thread(() -> {
            if (mainWindow.getEditPane().getText().isEmpty())
                return;
            if (!mainWindow.getFileOperation().save())//保存
                return;
            String path = mainWindow.getPathLabel().getText();
            //initialize a lexer
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                mainWindow.getOutputTabbedPane().setSelectedComponent(mainWindow.getParseOutputJSP());
                mainWindow.getFileOperation().setOutputEmpty();

                Lexer lex = new Lexer(reader);
                Parser myParser = new Parser(lex);

                myParser.addMessageListener(new ParserMessageListener());
                myParser.parse();
            } catch (IOException e1) {
                mainWindow.getOutputPane().setText(e1.getMessage());
                e1.printStackTrace();
            }
        }).start());
    }

    private void setExecuteButton(JButton button, boolean debug) {
        button.addActionListener(e -> new Thread(() -> {
            curThread = Thread.currentThread();
            if (mainWindow.getEditPane().getText().isEmpty())
                return;
            if (!mainWindow.getFileOperation().save())//保存
                return;
            String path = mainWindow.getPathLabel().getText();
            //initialize a lexer
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                mainWindow.getOutputTabbedPane().setSelectedComponent(mainWindow.getExecuteJPanel());
                mainWindow.getFileOperation().setOutputEmpty();

                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);
                interpreter = new Interpreter(parser);
                if (!debug) {
                    interpreter.addMessageListener(new ParserMessageListener());
                    ExecutorMessageListener executorMessageListener = new ExecutorMessageListener(mainWindow.getExecuteOutputPane());
                    interpreter.addMessageListener(executorMessageListener);
                    mainWindow.getParamTextField().addKeyListener(executorMessageListener);
                    //开始执行
                    interpreter.interpret();
                    mainWindow.getParamTextField().removeKeyListener(executorMessageListener);
                } else {
                    //debug模式
                    debuggerForm = new DebuggerForm(mainWindow);
                    debuggerForm.init();
                    //添加断点
                    List points = mainWindow.getmScrollPane().getLineNumList().getSelectedValuesList();
                    ArrayList<Breakpoint> breakpoints = new ArrayList<>();
                    for (Object point : points.toArray()) {
                        System.out.println((int) point);
                        breakpoints.add(new Breakpoint((int) point));
                    }
                    interpreter.initDebugger(breakpoints);
                    mainWindow.getmScrollPane().curLine = 0;

                    interpreter.addMessageListener(new ParserMessageListener());
                    executorMessageListener = new ExecutorMessageListener(mainWindow.getExecuteOutputPane());
                    interpreter.addMessageListener(executorMessageListener);
                    mainWindow.getParamTextField().addKeyListener(executorMessageListener);
                    //开始执行
                    interpreter.interpret();
                    mainWindow.getParamTextField().removeKeyListener(executorMessageListener);
                    debugOver();
                }
            } catch (IOException e1) {
                mainWindow.getOutputPane().setText(e1.getMessage());
                debugOver();
                e1.printStackTrace();
            }
        }).start());
    }

    private void debugOver() {
        setDebugEnabled(false);
        debuggerForm.close();
        executorMessageListener = null;
        //curThread=null;
        curLine = -1;
        mainWindow.getmScrollPane().freshList();
        mainWindow.getParamTextField().setEditable(false);
        mainWindow.getParamTextField().setBackground(MColor.paramTextFiledRunColor);
    }

    private class ExecutorMessageListener implements MessageListener, KeyListener {
        JTextPane executePane;
        public Message message;
        JTextField paramField = mainWindow.getParamTextField();

        public ExecutorMessageListener(JTextPane textField) {
            executePane = textField;
        }

        private void paneTextAppend(String str) {
            executePane.setText(executePane.getText() + str + '\n');
        }

        private void handleMessage() {
            Message.MessageType type = message.getType();
            switch (type) {
                case READ_INPUT: {
                    paramField.setEditable(true);
                    paramField.setText("");
                    paramField.setBackground(MColor.paramTextFiledWaitColor);
                    break;
                }
                case WRITE: {
                    String string = message.getBody().toString();
                    paneTextAppend(string);
                    break;
                }
                case INTERPRETER_SUMMARY: {
                    Object[] body = (Object[]) message.getBody();
                    Double exeElapsedTime = (Double) body[0];
                    int statusCode = (int) body[1];
                    String stringBuilder = "----Execute Elapsed Time: " + exeElapsedTime + "s -----\n" +
                            "---- Return Status Code----\n " + statusCode;
                    paneTextAppend(stringBuilder);
                    break;
                }
                case EXECUTION_ERROR: {
                    ExecutionError error = (ExecutionError) message.getBody();
                    String s = error.toString();
                    paneTextAppend(s);
                    break;
                }
                case SEMANTIC_ERROR: {
                    SemanticError error = (SemanticError) message.getBody();
                    String s = error.toString();
                    paneTextAppend(s);
                    break;
                }
                case FORCE_EXIT: {
                    String s = (String) message.getBody();
                    paneTextAppend(s);
                    break;
                }
                case SUSPEND_ON_TRAP: {
                    //System.out.println("stop");
                    curLine = (int) message.getBody();
                    mainWindow.getmScrollPane().freshList();
                    setDebugEnabled(true);
                    break;
                }
            }
        }

        @Override
        public void onMessageReceived(Message message) {
            this.message = message;
            handleMessage();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char key = e.getKeyChar();
            if (key == '\n' && message.getType().equals(Message.MessageType.READ_INPUT)) {
                paramField.setEditable(false);
                synchronized (message) {
                    paramField.setBackground(MColor.paramTextFiledRunColor);
                    message.setBody(paramField.getText());
                    message.notify();
                    System.out.println("notified " + paramField.getText());
                }
                //追加内容
                paneTextAppend(paramField.getText());
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    private class ParserMessageListener implements MessageListener {

        private void paneTextAppend(String str, JTextPane pane) {
            pane.setText(pane.getText() + str + '\n');
        }

        @Override
        public void onMessageReceived(Message message) {
            //消息种类
            Message.MessageType type = message.getType();
            //当前输出框内容
            //String curContent = mainWindow.getOutputPane().getText();
            String preContent = "";
            switch (type) {
                case LEXER_SUMMARY: {
                    //消息体
                    Object[] body = (Object[]) message.getBody();
                    Double lexElapsedTime = (Double) body[0];
                    ArrayList<Token> tokens = (ArrayList<Token>) body[1];

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("----Lex Elapsed Time: ").append(lexElapsedTime).append("s -----\n");
                    stringBuilder.append("----Tokens----\n\n");
                    for (Token token : tokens) {
                        stringBuilder.append(token.toString()).append('\n');
                    }
                    preContent = stringBuilder.toString();
                    mainWindow.getOutputPane().setText(preContent);
                    break;
                }
                case PARSER_SUMMARY: {
                    //消息体
                    Object[] body = (Object[]) message.getBody();
                    Double parseElapsedTime = (Double) body[0];
                    INode root = (INode) body[1];

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("----Parse Elapsed Time: ").append(parseElapsedTime).append("s -----\n");
                    stringBuilder.append("---- Tree----\n ").append(root.getAllChild()).append("\n");
//                    System.out.println(root.getSymbol().getSelfText());
                    preContent = stringBuilder.toString();
                    mainWindow.getParseOutputPane().setText(preContent);
                    mainWindow.getFileOperation().writeFile("src/test/res/GrammarTree.txt", root.getAllChild());
                    break;
                }
                case IO_ERROR: {
                    preContent += "\n-----IO Error----\n";
                    IOException exception = (IOException) message.getBody();
                    preContent += exception.getMessage();
                    mainWindow.getParseOutputPane().setText(preContent);
                    break;
                }
                case SYNTAX_PARSE_ERROR: {
                    preContent += "\n-----Syntax Parse Error----\n";
                    SyntaxError syntaxError = (SyntaxError) message.getBody();
                    preContent += syntaxError.getMessage();
                    mainWindow.getParseOutputPane().setText(preContent);
                    break;
                }
                case SYNTAX_LEX_ERROR: {
                    preContent += "\n-----Syntax Lex Error----\n";
                    SyntaxError syntaxError = (SyntaxError) message.getBody();
                    preContent += syntaxError.getMessage();
                    mainWindow.getOutputPane().setText(preContent);
                    break;
                }
                case SYS_ERROR: {
                    preContent += "\n-----System Error----\n";
                    Throwable sysError = (Throwable) message.getBody();
                    preContent += sysError.getMessage();
                    paneTextAppend(preContent, mainWindow.getOutputPane());
                    paneTextAppend(preContent, mainWindow.getParseOutputPane());
                    paneTextAppend(preContent, mainWindow.getExecuteOutputPane());
                    break;
                }
            }
        }

    }

}
