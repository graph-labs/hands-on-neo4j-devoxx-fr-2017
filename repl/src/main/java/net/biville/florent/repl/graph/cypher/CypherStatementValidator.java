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
        CypherParser parser = parser(statement);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        CypherErrorStatefulListener listener = new CypherErrorStatefulListener();
        parser.addErrorListener(listener);
        parser.cypher();
        return listener.getErrors();
    }

    private CypherParser parser(String statement) {
        CypherLexer lexer = new CypherLexer(new ANTLRInputStream(statement));
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        return new CypherParser(commonTokenStream);
    }

}
