import interpreter.Interpreter;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InterpreterTest {
    @Test
    public void interpretTest() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(TestConst.testFile));
            Interpreter interpreter = new Interpreter(reader);
            System.out.println(interpreter.interpret());
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
