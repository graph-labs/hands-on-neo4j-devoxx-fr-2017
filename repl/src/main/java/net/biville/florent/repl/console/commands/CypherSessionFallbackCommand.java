package net.biville.florent.repl.console.commands;

import net.biville.florent.repl.exercises.ExerciseValidation;
import net.biville.florent.repl.exercises.TraineeSession;
import net.biville.florent.repl.graph.cypher.CypherError;
import net.biville.florent.repl.graph.cypher.CypherQueryExecutor;
import net.biville.florent.repl.graph.cypher.CypherStatementValidator;
import net.biville.florent.repl.logging.ConsoleLogger;
import org.jline.utils.AttributedStyle;

import java.util.Collection;

/**
 * Default command meant to be executed if and only if other commands do not match.
 * This validates the expression against the embedded grammar and execute it if valid.
 */
public class CypherSessionFallbackCommand implements Command {

    private final ConsoleLogger logger;
    private final CypherQueryExecutor cypherQueryExecutor;
    private final CypherStatementValidator statementValidator;

    public CypherSessionFallbackCommand(ConsoleLogger logger,
                                        CypherQueryExecutor cypherQueryExecutor,
                                        CypherStatementValidator statementValidator) {

        this.logger = logger;
        this.cypherQueryExecutor = cypherQueryExecutor;
        this.statementValidator = statementValidator;
    }

    @Override
    public boolean matches(String query) {
        return false;
    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public void accept(TraineeSession session, String statement) {
        Collection<CypherError> errors = statementValidator.validate(statement);
        if (!errors.isEmpty()) {
            logger.error("An error occurred with your query. See details below:");
            errors.forEach(err -> logger.error(err.toString()));
        }
        else {
            ExerciseValidation validation = session.validate(cypherQueryExecutor.execute(statement));
            if (!validation.isSuccessful()) {
                logger.error(validation.getReport());
                return;
            }
            if (session.isCompleted()) {
                logger.log("Congrats, you're done!!!", AttributedStyle.BOLD.background(AttributedStyle.GREEN));
                return;
            }
            logger.log(validation.getReport());
            logger.log("Now moving on to next exercise! See instructions below...");
            logger.log(session.getCurrentExercise().getStatement());
        }
    }

}
