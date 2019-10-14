import interpreter.utils.lalr.LALRGrammar;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

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
        System.out.println(map.get("1234"));
    }

    @Test
    public void firstFollowSet() {
        LALRGrammar grammar = new LALRGrammar("src/test/res/grammar.yaml");
        HashMap<String, HashSet<String>> firstSets = grammar.getFirstSets();
        HashMap<String, HashSet<String>> followSets = grammar.getFollowSets();
        for (String key : firstSets.keySet()) {
            System.out.println(key + ":");
            System.out.println("\t" + "First: " + firstSets.get(key));
            System.out.println("\t" + "Follow: " + followSets.get(key));
        }
    }
}
