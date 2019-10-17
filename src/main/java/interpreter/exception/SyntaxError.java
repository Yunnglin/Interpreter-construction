package interpreter.exception;

import interpreter.Const;
import interpreter.Const.TokenTag;
import interpreter.lexer.token.IntNum;
import interpreter.lexer.token.Real;
import interpreter.lexer.token.Token;
import interpreter.lexer.token.Word;

import java.util.ArrayList;

public class SyntaxError extends InterpError {
    public SyntaxError(String msg, int line) {
        super(msg, line);
    }

    @Override
    public String getMessage() {
        return "Syntax Error at line "+this.getLine()+": "+this.getInnerMsg();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    private static String getTokenDescription(Token token) {
        if (token instanceof IntNum) {
            return "INTEGER " + ((IntNum) token).getValue();
        } else if (token instanceof Real) {
            return "REAL NUMBER " + ((Real) token).getValue();
        } else if (token instanceof Word) {
            Word word = (Word) token;
            if (Const.TokenTag.RESERVED_WORDS.containsKey(word.getLexeme())) {
                return "KEY WORD '" + word.getLexeme() + "'";
            } else {
                return "IDENTIFIER '" + word.getLexeme() + "'";
            }
        } else {
            return "TOKEN '" + token.getTagText() + "'";
        }
    }

    public static SyntaxError newLexicalError(String lex, int line) {
        return new SyntaxError("BAD CHARACTER '"+lex+"'", line);
    }

    public static SyntaxError newIdentifierError(String name, int line) {
        return new SyntaxError("BAD IDENTIFIER '"+name+"'", line);
    }

    public static SyntaxError newConstantError(String num, int line) {
        return new SyntaxError("BAD CONSTANT '"+num+"'", line);
    }

    public static SyntaxError newUnexpectedTokenError(Token token) {
        int line = token.getLineNum();
        String desc = getTokenDescription(token);

        return new SyntaxError(desc, line);
    }

    public static SyntaxError newMissingTokenError(Token token, ArrayList<TokenTag> expected) {
        StringBuilder stringBuilder = new StringBuilder("MISSING");
        String desc = getTokenDescription(token);
        for (int i=0; i<expected.size(); ++i) {
            TokenTag tag = expected.get(i);
            if (i > 1) {
                stringBuilder.append(" |");
            }
            stringBuilder.append(" ").append("'").append(tag.getText()).append("'");
        }
        stringBuilder.append(" BEHIND ").append(desc);

        return new SyntaxError(stringBuilder.toString(), token.getLineNum());
    }
}
