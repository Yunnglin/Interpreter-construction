package interpreter.lexer.token;

import interpreter.Const.TokenTag;

public class Token {

    protected TokenTag tag;     // token tag (specify the type of token)

    public Token(TokenTag t) {
        tag = t;
    }

    public TokenTag getTag() {
        return this.tag;
    }

    public int getTagValue() {
        return tag.getCode();
    }

    public String getTagText() {
        return tag.getSelfText();
    }

    public void setTag(TokenTag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Token{" + "tag=" + tag.getCode()
                + ", " + tag.getText() + ")";
    }
}
