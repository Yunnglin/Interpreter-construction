package interpreter.utils.lalr;

import interpreter.Const;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class LALRGrammar {
    public enum Nil implements GrammarSymbol {
        NIL;

        private String text;

        @Override
        public String getText() {
            return this.toString();
        }
    }

    public enum LALRNonterminalSymbol implements NonterminalSymbol {
        E, PROG, EXTERN_DECLARATION, FUNC_DEFINITION, FUNC_DECLARATOR,
        PARAM_LIST, PARAM_DECLARATION, STMT_LIST, STMT, DECLARE_STMT,
        COMPOUND_STMT, DECLARATOR_LIST, TYPE, DECLARATOR, INITIALIZER,
        INITIALIZER_LIST, IF_STMT, MORE_IF_ELSE, ELSE_STMT, WHILE_STMT,
        READ_STMT, WRITE_STMT, ASSIGN_STMT, OTHER_ASSIGN, EXPR,
        RELATIONAL_EXPR, RELATIONAL_EXPR_MORE, COMPARISON_OP,
        SIMPLE_EXPR, MORE_TERM, ADD_OP, TERM, MORE_FACTOR, MUL_OP,
        FACTOR, MORE_IDENTIFIER, NUMBER, NUMBER_INT, NUMBER_REAL;

        private String text;

        LALRNonterminalSymbol() {
            this.text = this.toString();
        }

        @Override
        public String getText() {
            return text;
        }
    }

    public static Nil NIL = Nil.NIL;

    private GrammarSymbol startSymbol;
    private LinkedHashMap strProductions;
    private ArrayList<Production> productions;
    private HashMap<String, HashSet<String>> firstSet;
    private HashMap<String, HashSet<String>> followSet;

    /**
     * Constructor
     * @param filepath the path of the cmm grammar text file
     */
    public LALRGrammar(String filepath) {
        this.startSymbol = LALRNonterminalSymbol.E;
        try {
            initProductions(filepath);
            generateFirstFollowSets();
        } catch (FileNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * init the strProductions of grammar with a yaml file
     * @param filepath the path of the grammar file
     */
    private void initProductions(String filepath) throws FileNotFoundException {
        // 读入文法文件
        Yaml yaml = new Yaml();
        File f = new File(filepath);

        this.strProductions = yaml.load(new FileInputStream(f));
        // convert the string to grammar symbols
        this.productions = new ArrayList<Production>();
        for (Object key : this.strProductions.keySet()) {
            ArrayList produceList = (ArrayList) this.strProductions.get(key);
            for (Object produce : produceList) {
                ArrayList produceArr = (ArrayList) produce;
                ArrayList<GrammarSymbol> right = new ArrayList<>();
                for (Object symbol : produceArr) {
                    String symbolStr = (String) symbol;
                    if (symbolStr.equals(NIL.getText())) {
                        right.add(NIL);
                    } else if (isTerminalSymbol(symbolStr)) {
                        right.add(Const.TokenTag.valueOf(symbolStr));
                    } else {
                        right.add(LALRNonterminalSymbol.valueOf(symbolStr));
                    }
                }
                this.productions.add(new Production(LALRNonterminalSymbol.valueOf((String) key),
                                                    right));
            }
        }
    }

    /**
     * generate the first sets and follow sets for all non-terminal symbols
     */
    private void generateFirstFollowSets() {
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();

        Set keys = this.strProductions.keySet();
        // fill the first set
        // initialize a empty followSet
        for (Object key : keys) {
            this.firstSet.put((String)key, firstSetOf((String) key));
            this.followSet.put((String) key, new HashSet<String>());
        }
        // fill the follow set
        // must do after the first set completed
        int followUpdates = 0;
        do {
            followUpdates = updateFollowSet();
        } while(followUpdates != 0);
    }

    /**
     * find the first set of a symbol in a recursive way
     * @param name the string of the symbol
     * @return the first set generated
     */
    private HashSet<String> firstSetOf(String name) {
        HashSet<String> set = new HashSet<String>();
        if (name.equals(NIL.getText()) || isTerminalSymbol(name)) {
            set.add(name);
        } else {
            HashSet<String> nonterminalSet = new HashSet<String>();
            ArrayList produceList = (ArrayList) this.strProductions.get(name);
            for (Object produce : produceList) {
                String firstSymbol = (String) ((ArrayList) produce).get(0);
                if (firstSymbol.equals(NIL.getText()) || isTerminalSymbol(firstSymbol)) {
                    set.add(firstSymbol);
                } else {
                    nonterminalSet.add(firstSymbol);
                    set.addAll(firstSetOf(firstSymbol));
                }
                try {
                    Const.TokenTag tokenTag = Const.TokenTag.valueOf(firstSymbol);
                    set.add(firstSymbol);
                } catch (IllegalArgumentException e) {
                    if (!nonterminalSet.contains(firstSymbol)) {
                        nonterminalSet.add(firstSymbol);
                        set.addAll(firstSetOf(firstSymbol));
                    }
                }
            }
        }

        return set;
    }

    /**
     * scan all strProductions and update the follow set for every non-terminal symbol
     * @return the change of the total size of the follow set
     */
    private int updateFollowSet() {
        // calculate the total size of the initial follow sets
        int initialSize = totalFollowSetsSize();

        Set keys = this.strProductions.keySet();
        for (Object key : keys) {
            HashSet<String> curFollowSet = this.followSet.get((String) key);
            if (((String) key).equals(startSymbol.toString())) {
                curFollowSet.add(Const.TokenTag.PROG_END.toString());
            }
            ArrayList produceList = (ArrayList) this.strProductions.get(key);
            for (Object produce : produceList) {
                // scan all the strProductions
                ArrayList produceArr = (ArrayList) produce;
                for (int i=0; i<produceArr.size(); ++i) {
                    // scan the production sequentially
                    String currentSym = (String) produceArr.get(i);
                    if (currentSym.equals(NIL.getText()) || isTerminalSymbol(currentSym)) {
                        continue;
                    }
                    for (int j=i+1; j<produceArr.size(); ++j) {
                        // add the first set of the follow symbol to the follow set
                        String followSym = (String) produceArr.get(j);
                        if (isTerminalSymbol(followSym)) {
                            // terminal symbol, add only the symbol to the follow set
                            this.followSet.get(currentSym).add(followSym);
                            break;
                        } else {
                            HashSet<String> followFirstSet = this.firstSet.get(followSym);
                            this.followSet.get(currentSym).addAll(followFirstSet);
                            if (followFirstSet.contains(NIL.getText())) {
                                // the following non-terminal symbol can produce nil
                                // continue the loop, consider symbols that follow the current follow symbol
                                this.followSet.get(currentSym).remove(NIL.getText());
                            } else {
                                // the following non-terminal symbol cannot produce nil
                                // only add its first set to the follow set
                                break;
                            }
                        }
                    }
                }
                for (int i=produceArr.size()-1; i>=0; i--) {
                    // scan the production reversely
                    String currentSym = (String) produceArr.get(i);
                    if (!currentSym.equals(NIL.getText()) && !isTerminalSymbol(currentSym)) {
                        // the symbol is non-terminal,
                        // its follow set contains the follow set of the symbol on the left of the production
                        this.followSet.get(currentSym).addAll(curFollowSet);
                        // if the symbol can produce nil, the follow set of the symbol before it also contains
                        // the follow set of the symbol on the left of the production
                        if (!this.firstSet.get(currentSym).contains(NIL.getText())) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        // calculate the total size of the final follow sets
        int finalSize = totalFollowSetsSize();

        return finalSize - initialSize;
    }

    /**
     * calculate the total size of all follow sets
     * @return the total size
     */
    private int totalFollowSetsSize() {
        int totalSize = 0;
        for (String key : this.followSet.keySet()) {
            totalSize += this.followSet.get(key).size();
        }

        return totalSize;
    }

    /**
     * get the total number of strProductions
     * @return the total number of strProductions
     */
    public int getNumOfProductions() {
        int tot = 0;
        for (Object key : this.strProductions.keySet()) {
            tot += ((ArrayList) this.strProductions.get(key)).size();
        }

        return tot;
    }

    /**
     * check if the symbol is a terminal symbol (a token tag)
     * @param name the string of the symbol
     * @return true if the symbol is a terminal symbol, false if it is not.
     */
    public boolean isTerminalSymbol(String name) {
        try {
            Const.TokenTag.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public HashMap<String, HashSet<String>> getFirstSets() {
        return this.firstSet;
    }

    public HashMap<String, HashSet<String>> getFollowSets() {
        return this.followSet;
    }
}
