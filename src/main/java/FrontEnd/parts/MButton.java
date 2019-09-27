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
    public MButton(MainWindow mainWindow){
        this.mainWindow = mainWindow;

    }

    public void init(){
        setEditButton(mainWindow.getEditButton());
        setFileButton(mainWindow.getFileButton());
        setLexerButton(mainWindow.getLexerBtn());
    }

    public void setFileButton(JButton button){
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MPopMenu().getFileMenu().show(button,button.getX(),button.getY()+button.getHeight());
            }
        });
    }

    public void setEditButton(JButton button){
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MPopMenu().getEditMenu().show(button,button.getX()-button.getWidth(),button.getY()+button.getHeight());
            }
        });
    }

    public void setLexerButton(JButton button){
        button.addActionListener(e -> {
            //get file path
            String path = mainWindow.getPathLabel().getText();
            File file=new File(path);
            //save the text
            String text = mainWindow.getEditPane().getText();
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
            StringBuilder stringBuilder = new StringBuilder();
            try {
                tokens = lex.getAllToken();
                for(Token token: tokens){
                    stringBuilder.append(token.toString()).append('\n');
                }
                //show the result
                mainWindow.getOutputPane().setText(stringBuilder.toString());
            } catch (IOException | SyntaxError e1) {
                e1.printStackTrace();
            }

        });
    }

}
