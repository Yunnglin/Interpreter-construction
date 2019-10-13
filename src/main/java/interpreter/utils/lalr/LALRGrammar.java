package interpreter.utils.lalr;

import interpreter.Const;

import java.util.LinkedHashMap;

public class LALRGrammar {
    public enum LALRUnterminalSymbol implements UnterminalSymbol {
        PROG, EXTERN_DECLARATION, FUNC_DEFINITION, FUNC_DECLARATOR,
        PARAM_LIST, PARAM_DECLARATION, STMT_LIST, STMT, DECLARE_STMT,
        COMPOUND_STMT, DECLARATOR_LIST, TYPE, DECLARATOR, INITIALIZER,
        INITIALIZER_LIST, IF_STMT, MORE_IF_ELSE, ELSE_STMT, WHILE_STMT,
        READ_STMT, WRITE_STMT, ASSIGN_STMT, OTHER_ASSIGN, EXPR,
        RELATIONAL_EXPR, RELATIONAL_EXPR_MORE, COMPARISON_OP,
        SIMPLE_EXPR, MORE_TERM, ADD_OP, TERM, MORE_FACTOR, MUL_OP,
        FACTOR, MORE_IDENTIFIER, NUMBER, NUMBER_INT, NUMBER_REAL;

        private String text;

        LALRUnterminalSymbol() {
            this.text = this.toString();
        }

        @Override
        public String getText() {
            return text;
        }
    }

    private LinkedHashMap<UnterminalSymbol, Production> productions;

    public LALRGrammar() {
        this.productions = new LinkedHashMap<>();
        initProductions();
    }

    private void addProduction(UnterminalSymbol usymbol, GrammarSymbol... symbols) {
        this.productions.put(usymbol, new Production(usymbol, symbols.length, symbols));
    }

    private void initProductions() {
        // prog -> extern-declaration
        addProduction(LALRUnterminalSymbol.PROG,
                LALRUnterminalSymbol.EXTERN_DECLARATION);
        // prog -> extern-declaration prog
        addProduction(LALRUnterminalSymbol.PROG,
                LALRUnterminalSymbol.EXTERN_DECLARATION, LALRUnterminalSymbol.PROG);
        // extern-declaration -> declare-stmt
        addProduction(LALRUnterminalSymbol.EXTERN_DECLARATION,
                LALRUnterminalSymbol.DECLARE_STMT);
        // extern-declaration -> func-definition
        addProduction(LALRUnterminalSymbol.EXTERN_DECLARATION,
                LALRUnterminalSymbol.FUNC_DEFINITION);
        // func-definition -> type func-declarator compound-stmt
        addProduction(LALRUnterminalSymbol.FUNC_DEFINITION,
                LALRUnterminalSymbol.TYPE, LALRUnterminalSymbol.FUNC_DECLARATOR, LALRUnterminalSymbol.COMPOUND_STMT);
        // func-declarator -> identifier ()
        addProduction(LALRUnterminalSymbol.FUNC_DECLARATOR,
                Const.TokenTag.IDENTIFIER, Const.TokenTag.L_PARENTHESES, Const.TokenTag.R_PARENTHESES);
        // func-declarator -> identifier (param-list)
        addProduction(LALRUnterminalSymbol.FUNC_DECLARATOR,
                Const.TokenTag.IDENTIFIER, Const.TokenTag.L_PARENTHESES, LALRUnterminalSymbol.PARAM_LIST,
                Const.TokenTag.R_PARENTHESES);
        // param-list -> param-declaration , param-list
        addProduction(LALRUnterminalSymbol.PARAM_LIST,
                LALRUnterminalSymbol.PARAM_DECLARATION, Const.TokenTag.COMMA, LALRUnterminalSymbol.PARAM_LIST);
        // param-list -> param-declaration
        addProduction(LALRUnterminalSymbol.PARAM_LIST,
                LALRUnterminalSymbol.PARAM_DECLARATION);
        // param-declaration -> type identifier
        addProduction(LALRUnterminalSymbol.PARAM_DECLARATION,
                LALRUnterminalSymbol.TYPE, Const.TokenTag.IDENTIFIER);
        // param-declaration -> type identifier[expr]
        addProduction(LALRUnterminalSymbol.PARAM_DECLARATION,
                LALRUnterminalSymbol.TYPE, Const.TokenTag.IDENTIFIER,
                Const.TokenTag.L_SQUARE_BRACKETS, Const.TokenTag.R_SQUARE_BRACKETS);
        // stmt-list -> stmt stmt-list
        addProduction(LALRUnterminalSymbol.STMT_LIST,
                LALRUnterminalSymbol.STMT, LALRUnterminalSymbol.STMT_LIST);
        // stmt-list -> stmt
        addProduction(LALRUnterminalSymbol.STMT_LIST,
                LALRUnterminalSymbol.STMT);
        // stmt -> if-stmt
        addProduction(LALRUnterminalSymbol.STMT,
                LALRUnterminalSymbol.IF_STMT);
        // stmt -> while-stmt
        addProduction(LALRUnterminalSymbol.STMT,
                LALRUnterminalSymbol.WHILE_STMT);
    }

    public int getNumOfProductions() {
        return this.productions.size();
    }
}
