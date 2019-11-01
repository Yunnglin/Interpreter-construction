package FrontEnd.parts;

import FrontEnd.MainWindow;
import interpreter.exception.SyntaxError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.subExecutor.E;
import interpreter.env.Env;
import interpreter.executor.subExecutor.ReturnStmt;
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

public class MButton {
    private MainWindow mainWindow;
    private INode rootNode = null;

    public MButton(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void init() {
        setEditButton(mainWindow.getEditButton());
        setFileButton(mainWindow.getFileButton());
        setLexerButton(mainWindow.getLexerBtn());
        setParserButton(mainWindow.getParserBtn());
        setExecuteButton(mainWindow.getExecuteBtn());
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
                    StringBuilder stringBuilder = new StringBuilder();
                    ArrayList<Token> tokens = lex.getAllToken();
                    for (Token token : tokens) {
                        stringBuilder.append(token.toString()).append('\n');
                    }
                    //设置词法分析结果区
                    mainWindow.getOutputPane().setText(stringBuilder.toString());
                } catch (IOException | SyntaxError e1) {
                    mainWindow.getOutputPane().setText(e1.getMessage());
                    e1.printStackTrace();
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
                Lexer lex = new Lexer(reader);
                Parser myParser = new Parser(lex);
                myParser.addMessageListener(new ParserMessageListener());
                mainWindow.getFileOperation().setOutputEmpty();
                myParser.parse();
            } catch (IOException e1) {
                mainWindow.getOutputPane().setText(e1.getMessage());
                e1.printStackTrace();
            }
        }).start());
    }

    private void setExecuteButton(JButton button) {
        button.addActionListener(e -> new Thread(() -> {
            if (mainWindow.getEditPane().getText().isEmpty())
                return;
            if (!mainWindow.getFileOperation().save())//保存
                return;
            String path = mainWindow.getPathLabel().getText();
            //initialize a lexer
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                Lexer lex = new Lexer(reader);
                Parser myParser = new Parser(lex);
                myParser.addMessageListener(new ParserMessageListener());
                mainWindow.getFileOperation().setOutputEmpty();
                myParser.parse();
                startExecute(rootNode);
            } catch (IOException e1) {
                mainWindow.getOutputPane().setText(e1.getMessage());
                e1.printStackTrace();
            }
        }).start());
    }

    private void startExecute(INode root) {
        if (root == null) {
            return;
        }
        new Thread(() -> {
            Env env = new Env();
            ExecutorMessageListener executorMessageListener = new ExecutorMessageListener();
            env.addMessageListener(executorMessageListener);
            mainWindow.getExecuteOutputPane().addKeyListener(executorMessageListener);
            BaseExecutor baseExecutor = new E(env);
            try {
                baseExecutor.Execute(root);
                env.runProgram();
            } catch (Exception | ReturnStmt.ReturnSignal e) {
                e.printStackTrace();
            }
            mainWindow.getExecuteOutputPane().removeKeyListener(executorMessageListener);
        }).start();

    }

    private class ExecutorMessageListener implements MessageListener, KeyListener {
        JTextPane executePane = mainWindow.getExecuteOutputPane();
        Message message;
        StringBuilder sb;
        boolean flag = true;

        private void paneTextAppend(String str) {
            executePane.setText(executePane.getText() + '\n' + str);
        }

        private void handleMessage() {
            Message.MessageType type = message.getType();
            String preContent = "";
            switch (type) {
                case READ_INPUT: {
                    sb = new StringBuilder();
                    executePane.setEditable(true);
                    break;

                }
                case WRITE: {
                    String string = message.getBody().toString();
                    paneTextAppend(string);
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
                executePane.setEditable(false);
                synchronized (message) {
                    message.setBody(sb.toString());
                    message.notify();
                    System.out.println("notified " + sb.toString());
                }
                //追加内容
                paneTextAppend(sb.toString());
            } else {
                sb.append(key);

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
                    float lexElapsedTime = (float) body[0];
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
                    float parseElapsedTime = (float) body[0];
                    INode root = (INode) body[1];

                    //执行
                    rootNode = root;

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
                case SYNTAX_ERROR: {
                    preContent += "\n-----Syntax Error----\n";
                    SyntaxError syntaxError = (SyntaxError) message.getBody();
                    preContent += syntaxError.getMessage();
                    mainWindow.getParseOutputPane().setText(preContent);
                    break;
                }
            }
        }

    }

}
