package interpreter.lexer.token;

import interpreter.Const.TokenTag;

public class Word extends Token {
    private String lexeme;

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (Word, lexeme='%s')", this.lexeme);
    }

    public Word(TokenTag t, String s) {
        super(t);
        lexeme = s;
    }

    public Word(TokenTag t) {
        super(t);
        lexeme = t.getText();
    }
}
