package interpreter.exception;

import interpreter.Const;
import interpreter.grammar.TokenTag;
import interpreter.lexer.token.IntNum;
import interpreter.lexer.token.Real;
import interpreter.lexer.token.Token;
import interpreter.lexer.token.Word;

import java.util.ArrayList;

public class SyntaxError extends InterpError {
    public SyntaxError(String msg, int line, ErrorCode code) {
        super(msg, line, code);
    }

    @Override
    public String getMessage() {
        return "Syntax error (" + getCode() + ") at line "+this.getLine()+": "+this.getInnerMsg();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    /**
     * format the content of a token for displaying
     * @param token Token, the token to display
     * @return String, a formatted string describing the token
     */
    private static String getTokenDescription(Token token) {
        if (token instanceof IntNum) {
            return "INTEGER " + ((IntNum) token).getValue();
        } else if (token instanceof Real) {
            return "REAL NUMBER " + ((Real) token).getValue();
        } else if (token instanceof Word) {
            Word word = (Word) token;
            if (TokenTag.RESERVED_WORDS.containsKey(word.getLexeme())) {
                return "KEY WORD '" + word.getLexeme() + "'";
            } else {
                return "IDENTIFIER '" + word.getLexeme() + "'";
            }
        } else {
            return "TOKEN '" + token.getTagText() + "'";
        }
    }

    /**
     * Unknown character
     * @param lex String, the unknown token encountered
     * @param line int, the number of the line where the character lies in
     * @return SyntaxError
     */
    public static SyntaxError newLexicalError(String lex, int line) {
        return new SyntaxError("BAD CHARACTER '"+lex+"'", line, ErrorCode.UNEXPECTED_CHAR);
    }

    /**
     * Bad identifier naming
     * @param name String, the name of the identifier
     * @param line int, the number of the line where the identifier lies in
     * @return SyntaxError
     */
    public static SyntaxError newIdentifierError(String name, int line) {
        return new SyntaxError("BAD IDENTIFIER '"+name+"'", line, ErrorCode.IDENTIFIER_NAMING);
    }

    /**
     * Bad constant using
     * @param num String, string of the constant
     * @param line int, the number of the line where the constant lies in
     * @return SyntaxError
     */
    public static SyntaxError newConstantError(String num, int line) {
        return new SyntaxError("BAD CONSTANT '"+num+"'", line, ErrorCode.CONSTANT_DEFINE);
    }

    /**
     * Unexpected token according to grammar
     * @param token Token, the unexpected token that encountered when parsing
     * @return SyntaxError
     */
    public static SyntaxError newUnexpectedTokenError(Token token) {
        int line = token.getLineNum();
        String desc = getTokenDescription(token);

        return new SyntaxError("UNEXPECTED " + desc, line, ErrorCode.UNEXPECTED_TOKEN);
    }

    /**
     * Missing tokens behind specified token
     * @param token Token, the token encountered
     * @param expected ArrayList of Token, the expected tokens behind 'token'
     * @return SyntaxError
     */
    public static SyntaxError newMissingTokenError(Token token, ArrayList<TokenTag> expected) {
        StringBuilder stringBuilder = new StringBuilder("MISSING");
        String desc = getTokenDescription(token);
        for (int i=0; i<expected.size(); ++i) {
            TokenTag tag = expected.get(i);
            if (i >= 1) {
                stringBuilder.append(" |");
            }
            stringBuilder.append(" ").append("'").append(tag.getText()).append("'");
        }
        stringBuilder.append(" BEHIND ").append(desc);

        return new SyntaxError(stringBuilder.toString(), token.getLineNum(), ErrorCode.MISSING_TOKEN);
    }

    /**
     * Encountered a char that used an illegal escape character
     * @param lex String, the string of the escape character description
     * @param line int, the number of the line where the character lies in
     * @return SyntaxError
     */
    public static SyntaxError newIllegalEscapeCharacterError(String lex, int line) {
        return new SyntaxError("ILLEGAL ESCAPE CHARACTER '" + lex + "'", line, ErrorCode.ILLEGAL_ESCAPE_CHAR);
    }

    /**
     * Encountered a char constant missing '
     * @param lex String, the string the character
     * @param line int, the number of the line where the character lies in
     * @return SyntaxError
     */
    public static SyntaxError newCharTerminatingMarkError(String lex, int line) {
        return new SyntaxError("MISSING TERMINATING ' behind '" + lex + "'", line, ErrorCode.MISSING_CHAR_MARK);
    }

    /**
     * Encountered an empty char constant
     * @param line int, the number of the line where the constant lies in
     * @return SyntaxError
     */
    public static SyntaxError newEmptyCharacter(int line) {
        return new SyntaxError("CHARACTER CONSTANT CANNOT BE EMPTY ''", line, ErrorCode.EMPTY_CHAR_CONST);
    }

    /**
     * Encountered a multi-character char constant
     * @param lex String, the char constant
     * @param line int, the number of the line where the constant lies in
     * @return SyntaxError
     */
    public static SyntaxError newMultiCharacterChar(String lex, int line) {
        return new SyntaxError("MULTI-CHARACTER CHARACTER CONSTANT '" + lex + "'",
                line, ErrorCode.MULTI_CHARACTER_CHAR);
    }

    public static SyntaxError newStrTerminatingMarkError(String lex, int line) {
        return new SyntaxError("MISSING TERMINATING \" BEHIND '" + lex + "'", line, ErrorCode.MISSING_STR_MARK);
    }

}
