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
            "read","write",
            "include","new","this",
            "switch","case","default","do","while","for","break","continue",
            "return","try","catch","private","protected","public","static","class","define",
            "const","using","namespace"
    };

    public static final String[] constants ={
            "true",
            "false",
            "NULL"
    };

    public static final String[] allowFiles = {
            "txt",
            "cmm",
            "py",
            "java",
            "c",
            "cpp",
            "html",
            "css",
            "js",
            "h"
    };
}
