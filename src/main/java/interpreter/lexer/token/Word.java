package interpreter.lexer.token;

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
        return "Word{tag=" + getTag()
                + ", lexeme='" + lexeme + '\''
                + '}';
    }

    // TODO
    public Word(int t, String s) {
        super(t);
        lexeme = s;
    }
}
