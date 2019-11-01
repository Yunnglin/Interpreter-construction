import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import org.junit.Test;
import interpreter.executor.subExecutor.Expr;

import java.util.HashMap;

public class ExecutorTest {

    @Test
    public void typeTest() {
        Integer i = 9;
        Double d = 0.9;
        System.out.println(i.doubleValue());
    }

    @Test
    public void envTest() {
       // Expr e=new Expr();

    }

    @Test
    public void symTblTest() {
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("333", "23333");
        HashMap<String, String> map2 = (HashMap<String, String>) map1.clone();
        map1.put("333", "333");
        System.out.println(map1);
        System.out.println(map2);

        SymTbl symTbl1 = new SymTbl();
        SymTblEntry a = new SymTblEntry("a");
        symTbl1.addEntry(a);

        SymTbl symTbl2 = symTbl1.copy();
        a.addValue(SymTbl.SymTblKey.LINE, 10);
        System.out.println(symTbl1.find("a").getValue(SymTbl.SymTblKey.LINE));
        System.out.println(symTbl2.find("a").getValue(SymTbl.SymTblKey.LINE));
    }
}
