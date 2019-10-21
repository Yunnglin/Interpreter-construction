package interpreter;

public final class Const {
    // 语法文件路径
    public static String grammarFilePath = "src/main/res/grammar.yaml";

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
