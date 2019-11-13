package interpreter.lexer.token;

import interpreter.grammar.TokenTag;

public class Text extends Token {
    public static int MAX_CHARACTERS_IN_ESCAPE = 4;

    private String text;
    private String lexeme;

    public Text(TokenTag t, int line, String lexeme, String text) {
        super(t, line);
        this.lexeme = lexeme;
        this.text = text;
    }

    public Text(TokenTag t, int line, String lexeme) {
        super(t, line);
        this.text = this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (Text, lexeme='%s')", this.lexeme);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLexeme() {
        return this.lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }
}
