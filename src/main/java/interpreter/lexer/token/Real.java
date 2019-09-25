package interpreter.lexer.token;

import interpreter.Const;

public class Real extends Token {
    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Real{" +
                "value=" + value +
                '}';
    }

    public void setValue(double value) {
        this.value = value;
    }

    // TODO
    private double value;

    public Real(double val) {
        super(Const.REAL_NUMBER);
        value = val;
    }
}
