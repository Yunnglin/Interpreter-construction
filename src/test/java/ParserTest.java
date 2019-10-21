import interpreter.grammar.TokenTag;
import interpreter.lexer.Lexer;
import interpreter.parser.Parser;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.lalr.LALRGrammar;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.grammar.lalr.state.LALRParseManager;
import interpreter.grammar.lalr.state.LALRStateMachine;
import interpreter.grammar.lalr.state.LRItem;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class ParserTest {
    @Test
    public void yamlTest() {
        String filepath = "src/test/res/grammar.yaml";
        // 读入文法文件
        Yaml yaml = new Yaml();
        File f = new File(filepath);

        try {
            LinkedHashMap grammar = yaml.load(new FileInputStream(f));
            System.out.println(grammar.values());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setMapOp() {
        HashSet<String> set = new HashSet<>();
        set.add("NULL");
        System.out.println(set.add("NULL"));

        HashMap<String, String> map = new HashMap<>();
        map.put("123", "123");
        System.out.println(map.put("123", "32154"));
        System.out.println(map);
    }

    @Test
    public void firstFollowSet() {
        LALRGrammar grammar = LALRGrammar.getGrammar();
        HashMap<String, HashSet<String>> firstSets = grammar.getFirstSets();
        HashMap<String, HashSet<String>> followSets = grammar.getFollowSets();
        for (String key : firstSets.keySet()) {
            System.out.println(key + ":");
            System.out.println("\t" + "First: " + firstSets.get(key));
            System.out.println("\t" + "Follow: " + followSets.get(key));
        }
    }

    @Test
    public void equalTest() {
        ArrayList<GrammarSymbol> arr1 = new ArrayList<>();
        ArrayList<GrammarSymbol> arr2 = new ArrayList<>();

        HashSet<LRItem> set1 = new HashSet<>();
        HashSet<LRItem> set2 = new HashSet<>();
        HashSet<GrammarSymbol> set3 = new HashSet<>();
        HashSet<GrammarSymbol> set4 = new HashSet<>();

        arr1.add(LALRNonterminalSymbol.PROG);
        arr2.add(LALRNonterminalSymbol.PROG);
        set3.add(TokenTag.PROG_END);
        set4.add(TokenTag.PROG_END);

        LRItem item1 = new LRItem(0);
        LRItem item2 = new LRItem(0);
        item1.getLookAheadSet().add(TokenTag.PROG_END);
        item2.getLookAheadSet().add(TokenTag.PROG_END);

        set1.add(item1);
        set2.add(item2);

        System.out.println("arr: " + arr1.equals(arr2));
        System.out.println("hashSet(object): " + set1.containsAll(set2));
        System.out.println("hashSet(enum): " + set3.equals(set4));
        System.out.println("hash of hashSet(enum): " + (Objects.hash(set3) == Objects.hash(set4)));
        System.out.println("item: " + ((Object)item1).equals((Object) item2));
    }

    @Test
    public void stateMachineTest() {
//        System.out.println(System.getProperty("user.dir"));
        LALRParseManager parseManager = new LALRParseManager();
        parseManager.runStateMachine();
        LALRStateMachine stateMachine = parseManager.getStateMachine();
        System.out.println(stateMachine.getTransitionTable().get(24));
    }

    @Test
    public void parserTest() {
        String filepath = "src/test/res/test.cmm";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            Lexer myLexer = new Lexer(reader);
            Parser myParser = new Parser(myLexer);
            myParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void serializeManager() {
        LALRParseManager manager = new LALRParseManager();
        manager.runStateMachine();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("src/main/res/LALRParserManagerInstance"));
            oos.writeObject(manager);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
