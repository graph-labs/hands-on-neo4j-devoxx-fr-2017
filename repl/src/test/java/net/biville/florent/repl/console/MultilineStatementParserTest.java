package net.biville.florent.repl.console;

import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class MultilineStatementParserTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Parser parser = new MultilineStatementParser();

    @Test
    public void statements_end_with_semicolon() {
        thrown.expect(EOFError.class);
        ParsedLine parsedLine = parser.parse("MATCH (n)", 4);

        parser.parse("RETURN n;", 8);

        assertThat(parsedLine.cursor()).isEqualTo(8);
        assertThat(parsedLine.words()).containsExactly("MATCH (n)\nRETURN n;");
        assertThat(parsedLine.word()).isEqualTo("MATCH (n)\nRETURN n;");
        assertThat(parsedLine.wordCursor()).isEqualTo(8);
    }

    @Test
    public void reserved_commands_are_handled() {
        ParsedLine line = parser.parse(":special-command", 15);

        assertThat(line.cursor()).isEqualTo(15);
    }
}
