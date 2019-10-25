package interpreter.lexer.token;


import interpreter.grammar.TokenTag;

public class IntNum extends Token {

    private int value;

    public IntNum(int val, int line) {
        super(TokenTag.INTEGER, line);
        value = val;
    }
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (Integer, value=%d)", value);
    }
}
