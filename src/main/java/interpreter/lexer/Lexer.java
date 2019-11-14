package interpreter.lexer;

import interpreter.grammar.TokenTag;
import interpreter.exception.SyntaxError;
import interpreter.lexer.token.*;
import interpreter.utils.AsciiUtils;
import message.Message;
import message.MessageListener;
import message.MessageProducer;
import message.MessageHandler;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class Lexer implements MessageProducer {
    private int curLine;
    private char peek;
    private Token curToken;
    private Reader reader;
    private MessageHandler lexHandler;

    private static final char EOF = 0;

    /**
     * reserve a new key word
     * @param tag the TokenTag that identifies the key word
     */
    public void reserve(TokenTag tag){
        TokenTag.RESERVED_WORDS.put(tag.getText(), tag);
    }

    /**
     * Constructor
     * set the initial peek a whitespace
     */
    private Lexer() {
        this.curLine = 1;
        this.peek = ' ';
        this.reader = null;
        this.lexHandler = new MessageHandler();
    }

    /**
     * Constructor
     * set the reader of file input stream of the input source code
     * @param reader
     */
    public Lexer(Reader reader) {
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
        } else {
            peek = EOF;
        }
    }

    /**
     * read a char from source code and check if it is a specified char
     * @param c the specified char
     * @return whether the char read from source is the specified char
     * @throws IOException
     */
    public boolean getNextChar(char c) throws IOException {
        // read the next character and check whether it is c
        getNextChar();
        return c == peek;
    }

    /**
     * do lexing, get all tokens lexed from source code
     * @return the list of tokens
     * @throws IOException
     * @throws SyntaxError the error occurred while lexing
     */
    public ArrayList<Token> lex() {
        // measure how much time lexer spent
        long lexStartTime = System.currentTimeMillis();

        ArrayList<Token> tokens = new ArrayList<Token>();
        Token token = null;
        // do lexing
        try {

            while((token = this.getNextToken()).getTag() != TokenTag.PROG_END) {
                tokens.add(token);
            }
            tokens.add(token);

            // lex part finished, sending summary message.
            double lexElapsedTime = (System.currentTimeMillis() - lexStartTime) / 1000.0;
            Object[] lexMsgBody = new Object[] {lexElapsedTime, tokens};
            this.sendMessage(new Message(Message.MessageType.LEXER_SUMMARY, lexMsgBody));

        } catch (IOException e) {
            // catch an io error
            Message errorMsg = new Message(Message.MessageType.IO_ERROR, e);
            this.sendMessage(errorMsg);
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (SyntaxError syntaxError) {
            // catch a syntax error when lexing
            Message errorMsg = new Message(Message.MessageType.SYNTAX_LEX_ERROR, syntaxError);
            this.sendMessage(errorMsg);
            System.out.println(syntaxError.getMessage());
            syntaxError.printStackTrace();
        } catch (Exception | Error syse) {
            // catch a unexpected error, may caused by our code
            Message systemError = new Message(Message.MessageType.SYS_ERROR, syse);
            this.sendMessage(systemError);
            System.out.println(syse.getMessage());
            syse.printStackTrace();
        }

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
                // end of file
                return (curToken = new Token(TokenTag.PROG_END, curLine));
            }
            // 解析操作符、注释等
            switch (peek) {
                case '=':
                    if(getNextChar('=')) {
                        getNextChar();
                        return (curToken = new Token(TokenTag.EQ, curLine));
                    } else {
                        return (curToken = new Token(TokenTag.ASSIGN, curLine));
                    }
                case '<':
                    if(getNextChar('>')) {
                        getNextChar();
                        return (curToken = new Token(TokenTag.NEQ, curLine));
                    } else if (peek == '=') {
                        getNextChar();
                        return (curToken = new Token(TokenTag.LEQ, curLine));
                    } else {
                        return (curToken = new Token(TokenTag.LESS_THAN, curLine));
                    }
                case '>':
                    if (getNextChar('=')) {
                        getNextChar();
                        return (curToken = new Token(TokenTag.GEQ, curLine));
                    } else {
                        return (curToken = new Token(TokenTag.GREATER_THAN, curLine));
                    }
                case '/':
                    if(getNextChar('*')) {
                        // 进入注释
                        ++comments;
                        while(comments != 0) {
                            if(getNextChar(EOF)) {
                                // end of file
                                return (curToken = new Token(TokenTag.PROG_END, curLine));
                            } else if (peek == '*'){
                                if(getNextChar(EOF)) {
                                    // end of file
                                    return (curToken = new Token(TokenTag.PROG_END, curLine));
                                } else if (peek == '/') {
                                    --comments;
                                }
                            } else if (peek == '/') {
                                if(getNextChar(EOF)) {
                                    // end of file
                                    return (curToken = new Token(TokenTag.PROG_END, curLine));
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
                            return (curToken = new Token(TokenTag.PROG_END, curLine));
                        } else {
                            ++curLine;
                        }
                        getNextChar();
                        goon = true;
                        continue;
                    } else {
                        return (curToken = new Token(TokenTag.DIVIDE, curLine));
                    }
                case '*':
                    getNextChar();
                    return (curToken = new Token(TokenTag.MULTIPLY, curLine));
                case '+':
                    getNextChar();
                    return (curToken = new Token(TokenTag.SUM, curLine));
                case '(':
                    getNextChar();
                    return (curToken = new Token(TokenTag.L_PARENTHESES, curLine));
                case ')':
                    getNextChar();
                    return (curToken = new Token(TokenTag.R_PARENTHESES, curLine));
                case '{':
                    getNextChar();
                    return (curToken = new Token(TokenTag.L_BRACES, curLine));
                case '}':
                    getNextChar();
                    return (curToken = new Token(TokenTag.R_BRACES, curLine));
                case '[':
                    getNextChar();
                    return (curToken = new Token(TokenTag.L_SQUARE_BRACKETS, curLine));
                case ']':
                    getNextChar();
                    return (curToken = new Token(TokenTag.R_SQUARE_BRACKETS, curLine));
                case ';':
                    getNextChar();
                    return (curToken = new Token(TokenTag.SEMICOLON, curLine));
                case ',':
                    getNextChar();
                    return (curToken = new Token(TokenTag.COMMA, curLine));
                case '-':
                    // 负号和减法在词法阶段相同，负数识别在语法分析阶段完成
                    getNextChar();
                    return (curToken = new Token(TokenTag.SUB, curLine));
                case '&':
                    if (getNextChar('&')) {
                        getNextChar();
                        return (curToken = new Token(TokenTag.AND, curLine));
                    } else {
                        // 无位与操作 &，故抛出异常
                        throw SyntaxError.newLexicalError(Character.toString(peek), curLine);
                    }
                case '|':
                    if (getNextChar('|')) {
                        getNextChar();
                        return (curToken = new Token(TokenTag.OR, curLine));
                    } else {
                        // 无位或操作 |，故抛出异常
                        throw SyntaxError.newLexicalError(Character.toString(peek), curLine);
                    }
                case '!':
                    getNextChar();
                    return (curToken = new Token(TokenTag.NOT, curLine));
                case '\'':
                    // 字符
                    StringBuilder strBuilder = new StringBuilder();
                    getNextChar();
                    if (peek == EOF || peek == '\n') {
                        // missing terminating '
                        throw SyntaxError.newCharTerminatingMarkError("'", curLine);
                    } else if (peek == '\'') {
                        // empty char constant
                        throw SyntaxError.newEmptyCharacter(curLine);
                    }
                    strBuilder.append(peek);
                    boolean escape = peek == '\\';
                    boolean terminate = !escape;
                    // 循环直到找到非转义的 '
                    while(!getNextChar('\'') || !terminate) {
                        terminate = true;
                        if (peek == '\n' || peek == EOF) {
                            throw SyntaxError.newCharTerminatingMarkError("\'"+strBuilder.toString(), curLine);
                        }
                        strBuilder.append(peek);
                    }
                    getNextChar();
                    String charStr = strBuilder.toString();
                    if (!escape) {
                        // 非转义字符长为1个字符
                        String asciiString = AsciiUtils.convert2AsciiString(charStr);
                        if (asciiString.length() > 1) {
                            throw SyntaxError.newMultiCharacterChar(charStr, curLine);
                        }

                        return (curToken = new Text(TokenTag.CHARACTER, curLine, charStr));
                    } else {
                        // 转义字符，字符的最大数量由 MAX_CHARACTERS_IN_ESCAPE 定义
                        if (strBuilder.length() > Text.MAX_CHARACTERS_IN_ESCAPE) {
                            throw SyntaxError.newMultiCharacterChar(charStr, curLine);
                        }
                        if (strBuilder.length() == 1) {
                            throw SyntaxError.newCharTerminatingMarkError(charStr, curLine);
                        }

                        Character character = AsciiUtils.convert2EscapeCharacter(charStr.substring(1));
                        if (character == null) {
                            throw SyntaxError.newIllegalEscapeCharacterError(charStr, curLine);
                        }

                        return (curToken = new Text(TokenTag.CHARACTER, curLine, charStr, character.toString()));
                    }
                case '\"':
                    // 字符串
                    StringBuilder rawBuilder = new StringBuilder();     // 词素
                    StringBuilder valueBuilder = new StringBuilder();   // 字符串内容
                    StringBuilder escapeBuilder = null;                 // 当前转义符构造器
                    Character curEscape = null;                         // 当前转义符
                    boolean strTerminated = true;                       // 遇到 " 是否终结
                    boolean hasEscape = false;                          // 是否处于转义字符构造中
                    while(!getNextChar('\"') || !strTerminated) {
                        if (peek == '\n' || peek == EOF) {
                            throw SyntaxError.newStrTerminatingMarkError(rawBuilder.toString(), curLine);
                        }
                        rawBuilder.append(peek);
                        if (!hasEscape && peek == '\\') {
                            // 转义字符
                            strTerminated = false;
                            hasEscape = true;
                            escapeBuilder = new StringBuilder();
                        } else if (hasEscape) {
                            // 构造当前最长的转义符
                            strTerminated = true;
                            escapeBuilder.append(peek);
                            Character escapeCharacter = AsciiUtils.convert2EscapeCharacter(escapeBuilder.toString());
                            if (escapeCharacter != null) {
                                curEscape = escapeCharacter;
                            } else {
                                if (curEscape != null) {
                                    valueBuilder.append(curEscape);
                                    curEscape = null;
                                    strTerminated = true;
                                    hasEscape = false;
                                } else {
                                    // cannot construct a escape character
                                    throw SyntaxError.newIllegalEscapeCharacterError("\\"+peek, curLine);
                                }
                                // 将当前字符加入
                                valueBuilder.append(peek);
                            }
                        } else {
                            // 单字符
                            valueBuilder.append(peek);
                        }
                    }

                    if (curEscape != null) {
                        valueBuilder.append(curEscape);
                    }
                    // 添加字符串末尾 \0
                    valueBuilder.append('\0');
                    String valueStr = AsciiUtils.convert2AsciiString(valueBuilder.toString());

                    getNextChar();
                    return (curToken = new Text(TokenTag.STRING, curLine, rawBuilder.toString(), valueStr));
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
                        return (curToken = new IntNum(val, curLine));
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
                    return (curToken = new Real(val, curLine));
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
                    return (curToken = new Word(TokenTag.RESERVED_WORDS.get(name.toString()), curLine, name.toString()));
                }
                return (curToken = new Word(TokenTag.IDENTIFIER, curLine, name.toString()));
            }

            // 无法识别的符号
            throw SyntaxError.newLexicalError(Character.toString(peek), curLine);
        }
        return (curToken = new Token(TokenTag.PROG_END, curLine));
    }

    /**
     * add a listener to lexing component
     * @param listener
     */
    @Override
    public void addMessageListener(MessageListener listener) {
        lexHandler.addListener(listener);
    }

    /**
     * remove a listener from the list of lexing component
     * @param listener
     */
    @Override
    public void removeMessageListener(MessageListener listener) {
        lexHandler.removeListener(listener);
    }

    /**
     * send a message to listeners of lexing component
     * @param message
     */
    @Override
    public void sendMessage(Message message) {
        lexHandler.sendMessage(message);
    }
}
