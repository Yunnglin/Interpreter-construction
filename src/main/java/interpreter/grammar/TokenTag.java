package interpreter.grammar;

import java.util.Hashtable;

// The type of token, also a kind of terminal symbol for parsing stage
public enum TokenTag implements TerminalSymbol {

    // Reserved Words
    IF(1), ELSE(2), INT(3), WHILE(4), READ(5), WRITE(6), REAL(7),
    VOID(8), CHAR(9), BREAK(10), CONTINUE(11), RETURN(50),

    // Symbols
    SUM(51, "+"), SUB(52, "-"), MULTIPLY(53, "*"), DIVIDE(54, "/"),
    ASSIGN(55, "="), LESS_THAN(56, "<"), GREATER_THAN(57, ">"),
    EQ(58, "=="), NEQ(59, "<>"), L_PARENTHESES(60, "("),
    R_PARENTHESES(61, ")"), SEMICOLON(62, ";"), L_BRACES(63, "{"),
    R_BRACES(64, "}"), LEQ(65, "<="), GEQ(66, ">="),
    L_SQUARE_BRACKETS(67, "["), R_SQUARE_BRACKETS(68, "]"), AND(69, "&&"),
    OR(70, "||"), NOT(71, "!"),
    COMMA(100, ","),

    // Others
    REAL_NUMBER(101), INTEGER(102), IDENTIFIER(103), CHARACTER(104), STRING(105),

    // terminal keyword
    CLEAR(51),

    // End
    PROG_END(200);

    private String text;    // token text
    private int code;       // token tag(type code)

    private static final int FIRST_RESERVED_INDEX = IF.ordinal();
    private static final int LAST_RESERVED_INDEX = RETURN.ordinal();

    private static final int FIRST_SYMBOL_INDEX = SUM.ordinal();
    private static final int LAST_SYMBOL_INDEX = COMMA.ordinal();

    /**
     * Constructor, the default text of the type is the lowercase of the enum value.
     * @param code the tag value
     */
    TokenTag(int code) {
        this.text = this.toString().toLowerCase();
        this.code = code;
    }

    /**
     * Constructor
     * @param code the tag value
     * @param text the text of the specific type
     */
    TokenTag(int code, String text) {
        this.text = text;
        this.code = code;
    }

    /**
     * get the code which identifies a specific symbol
     * @return a int value that represents the code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * get the text of specific type
     * @return the text of the type
     */
    public String getText() {
        return this.text;
    }
    public String getSelfText() {
        return this.toString();
    }

    // Hashtable of lowercase of reserved words in CMM (default interpreter).
    public static Hashtable<String, TokenTag> RESERVED_WORDS = new Hashtable<>();
    // Hashtable of CMM symbols (default interpreter)
    public static Hashtable<String, TokenTag> SYMBOLS = new Hashtable<>();
    static {
        TokenTag[] values = TokenTag.values();
        for (int i=FIRST_RESERVED_INDEX; i<=LAST_RESERVED_INDEX; ++i) {
            RESERVED_WORDS.put(values[i].getText(), values[i]);
        }
        for (int i=FIRST_SYMBOL_INDEX; i<=LAST_SYMBOL_INDEX; ++i) {
            SYMBOLS.put(values[i].getText(), values[i]);
        }
    }
}
