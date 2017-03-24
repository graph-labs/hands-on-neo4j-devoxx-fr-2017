package net.biville.florent.devoxxfr2017;

import net.biville.florent.devoxxfr2017.repl.MultilineStatementParser;
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

    @Test
    public void statements_end_with_semicolon() throws Exception {
        Parser parser = new MultilineStatementParser();

        thrown.expect(EOFError.class);
        ParsedLine parsedLine = parser.parse("MATCH (n)", 4);

        parser.parse("RETURN n;", 8);
        assertThat(parsedLine.words()).containsExactly("MATCH (n)\nRETURN n;");

    }
}
