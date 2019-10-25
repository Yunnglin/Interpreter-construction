package interpreter.lexer;

import interpreter.grammar.TokenTag;
import interpreter.exception.SyntaxError;
import interpreter.lexer.token.IntNum;
import interpreter.lexer.token.Real;
import interpreter.lexer.token.Token;
import interpreter.lexer.token.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
    private int curLine;
    private char peek;
    private Token curToken;
    private BufferedReader reader;

    private static final char EOF = 0;

    private void reserve(TokenTag tag){
        TokenTag.RESERVED_WORDS.put(tag.getText(), tag);
    }

    /**
     * Constructor
     * set the initial peek a whitespace
     */
    public Lexer() {
        this.curLine = 1;
        this.peek = ' ';
        this.reader = null;
    }

    /**
     * Constructor
     * set the reader of file input stream of the input source code
     * @param reader
     */
    public Lexer(BufferedReader reader) {
        this();
        this.reader = reader;
    }

    /**
     * check whether the word token is a key word
     * @param w the word token to check
     * @return whether it is key word
     */
    public boolean isKeyWord(Word w) {
        return TokenTag.RESERVED_WORDS.containsKey(w.getLexeme());
    }

    /**
     * check whether the string of a identifier is a key word
     * @param s the string to check
     * @return whether it is key word
     */
    public boolean isKeyWord(String s) {
        return TokenTag.RESERVED_WORDS.containsKey(s);
    }

    /**
     * read a char from source code
     * @throws IOException
     */
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

    /**
     * read a char from source code and check if it is a specified char
     * @param c the specified char
     * @return whether the char read from source is the specified char
     * @throws IOException
     */
    public boolean getNextChar(char c) throws IOException {
        // 读取下一个字符并判断是否为 c
        getNextChar();
        return c == peek;
    }

    /**
     * get all tokens lexed from source code
     * @return the list of tokens
     * @throws IOException
     * @throws SyntaxError the error occurred while lexing
     */
    public ArrayList<Token> getAllToken() throws IOException, SyntaxError {
        ArrayList<Token> tokens = new ArrayList<Token>();
        Token token = null;
        while((token = this.getNextToken()).getTag() != TokenTag.PROG_END) {
            tokens.add(token);
        }
        tokens.add(token);
        return tokens;
    }

    /**
     * read char from source code and do lexing till a token formed
     * @return the new token
     * @throws SyntaxError the error occurred while lexing
     * @throws IOException
     */
    public Token getNextToken() throws SyntaxError, IOException {
        boolean goon = true;                    // 一遍扫描后是否还需要继续扫描
        while(goon){
            goon = false;
            // 核心代码，解析得到新的token

            String whitespaces = " \t\r\n";     // 空白字符
            int comments = 0;                   // 注释层级
            // 读取新的非空字符
            while(whitespaces.indexOf(peek) != -1) {
                if(peek == '\n') {
                    ++curLine;
                }
                getNextChar();
            }
            if(peek == EOF) {
                return new Token(TokenTag.PROG_END, curLine);
            }
            // 解析操作符、注释等
            switch (peek) {
                case '=':
                    if(getNextChar('=')) {
                        getNextChar();
                        return new Token(TokenTag.EQ, curLine);
                    } else {
                        return new Token(TokenTag.ASSIGN, curLine);
                    }
                case '<':
                    if(getNextChar('>')) {
                        getNextChar();
                        return new Token(TokenTag.NEQ, curLine);
                    } else {
                        return new Token(TokenTag.LESS_THAN, curLine);
                    }
                case '>':
                    getNextChar();
                    return new Token(TokenTag.GREATER_THAN, curLine);
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
                            } else if (peek == '/') {
                                if(getNextChar(EOF)) {
                                    return null;
                                } else if (peek == '*') {
                                    ++comments;
                                }
                            } else if (peek == '\n') {
                                ++curLine;
                            }
                        }
                        getNextChar();
                        goon = true;
                        continue;
                    } else if (peek == '/'){
                        // 单行注释
                        getNextChar();
                        while(peek != '\n' && peek != EOF){
                            getNextChar();
                        }
                        if (peek == EOF) {
                            return null;
                        } else {
                            ++curLine;
                        }
                        getNextChar();
                        goon = true;
                        continue;
                    } else {
                        return new Token(TokenTag.DIVIDE, curLine);
                    }
                case '*':
                    getNextChar();
                    return new Token(TokenTag.MULTIPLY, curLine);
                case '+':
                    getNextChar();
                    return new Token(TokenTag.SUM, curLine);
                case '(':
                    getNextChar();
                    return new Token(TokenTag.L_PARENTHESES, curLine);
                case ')':
                    getNextChar();
                    return new Token(TokenTag.R_PARENTHESES, curLine);
                case '{':
                    getNextChar();
                    return new Token(TokenTag.L_BRACES, curLine);
                case '}':
                    getNextChar();
                    return new Token(TokenTag.R_BRACES, curLine);
                case '[':
                    getNextChar();
                    return new Token(TokenTag.L_SQUARE_BRACKETS, curLine);
                case ']':
                    getNextChar();
                    return new Token(TokenTag.R_SQUARE_BRACKETS, curLine);
                case ';':
                    getNextChar();
                    return new Token(TokenTag.SEMICOLON, curLine);
                case ',':
                    getNextChar();
                    return new Token(TokenTag.COMMA, curLine);
                case '-':
                    // 负号和减法在词法阶段相同，负数识别在语法分析阶段完成
                    getNextChar();
                    return new Token(TokenTag.SUB, curLine);
            }

            // 解析整数和实数
            if(Character.isDigit(peek)) {
                StringBuilder num = new StringBuilder();
                do{
                    num.append(peek);
                    getNextChar();
                }while (Character.isDigit(peek));
                if(peek!='.') {
                    try {
                        int val = Integer.parseInt(num.toString());
                        return new IntNum(val, curLine);
                    } catch (NumberFormatException e) {
                        throw SyntaxError.newConstantError(num.toString(), curLine);
                    }
                }
                num.append(peek);
                getNextChar();
                //记录行数 报错退出
                if(!Character.isDigit(peek)) throw SyntaxError.newConstantError(num.toString(), curLine);
                for(;;){
                    num.append(peek);
                    getNextChar();
                    if(!Character.isDigit(peek)) break;
                }
                try {
                    double val = Double.valueOf(num.toString());
                    return new Real(val, curLine);
                } catch (NumberFormatException e) {
                    throw SyntaxError.newConstantError(num.toString(), curLine);
                }
            }

            // 解析标识符和保留字等
            if(Character.isLetter(peek)) {
                StringBuilder name = new StringBuilder();
                do{
                    if(peek=='_'){
                        name.append(peek);
                        getNextChar();
                        if(!Character.isDigit(peek)&&!Character.isLetter(peek)&&peek!='_'){
                            throw SyntaxError.newIdentifierError(name.toString(), curLine);
                        }
                    }else{
                        name.append(peek);
                        getNextChar();
                    }
                }while (Character.isDigit(peek)||Character.isLetter(peek)||peek=='_');
                if(isKeyWord(name.toString())) {
                    return new Word(TokenTag.RESERVED_WORDS.get(name.toString()), curLine, name.toString());
                }
                return new Word(TokenTag.IDENTIFIER, curLine, name.toString());
            }

            // 无法识别的符号
            throw SyntaxError.newLexicalError(Character.toString(peek), curLine);
        }
        return new Token(TokenTag.PROG_END, curLine);
    }
}
