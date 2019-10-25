package interpreter.grammar.lalr;

import interpreter.grammar.NonterminalSymbol;

public enum LALRNonterminalSymbol implements NonterminalSymbol {
    E, PROG, EXTERN_DECLARATION, FUNC_DEFINITION, FUNC_DECLARATOR,
    PARAM_LIST, PARAM_DECLARATION, STMT_LIST, STMT, DECLARE_STMT,
    COMPOUND_STMT, DECLARATOR_LIST, TYPE, DECLARATOR, INITIALIZER,
    INITIALIZER_LIST, IF_STMT, MORE_IF_ELSE, ELSE_STMT, WHILE_STMT,
    READ_STMT, WRITE_STMT, ASSIGN_STMT, RETURN_STMT, OTHER_ASSIGN,
    EXPR, RELATIONAL_EXPR, RELATIONAL_EXPR_MORE, COMPARISON_OP,
    SIMPLE_EXPR, MORE_TERM, ADD_OP, TERM, MORE_FACTOR, MUL_OP,
    FACTOR, MORE_IDENTIFIER, NUMBER, NUMBER_INT, NUMBER_REAL;

    private String text;

    LALRNonterminalSymbol() {
        this.text = this.toString();
    }

    @Override
    public String getSelfText() {
        return text;
    }
}
