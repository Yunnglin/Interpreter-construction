package interpreter.lexer.token;


import interpreter.Const;

public class IntNum extends Token {
    // TODO
    private int value;

    public IntNum(int val) {
        super(Const.INTEGER);
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
        return "IntNum{" +
                "value=" + value +
                '}';
    }
}
