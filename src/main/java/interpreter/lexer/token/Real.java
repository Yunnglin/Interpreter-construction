package interpreter.lexer.token;

import interpreter.Const.TokenTag;

public class Real extends Token {

    private double value;

    public Real(double val) {
        super(TokenTag.REAL_NUMBER);
        value = val;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (Real, value=%f)", value);
    }
}
