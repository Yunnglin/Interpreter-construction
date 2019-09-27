package interpreter.lexer;

import interpreter.Const;
import interpreter.exception.SyntaxError;
import interpreter.lexer.token.IntNum;
import interpreter.lexer.token.Real;
import interpreter.lexer.token.Token;
import interpreter.lexer.token.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Lexer {
    private int line;
    private char peek;
    private Hashtable keyWords;
    private Token curToken;
    private BufferedReader reader;

    private static final char EOF = 0;

    private void reserve(Word w){
        keyWords.put(w.getLexeme(), w);
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
        // 初始化保留字
        reserve(new Word(Const.IF, "if"));
        reserve(new Word(Const.ELSE, "else"));
        reserve(new Word(Const.WHILE, "while"));
        reserve(new Word(Const.READ, "read"));
        reserve(new Word(Const.WRITE, "write"));
        reserve(new Word(Const.INT, "int"));
        reserve(new Word(Const.REAL, "real"));
    }

    public boolean isKeyWord(Word w) {
        return this.keyWords.containsKey(w.getLexeme());
    }

    public boolean isKeyWord(String s) {
        return this.keyWords.containsKey(s);
    }

    public void getNextChar() throws IOException {
        // 读取下一个字符
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
        // 读取下一个字符并判断是否为 c
        getNextChar();
        return c == peek;
    }

    public ArrayList<Token> getAllToken() throws IOException, SyntaxError {
        ArrayList<Token> tokens = new ArrayList<Token>();
        Token token = null;
        while((token = this.getNextToken())!=null) {
            tokens.add(token);
        }
        return tokens;
    }

    public Token getNextToken() throws SyntaxError, IOException {
        // 核心代码，解析得到新的token
        String whitespaces = " \t\r\n";
        int comments = 0;
        // 读取新的非空字符
        while(whitespaces.indexOf(peek) != -1) {
            if(peek == '\n') {
                ++line;
            }
            getNextChar();
        }
        if(peek == EOF) {
            return null;
        }
        // 解析操作符、注释等
        switch (peek) {
            case '=':
                if(getNextChar('=')) {
                    return new Word(Const.EQ, "==");
                } else {
                    return new Token(Const.ASSIGN);
                }
            case '<':
                if(getNextChar('>')) {
                    return new Word(Const.NEQ, "<>");
                } else {
                    return new Token(Const.LESS_THAN);
                }
            case '/':
                if(getNextChar('*')) {
                    // 进入注释
                    ++comments;
                    while(comments != 0) {
                        if(getNextChar(EOF)) {
                            return null;
                        } else if (peek == '*'){
                            if(getNextChar(EOF)) {
                                return null;
                            } else if (peek == '/') {
                                --comments;
                            }
                        }
                    }
                    getNextChar();
                } else {
                    return new Token(Const.DIVIDE);
                }
                break;
            case '*':
                getNextChar();
                return new Token(Const.MULTIPLY);
            case '+':
                getNextChar();
                return new Token(Const.SUM);
            case '(':
                getNextChar();
                return new Token(Const.L_PARENTHESES);
            case ')':
                getNextChar();
                return new Token(Const.R_PARENTHESES);
            case '{':
                getNextChar();
                return new Token(Const.L_BRACES);
            case '}':
                getNextChar();
                return new Token(Const.R_BRACES);
            case '[':
                getNextChar();
                return new Token(Const.L_SQUARE_BRACKETS);
            case ']':
                getNextChar();
                return new Token(Const.R_SQUARE_BRACKETS);
            case ';':
                getNextChar();
                return new Token(Const.SEMICOLON);
            case '-':
                // TODO 考虑是否和整数与实数的解析同时进行，即词法阶段完成对负数的识别
                getNextChar();
              return new Token(Const.SUB);
        }

        // TODO 解析整数和实数
        if(Character.isDigit(peek)) {
            String num;
            num="";
            do{
                num+=peek;
                getNextChar();
            }while (Character.isDigit(peek));
            if(peek!='.') return new IntNum(Integer.parseInt(num));
            num+=peek;
            getNextChar();
            //记录行数 报错退出
            if(!Character.isDigit(peek)) throw SyntaxError.newConstantError(num, line);
            for(;;){
                getNextChar();
                if(!Character.isDigit(peek)) break;
                num+=peek;
            }
            return new Real(Float.parseFloat(num));
        }

        // TODO 解析标识符和保留字等
        if(Character.isLetter(peek)) {
            String name="";
            do{
                if(peek=='_'){
                    //保留字
                    if(isKeyWord(name)) return (Word)keyWords.get(name);
                    name+=peek;
                    getNextChar();
                    if(!Character.isDigit(peek)&&!Character.isLetter(peek)&&peek!='_'){
                        throw SyntaxError.newIdentifierError(name, line);
                    }
                }else{
                    name+=peek;
                    getNextChar();
                }
            }while (Character.isDigit(peek)||Character.isLetter(peek)||peek=='_');
            return new Word(Const.IDENTIFIER,name);
        }

        // 无法识别的符号
        throw SyntaxError.newLexicalError(Character.toString(peek), line);
    }
}
