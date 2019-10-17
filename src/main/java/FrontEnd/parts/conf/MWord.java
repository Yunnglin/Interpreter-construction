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
            "char",
            "void",
            "string"
    };

    public static final String[] reservedWords={
            "include","new","this","NULL",
            "switch","case","default","do","while","for","break","continue",
            "return","try","catch","private","protected","public","static","class","define",
            "const","using","namespace"
    };

    public static final String[] constants ={
            "true",
            "false",
    };
}
