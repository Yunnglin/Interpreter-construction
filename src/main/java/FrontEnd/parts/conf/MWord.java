package FrontEnd.parts.conf;


public class MWord {
    public static final String[] symbols = {
            "&&", "==", "!=", ">=", "<=",
            "+=", "-=", "*=", "/=",
            ">", "<", "+", "-", "*", "/", ";", ",",
            "(", ")","[","]","{","}",
            "\"","\'"
    };

    public static final String[] basicType={
            "int",
            "float",
            "real",
            "long",
            "char"
    };

    public static final String[] reservedWords={
            "break",
            "if",
            "else",
            "while",
            "do",
            "continue",
            "for"
    };

    public static final String[] constants ={
            "true",
            "false",
    };
}
