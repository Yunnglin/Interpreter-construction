package interpreter.lexer.token;

import interpreter.grammar.TokenTag;

public class Text extends Token {

    // 转义字符 \ 后允许的单字符
    private static String escapeFollowChars = "abfnrt'\"\17";

    private String text;

    public Text(TokenTag t, int line, String text) {
        super(t, line);
        this.text = text;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" (Text, text='%s')", this.text);
    }
}
