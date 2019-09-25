import org.junit.Test;
import interpreter.exception.*;

public class lexerTest {
    @Test
    public void except(){
        InterpError ex = new SyntaxError("error!!!", 10);
        System.out.println(ex.getMessage());
    }
}
