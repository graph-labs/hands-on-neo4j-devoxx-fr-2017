package net.biville.florent.devoxxfr2017.repl;

import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.SyntaxError;

import java.util.List;

public class MultilineStatementParser implements Parser {

    @Override
    public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
        if (!line.trim().endsWith(";")) {
            throw new EOFError(-1, -1, "Query not terminated");
        }
        return new DefaultParsedLine();
    }

    private static class DefaultParsedLine implements ParsedLine {
        @Override
        public String word() {
            return null;
        }

        @Override
        public int wordCursor() {
            return 0;
        }

        @Override
        public int wordIndex() {
            return 0;
        }

        @Override
        public List<String> words() {
            return null;
        }

        @Override
        public String line() {
            return null;
        }

        @Override
        public int cursor() {
            return 0;
        }
    }
}
