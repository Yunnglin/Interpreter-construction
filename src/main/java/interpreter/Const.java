package interpreter;

public final class Const {
    // Token 类别
    public final static int
            IF = 1, ELSE = 2, INT = 3, WHILE = 4, READ = 5, WRITE = 6, REAL = 7, SUM = 8, SUB = 9,
            MULTIPLY = 10, DIVIDE = 11, ASSIGN = 12, LESS_THAN = 13, GREATER_THAN = 14, EQ = 15, NEQ = 16,
            L_PARENTHESES = 17, R_PARENTHESES = 18, SEMICOLON = 19, L_BRACES = 20, R_BRACES = 21, L_NOTES = 22,
            R_NOTES = 23, L_SQUARE_BRACKETS = 24, R_SQUARE_BRACKETS = 25, REAL_NUMBER = 48,INTEGER = 49, IDENTIFIER = 50;

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
