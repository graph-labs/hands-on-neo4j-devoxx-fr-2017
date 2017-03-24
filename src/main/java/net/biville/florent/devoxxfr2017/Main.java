package net.biville.florent.devoxxfr2017;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import net.biville.florent.devoxxfr2017.graph.ConnectionConfiguration;
import net.biville.florent.devoxxfr2017.graph.cypher.CypherQueryExecutor;
import net.biville.florent.devoxxfr2017.graph.cypher.CypherStatementValidator;
import net.biville.florent.devoxxfr2017.repl.Console;
import net.biville.florent.devoxxfr2017.repl.MultilineStatementParser;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.terminal.TerminalBuilder;
import org.neo4j.driver.v1.exceptions.Neo4jException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    @Parameter(names = {"-b", "--bolt-uri"}, description = "Neo4j Bolt URI")
    private String boltUri = "bolt://localhost:7687";

    @Parameter(names = {"-u",
            "--username"}, description = "Neo4j User name", required = true, validateValueWith = NonEmptyValueValidator.class)
    private String username = "neo4j";

    @Parameter(names = {"-p",
            "--password"}, description = "Neo4j password", required = true, password = true, validateValueWith = NonEmptyValueValidator.class)
    private String password;

    @Parameter(names = {"-h", "--help"}, description = "Help", help = true)
    private boolean help;

    public static void main(String[] args) {
        Main main = new Main();
        JCommander cli = parseCommandLineArguments(args, main);
        if (main.help) {
            cli.usage();
            return;
        }

        CypherQueryExecutor queryExecutor = cypherQueryExecutor(main.getConfiguration());
        validateConnection(queryExecutor);
        console(queryExecutor, statementValidator(), terminal(new MultilineStatementParser())).start();
    }

    private static JCommander parseCommandLineArguments(String[] args, Main main) {
        JCommander command = new JCommander(main, args);
        command.setProgramName("Hands on Neo4j - Devoxx France 2017");
        return command;
    }

    private static void validateConnection(CypherQueryExecutor queryExecutor) {
        try {
            List<Map<String, Object>> result = queryExecutor.execute("RETURN true as result");
            if (result.size() != 1 || !((boolean) result.get(0).get("result"))) {
                System.err.println("Uh-oh. Something is very wrong here. Aborting.");
                System.exit(42);
            }
        } catch (Neo4jException exception) {
            System.err.println("Connection parameters are invalid. Please specific the correct ones.");
            System.err.println(String.format("Error: %s", exception.getMessage()));
            System.exit(42);
        }
    }

    private static Console console(CypherQueryExecutor cypherQueryExecutor,
                                   CypherStatementValidator statementValidator, LineReader terminal) {
        return new Console(cypherQueryExecutor, statementValidator, terminal);
    }

    private static CypherQueryExecutor cypherQueryExecutor(ConnectionConfiguration configuration) {
        return new CypherQueryExecutor(configuration);
    }

    private static CypherStatementValidator statementValidator() {
        return new CypherStatementValidator();
    }

    private static LineReader terminal(Parser parser) {
        try {
            return LineReaderBuilder.builder().terminal(TerminalBuilder.terminal()).parser(parser).build();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private ConnectionConfiguration getConfiguration() {
        return new ConnectionConfiguration(boltUri, username, password);
    }
}
