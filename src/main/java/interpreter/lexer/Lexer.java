package interpreter.lexer;

import interpreter.exception.SyntaxError;
import interpreter.lexer.token.Token;
import interpreter.lexer.token.Word;

import java.io.BufferedReader;
import java.util.Hashtable;

public class Lexer {
    private int line;
    private char peek;
    private Hashtable keyWords = new Hashtable();
    private Token curToken;
    private BufferedReader reader;

    private void reserve(Word w){
        // TODO 保留关键字
    }

    public Lexer() {
        // TODO 无参构造函数
    }

    public Lexer(BufferedReader reader) {
        // TODO 字符流reader作参数的构造函数
    }

    private void initKeyWords() {
        // TODO 初始化保留字
    }

    public boolean isKeyWord(Word w) {
        // TODO 检查是否为保留字

        return false;
    }

    public void getNextChar() {
        // TODO 读取下一个字符
    }

    public boolean getNextChar(char c) {
        // TODO 读取下一个字符并判断是否为 c

        return false;
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
