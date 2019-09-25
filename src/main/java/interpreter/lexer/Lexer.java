package interpreter.lexer;

import interpreter.exception.SyntaxError;
import interpreter.lexer.token.Token;
import interpreter.lexer.token.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
    private int line;
    private char peek;
    private Hashtable keyWords;
    private Token curToken;
    private BufferedReader reader;

    public static final char EOF = 0;

    private void reserve(Word w){
//        keyWords.put(w.lexeme, w);
    }

    public Lexer() {
        this.line = 0;
        this.peek = ' ';
        this.keyWords = new Hashtable();
        this.reader = null;
        this.initKeyWords();
    }

    public Lexer(BufferedReader reader) {
        this();
        this.reader = reader;
    }

    private void initKeyWords() {
        // TODO 初始化保留字
    }

    public boolean isKeyWord(Word w) {
//        return this.keyWords.containsKey(w.lexeme);
        return false;
    }

    public boolean isKeyWord(String s) {
        return this.keyWords.containsKey(s);
    }

    public void getNextChar() throws IOException {
        // TODO 读取下一个字符
        if(reader != null) {
            int ch = reader.read();
            if (ch == -1){
                peek = EOF;
            } else {
                peek = (char) ch;
            }
        }
    }

    public boolean getNextChar(char c) throws IOException {
        // TODO 读取下一个字符并判断是否为 c
        getNextChar();
        if( peek != c ) {
            return false;
        }
        return true;
    }

    public Token getNextToken() throws SyntaxError {
        // 核心代码，解析得到新的token

        // TODO 读取新的非空字符

        // TODO 解析操作符、注释等

        // TODO 解析整数和实数
        if(Character.isDigit(peek)) {

        }

        // TODO 解析标识符和保留字等
        if(Character.isLetter(peek)) {

        }

        // TODO 无法识别的符号

        return null;
    }


}
