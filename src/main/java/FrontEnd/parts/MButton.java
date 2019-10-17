package FrontEnd.parts;

import FrontEnd.MainWindow;
import interpreter.exception.SyntaxError;
import interpreter.lexer.Lexer;
import interpreter.lexer.token.Token;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MButton {
    private MainWindow mainWindow;

    public MButton(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void init() {
        setEditButton(mainWindow.getEditButton());
        setFileButton(mainWindow.getFileButton());
        setLexerButton(mainWindow.getLexerBtn());
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
                mainWindow.getFileOperation().save();
                String path = mainWindow.getPathLabel().getText();
                //initialize a lexer
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                    Lexer lex = new Lexer(reader);
                    StringBuilder stringBuilder = new StringBuilder();
                    ArrayList<Token> tokens = lex.getAllToken();
                    for (Token token : tokens) {
                        stringBuilder.append(token.toString()).append('\n');
                    }
                    mainWindow.getOutputPane().setText(stringBuilder.toString());
                } catch (IOException | SyntaxError e1) {
                    mainWindow.getOutputPane().setText(e1.getMessage());
                    e1.printStackTrace();
                }
            }).start();
        });
    }

}
