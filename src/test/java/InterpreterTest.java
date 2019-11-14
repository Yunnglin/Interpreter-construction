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
            reader = new BufferedReader(new FileReader(TestConst.syntaxTestFile));
            Interpreter interpreter = new Interpreter(reader);
            System.out.println(interpreter.interpret());
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Test
    public void FibonacciTest() {
        System.out.println(Fibonacci(30));
    }

    private int Fibonacci(int n) {
        if (n<=0) {
            return -1;
        }
        if (n==1 || n==2) {
            return 1;
        }
        return Fibonacci(n-1) + Fibonacci(n-2);
    }

    @Test
    public void numberTest() {

    }
}
