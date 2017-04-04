package net.biville.florent.repl.console;

import net.biville.florent.repl.console.commands.Command;
import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.SyntaxError;

import java.util.Collections;
import java.util.List;

public class MultilineStatementParser implements Parser {

    @Override
    public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
        String input = line.trim();
        DefaultParsedLine defaultResult = new DefaultParsedLine(line, cursor);

        if (input.startsWith(Command.PREFIX)) {
            return defaultResult;
        }

        if (!input.endsWith(";")) {
            throw new EOFError(-1, -1, "Query not terminated");
        }
        return defaultResult;
    }

    private static class DefaultParsedLine implements ParsedLine {
        private final String line;
        private final int cursor;

        public DefaultParsedLine(String line, int cursor) {
            this.cursor = cursor;
            this.line = line;
        }

        @Override
        public String word() {
            return line;
        }

        @Override
        public int wordCursor() {
            return cursor;
        }

        @Override
        public int wordIndex() {
            return 0;
        }

        @Override
        public List<String> words() {
            return Collections.singletonList(line);
        }

        @Override
        public String line() {
            return line;
        }

        @Override
        public int cursor() {
            return cursor;
        }
    }
}
