package interpreter.grammar.lalr;

import interpreter.Const;
import interpreter.grammar.TokenTag;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.NonterminalSymbol;
import interpreter.grammar.Production;
import interpreter.grammar.TerminalSymbol;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

public class LALRGrammar implements Serializable {

    private static LALRGrammar defaultInstance = null;
    private static LALRGrammar terminalInstance = null;

    public static Nil NIL = Nil.NIL;
    public static String mode = "default";  // the switch for parsers of different mode


    /**
     * get a grammar object according to the mode.
     * 'default' and 'terminal' supported now.
     * @return the grammar object
     */
    public static LALRGrammar getGrammar() {
        if (mode.equals("terminal")) {
            if (terminalInstance == null) {
                terminalInstance = new LALRGrammar(Const.terminalGrammarFilePath);
            }
            return terminalInstance;
        }

        if (!mode.equals("default")) {
            System.out.println("No such grammar file that support mode '" + mode + "', use default instead");
        }

        if (defaultInstance == null) {
            defaultInstance = new LALRGrammar(Const.grammarFilePath);
        }

        return defaultInstance;
    }

    private NonterminalSymbol startSymbol;
    private TerminalSymbol endSymbol;
    private LinkedHashMap strProductions;
    private ArrayList<Production> productions;
    private HashMap<String, HashSet<String>> firstSet;
    private HashMap<String, HashSet<String>> followSet;
    private LinkedHashMap<String, ArrayList<Integer>> productionStartPosAndLength;

