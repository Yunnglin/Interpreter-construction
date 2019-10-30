package interpreter.lexer.token;

import interpreter.grammar.TokenTag;

public class Token {

    protected TokenTag tag;     // token tag (specify the type of token)
    protected int lineNum;

    public Token(TokenTag t, int line) {
        tag = t;
        lineNum = line;
    }

    public TokenTag getTag() {
        return this.tag;
    }

    public int getTagValue() {
        return tag.getCode();
    }

    public String getTagText() {
        return tag.getText();
    }

    public void setTag(TokenTag tag) {
        this.tag = tag;
    }

    public int getLineNum() {
        return this.lineNum;
    }

    @Override
    public String toString() {
        return "Token{" + "tag=" + tag.getCode()
                + ", " + tag.getText() + ")";
    }
}
