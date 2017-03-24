package net.biville.florent.devoxxfr2017.graph.cypher;

import net.biville.florent.devoxxfr2017.CypherLexer;
import net.biville.florent.devoxxfr2017.CypherParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Collection;
import java.util.stream.Stream;

public class CypherStatementValidator {

    public Stream<CypherError> validate(String statement) {
        return parse(statement).stream();
    }

    private Collection<CypherError> parse(String statement) {
        CypherParser parser = parser(statement);
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
