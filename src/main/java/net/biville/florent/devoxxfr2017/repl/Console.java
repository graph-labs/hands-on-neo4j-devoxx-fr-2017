package net.biville.florent.devoxxfr2017.repl;

import net.biville.florent.devoxxfr2017.graph.cypher.CypherError;
import net.biville.florent.devoxxfr2017.graph.cypher.CypherQueryExecutor;
import net.biville.florent.devoxxfr2017.graph.cypher.CypherStatementValidator;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

public class Console {

    private final CypherQueryExecutor queryExecutor;
    private final CypherStatementValidator statementValidator;
    private final LineReader terminal;

    public Console(CypherQueryExecutor cypherQueryExecutor,
                   CypherStatementValidator statementValidator,
                   LineReader terminal) {

        this.queryExecutor = cypherQueryExecutor;
        this.statementValidator = statementValidator;
        this.terminal = terminal;
    }

    public void start() {
        System.out.println();
        System.out.println("Welcome to Devoxx France 2017 Hands on Neo4j!");
        System.out.println("Please make sure your Cypher statements end with a semicolon.");
        System.out.println("First enter a username, with the command :login username"); //TODO
        // once the username is input, create session, keep it around and start first exercise
        // need command :show to show exercise statement
        System.out.println();
        while (true) {
            try {
                String statement = terminal.readLine("(:Devoxx)-[:`<3`]-(:Cypher)> ");
                statementValidator.validate(statement)
                        .map(CypherError::getMessage)
                        .forEachOrdered(System.err::println);
                queryExecutor.execute(statement);
            } catch (UserInterruptException ignored) {
            } catch (EndOfFileException e) {
                System.out.println("Goodbye!");
                return;
            }
        }
    }
}
