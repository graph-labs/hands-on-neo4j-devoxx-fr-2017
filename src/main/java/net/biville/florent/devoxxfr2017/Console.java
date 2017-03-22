package net.biville.florent.devoxxfr2017;

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
        System.out.println();
        while (true) {
            try {
                String statement = terminal.readLine("(:Devoxx)-[:`<3`]-(:Cypher)> ");
                statementValidator.validate(statement)
                        .map(CypherError::getMessage)
                        .forEachOrdered(System.err::println);
                queryExecutor.readOne(statement, "result");
            } catch (UserInterruptException e) {
            } catch (EndOfFileException e) {
                System.out.println("Goodbye!");
                return;
            }
        }
    }
}
