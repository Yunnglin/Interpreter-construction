package interpreter;

import interpreter.utils.lalr.TerminalSymbol;

import java.util.Hashtable;

public final class Const {
    // Token 类别
    public final static int
            IF = 1, ELSE = 2, INT = 3, WHILE = 4, READ = 5, WRITE = 6, REAL = 7, SUM = 8, SUB = 9,
            MULTIPLY = 10, DIVIDE = 11, ASSIGN = 12, LESS_THAN = 13, GREATER_THAN = 14, EQ = 15, NEQ = 16,
            L_PARENTHESES = 17, R_PARENTHESES = 18, SEMICOLON = 19, L_BRACES = 20, R_BRACES = 21, L_NOTES = 22,
            R_NOTES = 23, L_SQUARE_BRACKETS = 24, R_SQUARE_BRACKETS = 25, REAL_NUMBER = 48,INTEGER = 49, IDENTIFIER = 50;

    // The type of token, also a kind of terminal symbol for parsing stage
    public enum TokenTag implements TerminalSymbol {

        // Reserved Words
        IF(1), ELSE(2), INT(3), WHILE(4), READ(5), WRITE(6), REAL(7),

        // Symbols
        SUM(8, "+"), SUB(9, "-"), MULTIPLY(10, "*"), DIVIDE(11, "/"),
        ASSIGN(12, "="), LESS_THAN(13, "<"), GREATER_THAN(14, ">"),
        EQ(15, "=="), NEQ(16, "<>"), L_PARENTHESES(17, "("),
        R_PARENTHESES(18, ")"), SEMICOLON(19, ";"), L_BRACES(20, "{"),
        R_BRACES(21, ")"), LEQ(22, "<="), GEQ(23, ">="),
        L_SQUARE_BRACKETS(24, "["), R_SQUARE_BRACKETS(25, "]"), COMMA(26, ","),

        // Others
        REAL_NUMBER(48), INTEGER(49), IDENTIFIER(50),

        // End
        PROG_END(200);

        private String text;    // token text
        private int code;       // token tag(type code)

        private static final int FIRST_RESERVED_INDEX = IF.ordinal();
        private static final int LAST_RESERVED_INDEX = REAL.ordinal();

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

        // Hashtable of lowercase of reserved words in CMM.
        public static Hashtable<String, TokenTag> RESERVED_WORDS = new Hashtable<>();
        // Hashtable of CMM symbols
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

    public enum Definition {

        VARIABLE, PROGRAM, FUNCTION, PROCEDURE, UNDEFINED;

        private String text;

        /**
         * Constructor
         */
        Definition() {
            this.text = this.toString();
        }

        /**
         * Constructor
         * @param text the description of definition
         */
        Definition(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