    /**
     * Constructor
     * singleton pattern
     * @param filepath the path of the cmm grammar text file
     */
    private LALRGrammar(String filepath) {
        this.startSymbol = LALRNonterminalSymbol.E;
        this.endSymbol = TokenTag.PROG_END;
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
        // used to search the first id and number of the production of a non-terminal symbol
        this.productionStartPosAndLength = new LinkedHashMap<>();
        for (Object key : this.strProductions.keySet()) {
            // the production list of the symbol
            ArrayList produceList = (ArrayList) this.strProductions.get(key);
            // set the start position and length of its productions
            ArrayList<Integer> newStartPosAndLen = new ArrayList<>(2);
            newStartPosAndLen.add(this.productions.size());
            newStartPosAndLen.add(produceList.size());
            this.productionStartPosAndLength.put((String)key, newStartPosAndLen);

            for (Object produce : produceList) {
                ArrayList produceArr = (ArrayList) produce;
                ArrayList<GrammarSymbol> right = new ArrayList<>();
                for (Object symbol : produceArr) {
                    String symbolStr = (String) symbol;
                    if (symbolStr.equals(NIL.getSelfText())) {
                        right.add(NIL);
                    } else if (isTerminalSymbol(symbolStr)) {
                        right.add(TokenTag.valueOf(symbolStr));
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
        if (name.equals(NIL.getSelfText()) || isTerminalSymbol(name)) {
            set.add(name);
        } else {
            HashSet<String> nonterminalSet = new HashSet<String>();
            ArrayList produceList = (ArrayList) this.strProductions.get(name);
            for (Object produce : produceList) {
                String firstSymbol = (String) ((ArrayList) produce).get(0);
                if (firstSymbol.equals(NIL.getSelfText()) || isTerminalSymbol(firstSymbol)) {
                    set.add(firstSymbol);
                } else {
                    nonterminalSet.add(firstSymbol);
                    set.addAll(firstSetOf(firstSymbol));
                }
                if(isTerminalSymbol(firstSymbol)) {
                    set.add(firstSymbol);
                } else if (!nonterminalSet.contains(firstSymbol)) {
                    nonterminalSet.add(firstSymbol);
                    set.addAll(firstSetOf(firstSymbol));
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
                curFollowSet.add(TokenTag.PROG_END.toString());
            }
            ArrayList produceList = (ArrayList) this.strProductions.get(key);
            for (Object produce : produceList) {
                // scan all the strProductions
                ArrayList produceArr = (ArrayList) produce;
                for (int i=0; i<produceArr.size(); ++i) {
                    // scan the production sequentially
                    String currentSym = (String) produceArr.get(i);
                    if (currentSym.equals(NIL.getSelfText()) || isTerminalSymbol(currentSym)) {
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
                            if (followFirstSet.contains(NIL.getSelfText())) {
                                // the following non-terminal symbol can produce nil
                                // continue the loop, consider symbols that follow the current follow symbol
                                this.followSet.get(currentSym).remove(NIL.getSelfText());
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
                    if (!currentSym.equals(NIL.getSelfText()) && !isTerminalSymbol(currentSym)) {
                        // the symbol is non-terminal,
                        // its follow set contains the follow set of the symbol on the left of the production
                        this.followSet.get(currentSym).addAll(curFollowSet);
                        // if the symbol can produce nil, the follow set of the symbol before it also contains
                        // the follow set of the symbol on the left of the production
                        if (!this.firstSet.get(currentSym).contains(NIL.getSelfText())) {
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
     * get the start symbol of grammar
     * @return a non-terminal symbol
     */
    public NonterminalSymbol getStartSymbol() {
        return startSymbol;
    }

    /**
     * get the end symbol of grammar (a symbol that may not involved in code)
     * @return a terminal symbol
     */
    public TerminalSymbol getEndSymbol() {
        return endSymbol;
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
            TokenTag.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * get the start position and length of the productions in
     * all productions of the specific non-terminal symbol
     * @param nonterminal the selfText of a non-terminal symbol
     * @return a list that has two elements, index 0 is the start position
     *                                       index 1 is the number of productions
     */
    public ArrayList<Integer> getStartPosAndLen(String nonterminal) {
        return productionStartPosAndLength.get(nonterminal);
    }

    /**
     * get the first sets of all non-terminal symbols
     * @return a hash map, with the string of symbols as the key, and the first set as value
     */
    public HashMap<String, HashSet<String>> getFirstSets() {
        return this.firstSet;
    }

    /**
     * get the follow sets of all non-terminal symbols
     * @return a hash map, with the string of symbols as the key, and the follow set as value
     */
    public HashMap<String, HashSet<String>> getFollowSets() {
        return this.followSet;
    }

    /**
     * get the first set of specified symbol
     * @param symbol the specified symbol
     * @return a hash set of string that represents the first set,
     *         which only includes the symbol itself if it is terminal of NIL
     */
    public HashSet<String> getFirstSet(GrammarSymbol symbol) {
        HashSet<String> set;

        if (symbol.equals(NIL) || isTerminalSymbol(symbol.getSelfText())) {
            set = new HashSet<>();
            set.add(symbol.getSelfText());
            return set;
        }

        set = firstSet.get(symbol.getSelfText());
        return set;
    }

    /**
     * get the first set of specified non-terminal symbol
     * @param symbol the specified non-terminal symbol
     * @return the follow set
     */
    public HashSet<String> getFollowSet(GrammarSymbol symbol) {
        return followSet.get(symbol.getSelfText());
    }

    /**
     * get all productions in grammar
     * @return
     */
    public ArrayList<Production> getProductions() {
        return productions;
    }

    /**
     * get all the productions that have a specified symbol as left symbol
     * @param symbol the non-terminal symbol
     * @return the list of productions
     */
    public ArrayList<Production> getProductions(GrammarSymbol symbol) {
        if (symbol.equals(NIL) || isTerminalSymbol(symbol.getSelfText())) {
            return null;
        }

        ArrayList<Integer> startAndLen = getStartPosAndLen(symbol.getSelfText());
        ArrayList<Production> newProductions = new ArrayList<Production>();
        for (int i=startAndLen.get(0); i<startAndLen.get(0)+startAndLen.get(1); ++i) {
            newProductions.add(this.productions.get(i));
        }

        return newProductions;
    }

    /**
     * get the production of specified id (index)
     * @param productionId  the specified id of production
     * @return the specified production
     */
    public Production getProduction(Integer productionId) {
        return productions.get(productionId);
    }
}
