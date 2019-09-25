import org.junit.Test;
import interpreter.exception.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
}
