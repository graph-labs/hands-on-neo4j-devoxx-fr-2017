package net.biville.florent.repl;

import com.esotericsoftware.kryo.Kryo;
import net.biville.florent.repl.console.Console;
import net.biville.florent.repl.console.MultilineStatementParser;
import net.biville.florent.repl.console.commands.CommandRegistry;
import net.biville.florent.repl.console.commands.CommandScanner;
import net.biville.florent.repl.exercises.ExerciseRepository;
import net.biville.florent.repl.exercises.ExerciseValidator;
import net.biville.florent.repl.exercises.TraineeSession;
import net.biville.florent.repl.graph.ReplConfiguration;
import net.biville.florent.repl.graph.cypher.CypherQueryExecutor;
import net.biville.florent.repl.graph.cypher.CypherStatementValidator;
import net.biville.florent.repl.logging.ConsoleLogger;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.terminal.TerminalBuilder;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.exceptions.Neo4jException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Neo4jRepl implements Supplier<Console> {

    private final ReplConfiguration configuration;

    public Neo4jRepl(ReplConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Console get() {
        CypherQueryExecutor queryExecutor = cypherQueryExecutor(configuration);
        validateConnection(queryExecutor);

        CommandScanner commandScanner = new CommandScanner(configuration.getPackageToScan());
        LineReader lineReader = lineReader(new MultilineStatementParser());
        ConsoleLogger logger = consoleLogger(lineReader);

        return console(
                logger,
                new CommandRegistry(logger, commandScanner.scan()),
                queryExecutor,
                traineeSession(exerciseRepository(queryExecutor), exerciseValidator(logger)),
                statementValidator(),
                lineReader
        );
    }

    private static void validateConnection(CypherQueryExecutor queryExecutor) {
        try {
            List<Map<String, Object>> result = queryExecutor.rollback(tx -> {
                return tx.run("RETURN true as result").list(Record::asMap);
            });
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

    private static TraineeSession traineeSession(ExerciseRepository exerciseRepository, ExerciseValidator validator) {
        return new TraineeSession(exerciseRepository, validator);
    }

    private static ExerciseRepository exerciseRepository(CypherQueryExecutor queryExecutor) {
        return new ExerciseRepository(queryExecutor);
    }

    private static ConsoleLogger consoleLogger(LineReader lineReader) {
        return new ConsoleLogger(lineReader.getTerminal());
    }

    private static ExerciseValidator exerciseValidator(ConsoleLogger logger) {
        return new ExerciseValidator(logger, new Kryo());
    }

    private static Console console(ConsoleLogger logger,
                                   CommandRegistry commandRegistry,
                                   CypherQueryExecutor cypherQueryExecutor,
                                   TraineeSession traineeSession,
                                   CypherStatementValidator statementValidator,
                                   LineReader lineReader) {

        return new Console(
                logger,
                lineReader,
                commandRegistry,
                traineeSession,
                cypherQueryExecutor,
                statementValidator
        );
    }

    private static CypherQueryExecutor cypherQueryExecutor(ReplConfiguration configuration) {
        return new CypherQueryExecutor(configuration);
    }

    private static CypherStatementValidator statementValidator() {
        return new CypherStatementValidator();
    }

    private static LineReader lineReader(Parser parser) {
        try {
            return LineReaderBuilder.builder().terminal(TerminalBuilder.terminal()).parser(parser).build();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
