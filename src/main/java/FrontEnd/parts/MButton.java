package FrontEnd.parts;

import FrontEnd.MainWindow;
import interpreter.exception.SyntaxError;
import interpreter.lexer.Lexer;
import interpreter.lexer.token.Token;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
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
            //get file path
            String path = mainWindow.getPathLabel().getText();
            File file = new File(path);
            //save the text
            String text = mainWindow.getEditPane().getText();
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(text);
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            //read the file
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                //initialize a lexer
                try {
                    Lexer lex = new Lexer(reader);
                    ArrayList<Token> tokens = null;
                    StringBuilder stringBuilder = new StringBuilder();
                    tokens = lex.getAllToken();
                    for (Token token : tokens) {
                        stringBuilder.append(token.toString()).append('\n');
                        //show the result
                        mainWindow.getOutputPane().setText(stringBuilder.toString());
                    }
                } catch (IOException | SyntaxError e1) {
                    mainWindow.getOutputPane().setText(e1.getMessage());
                    e1.printStackTrace();
                } finally {
                    reader.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }

        });
    }

}
