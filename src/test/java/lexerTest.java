import interpreter.lexer.Lexer;
import interpreter.lexer.token.Token;
import org.junit.Test;
import interpreter.exception.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class lexerTest {
    @Test
    public void except(){
        InterpError ex = new SyntaxError("error!!!", 10);
        System.out.println(ex.getMessage());
    }

    @Test
    public void hashtable() {
        Hashtable t = new Hashtable();
        t.put("123", 123);
        System.out.println(t.containsKey("123"));
    }

    @Test
    public void readFile() {
        String filepath = "D://test.cmm";

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
        String whitespaces = " \t\r\n";
        System.out.println(whitespaces.indexOf('\0'));
    }

    @Test
    public void lexer() {
        String filepath = "D://test.cmm";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            try {
                Lexer myLexer = new Lexer(reader);
                ArrayList<Token> tokens = myLexer.getAllToken();
                System.out.println(tokens);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SyntaxError syntaxError) {
                System.out.println(syntaxError.getMessage());
                syntaxError.printStackTrace();
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
