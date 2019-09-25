package interpreter.lexer.token;

public class Token {
    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    // TODO
    protected int tag;
    public Token(int t) {
        tag = t;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tag=" + tag +
                '}';
    }
}
