package net.biville.florent.repl.graph.cypher;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.Collection;
import java.util.LinkedHashSet;

import static java.util.Collections.unmodifiableCollection;

class CypherErrorStatefulListener extends BaseErrorListener {

    private final Collection<CypherError> errors = new LinkedHashSet<>(4);

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        errors.add(new CypherError(offendingSymbol, line, charPositionInLine, msg, e));
    }

    public Collection<CypherError> getErrors() {
        return unmodifiableCollection(errors);
    }
}
