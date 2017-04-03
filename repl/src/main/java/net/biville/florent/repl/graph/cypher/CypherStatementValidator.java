package net.biville.florent.repl.graph.cypher;

import net.biville.florent.repl.CypherLexer;
import net.biville.florent.repl.CypherParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;

import java.util.Collection;

public class CypherStatementValidator {

    public Collection<CypherError> validate(String statement) {
        return parse(statement);
    }

    private Collection<CypherError> parse(String statement) {
        CypherErrorStatefulListener listener = new CypherErrorStatefulListener();
        CypherLexer lexer = new CypherLexer(new ANTLRInputStream(statement));
        listenToLexerErrors(listener, lexer);
        CypherParser parser = new CypherParser(new CommonTokenStream(lexer));
        listenToParserErrors(listener, parser);
        parser.cypher();
        return listener.getErrors();
    }

    private void listenToLexerErrors(CypherErrorStatefulListener listener, CypherLexer lexer) {
        lexer.removeErrorListeners();
        lexer.addErrorListener(listener);
    }

    private void listenToParserErrors(CypherErrorStatefulListener listener, CypherParser parser) {
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(listener);
    }

}
