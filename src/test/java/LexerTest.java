import interpreter.grammar.TokenTag;
import interpreter.lexer.Lexer;
import interpreter.lexer.token.Token;
import interpreter.utils.AsciiUtils;
import org.junit.Test;
import interpreter.exception.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;

public class LexerTest {

    @Test
    public void except(){
        InterpError ex = new SyntaxError("error!!!", 10, ErrorCode.UNEXPECTED_CHAR);
        System.out.println(ex.getMessage());

        System.out.println(Integer.parseInt("10000000000"));
    }

    @Test
    public void hashtable() {
        Hashtable t = new Hashtable();
        t.put("123", 123);
        System.out.println(t.containsKey("123"));
    }

    @Test
    public void readFile() {
        String filepath = TestConst.escapeCharsFile;

        try (FileReader reader = new FileReader(filepath);
             BufferedReader br = new BufferedReader(reader)) {
            int peek;
            while((peek= br.read()) != -1) {
                System.out.println((char)peek);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void general() {
        String whitespaces = " \t\r\n\11";
        System.out.println(whitespaces.indexOf('\0'));
        System.out.println("abc" + "def");
    }

    @Test
    public void lexTest() {
        String filepath = TestConst.syntaxTestFile;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            Lexer myLexer = new Lexer(reader);
            ArrayList<Token> tokens = myLexer.lex();
            for(Token token : tokens){
                System.out.println(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TokenTagTest() {
        String name = "IDENTIFIER";
        System.out.println(TokenTag.valueOf(name));
    }

    @Test
    public void escapeCharTest() {
        System.out.println("\n");
    }

    @Test
    public void hexTest() {
//        String str = "\\" + "b";
//        byte[] bytes = str.getBytes(StandardCharsets.US_ASCII);
//        for (byte aByte : bytes) {
//            System.out.println(aByte);
//        }

        System.out.println("test");
        Byte aByte = AsciiUtils.hexStr2Byte("9");
        byte[] bytes = new byte[] {aByte};
        String s = new String(bytes, StandardCharsets.US_ASCII);

        System.out.println(s.charAt(0));
        System.out.println("test");

        System.out.println("test1");
        System.out.println(AsciiUtils.hex2Ascii("9"));
        System.out.println("test1");
    }
}
