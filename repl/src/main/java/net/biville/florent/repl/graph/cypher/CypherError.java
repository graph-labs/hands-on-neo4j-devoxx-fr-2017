package net.biville.florent.repl.graph.cypher;

import org.antlr.v4.runtime.RecognitionException;

import java.util.Objects;

public class CypherError {

    private final Object offendingSymbol;
    private final int line;
    private final int charPositionInLine;
    private final String message;
    private final RecognitionException exception;

    public CypherError(Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException exception) {
        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.message = message;
        this.exception = exception;
    }

    public Object getOffendingSymbol() {
        return offendingSymbol;
    }

    public int getLine() {
        return line;
    }

    public int getCharPositionInLine() {
        return charPositionInLine;
    }

    public String getMessage() {
        return message;
    }

    public RecognitionException getException() {
        return exception;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offendingSymbol, line, charPositionInLine, message, exception);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CypherError other = (CypherError) obj;
        return Objects.equals(this.offendingSymbol, other.offendingSymbol)
                && Objects.equals(this.line, other.line)
                && Objects.equals(this.charPositionInLine, other.charPositionInLine)
                && Objects.equals(this.message, other.message)
                && Objects.equals(this.exception, other.exception);
    }

    @Override
    public String toString() {
        return String.format("Line: %d, position: %d - ", line, charPositionInLine) + message;
    }
}
