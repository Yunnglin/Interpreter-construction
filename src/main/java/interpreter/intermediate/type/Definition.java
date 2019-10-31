package interpreter.intermediate.type;

public enum Definition {

    VARIABLE, PROGRAM, FUNCTION, PROCEDURE, UNDEFINED, CONSTANT;

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
