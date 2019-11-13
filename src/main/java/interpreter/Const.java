package interpreter;

import java.util.HashMap;

public final class Const {
    // 解释器语法文件路径
//    public static String grammarFilePath = "src/main/res/grammar.yaml";
    public static String grammarFilePath = "src/main/res/newGrammar.yaml";
    // 终端解释器语法文件路径
    public static String terminalGrammarFilePath = "src/main/res/terminalGrammar.yaml";
    // 解释器分析表工具路径
//    public static String parseManagerInstancePath = "src/main/res/LALRParserManagerInstance";
    public static String parseManagerInstancePath = "src/main/res/TestParserManagerInstance";
    // 终端解释器分析表工具路径
    public static String terminalParseManagerInstancePath = "src/main/res/TerminalParseManagerInstance";
    // 控制字符对应的字节
    public static HashMap<Character, Byte> controlCharacters = new HashMap<>();

    static {
        // \a  007
        controlCharacters.put('a', (byte) 7);
        // \b  008
        controlCharacters.put('b', (byte) 8);
        // \f  012
        controlCharacters.put('f', (byte) 12);
        // \b  010
        controlCharacters.put('n', (byte) 10);
        // \r  013
        controlCharacters.put('r', (byte) 13);
        // \t  009
        controlCharacters.put('t', (byte) 9);
        // \v  011
        controlCharacters.put('v', (byte) 11);
        // \'  039
        controlCharacters.put('\'', (byte) 39);
        // \"  034
        controlCharacters.put('\"', (byte) 34);
        // \\  092
        controlCharacters.put('\\', (byte) 92);
    }
}
